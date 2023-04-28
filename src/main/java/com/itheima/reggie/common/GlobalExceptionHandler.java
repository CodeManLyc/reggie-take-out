package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 *
 * @Author Lyc
 * @Date 2023/4/21 23:25
 * @Description: 全局异常处理器
 */
@ControllerAdvice(annotations = {RestController.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 全局中添加时候添加内容重复的异常处理
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 自定义异常CustomerException的处理
     * @param customerException
     * @return
     */
    @ExceptionHandler(CustomerException.class)
    public R<String> customerExceptionHandler(CustomerException customerException){
        log.info("异常信息：{}",customerException.getMessage());
        return R.error(customerException.getMessage());
    }

}
