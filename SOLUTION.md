# Solution Notes

## Assumptions

- The calendar represents one day, from 07:00 to 19:00.
- The CSV input uses `HH:mm`, so calendar precision is minutes.
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
- `App.java` is used only for loading input, calling the finder, and printing results.
- During construction, events are converted into per-person busy-minute `BitSet`s.
- Each availability request combines only the requested people's `BitSet`s and checks candidate start times against the requested duration.

## Project choices

- Kept the starter Java 11 Maven setup and JUnit 4 test stack to stay aligned with the provided project.
- Did not add Spring or a REST API because the exercise asks for a simple Java application with a `main` entry point.
- Added one small `CalendarEventLoader` interface for the input boundary; the availability algorithm remains a concrete, focused class because there is only one implementation.
- Did not add logging frameworks, Lombok, or additional abstractions to avoid over-engineering a focused CLI exercise.

## Extensibility

- The loader validates the current 4-column CSV contract strictly. If the CSV schema changes, the change is localized to the loader and, only if needed, the domain model.
- Additional input formats can be added by implementing `CalendarEventLoader`.
- Multi-day support would add a date dimension to the current per-person busy-minute index while reusing the same per-day availability calculation.

## Run

```bash
# run all tests
mvn clean test

# default: Alice and Jack, 60-minute meeting
mvn compile exec:java

# custom: pass comma-separated people and duration in minutes
mvn compile exec:java -Dexec.args="Alice,Jack,Bob 30"
```
