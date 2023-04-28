package com.itheima.reggie.service;

import com.itheima.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.DishDto;

import java.util.List;

/**
* @author 26541
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2023-04-24 09:59:27
*/
public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto selectByID(Long id);

    void updateDish(DishDto dishDto);

    void delete(List<Long> ids);
}
