package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author 26541
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2023-04-21 17:41:52
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}




