package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Entity.Category;
import com.itheima.reggie.Entity.Commodity;
import com.itheima.reggie.Entity.Setmeal;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.CommodityService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
   //注入一个商品的Service
    @Autowired
    private CommodityService commodityService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 自定义的删除方法，当分类与菜品关联时删除失败；
     */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Commodity> queryWrapper = new LambdaQueryWrapper<>();

        //添加查询条件,商品类中的参数CategoryId 这个值等于创建分类的Id ，表示这个商品添加到这个分类里面
        queryWrapper.eq(Commodity::getCategoryId,ids);

        //表示这个分类中能查寻到多少商品
        int count = commodityService.count(queryWrapper);

        //判断当前分类是否关联了商品，如果关联，则在调用这个删除方法时抛出异常，然后在全局异常处理器中捕获这个异常
        if(count > 0){
            //说明分类中有关联的商品
            throw new CustomException("此分类关联有商品，不能删除");

        }

        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId,ids);

        int count1 = setmealService.count(queryWrapper1);

        //判断当前分类是否关联了套餐，如果关联，则在调用这个删除方法时抛出异常
        if(count1 > 0){
            //说明已经关联了套餐
            throw new CustomException("此分类关联有套餐，不能删除");
        }
        //正常删除分类
        super.removeById(ids);
    }
}
