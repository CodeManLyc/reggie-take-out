package com.itheima.reggie.common;

/**
 * 用于封装ThreadLocal的set和get方法，获取当前线程下登录用户的id
 *
 * @Author Lyc
 * @Date 2023/4/23 19:59
 * @Description: 用于封装ThreadLocal的set和get方法
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long empId){
        threadLocal.set(empId);
    }
    public static Long getCurrentID(){
       return threadLocal.get();
    }

}
