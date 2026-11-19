package com.store.schedule;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StoreScheduleTest {

    @Test
    void shouldMergeOverlappingAndAdjacentSchedules() {
        StoreSchedule store = new StoreSchedule("BLR-101", new UnionScheduleMerger());
        LocalDate today = LocalDate.of(2026, 11, 20);

        store.addSchedule(ScheduleSource.HEAD_OFFICE, RuleType.OPEN, today.atTime(9, 0), today.atTime(13, 0));
        store.addSchedule(ScheduleSource.FRANCHISE, RuleType.OPEN, today.atTime(13, 0), today.atTime(17, 0));
        store.addSchedule(ScheduleSource.TEMP_EXTENSION, RuleType.OPEN, today.atTime(16, 0), today.atTime(19, 0));

        assertEquals(1, store.getCanonicalWindows().size());
        assertEquals(Duration.ofHours(10), store.getTotalOpenDurationForDate(today));
        assertTrue(store.isStoreOpenAt(today.atTime(9, 0)));
        assertFalse(store.isStoreOpenAt(today.atTime(19, 0)));
    }

    @Test
    void shouldSubtractClosedOverridesCorrectly() {
        StoreSchedule store = new StoreSchedule("BLR-102", new OverrideScheduleMerger());
        LocalDate today = LocalDate.of(2026, 11, 20);

        store.addSchedule(ScheduleSource.HEAD_OFFICE, RuleType.OPEN, today.atTime(9, 0), today.atTime(17, 0));
        store.addSchedule(ScheduleSource.HEAD_OFFICE, RuleType.CLOSED, today.atTime(13, 0), today.atTime(14, 0));

        assertEquals(2, store.getCanonicalWindows().size());
        assertEquals(Duration.ofHours(7), store.getTotalOpenDurationForDate(today));
        assertFalse(store.isStoreOpenAt(today.atTime(13, 30)));
    }

    @Test
    void shouldHandleOvernightShiftAcrossTwoDays() {
        StoreSchedule store = new StoreSchedule("BLR-103", new UnionScheduleMerger());
        LocalDate friday = LocalDate.of(2026, 11, 20);
        LocalDate saturday = LocalDate.of(2026, 11, 21);

        store.addSchedule(ScheduleSource.TEMP_EXTENSION, RuleType.OPEN, friday.atTime(22, 0), saturday.atTime(2, 0));

        assertEquals(Duration.ofHours(2), store.getTotalOpenDurationForDate(friday));
        assertEquals(Duration.ofHours(2), store.getTotalOpenDurationForDate(saturday));
        assertTrue(store.isStoreOpenAt(saturday.atTime(1, 30)));
    }
}
