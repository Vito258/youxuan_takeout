package com.itheima;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.Entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReggieTakeOutApplicationTests {
   @Autowired
  private EmployeeService esi;

    @Test
    void contextLoads() {

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getId,1);

        Employee one = esi.getOne(lqw);
        System.out.println(one);
    }

}
