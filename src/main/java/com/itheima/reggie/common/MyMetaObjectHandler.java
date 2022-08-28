package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据处理器
 * 通过ThreadLocal 引入Session对象
 */

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    //插入时填充数据
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());

//        Long id = Thread.currentThread().getId();
//        log.info("当前线程的Id "+id);

        //在元数据处理器中无法获取session 对象，通过LocalThread 工具类获取当前用户的Id
        Long id = BaseContext.getCurrentId();
        metaObject.setValue("createUser",id);
        metaObject.setValue("updateUser",id);
    }

    //更新时填充数据
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime",LocalDateTime.now());

        Long id = BaseContext.getCurrentId();
        metaObject.setValue("updateUser",id);
    }
}
