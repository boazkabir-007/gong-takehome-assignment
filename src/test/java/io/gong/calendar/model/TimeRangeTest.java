package io.gong.calendar.model;

import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertThrows;

public class TimeRangeTest {

    @Test
    public void throwsWhenStartIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new TimeRange(null, LocalTime.of(9, 0)));
    }

    @Test
    public void throwsWhenEndIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new TimeRange(LocalTime.of(8, 0), null));
    }

    @Test
    public void throwsWhenEndEqualsStart() {
        assertThrows(IllegalArgumentException.class, () ->
            new TimeRange(LocalTime.of(9, 0), LocalTime.of(9, 0)));
    }

    @Test
    public void throwsWhenEndIsBeforeStart() {
        assertThrows(IllegalArgumentException.class, () ->
            new TimeRange(LocalTime.of(10, 0), LocalTime.of(9, 0)));
    }
}
