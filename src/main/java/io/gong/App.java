package io.gong;

import io.gong.calendar.AvailabilityFinder;
import io.gong.calendar.csv.CsvCalendarLoader;
import io.gong.calendar.model.CalendarEvent;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {

    private static final String CALENDAR_RESOURCE = "/io/gong/calendar.csv";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        InputStream in = App.class.getResourceAsStream(CALENDAR_RESOURCE);
        if (in == null) {
            throw new IllegalStateException("calendar.csv not found on classpath: " + CALENDAR_RESOURCE);
        }

        List<CalendarEvent> events = new CsvCalendarLoader().load(in);
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            List.of("Alice", "Jack"),
            Duration.ofMinutes(60)
        );

        for (LocalTime slot : slots) {
            System.out.println("Available slot: " + slot.format(TIME_FORMAT));
        }
    }
}
