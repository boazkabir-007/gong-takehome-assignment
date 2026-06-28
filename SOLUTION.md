# Solution Notes

## Assumptions

- The calendar represents one day, from 07:00 to 19:00.
- The CSV input uses `HH:mm` format strictly; partial formats (`8:00`) and extended formats (`08:00:00`) are rejected.
- A matching header row is accepted and skipped (exact ordered field names, case-insensitive).
- Blank lines in the CSV are skipped.
- A leading UTF-8 BOM is stripped and tolerated.
- UTF-8 input is supported, including non-ASCII names and titles.
- Meeting durations are accepted as positive whole-minute values.
- Sub-minute durations are rejected instead of rounded, to avoid silently changing the requested meeting length.
- Candidate start times are evaluated on boundaries of the requested duration, starting from 07:00. For a 60-minute meeting this produces hourly candidates and matches the provided example.
- A meeting ending exactly at 19:00 is valid.
- A person without events is considered available for the full day.
- Events outside business hours are clamped to the 07:00–19:00 window.
- Person names are matched case-insensitively after trimming surrounding whitespace. Original names are preserved in CalendarEvent; normalization is used only for matching.
- The requested person list must contain at least one person.
- Blank person names in the requested list are rejected.
- Duplicate requested names are ignored after trimming and case normalization.

## Design

- CSV parsing and availability calculation are separated.
- Core availability logic returns values and does not print.
- `App.java` is the composition root: loads the CSV, wires the finder, and prints results. Optionally accepts people and duration as CLI arguments.
- During construction, events are converted into per-person busy-minute `BitSet`s.
- Building the per-person busy index is a single pass over the events. Each query combines only the requested people's `BitSet`s and scans the fixed 720-minute business day, so queries do not rescan the full event list.

## Project choices

- Kept the starter Java 11 Maven setup and JUnit 4 test stack to stay aligned with the provided project.
- Did not add Spring or a REST API because the exercise asks for a simple Java application with a `main` entry point.
- Added one small `CalendarEventLoader` interface for the input boundary; the availability algorithm remains a concrete, focused class because there is only one implementation.
- Did not add logging frameworks, Lombok, or additional abstractions to avoid over-engineering a focused CLI exercise.

## Extensibility

- The loader validates the current 4-column CSV contract strictly. If the CSV schema changes, the change is localized to the loader and, only if needed, the domain model.
- Additional input formats can be added by implementing `CalendarEventLoader`.
- Multi-day support would add a date dimension to the current per-person busy-minute index while reusing the same per-day availability calculation.

## Extra features

- `findFirstAvailableSlot(personList, eventDuration)` returns the earliest available start time as an `Optional<LocalTime>`, delegating to `findAvailableSlots` and taking the first element.
- `findFreeWindows(personList)` returns all maximal free `TimeRange`s for the requested people, independent of any meeting duration, using half-open `[start, end)` ranges.
- Both methods share the same `BitSet` model: `findAvailableSlots` derives duration-aligned start candidates from it, while `findFreeWindows` derives raw free ranges — two different projections over one data structure.

## Run

```bash
# run all tests
mvn clean test

# default: Alice and Jack, 60-minute meeting
mvn compile exec:java

# custom: pass comma-separated people and duration in minutes
mvn compile exec:java -Dexec.args="Alice,Jack,Bob 30"
```
