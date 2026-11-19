package com.store.schedule;

import java.util.List;

public interface ScheduleMerger {
    List<TimeWindow> merge(List<SourceScheduleEntry> entries);
}
