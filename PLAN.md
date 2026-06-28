# Implementation Plan

## Slice 1 — Domain Model ✅
- `CalendarEvent` — immutable class: person, title, start, end

## Slice 2 — CSV Loading
- `CsvCalendarLoader` — `List<CalendarEvent> load(InputStream in)`
- Add `commons-csv:1.10.0` to pom.xml
- Tests: 12 events load correctly, malformed row throws

## Slice 3 — Core Algorithm
- `AvailabilityFinder(List<CalendarEvent> events)`
- `List<LocalTime> findAvailableSlots(List<String> personList, Duration eventDuration)`
- BitSet of 720 minutes (07:00–19:00), mark busy minutes, scan hourly candidates
- Tests: README example, back-to-back events, person with no events, boundary cases

## Slice 4 — App.java Wiring
- Load CSV from classpath, construct finder, print results
- `mvn compile exec:java` prints the 8 expected slots

## Slice 5 — Tests and Fixtures
- Copy `calendar.csv` to `src/test/resources/io/gong/`
- `CalendarAcceptanceTest` — end-to-end with fixture
- Edge cases: empty person list, all busy, exact-fit duration

# TODO - just for me not for claude:
- configurable business day
- configurable slot granularity
- repository abstraction
- validator classes
- metrics/performance instrumentation
- Lombok
- explicit thread-safety docs

