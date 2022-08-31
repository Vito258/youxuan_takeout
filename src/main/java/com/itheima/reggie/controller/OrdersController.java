package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Entity.Category;
import com.itheima.reggie.Entity.Orders;
import com.itheima.reggie.common.R;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 对订单的操作
 */
@Slf4j
@RestController
@RequestMapping("order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("/page")
    public R<Page> selectByPage(int page , int pageSize){
        // 创建一个分页构造器
        Page pageInfo = new Page(page,pageSize);

        //创建一个条件构造器
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper();

        //设置排序条件，按照下单时间排序 升序
        lambdaQueryWrapper.orderByAsc(Orders::getOrderTime);

        //调用分页查询的方法
        ordersService.page(pageInfo, lambdaQueryWrapper);

        //返回
        return R.success(pageInfo);
    }

    /**
     *  用户支付的方法
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit (@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("跳转到支付页面");
    }

}
