package io.gong.calendar;

import io.gong.calendar.model.CalendarEvent;

import java.io.InputStream;
import java.util.List;

public interface CalendarEventLoader {
    /**
     * Implementations consume and close the stream.
     */
    List<CalendarEvent> load(InputStream in);
}
