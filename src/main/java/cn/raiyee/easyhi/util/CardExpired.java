package cn.raiyee.easyhi.util;

import lombok.Value;
import lombok.val;
import org.joda.time.DateTime;

@Value
public class CardExpired {
    private final ExpiredUnit.Expired expired;
    private final DateTime effective;
    private final DateTime expiredTime;

    /**
     * 生成卡片有效期。
     * 首次使用时激活，包括订课激活，以及补扣激活。
     * 补扣激活是以补扣课程时间为有效期开始，正常订课时以订课时间（非课程时间）为有效期开始
     *
     * @param expiredValue  有效期取值
     * @param expiredUnit   有效期单位
     * @param scheduleStart 课程开始时间
     */
    public static CardExpired of(int expiredValue, String expiredUnit, DateTime scheduleStart) {
        val expired = ExpiredUnit.of(expiredValue, expiredUnit);
        val effective = (scheduleStart != null && scheduleStart.isBeforeNow() ? scheduleStart : DateTime.now()).withTimeAtStartOfDay();
        return new CardExpired(expired, effective, expired.createExpired(effective));
    }

    /**
     * 生成卡片有效期。
     * 首次使用时激活，包括订课激活，以及补扣激活。
     * 补扣激活是以补扣课程时间为有效期开始，正常订课时以订课时间（非课程时间）为有效期开始
     *
     * @param expiredValue 有效期取值
     * @param expiredUnit  有效期单位
     */
    public static CardExpired of(int expiredValue, String expiredUnit) {
        return of(expiredValue, expiredUnit, DateTime.now());
    }

    public String getDesc() {
        return expired.getDesc();
    }
}
