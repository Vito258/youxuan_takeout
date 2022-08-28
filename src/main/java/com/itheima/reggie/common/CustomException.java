package com.itheima.reggie.common;

//定义一个业务异常
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }

}
