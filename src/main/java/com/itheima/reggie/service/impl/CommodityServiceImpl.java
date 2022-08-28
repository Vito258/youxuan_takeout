package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Entity.Commodity;
import com.itheima.reggie.mapper.CommodityMapper;
import com.itheima.reggie.service.CommodityService;
import org.springframework.stereotype.Service;

@Service
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements CommodityService {
}
