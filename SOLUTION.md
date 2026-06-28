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
- Person names are treated as identifiers and matched case-sensitively after trimming surrounding whitespace.
- Blank person names in the requested list are rejected.
- Duplicate requested names are ignored after trimming.

## Design

- CSV parsing and availability calculation are separated.
- Core availability logic returns values and does not print.
- `App.java` is used only for loading input, calling the finder, and printing results.
- During construction, events are converted into per-person busy-minute `BitSet`s.
- Each availability request combines only the requested people's `BitSet`s and checks candidate start times against the requested duration.

## Run

```bash
mvn clean test
mvn compile exec:java
```
