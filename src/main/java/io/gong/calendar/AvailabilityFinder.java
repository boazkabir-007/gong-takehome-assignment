package io.gong.calendar;

import io.gong.calendar.model.CalendarEvent;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AvailabilityFinder {

    private static final LocalTime DAY_START = LocalTime.of(7, 0);
    private static final LocalTime DAY_END = LocalTime.of(19, 0);
    private static final int DAY_MINUTES = (int) Duration.between(DAY_START, DAY_END).toMinutes();
    private final Map<String, BitSet> busyMinutesByPerson;

    public AvailabilityFinder(List<CalendarEvent> events) {
        Objects.requireNonNull(events, "Events list must not be null");
        this.busyMinutesByPerson = buildBusyMinutesByPerson(events);
    }

    public List<LocalTime> findAvailableSlots(List<String> personList, Duration eventDuration) {
        Set<String> people = requestedPeople(personList);
        int durationMinutes = durationInWholeMinutes(eventDuration);

        if (durationMinutes > DAY_MINUTES) {
            return List.of();
        }

        BitSet busyMinutes = combineBusyMinutes(people);
        return findAvailableStartTimes(busyMinutes, durationMinutes);
    }

    private Map<String, BitSet> buildBusyMinutesByPerson(List<CalendarEvent> events) {
        Map<String, BitSet> map = new HashMap<>();

        for (CalendarEvent event : events) {
            Objects.requireNonNull(event, "Event must not be null");
            BitSet busy = map.computeIfAbsent(personLookupKey(event.getPerson()), p -> new BitSet(DAY_MINUTES));
            busy.set(minuteOffset(event.getStart()), minuteOffset(event.getEnd()));
        }

        return map;
    }

    private BitSet combineBusyMinutes(Set<String> people) {
        BitSet busy = new BitSet(DAY_MINUTES);

        for (String person : people) {
            BitSet personBusy = busyMinutesByPerson.get(person);
            if (personBusy != null) {
                busy.or(personBusy);
            }
        }

        return busy;
    }

    private Set<String> requestedPeople(List<String> personList) {
        Objects.requireNonNull(personList, "Person list must not be null");

        Set<String> people = new HashSet<>();
        for (String person : personList) {
            people.add(personLookupKey(person));
        }

        if (people.isEmpty()) {
            throw new IllegalArgumentException("Person list must not be empty");
        }

        return people;
    }

    private int durationInWholeMinutes(Duration duration) {
        Objects.requireNonNull(duration, "Event duration must not be null");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("Event duration must be positive");
        }
        if (duration.getNano() != 0 || duration.getSeconds() % 60 != 0) {
            throw new IllegalArgumentException("Event duration must be a whole number of minutes");
        }
        return Math.toIntExact(duration.toMinutes());
    }

    private List<LocalTime> findAvailableStartTimes(BitSet busyMinutes, int durationMinutes) {
        List<LocalTime> slots = new ArrayList<>();

        for (int start = 0; start + durationMinutes <= DAY_MINUTES; start += durationMinutes) {
            if (isAvailable(busyMinutes, start, durationMinutes)) {
                slots.add(DAY_START.plusMinutes(start));
            }
        }

        return slots;
    }

    private boolean isAvailable(BitSet busyMinutes, int startMinute, int durationMinutes) {
        int nextBusy = busyMinutes.nextSetBit(startMinute);
        return nextBusy == -1 || nextBusy >= startMinute + durationMinutes;
    }

    private String personLookupKey(String person) {
        if (person == null || person.isBlank()) {
            throw new IllegalArgumentException("Person names must not be blank");
        }
        return person.trim().toLowerCase(Locale.ROOT);
    }

    private int minuteOffset(LocalTime time) {
        int offset = (int) Duration.between(DAY_START, time).toMinutes();
        return Math.max(0, Math.min(offset, DAY_MINUTES));
    }
}
