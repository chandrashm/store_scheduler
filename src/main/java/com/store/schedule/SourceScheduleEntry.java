package com.store.schedule;

public class SourceScheduleEntry {
    private final ScheduleSource source;
    private final RuleType ruleType;
    private final TimeWindow window;

    public SourceScheduleEntry(ScheduleSource source, RuleType ruleType, TimeWindow window) {
        this.source = source;
        this.ruleType = ruleType;
        this.window = window;
    }

    public SourceScheduleEntry(ScheduleSource source, TimeWindow window) {
        this(source, RuleType.OPEN, window);
    }

    public ScheduleSource getSource() {
        return source;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public TimeWindow getWindow() {
        return window;
    }
}
