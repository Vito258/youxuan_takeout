package com.itheima.reggie.Entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 商品
 */
@Data
public class Commodity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //商品名称
    private String name;


    //商品分类id
    private Long categoryId;


    //商品价格
    private BigDecimal price;


    //商品码，与套餐区分
    private  String code ;


    //图片
    private String image;


    //描述信息
    private String description;


    //0 停售 1 起售
    private Integer status;


    //顺序
    private Integer sort;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


    //是否删除
    //private Integer isDeleted;


    public void setCode(String code) {
        this.code = "商品";
    }
}
