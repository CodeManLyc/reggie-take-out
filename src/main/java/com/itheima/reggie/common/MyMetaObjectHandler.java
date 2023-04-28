package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 对公共字段进行填充赋值
 *
 * @Author Lyc
 * @Date 2023/4/23 20:07
 * @Description: 对公共字段进行填充赋值
 *
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
      log.info("对插入时候的公共字段进行填充..");
      metaObject.setValue("createTime", LocalDateTime.now());
      metaObject.setValue("updateTime",LocalDateTime.now());
      /*
       在该类是无法使用request来获取得到保存在session中的此时登录者的信息的，所以这个创建人和更新人的这两个字段需要使用其它的技术手段来解决
       即可以使用jdk提供的ThreadLocal中的set 和 get方法来得到此时登录人的id  set方法使用在过滤器中登陆成功时候  get方法使用在这里，就是将
       当前登陆人的id填入到这里
       */
        log.info("{}",BaseContext.getCurrentID());
      metaObject.setValue("createUser",BaseContext.getCurrentID());
      metaObject.setValue("updateUser",BaseContext.getCurrentID());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("对更新时候的公共字段进行填充..}");
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentID());
    }
}
