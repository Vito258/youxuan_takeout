package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Entity.Setmeal;
import com.itheima.reggie.Entity.SetmealCommodity;
import com.itheima.reggie.Entity.SetmealDto;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealCommodityService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealCommodityService setmealCommodityService;
    /**
     *
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
       // 储存套餐的基本信息
        this.save(setmealDto);

       //将商品和套餐的关系储存到关系中
        List<SetmealCommodity> dishes = setmealDto.getSetmealDishes();

        //给 SetmealDto 中的id 属性附上值
        dishes.stream().map((item) -> {

            item.setSetmealId(setmealDto.getId());

            return item;
        }).collect(Collectors.toList());

       //保存这个关联信息
        setmealCommodityService.saveBatch(dishes);
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新Setmeal 表的基本消息
        this.updateById(setmealDto);

        //清理当前套餐对应商品数据---setmeal_commodity表的delete操作
        LambdaQueryWrapper<SetmealCommodity> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealCommodity::getSetmealId,setmealDto.getId());

        setmealCommodityService.remove(queryWrapper);

        //添加当前提交过来的商品数据---setmeal_commodity表的insert操作
        List<SetmealCommodity> dishes = setmealDto.getSetmealDishes();

         dishes.stream().map((item) -> {

            item.setSetmealId(setmealDto.getId());
            return item;

        }).collect(Collectors.toList());

        setmealCommodityService.saveBatch(dishes);
    }

    }