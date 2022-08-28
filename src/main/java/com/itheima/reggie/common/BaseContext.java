package com.itheima.reggie.common;

public class BaseContext {


     private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

     //设置当前用户的Id 的方法
     public static void setCurrentId(Long id){
         threadLocal.set(id);
     }

     //获取当前用户Id 的方法
      public static Long getCurrentId(){
          Long id = threadLocal.get();
          return id;
      }
}
