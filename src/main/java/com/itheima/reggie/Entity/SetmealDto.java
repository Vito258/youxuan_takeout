package com.itheima.reggie.Entity;


import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealCommodity> setmealDishes;

    private String categoryName;
}
