package cn.raiyee.easyhi.util;

import com.alibaba.fastjson.JSON;
import lombok.val;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class DayTimeRulerTest {
    @Test
    public void testBlank() {
        val dayTimeRuler = new DayTimeRuler(millisDay("2018-08-01"), 1);
        val ruler1 = "\n08-01          08-02\n" +
                /**/   "|-----REST-----|\n" +
                /**/   "00:00          00:00\n";
        assertThat(dayTimeRuler.createRuler()).isEqualTo(ruler1);
    }

    @Test
    public void testWorkingTimes() {
        val dayTimeRuler = new DayTimeRuler(millisDay("2018-08-01"), 1);
        dayTimeRuler.addWorkingTimeSpan(millis("2018-08-01 08:00"), millis("2018-08-01 12:00"));
        dayTimeRuler.addWorkingTimeSpan(millis("2018-08-01 14:00"), millis("2018-08-01 20:00"));
        val ruler2 = "\n08-01                                                                        08-02\n" +
                /**/   "|-----REST-----|+++++AVAIL+++++|-----REST-----|+++++AVAIL+++++|-----REST-----|\n" +
                /**/   "00:00          08:00           12:00          14:00           20:00          00:00\n";
        assertThat(dayTimeRuler.createRuler()).isEqualTo(ruler2);
    }

    @Test
    public void testUsedTimes() {
        val dayTimeRuler = new DayTimeRuler(millisDay("2018-08-01"), 1);
        dayTimeRuler.addWorkingTimeSpan(millis("2018-08-01 08:00"), millis("2018-08-01 12:00"));
        dayTimeRuler.addWorkingTimeSpan(millis("2018-08-01 14:00"), millis("2018-08-01 20:00"));

        dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 08:00"), millis("2018-08-01 09:00"), "流瑜伽", "100");
        dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 14:00"), millis("2018-08-01 15:00"), "孕瑜伽", "101");

        val ruler1 = "\n08-01                                                                                                      08-02\n" +
                /**/   "|-----REST-----|=====USED=====|+++++AVAIL+++++|-----REST-----|=====USED=====|+++++AVAIL+++++|-----REST-----|\n" +
                /**/   "00:00          08:00          09:00           12:00          14:00          15:00           20:00          00:00\n" +
                /**/   "08:00-09:00 流瑜伽(100)\n" +
                /**/   "14:00-15:00 孕瑜伽(101)\n";
        assertThat(dayTimeRuler.createRuler()).isEqualTo(ruler1);

        dayTimeRuler.delUsedTimeSpan("100");
        val ruler2 = "\n08-01                                                                                       08-02\n" +
                /**/   "|-----REST-----|+++++AVAIL+++++|-----REST-----|=====USED=====|+++++AVAIL+++++|-----REST-----|\n" +
                /**/   "00:00          08:00           12:00          14:00          15:00           20:00          00:00\n" +
                /**/   "14:00-15:00 孕瑜伽(101)\n";
        assertThat(dayTimeRuler.createRuler()).isEqualTo(ruler2);

        dayTimeRuler.delUsedTimeSpan("101");
        val ruler3 = "\n08-01                                                                        08-02\n" +
                /**/   "|-----REST-----|+++++AVAIL+++++|-----REST-----|+++++AVAIL+++++|-----REST-----|\n" +
                /**/   "00:00          08:00           12:00          14:00           20:00          00:00\n";
        assertThat(dayTimeRuler.createRuler()).isEqualTo(ruler3);

        String json = JSON.toJSONString(dayTimeRuler);
        val timeRuler = JSON.parseObject(json, DayTimeRuler.class);
        assertThat(timeRuler).isEqualTo(dayTimeRuler);

        dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 08:00"), millis("2018-08-01 09:00"), "流瑜伽", "100");
        dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 14:00"), millis("2018-08-01 15:00"), "孕瑜伽", "101");
        try {
            dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 14:00"), millis("2018-08-01 14:30"), "孕瑜伽", "103");
            Assert.fail();
        } catch (DayTimeRuler.BadTimeSpanException ex) {
            assertThat(ex.getMessage()).isEqualTo("[14:00, 14:30) can not added for current timespans");
        }
    }
//
//    @Test
//    public void maxConcurrent() {
//        val dayTimeRuler = new DayTimeRuler(millisDay("2018-08-01"), 2);
//        dayTimeRuler.addWorkingTimeSpan(millis("2018-08-01 08:00"), millis("2018-08-01 20:00"));
//
//        dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 08:00"), millis("2018-08-01 09:00"), "流瑜伽", "100");
//        dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 08:00"), millis("2018-08-01 08:30"), "阴瑜伽", "101");
//        dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 08:00"), millis("2018-08-01 08:30"), "阴瑜伽", "101");
//        dayTimeRuler.addUsedTimeSpan(millis("2018-08-01 14:00"), millis("2018-08-01 15:00"), "孕瑜伽", "102");
//
//        val ruler1 = "\n08-01                                                                                       08-02\n" +
//                /**/   "|-----REST-----|=====USED=====|+++++AVAIL+++++|=====USED=====|+++++AVAIL+++++|-----REST-----|\n" +
//                /**/   "00:00          08:00          09:00           14:00          15:00           20:00          00:00\n" +
//                /**/   "08:00-08:30 流瑜伽(100)\n" +
//                /**/   "08:30-09:00 阴瑜伽(101)\n" +
//                /**/   "08:00-09:00 阳瑜伽(101)\n" +
//                /**/   "14:00-15:00 孕瑜伽(102)\n";
//        assertThat(dayTimeRuler.createRuler()).isEqualTo(ruler1);
//
//    }

    private long millisDay(String day) {
        return DateTime.parse(day).getMillis();
    }

    private long millis(String s) {
        return DateTime.parse(s.replace(' ', 'T') + ":00").getMillis();
    }
}
