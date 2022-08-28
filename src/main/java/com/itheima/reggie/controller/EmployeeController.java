package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.Entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService ;

    //创建一个登陆方法
    @PostMapping("/login")
    public R<Employee> login (HttpServletRequest request, @RequestBody Employee employee){

       //1.将页面提交过来的密码进行md5 加密处理
        String password = employee.getPassword();
        password =  DigestUtils.md5DigestAsHex(password.getBytes());
       //2.根据页面提供的用户名查询数据库
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        lqw.eq(Employee::getUsername,username);
        Employee one = employeeService.getOne(lqw);

        //3.密码比对如果登陆失败返回结果
        boolean flag = password.equals(one.getPassword());

        if(one != null && flag){
            //4. 查看用户状态，如果状态禁用，返回状态禁用结果
            if(one.getStatus() == 1){
                //登陆成功，将员工Id 存入Session 并返回登录成功结果
               Long id = one.getId();
               //5.登陆成功将员工id存入session 并返回登陆成功结果
               request.getSession().setAttribute("employee",id);

               return R.success(one);

            }else{
                //返回状态禁用结果
                return R.error("状态禁用，请重试");
            }

        }else{
            //5.用户不存在或密码错误，返回登录失败结果
                return R.error("用户不存在或密码错误，登录失败");
        }
    }

    //管理员退出的操作
      @PostMapping("logout")
    public R<String> logout(HttpServletRequest request){
        //1.清理session 对象中的Id
          request.getSession().removeAttribute("employee");
        //2.返回结果
          return R.success("退出成功");
      }

    //添加员工的方法
      @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("添加员工的信息：{}",employee.toString());

        //设置初始密码123456
          employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));


        //获取当前系统时间
           // employee.setCreateTime(LocalDateTime.now());
        //设置更新时间和上一个时间相同
           // employee.setUpdateTime(LocalDateTime.now());

          //获得当前用户的ID
           // Long empId  =(Long) request.getSession().getAttribute("employee");
         //设置当前的更新的人是谁
           // employee.setUpdateUser(empId);
           // employee.setCreateUser(empId);

         //调用Service 中的save方法
          employeeService.save(employee);

          //返回执行成功的结果
           return R.success("添加成功");

      }

    //创建分页查询员工信息的方法
    @GetMapping("/page")
    public R<Page> selectByPage(int page,int pageSize,String name){
        // 创建一个分页构造器
        Page pageInfo = new Page(page,pageSize);

        //创建一个条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件，按照员工的更新时间排序,默认按字符串首字母排序
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询,将所查的的讯息例如totols records 自动封装到pageInfo 中
        employeeService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    //根据用户Id查询单个员工的信息
    @GetMapping("/{id}")
    public  R<Employee> selectById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
       if(employee != null){
           return R.success(employee);
       }
        return R.error("未能查询到相关信息");
    }


    //根据Id来修改信息，设置状态
    @PutMapping
    public R<String>  update(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        //此时传过来的status 已经是和原来的不同的了
        //设置修改时间
        //employee.setUpdateTime(LocalDateTime.now());

        //设置当前的修改用户,通过session 获取
        //Long id = (Long) httpServletRequest.getSession().getAttribute("employee");
        //employee.setUpdateUser(id);

        //直接调用修改方法
         employeeService.updateById(employee);

        //修改之后刷新页面
         return R.success("修改状态成功");
    }
 }
