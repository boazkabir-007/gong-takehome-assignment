package io.gong.calendar.input;

import io.gong.calendar.model.CalendarEvent;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvCalendarLoader implements CalendarEventLoader {

    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final String UTF8_BOM = "\uFEFF";
    private static final String[] HEADER_FIELDS = {
        "person name", "event subject", "event start time", "event end time"
    };

    public List<CalendarEvent> load(InputStream in) {
        Objects.requireNonNull(in, "Calendar input stream must not be null");
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            List<CalendarEvent> events = new ArrayList<>();
            boolean firstRecord = true;
            for (CSVRecord record : records) {
                if (firstRecord) {
                    firstRecord = false;
                    if (isHeaderRow(record)) {
                        continue;
                    }
                }
                events.add(parseRecord(record));
            }
            return events;
        } catch (IOException | UncheckedIOException e) {
            throw new IllegalStateException("Failed to read calendar data", e);
        }
    }

    private boolean isHeaderRow(CSVRecord record) {
        if (record.size() != 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            String field = record.get(i).trim();
            if (i == 0) {
                field = stripBom(field);
            }
            if (!field.equalsIgnoreCase(HEADER_FIELDS[i])) {
                return false;
            }
        }
        return true;
    }

    private CalendarEvent parseRecord(CSVRecord record) {
        if (record.size() != 4) {
            throw new IllegalArgumentException(
                "Expected 4 fields per row, got " + record.size() + " at line " + record.getRecordNumber()
            );
        }
        try {
            String person = stripBom(record.get(0).trim());
            String title  = record.get(1).trim();
            LocalTime start = LocalTime.parse(record.get(2).trim(), TIME_FORMAT);
            LocalTime end   = LocalTime.parse(record.get(3).trim(), TIME_FORMAT);
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

    private String stripBom(String value) {
        return value.startsWith(UTF8_BOM) ? value.substring(1) : value;
    }
}
