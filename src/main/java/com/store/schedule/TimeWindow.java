package com.store.schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public final class TimeWindow implements Comparable<TimeWindow> {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeWindow(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be strictly before end time.");
        }
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public boolean contains(LocalDateTime time) {
        return !time.isBefore(start) && time.isBefore(end);
    }

    public long getDurationInMinutes() {
        return Duration.between(start, end).toMinutes();
    }

    @Override
    public int compareTo(TimeWindow other) {
        int startComparison = this.start.compareTo(other.start);
        return startComparison != 0 ? startComparison : this.end.compareTo(other.end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeWindow)) return false;
        TimeWindow that = (TimeWindow) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "[" + start + " - " + end + ")";
    }
}
