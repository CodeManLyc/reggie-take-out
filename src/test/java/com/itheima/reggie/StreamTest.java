package com.itheima.reggie;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stream流的测试
 *
 * @Author Lyc
 * @Date 2023/4/24 18:06
 * @Description: Stream流的测试
 */
public class StreamTest {
    @Test
    public void streamTest01() {
        List<Test01> testList = new ArrayList<>();
        testList.add(new Test01("张三", 23, new BigDecimal("3000"), new BigDecimal("1.1")));
        testList.add(new Test01("李四", 24, new BigDecimal("2800"), new BigDecimal("1.2")));
        testList.add(new Test01("王五", 22, new BigDecimal("3200"), new BigDecimal("1.3")));
        //根据姓名转map,map的key为name
        Map<String, Test01> collect = testList.stream().collect(Collectors.toMap(Test01::getName, Test01 -> Test01));
        System.out.println(collect);

    }
}

