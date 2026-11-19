package com.store.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnionScheduleMerger implements ScheduleMerger {
    @Override
    public List<TimeWindow> merge(List<SourceScheduleEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyList();
        }

        List<TimeWindow> windows = new ArrayList<>();
        for (SourceScheduleEntry entry : entries) {
            if (entry.getRuleType() == RuleType.OPEN) {
                windows.add(entry.getWindow());
            }
        }

        if (windows.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.sort(windows);

        List<TimeWindow> merged = new ArrayList<>();
        TimeWindow current = windows.get(0);

        for (int i = 1; i < windows.size(); i++) {
            TimeWindow next = windows.get(i);
            if (!next.getStart().isAfter(current.getEnd())) {
                LocalDateTime maxEnd = current.getEnd().isAfter(next.getEnd())
                        ? current.getEnd()
                        : next.getEnd();
                current = new TimeWindow(current.getStart(), maxEnd);
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);

        return Collections.unmodifiableList(merged);
    }
}
