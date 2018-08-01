# saas-utils
some utilities for saas

[![Build Status](https://travis-ci.org/bingoohuang/saas-utils.svg?branch=master)](https://travis-ci.org/bingoohuang/saas-utils)
[![Coverage Status](https://coveralls.io/repos/github/bingoohuang/saas-utils/badge.svg?branch=master)](https://coveralls.io/github/bingoohuang/saas-utils?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.bingoohuang/saas-utils/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.bingoohuang/saas-utils/)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Mutable Tuples
1. Tuple2<T1, T2>
1. Tuple3<T1, T2, T3>
1. Tuple4<T1, T2, T3, T4>
1. Tuple5<T1, T2, T3, T4, T5>
1. Tuple6<T1, T2, T3, T4, T5, T6>
1. Tuple7<T1, T2, T3, T4, T5, T6, T7>
1. Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>

## ExpiredUnit
有效期单位及有效期计算

```java
// 年("N", "年"), 月("Y", "个月"),
// 天("T", "天"), 周("Z", "周");
// 单位年
ExpiredUnit n = ExpiredUnit.ofUnit("N");
// 6个月
ExpiredUnit.Expired y = ExpiredUnit.of(6, "Y");
assertThat(y.getUnit()).isEqualTo(ExpiredUnit.月);
assertThat(y.getDesc()).isEqualTo("6个月");
assertThat(y.getOfDesc()).isEqualTo("6月");

// 计算过期时间, 会自动多一天，下面结果是 2019-08-02 00:00:00
DateTime expired = ExpiredUnit.of(1, "Y").createExpired(DateTime.parse("2018-08-01")); 

```

## CardExpired
卡片有效期计算，包含卡片激活处理。
