package cn.raiyee.easyhi.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data @NoArgsConstructor
public class DayTimeRuler {
    private int maxConcurrent = 1;     // 任一时刻同时允许最大人数
    private LongRange dayRange;
    private ArrayList<DayTimeRulerPart> times = Lists.newArrayList();
    private HashSet<String> ids = Sets.newHashSet();

    public DayTimeRuler(long millisAtStartOfDay, int maxConcurrent) {
        val millis = new DateTime(millisAtStartOfDay).plusDays(1).getMillis();
        this.dayRange = LongRange.of(millisAtStartOfDay, millis);
        this.times.add(DayTimeRulerPart.of(LongRange.of(dayRange), DayTimeRulerPart.REST, "休", "0"));
        this.maxConcurrent = maxConcurrent;
    }

    public void addUsedTimeSpan(long start, long end, String timeSpanName, String id) {
        if (ids.contains(id)) throw new RuntimeException("id(" + id + ") is already added!");

        addTimeSpan(start, end, DayTimeRulerPart.AVAIL, DayTimeRulerPart.USED, timeSpanName, id);
        ids.add(id);
    }

    public void delUsedTimeSpan(String id) {
        val found = ids.remove(id);
        val foundPos = found ? find(id) : -1;
        if (!found || foundPos < 0) throw new RuntimeException("id(" + id + ") does not exist!");

        ArrayList<DayTimeRulerPart> newTimes = Lists.newArrayList();

        addUnchanged(newTimes, 0, foundPos - 1);
        merge(newTimes, foundPos);
        addUnchanged(newTimes, foundPos + 2, times.size());

        times = newTimes;
    }

    private int find(String id) {
        for (int i = 0, ii = times.size(); i < ii; ++i) {
            if (id.equals(times.get(i).getId())) return i;
        }

        return -1;
    }

    private void merge(List<DayTimeRulerPart> newTimes, int foundPos) {
        val prev = foundPos == 0 ? null : times.get(foundPos - 1);
        val found = times.get(foundPos);
        val next = foundPos == times.size() - 1 ? null : times.get(foundPos + 1);
        if (prev != null && prev.getType() == DayTimeRulerPart.AVAIL) {
            prev.setRange(LongRange.of(prev.getRange().getStart(), found.getRange().getEnd()));
            newTimes.add(prev);
            newTimes.add(next);
        } else if (next != null && next.getType() == DayTimeRulerPart.AVAIL) {
            newTimes.add(prev);
            next.setRange(LongRange.of(found.getRange().getStart(), next.getRange().getEnd()));
            newTimes.add(next);
        } else {
            newTimes.add(prev);
            found.setType(DayTimeRulerPart.AVAIL);
            newTimes.add(found);
            newTimes.add(next);
        }
    }

    private void addUnchanged(List<DayTimeRulerPart> newTimes, int start, int end) {
        for (int i = start; i < end; ++i) {
            newTimes.add(times.get(i));
        }
    }

    public void addWorkingTimeSpan(long start, long end) {
        addTimeSpan(start, end, DayTimeRulerPart.REST, DayTimeRulerPart.AVAIL, "订", "0");
    }

    private void addTimeSpan(long start, long end, int from, int to, String timeSpanName, String id) {
        LongRange range = checkStartEnd(start, end);

        ArrayList<DayTimeRulerPart> newTimes = Lists.newArrayList();
        boolean processed = false;

        for (val t : times) {
            if (!processed && availForSpanType(range, t, from)) {
                val t1 = t.getRange();
                if (t1.getStart() < start) {
                    newTimes.add(DayTimeRulerPart.of(LongRange.of(t1.getStart(), start), from, t.getName(), t.getId()));
                }
                val added = DayTimeRulerPart.of(range, to, timeSpanName, id);
                newTimes.add(added);
                if (end < t1.getEnd()) {
                    newTimes.add(DayTimeRulerPart.of(LongRange.of(end, t1.getEnd()), from, t.getName(), t.getId()));
                }
                processed = true;
            } else {
                newTimes.add(t);
            }
        }

        if (!processed) throw new BadTimeSpanException(format(range) + " can not added for current timespans");

        times = newTimes;
    }

    private LongRange checkStartEnd(long start, long end) {
        val range = LongRange.of(start, end);
        if (!dayRange.encloses(range))
            throw new IllegalArgumentException(dayRange + " is not in day " + new DateTime(dayRange.getStart()).toString("yyyy-MM-dd"));
        return range;
    }

    private boolean availForSpanType(LongRange range, DayTimeRulerPart time, Integer spanType) {
        if (time.getType() != spanType) return false;
        if (time.getRange().encloses(range)) return true;
        if (time.getRange().isConnected(range)) {
            throw new BadTimeSpanException(range + " is connected to " + format(time));
        }

        return false;
    }

    private String format(DayTimeRulerPart time) {
        return format(time.getRange()) + ":" + nameType(time.getType());
    }

    private String format(LongRange range) {
        return '[' + format(range.getStart()) + ", " + format(range.getEnd()) + ")";
    }

    private String format(Long millis) {
        return new DateTime(millis).toString("HH:mm");
    }

    public String createRuler() {
        val l1 = new StringBuilder(new DateTime(dayRange.getStart()).toString("MM-dd"));
        StringBuilder l2 = new StringBuilder(), l3 = new StringBuilder("00:00"), l4 = new StringBuilder();

        for (val t : times) {
            val wrap = repeat(t);
            l2.append("|").append(wrap).append(nameType(t.getType())).append(wrap);
            align(l2.length(), l1, l3);

            l3.append(format(t.getRange().getEnd()));
            if (t.getType() == DayTimeRulerPart.USED) {
                val from = format(t.getRange().getStart());
                val to = format(t.getRange().getEnd());
                l4.append(from).append("-").append(to).append(" ").append(t.getName()).append('(').append(t.getId()).append(')').append("\n");
            }
        }

        align(l2.length(), l1, l3);
        l2.append("|");
        l1.append(new DateTime(dayRange.getEnd()).toString("MM-dd"));

        return "\n" + l1 + '\n' + l2 + '\n' + l3 + '\n' + l4;
    }

    private String nameType(int t2) {
        switch (t2) {
            case DayTimeRulerPart.REST: return "REST";
            case DayTimeRulerPart.AVAIL: return "AVAIL";
            case DayTimeRulerPart.USED:
            default: return "USED";
        }
    }

    private String repeat(DayTimeRulerPart t) {
        return StringUtils.repeat(separator(t.getType()), 5);
    }

    private char separator(int t2) {
        switch (t2) {
            case DayTimeRulerPart.REST: return '-';
            case DayTimeRulerPart.AVAIL: return '+';
            case DayTimeRulerPart.USED:
            default: return '=';
        }
    }

    private void align(int sb2Len, StringBuilder... sbs) {
        for (val sb : sbs)
            sb.append(StringUtils.repeat(' ', sb2Len - sb.length()));
    }

    public static class BadTimeSpanException extends RuntimeException {
        public BadTimeSpanException(String message) {
            super(message);
        }
    }
}
