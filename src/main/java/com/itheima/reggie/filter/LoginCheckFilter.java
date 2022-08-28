package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未登录时就能访问管理页面不符合逻辑，因此设置过滤器解决这个问题
 * 过滤器的具体请求逻辑如下:
 * 获取本次请求的uri
 * 判断本次请求是否需要处理
 * 如果不需要处理直接放行
 * 判断登录状态，如果已登录则直接放行
 * 如果未登录则返回登录失败的结果
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //创建路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 向下转型
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        //1.获取本次请求的uri
        String requestURI = httpServletRequest.getRequestURI();
        log.info("本次拦截到的请求：{}",requestURI);
        //2.定义要放行通过的url
        String[] urls =new String[] {
           "/employee/login",
           "/employee/logout",
           "/backend/**",
           "/front/**",
           "/user/sendMsg",
           "/user/login"
        };
        //3.判断本次请求是否需要处理，就是查看是否在这个String 数组内
        boolean check = check(urls, requestURI);
        //4.如果不需要处理直接放行
        if(check){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            log.info("本次不需要处理的请求：{}",requestURI);
            return;      //后面的方法无需执行，直接结束
        }
        //5.如果已登录，直接放行
        if (httpServletRequest.getSession().getAttribute("employee") != null) {
            //session携带有信息，说明已经登录

            //调用LocalThread 工具类，设置当前用户的Id
            //1.首先获取当前请求中用户的Id
            Long id = (long) httpServletRequest.getSession().getAttribute("employee");
            //2.调用BaseContext 中的set 方法设置当前用户的Id
            BaseContext.setCurrentId(id);

            filterChain.doFilter(httpServletRequest,httpServletResponse);
            log.info("本次登录成功的请求, 用户Id为{}",httpServletRequest.getSession().getAttribute("employee"));
            return;
        }
        // 4-2、判断 移动端(消费者端)登录状态(session中含有employee的登录信息)，如果已经登录，则直接放行
        Long userId = (Long) httpServletRequest.getSession().getAttribute("user");
        if (userId != null) {
            log.info("用户已经登录，用户id为:{}", userId);
            // 自定义元数据对象处理器 MyMetaObjectHandler中需要使用 登录用户id
            //   通过ThreadLocal set和get用户id
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        log.info("用户未登录");
        //如果未登录返回未登录结果,通过输出流的方式向客户端响应页面
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }

    //做一个判断方法
    public boolean check(String[] urls, String requestURL){
        for(String url :urls){
          boolean match = PATH_MATCHER.match(url,requestURL); //判断是否在通行范围内
            if(match){
                //通行
                return true;
            }
        }
        return false;
    }
}
