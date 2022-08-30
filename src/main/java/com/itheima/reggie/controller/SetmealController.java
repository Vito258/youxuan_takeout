package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Entity.*;
import com.itheima.reggie.common.R;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.CommodityService;
import com.itheima.reggie.service.SetmealCommodityService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealCommodityService setmealCommodityService;

    @Autowired
    private CommodityService commodityService;

    /**
     * 在套餐管理界面的分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
   public R<Page> selectBypage(int page,int pageSize,String name){
        Page pageInfo = new Page(page,pageSize);

        Page pageInfo1 = new Page();

        //创建一个条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        lambdaQueryWrapper.like(name != null,Setmeal::getName,name);

        //设置排序条件，按照更新时间排序 升序
        lambdaQueryWrapper.orderByAsc(Setmeal::getId).orderByAsc(Setmeal::getUpdateTime);

        //调用分页查询的方法
        setmealService.page(pageInfo, lambdaQueryWrapper);

        //数据拷贝
        BeanUtils.copyProperties(pageInfo,pageInfo1);

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            //创建Setmeal 的子类对象SetmealDto
            SetmealDto setmealDto = new SetmealDto();
            //将每个Setmeal 的属性拷贝
            BeanUtils.copyProperties(item, setmealDto);

            //根据商品的分类Id获得套餐的名称从而发送给页面显示
            Long categoryId = item.getCategoryId(); //分类Id
            Category c = categoryService.getById(categoryId);
            String categoryName = c.getName();

            //在每个子类中设置查询到的属性categoryName
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());

        //将收集到的集合数据作为Records 参数赋值给pageInfo1
        pageInfo1.setRecords(list);

        //返回
        return R.success(pageInfo1);
    }

    /**
     *  添加套餐的方法
     * @param setmealDto
     * @return
     */

    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功");

    }

    /**
     * 查询单个套餐具体内容的方法，用于修改数据时的回显内容
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){

        Setmeal setmeal = setmealService.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealCommodity> queryWrapper = new LambdaQueryWrapper<>();

        //获取套餐和商品的关系表
        queryWrapper.eq(SetmealCommodity::getSetmealId,setmeal.getId());

        List<SetmealCommodity> list = setmealCommodityService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);

        return  R.success(setmealDto);

    }

    /**
     * 设置销售状态的方法
     * @return
     */

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> setStatus(@PathVariable int status,Long[] ids){
        //根据传递过来的Id查询Commodity 对象
        for(Long id :ids) {
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(Setmeal::getId, id);

            Setmeal setmeal = setmealService.getOne(queryWrapper);

            //给 这个对象设置新的状态

            setmeal.setStatus(status);

            setmealService.updateById(setmeal);


        }
        return R.success("修改状态成功");
    }

    /**
     * 删除套餐的方法,这里传来的参数是套餐的Id，套餐的Id 与SetmealCommodity 中的属性Setmeal_Id 相关（相等）
     * 这里设置一个需求，只能删除停售的套餐
     * 删除套餐，同时删除套餐和商品的关联数据
     * @param ids
     * @return
     */

   @DeleteMapping
   @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(Long[] ids){
        for(Long id : ids){
//            Setmeal setmeal = setmealService.getById(id);
//            if(setmeal.getStatus() == 0){

            //删除套餐表中的数据
                setmealService.removeById(id);
//            }else{
//             return  R.error("所选套餐还在启售状态，请先停售...");
//            }

            //通过套餐中的setmeal_Id 属性，删除关联表中的数据
            LambdaQueryWrapper<SetmealCommodity> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(SetmealCommodity::getSetmealId,id);

            setmealCommodityService.remove(queryWrapper);
        }
        return R.success("删除套餐成功");
   }

    /**
     * 修改套餐的方法
     * @param setmealDto
     * @return
     */

   @PutMapping
   @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){

       setmealService.updateWithDish(setmealDto);

       return R.success("修改套餐成功");
   }

    /**
     * 对顾客展示的套餐界面，展示在售状态的套餐
     * @param setmeal
     * @return
     */

   @GetMapping("/list")
   @Cacheable(value = "setmealCache",key = "#setmeal.categoryId +'_'+#setmeal.status",unless = "#result == null")
    public R<List<Setmeal>> list(Setmeal setmeal){
       LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.eq(setmeal.getStatus() != 0,Setmeal::getStatus,setmeal.getStatus());

       queryWrapper.orderByAsc(Setmeal::getUpdateTime);

       List<Setmeal> list = setmealService.list(queryWrapper);

       return  R.success(list);
   }

    /**
     * 在客户端详细查看每种分类的商品
     * @param id
     * @return
     */
   @GetMapping("/dish/{id}")
    public R<Commodity> selectByid(@PathVariable Long id){

       Commodity commodity = commodityService.getById(id);

       return R.success(commodity);
   }


}
