package com.itheima.reggie.Entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommodityDto extends Commodity{

    private List<DishFlavor> flavors = new ArrayList<>();

    //分类名称
    private String categoryName;

    private Integer copies;
}
