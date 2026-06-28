package io.gong.calendar.input;

import io.gong.calendar.model.CalendarEvent;

import java.io.InputStream;
import java.util.List;

public interface CalendarEventLoader {
    /**
     * Loads calendar events from the given stream and closes it.
     */
    List<CalendarEvent> load(InputStream in);
}
