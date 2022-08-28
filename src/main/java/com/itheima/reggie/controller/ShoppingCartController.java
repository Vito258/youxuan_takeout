package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Entity.ShoppingCart;
import com.itheima.reggie.Entity.User;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车操作的方法
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 展示购物清单的方法
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        Long currentId = BaseContext.getCurrentId();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        //按照创建时间排序
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);


        return  R.success(list);
    }

    /**
     * 添加购物车的方法
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //设置用户Id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //判断以下原有的购物车中是否已经有了所选的商品，如果有了那么就在数据的number属性+1
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //传递过来的数据可能是商品也可能是套餐，所以需要加上两个判断
        queryWrapper.eq(shoppingCart.getDishId() != null ,ShoppingCart::getDishId,dishId);
        queryWrapper.eq(shoppingCart.getSetmealId() != null ,ShoppingCart::getSetmealId,setmealId);
        //输入用户Id 的查询条件
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        //查询数据
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if(one == null){
            //说明购物车里面并没有添加这个商品/套餐,所以需要添加

            //将这条数据的number 设置成1
            shoppingCart.setNumber(1);
            //设置创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }else{
            // 在数据的number属性+1
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
            //把改好的数据One 赋给传递过来的原数据方便页面返回
            shoppingCart = one;
        }

        return R.success(shoppingCart);

    }

    /**
     * 在购物车删减商品的方法
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub (@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dishId != null,ShoppingCart::getDishId,dishId);
        queryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId,setmealId);

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        Integer number = one.getNumber();

        if(number == 1){
            //说明是最后一条数据，直接把这条删除
            shoppingCartService.remove(queryWrapper);
        }else{
            //说明不是最后一条数据，所以要把number 减一
            one.setNumber(number - 1);
            shoppingCartService.updateById(one);
            shoppingCart = one ;
        }
        return R.success(shoppingCart);
    }

    /**
     * 清空购物车的方法
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //根据用户Id 查询到所有购物车中选择的商品
        Long currentId = BaseContext.getCurrentId();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        shoppingCartService.remove(queryWrapper);

        return R.success("已清空购物车");
    }



}
