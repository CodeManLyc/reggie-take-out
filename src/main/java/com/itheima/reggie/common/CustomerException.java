package com.itheima.reggie.common;

/**
 * 自定义业务异常类
 *
 * @Author Lyc
 * @Date 2023/4/24 10:15
 * @Description: 自定义业务异常类
 */
public class CustomerException extends RuntimeException{
    public CustomerException() {
    }

    public CustomerException(String message) {
        super(message);
    }
}
