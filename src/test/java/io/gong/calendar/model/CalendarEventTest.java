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
}
