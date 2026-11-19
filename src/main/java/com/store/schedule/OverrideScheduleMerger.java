package com.store.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OverrideScheduleMerger implements ScheduleMerger {
    private final UnionScheduleMerger unionMerger = new UnionScheduleMerger();

    @Override
    public List<TimeWindow> merge(List<SourceScheduleEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyList();
        }

        List<SourceScheduleEntry> openEntries = new ArrayList<>();
        List<SourceScheduleEntry> closedAsOpenEntries = new ArrayList<>();

        for (SourceScheduleEntry entry : entries) {
            if (entry.getRuleType() == RuleType.OPEN) {
                openEntries.add(entry);
            } else {
                closedAsOpenEntries.add(new SourceScheduleEntry(entry.getSource(), RuleType.OPEN, entry.getWindow()));
            }
        }

        List<TimeWindow> mergedOpen = unionMerger.merge(openEntries);
        List<TimeWindow> mergedClosed = unionMerger.merge(closedAsOpenEntries);

        return subtractClosedWindows(mergedOpen, mergedClosed);
    }

    private List<TimeWindow> subtractClosedWindows(List<TimeWindow> opens, List<TimeWindow> closeds) {
        List<TimeWindow> result = new ArrayList<>();
        int cIdx = 0;

        for (TimeWindow open : opens) {
            LocalDateTime currentStart = open.getStart();
            LocalDateTime openEnd = open.getEnd();

            while (cIdx < closeds.size() && !closeds.get(cIdx).getEnd().isAfter(currentStart)) {
                cIdx++;
            }

            int tempIdx = cIdx;
            while (tempIdx < closeds.size() && closeds.get(tempIdx).getStart().isBefore(openEnd)) {
                TimeWindow closed = closeds.get(tempIdx);

                if (closed.getStart().isAfter(currentStart)) {
                    result.add(new TimeWindow(currentStart, closed.getStart()));
                }
                currentStart = closed.getEnd().isAfter(currentStart) ? closed.getEnd() : currentStart;

                if (!currentStart.isBefore(openEnd)) {
                    break;
                }
                tempIdx++;
            }

            if (currentStart.isBefore(openEnd)) {
                result.add(new TimeWindow(currentStart, openEnd));
            }
        }
        return Collections.unmodifiableList(result);
    }
}
