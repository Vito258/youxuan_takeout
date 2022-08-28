package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.Entity.Setmeal;
import com.itheima.reggie.Entity.SetmealDto;

public interface SetmealService extends IService<Setmeal> {

    //定义一个专门的储存方法，在新增套餐时将套餐和菜品的关系储存到关系表中
    public void saveWithDish(SetmealDto setmealDto);

    //定义一个专门的修改的方法，在修改套餐时将套餐和菜品的关系储存到关系表中
    public void updateWithDish(SetmealDto setmealDto);
}
