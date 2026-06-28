# Solution Notes

## Assumptions

- The calendar is a single day, from 07:00 to 19:00.
- Times in the CSV must use `HH:mm`. Shorter forms like `8:00` and longer forms like `08:00:00` are rejected to keep the format predictable.
- If the first row is exactly the header (`Person name, Event subject, Event start time, Event end time`, in that order, ignoring case), it is skipped. Any other first row is read as data.
- Blank lines in the CSV are ignored.
- A leading UTF-8 BOM is stripped so the first name or header still matches.
- The file is read as UTF-8, so non-English names and titles work, for example `Renée`.
- Meeting duration must be a positive whole number of minutes.
- Durations shorter than a minute are rejected instead of rounded, so the requested meeting length is not silently changed.
- Candidate start times are checked in steps of the meeting length, beginning at 07:00. For a 60-minute meeting, this gives hourly start times and matches the README example.
- A meeting ending exactly at 19:00 is allowed.
- A person with no events is treated as free for the whole day.
- If an event starts before 07:00 or ends after 19:00, only the part inside the day is counted.
- People are matched by name, ignoring case and surrounding spaces. The original name is kept on `CalendarEvent`; the lower-cased version is only used for matching.
- The requested person list must contain at least one name.
- Blank names in the requested list are rejected.
- If the same name is requested more than once, in any casing, it is counted once.

## Design

- CSV parsing is separated from availability calculation.
- Availability logic returns values and does not print.
- `App.java` is the entry point: it loads the CSV, wires the finder, and prints the results. It also accepts optional people and duration command-line arguments.
- When the finder is built, each person's events are converted into a "busy minutes" `BitSet` for the day.
- Building the busy index is a single pass over the events. Each query combines only the requested people's `BitSet`s and scans the fixed 720-minute day, so queries do not walk the full event list again.

## Project choices

- The project stays on the provided Java 11, Maven, and JUnit 4 setup.
- Apache Commons CSV is used for parsing instead of manual `String.split`, because CSV has edge cases such as quoted commas and escaped quotes.
- Spring and REST APIs were not added because the exercise asks for a simple Java app with a `main` entry point.
- A small `CalendarEventLoader` interface is used for the input boundary. The availability logic stays a single concrete class because there is only one implementation, and splitting it further would make it harder to read.
- No logging framework was added. The CLI prints the required output and usage errors, while core logic returns values or throws clear exceptions.
- Lombok and extra abstractions were not added to avoid over-engineering a small CLI exercise.

## Extensibility

- The loader checks the 4-column CSV format strictly. If the format changes, the change stays inside the loader, and only reaches the model if the new field affects availability.
- A new input format can be added by implementing another `CalendarEventLoader`.
- To support more than one day, a date can be added to the busy index while reusing the same per-day calculation.

## Extra features

- `findFirstAvailableSlot(personList, eventDuration)` returns the earliest start time everyone is free as an `Optional<LocalTime>`. It reuses `findAvailableSlots` and takes the first result.
- `findFreeWindows(personList)` returns all free time ranges for the requested people as `TimeRange`s, without needing a meeting length. It uses half-open `[start, end)` ranges.
- Both methods read from the same `BitSet`: `findAvailableSlots` turns it into duration-aligned start times, while `findFreeWindows` turns it into raw free ranges. They are two views of the same data.

## Run

```bash
# run all tests
mvn clean test

# default: Alice and Jack, 60-minute meeting
mvn compile exec:java

# custom: pass comma-separated people and duration in minutes
mvn compile exec:java -Dexec.args="Alice,Jack,Bob 30"
```
