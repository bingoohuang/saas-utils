package cn.raiyee.easyhi.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.val;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.truth.Truth.assertThat;

public class TupleTest {
    @Test
    public void test() {
        val t1 = Tuples.of("abc", 1, 2, 3, 4, 5, 6, 7);
        val t2 = Tuples.of("abc", 1, 2, 3, 4, 5, 6, 7);
        assertThat(t1.toString()).isEqualTo("[abc,1,2,3,4,5,6,7]");
        assertThat(t1).isEqualTo(t2);
        assertThat(t1.toList()).isEqualTo(Lists.newArrayList("abc", 1, 2, 3, 4, 5, 6, 7));
        assertThat(t1.size()).isEqualTo(8);
        assertThat(t1.get(0)).isEqualTo("abc");
        for (int i = 1; i <= 7; ++i)
            assertThat(t1.get(i)).isEqualTo(i);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutOfBoundsException() {
        Tuples.of("abc", 1, 2, 3, 4, 5, 6, 7).get(8);
    }

    private void tupleX(int size) {
        List<Integer> list = IntStream.range(0, size).mapToObj(Integer::new).collect(Collectors.toList());
        Integer[] integers = list.toArray(new Integer[0]);
        Tuple2 from = Tuples.from(integers);
        assertThat(from.size()).isEqualTo(size);
        assertThat(from.toList()).isEqualTo(list);
        assertThat(from.toArray()).isEqualTo(integers);
        assertThat(from.iterator().next()).isEqualTo(new Integer(0));
        assertThat(Iterables.size(from)).isEqualTo(size);
    }

    @Test
    public void converge() {
        IntStream.range(2, 9).forEach(x -> tupleX(x));
    }

    @Test
    public void json() {
        val t1 = Tuples.of("abc", 1, 2, 3, 4, 5, 6, 7);
        val json = JSON.toJSONString(t1);
        assertThat(json).isEqualTo("{\"t1\":\"abc\",\"t2\":1,\"t3\":2,\"t4\":3,\"t5\":4,\"t6\":5,\"t7\":6,\"t8\":7}");

        val t2 = JSON.parseObject(json, new TypeReference<Tuple8<String, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>() {
        });
        assertThat(t1).isEqualTo(t2);

        val t3 = JSON.parseObject(json, Tuple8.class);

        assertThat(t1).isEqualTo(t3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentException() {
        Tuples.from(new Object[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyArguments() {
        Tuples.from(IntStream.range(1, 10).mapToObj(Integer::new).collect(Collectors.toList()).toArray(new Integer[0]));
    }
}
