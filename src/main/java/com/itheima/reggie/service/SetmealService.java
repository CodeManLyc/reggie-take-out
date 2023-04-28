package com.itheima.reggie.service;

import com.itheima.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.SetmealDto;

import java.util.List;

/**
* @author 26541
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2023-04-24 09:59:42
*/
public interface SetmealService extends IService<Setmeal> {

    void saveSetMeal(SetmealDto setmealDto);

    SetmealDto selectById(Long id);

    void updateWithSetMeal(SetmealDto setmealDto);

    void delete(List<Long> ids);
}
