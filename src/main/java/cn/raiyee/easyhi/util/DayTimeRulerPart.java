package cn.raiyee.easyhi.util;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
public class DayTimeRulerPart implements Comparable<DayTimeRulerPart> {
    final static int REST = 1; // 休息时间
    final static int AVAIL = 2;// 工作时间
    final static int USED = 3; // 上课时间

    private LongRange range;
    private int type;
    private String name;
    private String id;
    private Set<DayTimeRulerPart> encloses; // 中间包含的并发课程，以顺序排列

    public static DayTimeRulerPart of(LongRange range, int type, String name, String id) {
        return new DayTimeRulerPart(range, type, name, id, type == USED ? Sets.newTreeSet() : null);
    }

    @Override public int compareTo(DayTimeRulerPart o) {
        return range.compareTo(o.range);
    }
}
