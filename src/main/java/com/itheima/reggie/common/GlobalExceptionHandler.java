package com.itheima.reggie.common;

//创建一个异常处理器进行全局异常捕获

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;


@ControllerAdvice(annotations = {RestController.class, Controller.class})
//因为最终要返回JSON数据
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //设置全局抛出某种异常类时进行处理
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        //输出以下异常
        log.error(exception.getMessage());
        //有关数据库的全局异常不一定是注册时username 的重复，所以需要对错误信息进行判断，以用于给出具体的提示
        String message = exception.getMessage();
        //

        if(message.contains("Duplicate entry")){
            //说明是关键字重复，说明原因
            //以空格分割错误信息 Duplicate entry 'zhangsan' for key 'idx_username'
            String[] split = message.split(" ");
            //从字符数组中取出重复的关键字
            String msg = split[2];
             return R.error("账号"+msg+"已存在，请重试...");
        }

        return R.error("失败了");
    }

    //设置对抛出业务异常的捕获和处理
    @ExceptionHandler(CustomException.class)
    public R<String> customExceptionHandler(CustomException exception){
        String message = exception.getMessage();

        return R.error(message);
    }


}
