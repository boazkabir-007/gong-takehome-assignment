package io.gong.calendar.model;

import java.time.LocalTime;
import java.util.Objects;

public final class TimeRange {

    private final LocalTime start;
    private final LocalTime end;

    public TimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("TimeRange start and end must not be null");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("TimeRange end must be after start");
        }
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() { return start; }
    public LocalTime getEnd()   { return end; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeRange)) return false;
        TimeRange other = (TimeRange) o;
        return start.equals(other.start) && end.equals(other.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "TimeRange{start=" + start + ", end=" + end + '}';
    }
}
