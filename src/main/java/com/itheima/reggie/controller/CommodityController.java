package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Entity.Category;
import com.itheima.reggie.Entity.Commodity;
import com.itheima.reggie.Entity.CommodityDto;
import com.itheima.reggie.Entity.Setmeal;
import com.itheima.reggie.common.R;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.CommodityService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 对商品的控制层
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class CommodityController {

    @Autowired
    private CommodityService commodityService;

    @Autowired
    private CategoryService  categoryService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加商品的方法
     * @param commodity
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Commodity commodity){
     // log.info(commodity.toString());

     //把商品的分类Id 和状态拼成字符串作为Redis 的Id
     String key = "commodity_"+commodity.getCategoryId()+"_"+commodity.getStatus();



     commodityService.save(commodity);

     //删除Redis 中对应的分类信息，防止在查询数据时数据不变

     redisTemplate.delete(key);
     return R.success("添加商品成功");
    }

    /**
     * 分页查询的方法
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectByPage(int page, int pageSize, String name ){



        // 创建一个分页构造器
        Page<Commodity> pageInfo = new Page(page,pageSize);
        //创建第二个泛型的分页构造器
        Page<CommodityDto> pageInfo1 = new Page<>();

        //创建一个条件构造器
        LambdaQueryWrapper<Commodity> lambdaQueryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        lambdaQueryWrapper.like(name != null, Commodity::getName,name);

        //设置排序条件，按照sort排序 升序
        lambdaQueryWrapper.orderByAsc(Commodity::getSort);

        //调用分页查询的方法
        commodityService.page(pageInfo, lambdaQueryWrapper);

        //调用工具类进行对象拷贝
        BeanUtils.copyProperties(pageInfo,pageInfo1,"records");
        List<Commodity> records = pageInfo.getRecords();

        //遍历records 中的每一个元素，并对它们进行处理，然后收集成为新的集合
        List<CommodityDto> list = records.stream().map((item) ->{
                    //创建Commodity 的子类对象CommodityDto
                    CommodityDto commodityDto = new CommodityDto();
                    //将每个Commodity 的属性拷贝
                    BeanUtils.copyProperties(item,commodityDto);

                    //根据商品的分类Id获得分类的名称从而发送给页面显示
                    Long categoryId = item.getCategoryId(); //分类Id
                    Category c = categoryService.getById(categoryId);
                    String categoryName = c.getName();

                    //在每个子类中设置查询到的属性categoryName
                    commodityDto.setCategoryName(categoryName);
                    return commodityDto;
                }
                ).collect(Collectors.toList());

        //将收集到的集合数据作为Records 参数赋值给pageInfo1
        pageInfo1.setRecords(list);

        //返回
        return R.success(pageInfo1);
    }

    /**
     * 修改商品的方法
     * @param commodity
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody CommodityDto commodity){
        //把商品的分类Id 和状态拼成字符串作为Redis 的Id
        String key = "commodity_"+commodity.getCategoryId()+"_"+commodity.getStatus();

        commodityService.updateById(commodity);

        redisTemplate.delete(key);

        return R.success("修改成功");
    }

    /**
     * 根据Id 查询单个信息的方法，用于数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<CommodityDto> selectById(@PathVariable Long id){

        Commodity commodity = commodityService.getById(id);

        //创建新的CommodityDto 类
        CommodityDto commodityDto = new CommodityDto();

        //拷贝数据
        BeanUtils.copyProperties(commodity,commodityDto);

        if(commodityDto != null ){
            return R.success(commodityDto);
        }else{
            return R.error("未查询到相关信息");
        }

    }

    /**
     * 删除商品的方法
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        //创建构造器

        //
//        Commodity commodity = commodityService.getById(id);
//        queryWrapper.eq(commodity != null,Commodity::getId,id);
//        commodityService.remove(queryWrapper);

        //遍历并逐个删除
        for(Long id : ids){
            //根据Id 查询商品
            Commodity commodity = commodityService.getById(id);
            //把商品的分类Id 和状态拼成字符串作为Redis 的Id
            String key = "commodity_"+commodity.getCategoryId()+"_"+commodity.getStatus();
            //删除商品
            commodityService.removeById(id);
            //删除Redis 中商品（key）对应的value 数据
            redisTemplate.delete(key);
        }
        return R.success("删除成功");
    }

    /**
     * 设置销售状态的方法
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable int status,Long[] ids){
        //根据传递过来的Id查询Commodity 对象
        for(Long id :ids) {
            LambdaQueryWrapper<Commodity> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(Commodity::getId, id);

            Commodity commodity = commodityService.getOne(queryWrapper);

            //给 这个对象设置新的状态

            commodity.setStatus(status);

            commodityService.updateById(commodity);


        }
        return R.success("修改状态成功");
    }

    /**
     * 根据条件查询分类数据
     * @param commodity
     * @return
     */
    @GetMapping("/list")
    public R<List<Commodity>> list(Commodity commodity){
        List<Commodity> list = null;
        //把商品的分类Id 和状态拼成字符串作为Redis 的Id
        String key = "commodity_"+commodity.getCategoryId()+"_"+commodity.getStatus();

        //先从redis 中获取缓存数据
        list =(List<Commodity>) redisTemplate.opsForValue().get(key);

        //如果redis 中有缓存数据，那么直接调用，无需查询
        if(list != null){
            return R.success(list);
        }
        //如果redis中没有缓存数据，调用数据库查询并将数据储存到 redis

       //获得
        Long categoryId = commodity.getCategoryId();

        //条件构造器
        LambdaQueryWrapper<Commodity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null,Commodity::getCategoryId,categoryId);

        //添加过滤条件，只查询在售的商品
        queryWrapper.eq(Commodity::getStatus,1);

        //排序根据sort 排序，如果sort相同则根据更新时间排序
        queryWrapper.orderByAsc(Commodity::getSort).orderByAsc(Commodity::getUpdateTime);

         list = commodityService.list(queryWrapper);

         //将数据储存到redis,设置缓存存在的时间为60 分钟
        redisTemplate.opsForValue().set(key,list,60, TimeUnit.MINUTES);

        return R.success(list);
    }
}
