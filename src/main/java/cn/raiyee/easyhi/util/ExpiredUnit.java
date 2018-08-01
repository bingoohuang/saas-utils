package cn.raiyee.easyhi.util;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;

import java.util.Arrays;

//  时间单位对象。
@RequiredArgsConstructor
public enum ExpiredUnit {
    年("N", "年"), 月("Y", "个月"),
    天("T", "天"), 周("Z", "周");

    @Getter private final String unit;
    @Getter private final String suffix; // 单位作为数量后缀时的名称

    public static ExpiredUnit ofUnit(String unit) {
        return Arrays.stream(ExpiredUnit.values()).filter(x -> x.unit.equals(unit)).findFirst()
                .orElseThrow(() -> new RuntimeException("unknown expire unit " + unit));
    }

    public static ExpiredUnit ofUnit(String unit, ExpiredUnit defaultUnit) {
        return Arrays.stream(ExpiredUnit.values()).filter(x -> x.unit.equals(unit)).findFirst()
                .orElse(defaultUnit);
    }

    public static boolean isValid(String unit) {
        return Arrays.stream(ExpiredUnit.values()).anyMatch(x -> x.unit.equals(unit));
    }

    public static Expired of(String value, String unit) {
        return of(NumberUtils.toInt(value), unit);
    }

    public static Expired of(int value, String unit) {
        val expiredUnit = ofUnit(unit, ExpiredUnit.年);
        return expiredUnit == ExpiredUnit.天 && value % 7 == 0
                ? new Expired(value / 7, ExpiredUnit.周)
                : new Expired(value, expiredUnit);
    }

    @Value
    public static class Expired {
        private final int value;
        private final ExpiredUnit unit;

        public String getDesc() {
            return value < 0 ? "无限期" : (value + unit.getSuffix());
        }

        // 作为/后面的文字描述。例如1次/月,3次/2周
        public String getOfDesc() {
            return value < 0
                    ? "无限期"

                    : value == 1
                    ? unit.name()
                    : value + unit.name();
        }

        public DateTime createExpired(DateTime effective) {
            val start = effective.withTimeAtStartOfDay().plusDays(1);
            if (value < 0) return start.plusYears(100);

            switch (unit) {
                case 月: return start.plusMonths(value);
                case 天: return start.plusDays(value);
                case 周: return start.plusWeeks(value);
                case 年:
                default: return start.plusYears(value);
            }
        }
    }
}
