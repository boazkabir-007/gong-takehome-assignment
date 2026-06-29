package io.gong.calendar.model;

import java.time.LocalTime;

public final class CalendarEvent {

    private final String person;
    private final String title;
    private final LocalTime start;
    private final LocalTime end;

    public CalendarEvent(String person, String title, LocalTime start, LocalTime end) {
        if (person == null || person.isBlank()) {
            throw new IllegalArgumentException("Person name must not be blank");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("Event start and end times must not be null");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Event end time must be after start time");
        }
        if (title == null) {
            throw new IllegalArgumentException("Event title must not be null");
        }
        this.person = person.trim();
        this.title  = title;
        this.start  = start;
        this.end    = end;
    }

    public String getPerson() { return person; }
    public String getTitle()  { return title; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd()   { return end; }

    @Override
    public String toString() {
        return "CalendarEvent{person='" + person + "', title='" + title + "', start=" + start + ", end=" + end + '}';
    }
}
