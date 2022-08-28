package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.Entity.Orders;

public interface OrdersService extends IService<Orders> {
   //定义一个提交的方法
   public void submit(Orders orders);
}
