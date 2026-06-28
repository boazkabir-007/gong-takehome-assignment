package io.gong.calendar.csv;

import io.gong.calendar.model.CalendarEvent;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CsvCalendarLoaderTest {

    private final CsvCalendarLoader loader = new CsvCalendarLoader();

    @Test
    public void loadsAllEventsFromFixture() {
        List<CalendarEvent> events = loader.load(fixture());

        assertEquals(12, events.size());
    }

    @Test
    public void parsesFieldsCorrectly() {
        List<CalendarEvent> events = loader.load(fixture());

        CalendarEvent first = events.get(0);
        assertEquals("Alice", first.getPerson());
        assertEquals("Morning meeting", first.getTitle());
        assertEquals(LocalTime.of(8, 0), first.getStart());
        assertEquals(LocalTime.of(9, 30), first.getEnd());
    }

    @Test
    public void throwsOnMalformedRow() {
        String csv = "Alice,\"Missing end time\",08:00\n";

        assertThrows(IllegalArgumentException.class, () -> loader.load(input(csv)));
    }

    @Test
    public void parsesQuotedTitleWithComma() {
        String csv = "Alice,\"Lunch, with Jack\",13:00,14:00\n";

        List<CalendarEvent> events = loader.load(input(csv));

        assertEquals(1, events.size());
        assertEquals("Lunch, with Jack", events.get(0).getTitle());
    }

    @Test
    public void throwsOnInvalidTimeFormat() {
        String csv = "Alice,\"Bad time\",not-a-time,09:00\n";

        assertThrows(IllegalArgumentException.class, () -> loader.load(input(csv)));
    }

    @Test
    public void throwsWhenEventEndIsBeforeStart() {
        String csv = "Alice,\"Bad range\",10:00,09:00\n";

        assertThrows(IllegalArgumentException.class, () -> loader.load(input(csv)));
    }

    @Test
    public void emptyCsvReturnsEmptyList() {
        List<CalendarEvent> events = loader.load(input(""));

        assertEquals(0, events.size());
    }

    private InputStream fixture() {
        InputStream in = getClass().getResourceAsStream("/io/gong/calendar.csv");
        if (in == null) {
            throw new IllegalStateException("Test fixture not found: /io/gong/calendar.csv");
        }
        return in;
    }

    private InputStream input(String csv) {
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
}
