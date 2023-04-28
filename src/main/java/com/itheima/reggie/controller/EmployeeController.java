package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工请求响应
 *
 * @Author Lyc
 * @Date 2023/4/21 17:52
 * @Description: 员工请求响应
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //将页面提交过来的密码password进行md5加密处理
        String password = employee.getPassword();
        //这里使用getBytes()将其转换为字节数组，这是因为这里使用的加密方法，需要传入的就是一个字节数组
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的username去数据库里面查找
        LambdaQueryWrapper<Employee> queryWrapper = Wrappers.lambdaQuery(Employee.class);
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        //getOne
        Employee emp = employeeService.getOne(queryWrapper);

        //如果没有查找到的话那么就登录失败
        if (emp == null){
            return R.error("登陆失败");
        }

        //如果找到了那么就进行比对密码
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //如果密码比对也成功 那么就进行员工的账号状态进行比对判断 0代表禁用 1代表可用
        if (emp.getStatus() == 0){
            return R.error("账号已经禁用");
        }
        // 如果以上全部通过  那么就代表登录成功 然后将结果存入到Session并且返回登录成功的结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logOut(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("表单提交的员工信息：{}",employee.toString());
        //设置初始密码123456 并进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录的用户id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工列表的分页查询+根据名字模糊查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> select( int page, int pageSize,String name){
        log.info("page = {},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = Wrappers.lambdaQuery(Employee.class);
        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name)
                .orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改员工账号的状态信息  但是由于MP的存在该类已经成为了一个通用的修改类 只要是和员工相关的修改操作
     * 都是直接可以自动走这个方法 然后根据传入进来的Employee的相关参数进行修改
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("id:{},status:{}",employee.getId(),employee.getStatus());
        //employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录人存放在session作用域中的id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(empId);
        employee.setStatus(employee.getStatus());
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

   @GetMapping("/{id}")
    public R <Employee> find(@PathVariable Long id){
        log.info("接收到的id：{}",id);
       Employee emp = employeeService.getById(id);
       return R.success(emp);
    }
}
