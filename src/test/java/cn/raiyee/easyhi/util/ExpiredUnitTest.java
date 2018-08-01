package cn.raiyee.easyhi.util;

import lombok.val;
import org.joda.time.DateTime;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ExpiredUnitTest {
    @Test
    public void test() {
        ExpiredUnit n = ExpiredUnit.ofUnit("N");
        assertThat(n.getUnit()).isEqualTo("N");
        assertThat(n.name()).isEqualTo("年");
        assertThat(n.getSuffix()).isEqualTo("年");

        ExpiredUnit y = ExpiredUnit.ofUnit("Y");
        assertThat(y.getUnit()).isEqualTo("Y");
        assertThat(y.name()).isEqualTo("月");
        assertThat(y.getSuffix()).isEqualTo("个月");

        ExpiredUnit t = ExpiredUnit.ofUnit("T");
        assertThat(t.getUnit()).isEqualTo("T");
        assertThat(t.name()).isEqualTo("天");
        assertThat(t.getSuffix()).isEqualTo("天");

        ExpiredUnit z = ExpiredUnit.ofUnit("Z");
        assertThat(z.getUnit()).isEqualTo("Z");
        assertThat(z.name()).isEqualTo("周");
        assertThat(z.getSuffix()).isEqualTo("周");

        ExpiredUnit x = ExpiredUnit.ofUnit("X", ExpiredUnit.年);
        assertThat(x.getUnit()).isEqualTo("N");
    }

    @Test(expected = RuntimeException.class)
    public void exception() {
        assertThat(ExpiredUnit.isValid("X")).isFalse();
        ExpiredUnit.ofUnit("X");
    }

    @Test
    public void value() {
        ExpiredUnit.Expired y = ExpiredUnit.of("6", "Y");
        assertThat(y.getValue()).isEqualTo(6);
        assertThat(y.getUnit()).isEqualTo(ExpiredUnit.月);
        assertThat(y.getDesc()).isEqualTo("6个月");
        assertThat(y.getOfDesc()).isEqualTo("6月");

        ExpiredUnit.Expired z = ExpiredUnit.of(7, "T");
        assertThat(z.getValue()).isEqualTo(1);
        assertThat(z.getUnit()).isEqualTo(ExpiredUnit.周);

        assertThat(z.getDesc()).isEqualTo("1周");
        assertThat(z.getOfDesc()).isEqualTo("周");
    }

    @Test
    public void unlimited() {
        val y = ExpiredUnit.of(-1, "Y");
        assertThat(y.getDesc()).isEqualTo("无限期");
        assertThat(y.getOfDesc()).isEqualTo("无限期");
    }

    @Test
    public void createExpired() {
        assertThat(ExpiredUnit.of(1, "Y").createExpired(DateTime.parse("2018-08-01"))).isEqualTo(DateTime.parse("2018-09-02"));
        assertThat(ExpiredUnit.of(1, "T").createExpired(DateTime.parse("2018-08-01"))).isEqualTo(DateTime.parse("2018-08-03"));
        assertThat(ExpiredUnit.of(1, "N").createExpired(DateTime.parse("2018-08-01"))).isEqualTo(DateTime.parse("2019-08-02"));
        assertThat(ExpiredUnit.of(1, "Z").createExpired(DateTime.parse("2018-08-01"))).isEqualTo(DateTime.parse("2018-08-09"));
        assertThat(ExpiredUnit.of(-1, "X").createExpired(DateTime.parse("2018-08-01"))).isEqualTo(DateTime.parse("2118-08-02"));
    }
}
