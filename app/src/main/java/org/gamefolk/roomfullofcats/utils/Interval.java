package org.gamefolk.roomfullofcats.utils;

/**
 * Simple wrapper around two millisecond values representing a time period.
 */
public class Interval {
    private final long start;
    private final long end;

    public Interval(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getDifference() {
        return end - start;
    }

    public boolean contains(long time) {
        return start <= time && time < end;
    }
}
