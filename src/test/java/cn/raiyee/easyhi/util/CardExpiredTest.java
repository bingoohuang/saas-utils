package cn.raiyee.easyhi.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class CardExpiredTest {
    @Test
    public void test() {
        CardExpired y = CardExpired.of(3, "Y");
        assertThat(y.getExpiredTime()).isEqualTo(DateTime.now().plusMonths(3).plusDays(1).withTimeAtStartOfDay());
        assertThat(y.getDesc()).isEqualTo("3个月");
    }

    @Test
    public void testPast() {
        CardExpired y = CardExpired.of(3, "Y", DateTime.parse("2017-08-01"));
        assertThat(y.getExpiredTime()).isEqualTo(DateTime.parse("2017-11-02"));
        assertThat(y.getDesc()).isEqualTo("3个月");
        assertThat(y.getEffective()).isEqualTo(DateTime.parse("2017-08-01"));
    }

    @Test
    public void testFuture() {
        CardExpired y = CardExpired.of(3, "Y", DateTime.parse("2117-08-01"));
        assertThat(y.getExpiredTime()).isEqualTo(DateTime.now().plusMonths(3).plusDays(1).withTimeAtStartOfDay());
        assertThat(y.getDesc()).isEqualTo("3个月");
        assertThat(y.getExpired()).isEqualTo(ExpiredUnit.of(3, "Y"));
        assertThat(y.getEffective()).isEqualTo(DateTime.now().withTimeAtStartOfDay());
    }
}
