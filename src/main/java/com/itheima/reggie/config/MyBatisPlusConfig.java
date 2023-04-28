package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MP分页插件的配置
 *
 * @Author Lyc
 * @Date 2023/4/21 23:58
 * @Description: MP分页插件的配置
 */
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    //    添加一个分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return  interceptor;
    }
}
