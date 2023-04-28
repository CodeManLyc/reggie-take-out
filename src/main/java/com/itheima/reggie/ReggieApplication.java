package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 启动类
 *
 * @Author Lyc
 * @Date 2023/4/21 16:33
 * @Description: 启动类
 */
@Slf4j
@ServletComponentScan //开启对servlet的组件的扫描 使过滤器生效
@SpringBootApplication
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功了");
    }
}
