package io.gong.calendar.csv;

import io.gong.calendar.model.CalendarEvent;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvCalendarLoader {

    public List<CalendarEvent> load(InputStream in) {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            List<CalendarEvent> events = new ArrayList<>();
            for (CSVRecord record : records) {
                events.add(parseRecord(record));
            }
            return events;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read calendar data", e);
        }
    }

    private CalendarEvent parseRecord(CSVRecord record) {
        if (record.size() != 4) {
            throw new IllegalArgumentException(
                "Expected 4 fields per row, got " + record.size() + " at line " + record.getRecordNumber()
            );
        }
        try {
            String person = record.get(0).trim();
            String title  = record.get(1).trim();
            LocalTime start = LocalTime.parse(record.get(2).trim());
            LocalTime end   = LocalTime.parse(record.get(3).trim());
            return new CalendarEvent(person, title, start, end);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid time format at line " + record.getRecordNumber() + ": " + e.getParsedString(), e
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid calendar row at line " + record.getRecordNumber() + ": " + e.getMessage(), e
            );
        }
    }
}
