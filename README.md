# Store Schedule Aggregation — Java LLD Solution

## Overview
Aggregates overlapping opening and closing schedule windows from multiple sources (`HEAD_OFFICE`, `FRANCHISE`, `TEMP_EXTENSION`) into a canonical non-overlapping schedule.

## Key Features
- **O(N log N)** interval merging (`UnionScheduleMerger`)
- **O(N log N)** blackout/closed override subtraction (`OverrideScheduleMerger`)
- **O(log K)** binary search query for `isStoreOpenAt(LocalDateTime)`
- **O(K)** daily duration clipping across overnight shifts (`getTotalOpenDurationForDate(LocalDate)`)
- **Thread-Safe Reads/Writes** using `ReentrantReadWriteLock` + `volatile` immutable snapshots
