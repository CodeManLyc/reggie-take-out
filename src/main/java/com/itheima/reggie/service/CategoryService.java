package com.itheima.reggie.service;

import com.itheima.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 26541
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2023-04-23 19:55:52
*/
public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
