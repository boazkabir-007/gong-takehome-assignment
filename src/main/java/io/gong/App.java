package io.gong;

import io.gong.calendar.AvailabilityFinder;
import io.gong.calendar.input.CalendarEventLoader;
import io.gong.calendar.input.CsvCalendarLoader;
import io.gong.calendar.model.CalendarEvent;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class App {

    private static final String CALENDAR_RESOURCE = "/io/gong/calendar.csv";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final List<String> DEFAULT_PEOPLE = List.of("Alice", "Jack");
    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(60);

    public static void main(String[] args) {
        run(args, App::createAvailabilityFinder);
    }

    static void run(String[] args, Supplier<AvailabilityFinder> finderSupplier) {
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
                return;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
            return;
        }

        try {
            AvailabilityFinder finder = finderSupplier.get();
            List<LocalTime> slots = finder.findAvailableSlots(people, duration);
            printSlots(slots);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static AvailabilityFinder createAvailabilityFinder() {
        CalendarEventLoader loader = new CsvCalendarLoader();
        List<CalendarEvent> events = loader.load(openCalendarResource());
        return new AvailabilityFinder(events);
    }

    private static InputStream openCalendarResource() {
        InputStream in = App.class.getResourceAsStream(CALENDAR_RESOURCE);
        if (in == null) {
            throw new IllegalStateException("calendar.csv not found on classpath: " + CALENDAR_RESOURCE);
        }
        return in;
    }

    private static void printSlots(List<LocalTime> slots) {
        if (slots.isEmpty()) {
            System.out.println("No available slots found.");
            return;
        }
        for (LocalTime slot : slots) {
            System.out.println("Available slot: " + slot.format(TIME_FORMAT));
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
        String value = arg.trim();
        int minutes;
        try {
            minutes = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Duration must be a positive integer number of minutes: " + arg);
        }
        if (minutes <= 0) {
            throw new IllegalArgumentException("Duration must be a positive integer number of minutes: " + arg);
        }
        return Duration.ofMinutes(minutes);
    }
}
