package cn.raiyee.easyhi.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class LongRange implements Comparable<LongRange> {
    private long start;
    private long end;

    public static LongRange of(long start, long end) {
        return new LongRange(start, end);
    }

    public static LongRange of(LongRange l) {
        return new LongRange(l.start, l.end);
    }

    public boolean encloses(LongRange range) {
        return start <= range.start && range.end <= end;
    }

    public boolean isConnected(LongRange other) {
        return start <= other.end && other.start <= end;
    }

    @Override public String toString() {
        return "[" + start + ", " + end + ')';
    }

    @Override public int compareTo(LongRange o) {
        return start != o.start ? Long.compare(start, o.start) : Long.compare(end, o.end);
    }
}
