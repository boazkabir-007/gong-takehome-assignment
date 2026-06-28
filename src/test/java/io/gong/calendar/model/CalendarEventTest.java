package io.gong.calendar.model;

import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertThrows;

public class CalendarEventTest {

    @Test
    public void throwsWhenStartAndEndAreEqual() {
        assertThrows(IllegalArgumentException.class, () ->
            new CalendarEvent("Alice", "Zero length", LocalTime.of(9, 0), LocalTime.of(9, 0))
        );
    }

    @Test
    public void throwsWhenEndIsBeforeStart() {
        assertThrows(IllegalArgumentException.class, () ->
            new CalendarEvent("Alice", "Reversed", LocalTime.of(10, 0), LocalTime.of(9, 0))
        );
    }

    @Test
    public void throwsWhenPersonIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new CalendarEvent(null, "Meeting", LocalTime.of(9, 0), LocalTime.of(10, 0))
        );
    }

    @Test
    public void throwsWhenPersonIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            new CalendarEvent("   ", "Meeting", LocalTime.of(9, 0), LocalTime.of(10, 0))
        );
    }

    @Test
    public void throwsWhenTitleIsNull() {
        assertThrows(NullPointerException.class, () ->
            new CalendarEvent("Alice", null, LocalTime.of(9, 0), LocalTime.of(10, 0))
        );
    }

    @Test
    public void throwsWhenStartIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new CalendarEvent("Alice", "Meeting", null, LocalTime.of(10, 0))
        );
    }

    @Test
    public void throwsWhenEndIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new CalendarEvent("Alice", "Meeting", LocalTime.of(9, 0), null)
        );
    }
}
