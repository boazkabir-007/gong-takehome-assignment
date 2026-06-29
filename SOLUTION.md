# Solution Notes

## Assumptions

- Times in the CSV must be in `HH:mm` format. Values like `8:00`, `08:00:00`, and `24:00` are rejected.
- A first row that exactly matches the column names is skipped. Any other first row is treated as data.
- Blank CSV lines are ignored.
- A leading UTF-8 BOM is stripped, so the first field still loads correctly.
- If an event starts before 07:00 or ends after 19:00, only the part inside the working day is used.
- Start times are checked in steps of the requested meeting length, starting at 07:00. This matches the README example, where a 60-minute meeting returns hourly starts.
- A meeting can end exactly at 19:00.
- A person with no events is considered free for the whole day.
- Names are matched ignoring case and spaces around the name.
- The requested people list must not be empty, and blank names are rejected.
- Meeting duration must be a positive whole number of minutes.

## Design

- CSV loading and availability calculation are separate.
- `App.java` loads the CSV, creates the finder, and prints the result.
- Input loading goes through a small `CalendarEventLoader` interface. `CsvCalendarLoader` is the current implementation.
- The availability code returns values. It does not print by itself.
- Each person's events are converted once into busy minutes for the working day, using a `BitSet`.
- Each search combines the requested people's busy time and scans the day once.
- `AvailabilityFinder` contains the availability calculation. There is only one availability algorithm.

## Project choices

- The project keeps the provided Java 11, Maven, and JUnit 4 setup.
- Apache Commons CSV is used instead of `String.split`, because real CSV can contain quoted commas and escaped quotes.
- Spring, REST, Lombok, and a logging framework were not added. The command line app prints output and errors, and the calculation code throws clear exceptions.

## Extra features

- `findFirstAvailableSlot(personList, eventDuration)` returns the first available start time.
- `findFreeWindows(personList)` returns the free time ranges for the requested people.
- The extra methods reuse the same busy-time data: one returns the first slot, and the other returns free ranges.

## Run

```bash
mvn clean test
mvn compile exec:java
mvn compile exec:java -Dexec.args="Alice,Jack,Bob 30"
```
