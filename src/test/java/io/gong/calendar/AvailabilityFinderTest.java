package io.gong.calendar;

import io.gong.calendar.model.CalendarEvent;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class AvailabilityFinderTest {

    @Test
    public void readmeExampleAliceAndJack60Minutes() {
        List<CalendarEvent> events = Arrays.asList(
            event("Alice", "Morning meeting",   "08:00", "09:30"),
            event("Alice", "Lunch with Jack",   "13:00", "14:00"),
            event("Alice", "Yoga",              "16:00", "17:00"),
            event("Jack",  "Morning meeting",   "08:00", "08:50"),
            event("Jack",  "Sales call",        "09:00", "09:40"),
            event("Jack",  "Lunch with Alice",  "13:00", "14:00"),
            event("Jack",  "Yoga",              "16:00", "17:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Arrays.asList("Alice", "Jack"), Duration.ofMinutes(60)
        );

        assertEquals(times("07:00", "10:00", "11:00", "12:00", "14:00", "15:00", "17:00", "18:00"), slots);
    }

    @Test
    public void personWithNoEventsIsAvailableAllDay() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Eve"), Duration.ofMinutes(60)
        );

        assertEquals(times("07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
                           "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"), slots);
    }

    @Test
    public void ignoresEventsForPeopleNotRequested() {
        List<CalendarEvent> events = Collections.singletonList(
            event("Bob", "Busy", "07:00", "19:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(60)
        );

        assertEquals(times("07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
                           "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"), slots);
    }

    @Test
    public void caseInsensitivePersonNameMatchesCalendar() {
        List<CalendarEvent> events = Collections.singletonList(
            event("Alice", "Morning meeting", "08:00", "09:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("alice"), Duration.ofMinutes(60)
        );

        assertEquals(times("07:00", "09:00", "10:00", "11:00", "12:00",
                           "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"), slots);
    }

    @Test
    public void personNameWithSurroundingWhitespaceMatchesCalendar() {
        List<CalendarEvent> events = Collections.singletonList(
            event("Alice", "Morning meeting", "08:00", "09:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList(" Alice "), Duration.ofMinutes(60)
        );

        assertEquals(times("07:00", "09:00", "10:00", "11:00", "12:00",
                           "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"), slots);
    }

    @Test
    public void duplicateRequestedPeopleAreIgnored() {
        List<CalendarEvent> events = Collections.singletonList(
            event("Alice", "Morning", "08:00", "09:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> singular  = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(60)
        );
        List<LocalTime> duplicates = finder.findAvailableSlots(
            Arrays.asList("Alice", "alice", " ALICE "), Duration.ofMinutes(60)
        );

        assertEquals(singular, duplicates);
    }

    @Test
    public void backToBackEventsBlockContinuously() {
        List<CalendarEvent> events = Arrays.asList(
            event("Bob", "Morning meeting",   "08:00", "09:30"),
            event("Bob", "Morning meeting 2", "09:30", "09:40")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Bob"), Duration.ofMinutes(60)
        );

        assertEquals(times("07:00", "10:00", "11:00", "12:00", "13:00",
                           "14:00", "15:00", "16:00", "17:00", "18:00"), slots);
    }

    @Test
    public void overlappingEventsAreTreatedAsOneBusyBlock() {
        List<CalendarEvent> events = Arrays.asList(
            event("Alice", "Meeting A", "08:00", "10:00"),
            event("Alice", "Meeting B", "09:00", "11:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(60)
        );

        assertEquals(times("07:00", "11:00", "12:00", "13:00", "14:00",
                           "15:00", "16:00", "17:00", "18:00"), slots);
    }

    @Test
    public void slotAt18IsValidWhenMeetingEndsAt19() {
        List<CalendarEvent> events = Collections.singletonList(
            event("Alice", "Morning block", "07:00", "18:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(60)
        );

        assertEquals(times("18:00"), slots);
    }

    @Test
    public void exactFullDayDurationReturnsOnlySevenAm() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofHours(12)
        );

        assertEquals(times("07:00"), slots);
    }

    @Test
    public void durationLongerThanBusinessDayReturnsEmpty() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(721)
        );

        assertTrue(slots.isEmpty());
    }

    @Test
    public void returnsEmptyWhenPersonIsBusyAllDay() {
        List<CalendarEvent> events = Collections.singletonList(
            event("Alice", "All day", "07:00", "19:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(60)
        );

        assertTrue(slots.isEmpty());
    }

    @Test
    public void clampsEventsOutsideBusinessHours() {
        List<CalendarEvent> events = Arrays.asList(
            event("Alice", "Early", "06:00", "08:00"),
            event("Alice", "Late",  "18:00", "20:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(60)
        );

        assertEquals(times("08:00", "09:00", "10:00", "11:00", "12:00",
                           "13:00", "14:00", "15:00", "16:00", "17:00"), slots);
    }

    @Test
    public void oneMinuteSlotCanStartWhenPreviousEventEnds() {
        List<CalendarEvent> events = Arrays.asList(
            event("Alice", "Morning block", "07:00", "09:39"),
            event("Alice", "Next block",    "09:40", "19:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(1)
        );

        assertEquals(times("09:39"), slots);
    }

    @Test
    public void supportsNinetyMinuteDuration() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(90)
        );

        assertEquals(times("07:00", "08:30", "10:00", "11:30", "13:00", "14:30", "16:00", "17:30"), slots);
    }

    @Test
    public void thirtyMinuteDurationStepsByThirtyMinutes() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("Alice"), Duration.ofMinutes(30)
        );

        assertEquals(24, slots.size());
        assertEquals(LocalTime.of(7, 0), slots.get(0));
        assertEquals(LocalTime.of(7, 30), slots.get(1));
        assertEquals(LocalTime.of(18, 30), slots.get(slots.size() - 1));
    }

    @Test
    public void throwsOnEmptyPersonList() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
            finder.findAvailableSlots(Collections.emptyList(), Duration.ofMinutes(60))
        );
    }

    @Test
    public void throwsOnNullPersonList() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        assertThrows(NullPointerException.class, () ->
            finder.findAvailableSlots(null, Duration.ofMinutes(60))
        );
    }

    @Test
    public void throwsOnNullPersonName() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
            finder.findAvailableSlots(Arrays.asList("Alice", null), Duration.ofMinutes(60))
        );
    }

    @Test
    public void throwsOnBlankPersonName() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
            finder.findAvailableSlots(Arrays.asList("Alice", "  "), Duration.ofMinutes(60))
        );
    }

    @Test
    public void throwsOnNullDuration() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        assertThrows(NullPointerException.class, () ->
            finder.findAvailableSlots(Collections.singletonList("Alice"), null)
        );
    }

    @Test
    public void throwsOnNegativeDuration() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
            finder.findAvailableSlots(Collections.singletonList("Alice"), Duration.ofMinutes(-30))
        );
    }

    @Test
    public void throwsOnZeroDuration() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
            finder.findAvailableSlots(Collections.singletonList("Alice"), Duration.ZERO)
        );
    }

    @Test
    public void throwsOnSubMinuteDuration() {
        AvailabilityFinder finder = new AvailabilityFinder(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
            finder.findAvailableSlots(Collections.singletonList("Alice"), Duration.ofSeconds(30))
        );
    }

    @Test
    public void throwsOnNullEvent() {
        assertThrows(NullPointerException.class, () ->
            new AvailabilityFinder(Arrays.asList(
                event("Alice", "Morning", "08:00", "09:00"),
                null
            ))
        );
    }

    @Test
    public void sourceRowsWithDifferentPersonCaseAreMergedAsSamePerson() {
        List<CalendarEvent> events = Arrays.asList(
            event("Alice", "Morning",   "08:00", "09:00"),
            event("alice", "Afternoon", "14:00", "15:00")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Collections.singletonList("ALICE"), Duration.ofMinutes(60)
        );

        assertEquals(times("07:00", "09:00", "10:00", "11:00", "12:00",
                           "13:00", "15:00", "16:00", "17:00", "18:00"), slots);
    }

    @Test
    public void findsSlotsForDenseMultiPersonCalendar() {
        List<CalendarEvent> events = Arrays.asList(
            event("Alice", "Meeting",  "08:00", "09:00"),
            event("Alice", "Standup",  "10:30", "11:00"),
            event("Jack",  "Meeting",  "08:30", "09:30"),
            event("Jack",  "Lunch",    "12:00", "13:00"),
            event("Bob",   "Meeting",  "09:00", "10:00"),
            event("Bob",   "Review",   "14:00", "15:30")
        );
        AvailabilityFinder finder = new AvailabilityFinder(events);

        List<LocalTime> slots = finder.findAvailableSlots(
            Arrays.asList("Alice", "Jack", "Bob"), Duration.ofMinutes(30)
        );

        assertEquals(times(
            "07:00", "07:30", "10:00", "11:00", "11:30",
            "13:00", "13:30", "15:30", "16:00", "16:30",
            "17:00", "17:30", "18:00", "18:30"
        ), slots);
    }

    private static CalendarEvent event(String person, String title, String start, String end) {
        return new CalendarEvent(person, title, LocalTime.parse(start), LocalTime.parse(end));
    }

    private static List<LocalTime> times(String... times) {
        LocalTime[] result = new LocalTime[times.length];
        for (int i = 0; i < times.length; i++) {
            result[i] = LocalTime.parse(times[i]);
        }
        return Arrays.asList(result);
    }
}
