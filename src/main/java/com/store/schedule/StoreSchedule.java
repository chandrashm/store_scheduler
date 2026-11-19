package com.store.schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StoreSchedule {
    private final String storeId;
    private final ScheduleMerger merger;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final List<SourceScheduleEntry> rawEntries = new ArrayList<>();
    private volatile List<TimeWindow> canonicalWindows = Collections.emptyList();

    public StoreSchedule(String storeId, ScheduleMerger merger) {
        this.storeId = storeId;
        this.merger = merger;
    }

    public String getStoreId() {
        return storeId;
    }

    public void addSchedule(ScheduleSource source, RuleType ruleType, LocalDateTime start, LocalDateTime end) {
        rwLock.writeLock().lock();
        try {
            rawEntries.add(new SourceScheduleEntry(source, ruleType, new TimeWindow(start, end)));
            canonicalWindows = merger.merge(rawEntries);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<TimeWindow> getCanonicalWindows() {
        return canonicalWindows;
    }

    public boolean isStoreOpenAt(LocalDateTime time) {
        List<TimeWindow> windows = this.canonicalWindows;
        int low = 0;
        int high = windows.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            TimeWindow midWindow = windows.get(mid);

            if (midWindow.contains(time)) {
                return true;
            } else if (time.isBefore(midWindow.getStart())) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return false;
    }

    public Duration getTotalOpenDurationForDate(LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        long totalMinutes = 0;
        for (TimeWindow window : this.canonicalWindows) {
            LocalDateTime effectiveStart = window.getStart().isAfter(dayStart) ? window.getStart() : dayStart;
            LocalDateTime effectiveEnd = window.getEnd().isBefore(dayEnd) ? window.getEnd() : dayEnd;

            if (effectiveStart.isBefore(effectiveEnd)) {
                totalMinutes += Duration.between(effectiveStart, effectiveEnd).toMinutes();
            }
        }
        return Duration.ofMinutes(totalMinutes);
    }
}
