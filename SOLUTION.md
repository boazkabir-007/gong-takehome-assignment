# Solution Notes

## Assumptions

- The calendar is a single day, from 07:00 to 19:00.
- Times in the CSV must be written as `HH:mm`. I reject shorter forms like `8:00` and longer ones like `08:00:00`, so the format stays predictable.
- If the first row is exactly the header (`Person name, Event subject, Event start time, Event end time`, in that order, ignoring case), I skip it. Any other first row is read as data.
- Blank lines in the CSV are ignored.
- If the file starts with a UTF-8 BOM, I strip it so the first name still matches.
- The file is read as UTF-8, so non-English names and titles work (for example `Renée`).
- A meeting duration has to be a positive whole number of minutes.
- I reject durations shorter than a minute instead of rounding them, so I never quietly change the meeting length that was asked for.
- I check start times in steps of the meeting length, beginning at 07:00. For a 60-minute meeting this gives hourly start times, which matches the example in the README.
- A meeting that ends exactly at 19:00 is allowed.
- Someone with no events is treated as free for the whole day.
- If an event starts before 07:00 or ends after 19:00, I only count the part that falls inside the day.
- I match people by name, ignoring case and surrounding spaces. The original name is kept on the `CalendarEvent`; the lower-cased version is only used for matching.
- The list of requested people has to have at least one name.
- Blank names in the requested list are rejected.
- If the same name is requested more than once (in any casing), I only count it once.

## Design

- I kept CSV parsing separate from the availability calculation.
- The availability logic returns values and never prints anything itself.
- `App.java` is the entry point: it loads the CSV, wires the finder together, and prints the results. It can also take a list of people and a duration as command-line arguments.
- When the finder is built, I turn each person's events into a "busy minutes" `BitSet` for the day.
- Building that busy index is a single pass over the events. Each query only combines the requested people's `BitSet`s and scans the fixed 720-minute day, so a query never has to walk the whole event list again.

## Project choices

- I stayed on the starter Java 11 + Maven setup and JUnit 4, so the project matches what was provided.
- I didn't add Spring or a REST API, because the exercise asks for a simple Java app with a `main` entry point.
- I added one small `CalendarEventLoader` interface for the input boundary. The availability logic stays a single concrete class, since there is only one implementation and splitting it would make it harder to read.
- I didn't add logging frameworks, Lombok, or extra abstractions, to avoid over-engineering a small CLI exercise.

## Extensibility

- The loader checks the 4-column CSV format strictly. If that format ever changes, the change stays inside the loader (and the model only if it has to).
- A new input format can be added by writing another `CalendarEventLoader`.
- To support more than one day, I would add a date to the busy index and reuse the same per-day calculation.

## Extra features

- `findFirstAvailableSlot(personList, eventDuration)` returns the earliest start time everyone is free, as an `Optional<LocalTime>`. It just reuses `findAvailableSlots` and takes the first result.
- `findFreeWindows(personList)` returns all the free time ranges for the requested people as `TimeRange`s, without needing a meeting length. It uses half-open `[start, end)` ranges.
- Both methods read from the same `BitSet`: `findAvailableSlots` turns it into duration-aligned start times, while `findFreeWindows` turns it into the raw free ranges. They are two views of the same data.

## Run

```bash
# run all tests
mvn clean test

# default: Alice and Jack, 60-minute meeting
mvn compile exec:java

# custom: pass comma-separated people and duration in minutes
mvn compile exec:java -Dexec.args="Alice,Jack,Bob 30"
```
