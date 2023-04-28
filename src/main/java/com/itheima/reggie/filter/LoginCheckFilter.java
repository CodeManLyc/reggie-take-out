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
 * 检查用户是否已经完成了登录
 *
 * @Author Lyc
 * @Date 2023/4/21 19:17
 * @Description: 检查用户是否已经完成了登录
 */
@Slf4j
@WebFilter( filterName= "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器 支持通配符
    public static final AntPathMatcher  PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        //获取本次请求的URI
        String requestURI = req.getRequestURI();
        log.info("拦截到的请求是:{}",requestURI);

        //将不需要的处理的请求 放入一个数组 匹配到这些数组的请求 那么直接放行
        /*
        "/employee/login","/employee/logout","/employee/**","/front/**"
         */
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };
        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //如果不需要处理  即当check等于true的时候不需要处理
        if (check){
            log.info("本次请求不需要进行拦截，直接放行:{}",requestURI);
            chain.doFilter(req,resp);
            return;
        }
        //判断登录状态 如果已经登录那么直接放行
        if(req.getSession().getAttribute("employee") !=null){
            log.info("用户已经登录，id是：{}",req.getSession().getAttribute("employee"));
            //当执行到这里的时候就说明已经登陆成功，那么就可以（有必要）来获取到此时登陆人的id了，为公共字段的填充做准备
            Long empId = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            chain.doFilter(req,resp);
            return;
        }
        log.info("用户未登录");
        //如果没有登录 即session中为null的时候 那么就是返回未登录结果
        //通过输出流的方式向客户端响应数据
        resp.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配  判断请求路径是否需要进行拦截
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
