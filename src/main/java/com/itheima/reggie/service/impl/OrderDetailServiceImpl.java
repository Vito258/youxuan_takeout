package com.itheima.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Entity.OrderDetail;
import com.itheima.reggie.mapper.OrderDetailsMapper;
import com.itheima.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailsMapper, OrderDetail> implements OrderDetailService {
}
