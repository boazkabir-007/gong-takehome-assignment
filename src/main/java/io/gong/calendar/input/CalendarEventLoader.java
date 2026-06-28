package io.gong.calendar.input;

import io.gong.calendar.model.CalendarEvent;

import java.io.InputStream;
import java.util.List;

public interface CalendarEventLoader {
    /**
     * Loads calendar events from the given stream.
     * Implementations consume and close the stream.
     */
    List<CalendarEvent> load(InputStream in);
}
