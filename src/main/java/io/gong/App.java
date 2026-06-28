package io.gong;

import io.gong.calendar.AvailabilityFinder;
import io.gong.calendar.csv.CsvCalendarLoader;
import io.gong.calendar.input.CalendarEventLoader;
import io.gong.calendar.model.CalendarEvent;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class App {

    private static final String CALENDAR_RESOURCE = "/io/gong/calendar.csv";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final List<String> DEFAULT_PEOPLE = List.of("Alice", "Jack");
    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(60);

    public static void main(String[] args) {
        List<String> people;
        Duration duration;

        try {
            if (args.length == 0) {
                people = DEFAULT_PEOPLE;
                duration = DEFAULT_DURATION;
            } else if (args.length == 2) {
                people = parsePeople(args[0]);
                duration = parseDuration(args[1]);
            } else {
                printUsage();
                System.exit(1);
                return;
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(1);
            return;
        }

        InputStream in = App.class.getResourceAsStream(CALENDAR_RESOURCE);
        if (in == null) {
            throw new IllegalStateException("calendar.csv not found on classpath: " + CALENDAR_RESOURCE);
        }

        CalendarEventLoader loader = new CsvCalendarLoader();
        List<CalendarEvent> events = loader.load(in);
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(people, duration);

        if (slots.isEmpty()) {
            System.out.println("No available slots found.");
        } else {
            for (LocalTime slot : slots) {
                System.out.println("Available slot: " + slot.format(TIME_FORMAT));
            }
        }
    }

    private static void printUsage() {
        System.err.println("Usage: <comma-separated-people> <duration-minutes>");
        System.err.println("Example: Alice,Jack,Bob 30");
    }

    private static List<String> parsePeople(String arg) {
        if (arg == null || arg.isBlank()) {
            throw new IllegalArgumentException("People argument must not be blank");
        }
        String[] names = arg.split(",", -1);
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].trim();
            if (names[i].isEmpty()) {
                throw new IllegalArgumentException("People argument must not contain blank names");
            }
        }
        return Arrays.asList(names);
    }

    private static Duration parseDuration(String arg) {
        int minutes;
        try {
            minutes = Integer.parseInt(arg.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Duration must be a positive integer number of minutes: " + arg);
        }
        if (minutes <= 0) {
            throw new IllegalArgumentException("Duration must be a positive integer number of minutes: " + arg);
        }
        return Duration.ofMinutes(minutes);
    }
}
