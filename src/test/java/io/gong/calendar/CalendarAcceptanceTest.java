package io.gong.calendar;

import io.gong.calendar.input.CsvCalendarLoader;
import io.gong.calendar.model.CalendarEvent;
import org.junit.Test;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CalendarAcceptanceTest {

    @Test
    public void aliceAndJack60MinutesMatchesReadmeExample() {
        List<CalendarEvent> events = new CsvCalendarLoader().load(fixture());
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            List.of("Alice", "Jack"), Duration.ofMinutes(60)
        );

        List<LocalTime> expected = List.of(
            LocalTime.of(7, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(12, 0),
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            LocalTime.of(17, 0),
            LocalTime.of(18, 0)
        );
        assertEquals(expected, slots);
    }

    private InputStream fixture() {
        InputStream in = getClass().getResourceAsStream("/io/gong/calendar.csv");
        if (in == null) {
            throw new IllegalStateException("Test fixture not found: /io/gong/calendar.csv");
        }
        return in;
    }
}
