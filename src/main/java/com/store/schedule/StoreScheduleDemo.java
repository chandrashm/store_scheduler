package com.store.schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StoreScheduleDemo {
    public static void main(String[] args) {
        StoreSchedule store = new StoreSchedule("BLR-101", new OverrideScheduleMerger());
        LocalDate today = LocalDate.of(2026, 11, 20);

        // 1. Head office standard hours: 09:00 to 17:00
        store.addSchedule(ScheduleSource.HEAD_OFFICE, RuleType.OPEN,
                today.atTime(9, 0), today.atTime(17, 0));

        // 2. Franchise extension: 16:00 to 20:00
        store.addSchedule(ScheduleSource.FRANCHISE, RuleType.OPEN,
                today.atTime(16, 0), today.atTime(20, 0));

        // 3. Temporary late-night extension: 21:00 to 23:00
        store.addSchedule(ScheduleSource.TEMP_EXTENSION, RuleType.OPEN,
                today.atTime(21, 0), today.atTime(23, 0));

        // 4. Head office mandatory maintenance closure: 13:00 to 14:00
        store.addSchedule(ScheduleSource.HEAD_OFFICE, RuleType.CLOSED,
                today.atTime(13, 0), today.atTime(14, 0));

        System.out.println("Canonical Windows: " + store.getCanonicalWindows());
        System.out.println("Is open at 13:30? " + store.isStoreOpenAt(today.atTime(13, 30)));
        System.out.println("Is open at 18:30? " + store.isStoreOpenAt(today.atTime(18, 30)));
        System.out.println("Is open at 20:00? " + store.isStoreOpenAt(today.atTime(20, 0)));

        Duration total = store.getTotalOpenDurationForDate(today);
        System.out.println("Total Open Duration: " + total.toHours() + " hours (" + total.toMinutes() + " mins)");
    }
}
