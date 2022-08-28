package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.Entity.Category;

public interface CategoryService extends IService<Category> {
    //定义一个删除方法
    public void remove(Long ids);
}
