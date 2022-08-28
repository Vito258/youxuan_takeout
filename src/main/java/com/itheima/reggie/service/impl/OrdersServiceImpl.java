package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Entity.*;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public void submit(Orders orders) {
        //获取用户的Id
        Long currentId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        //做一个判断，防止空的数据进来
        if(list == null || list.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(currentId);

        //查询地址数据
        AddressBook address = addressBookService.getById(orders.getAddressBookId());
        //做一个判断，防止空的数据进来
        if(address == null){
            throw new CustomException("地址为空，不能下单");
        }

        //用MybatisPlus 的工具类生成一个订单号
        long orderId = IdWorker.getId();

        //创建一个AtomicInteger 类,设置默认值为0，用于计算订单金额
        AtomicInteger amount = new AtomicInteger(0);

        //将金额总数计算出来
        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        //向订单表插入数据
        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orders.setUserId(user.getId());
        orders.setConsignee(address.getConsignee());
        orders.setPhone(user.getPhone());
        orders.setAddress(address.getProvinceName() == null ?"" : address.getProvinceName()+
        address.getCityName() == null ?"":address.getCityName()+
        address.getDistrictName() == null ?"": address.getDistrictName() +
        address.getDetail() == null ?"" : address.getDetail()
        );

       //插入一条数据
        this.save(orders);
       //在订单明细表中插入多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
