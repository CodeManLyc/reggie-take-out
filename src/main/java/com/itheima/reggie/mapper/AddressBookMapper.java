package com.itheima.reggie.mapper;

import com.itheima.reggie.entity.AddressBook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 26541
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2023-04-26 20:38:22
* @Entity com.itheima.reggie.entity.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}




