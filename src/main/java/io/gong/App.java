package io.gong;

import io.gong.calendar.AvailabilityFinder;
import io.gong.calendar.csv.CsvCalendarLoader;
import io.gong.calendar.input.CalendarEventLoader;
import io.gong.calendar.model.CalendarEvent;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {

    private static final String CALENDAR_RESOURCE = "/io/gong/calendar.csv";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final List<String> REQUESTED_PEOPLE = List.of("Alice", "Jack");
    private static final Duration MEETING_DURATION = Duration.ofMinutes(60);

    public static void main(String[] args) {
        InputStream in = App.class.getResourceAsStream(CALENDAR_RESOURCE);
        if (in == null) {
            throw new IllegalStateException("calendar.csv not found on classpath: " + CALENDAR_RESOURCE);
        }

        CalendarEventLoader loader = new CsvCalendarLoader();
        List<CalendarEvent> events = loader.load(in);
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(REQUESTED_PEOPLE, MEETING_DURATION);

        for (LocalTime slot : slots) {
            System.out.println("Available slot: " + slot.format(TIME_FORMAT));
        }
    }
}
