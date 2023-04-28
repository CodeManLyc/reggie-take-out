package com.itheima.reggie;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 测试
 *
 * @Author Lyc
 * @Date 2023/4/24 18:10
 * @Description: 测试
 */
@Data
public class Test01 {
    private String name;
    private int age;
    private BigDecimal sal;
    private BigDecimal hei;

    public Test01(String name, int age, BigDecimal sal, BigDecimal hei) {
        this.name = name;
        this.age = age;
        this.sal = sal;
        this.hei = hei;
    }
}
