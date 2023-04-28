package com.itheima.reggie.mapper;

import com.itheima.reggie.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 26541
* @description 针对表【employee(员工信息)】的数据库操作Mapper
* @createDate 2023-04-21 17:41:52
* @Entity com.itheima.reggie.entity.Employee
*/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}




