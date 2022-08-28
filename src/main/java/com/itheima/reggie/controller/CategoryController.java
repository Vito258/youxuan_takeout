package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Entity.Category;
import com.itheima.reggie.Entity.Employee;
import com.itheima.reggie.common.R;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
//对商品分类的控制层
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增商品分类
     * @param category
     * @return
     */
    @PostMapping
     public R<String> save(@RequestBody Category category){
        categoryService.save(category);

        return R.success("保存分类成功");
     }

    /**
     * 分页查询分类
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectByPage(int page,int pageSize){
        // 创建一个分页构造器
        Page pageInfo = new Page(page,pageSize);

        //创建一个条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();

        //设置排序条件，按照sort排序 升序
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        //调用分页查询的方法
        categoryService.page(pageInfo, lambdaQueryWrapper);

        //返回
        return R.success(pageInfo);
    }

    /**
     * 删除分类的功能，要注意的是当分类关联商品时不能被删除
     * @return
     */
    @DeleteMapping
     public R<String> delete(Long ids){
     //调用删除功能
    // categoryService.removeById(ids);
        categoryService.remove(ids);

     return R.success("删除成功");
   }

   //修改分类的功能
   @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);

        return R.success("修改分类成功");
   }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list( Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());

        //排序根据sort 排序，如果sort相同则根据更新时间排序
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
