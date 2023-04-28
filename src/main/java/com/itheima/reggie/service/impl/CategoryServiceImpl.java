package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 26541
 * @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
 * @createDate 2023-04-23 19:55:52
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 自定义删除分类的方法，在删除时候需要进行判断，该分类下时候含有菜品或者套餐，如果有将不能删除，抛出异常。
     * @param id
     */

    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = Wrappers.lambdaQuery(Dish.class);
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        Integer dishCount = dishMapper.selectCount(dishLambdaQueryWrapper);
        if (dishCount > 0) {
            throw new CustomerException("该分类下还包含有菜品，无法删除！");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = Wrappers.lambdaQuery(Setmeal.class);
        Integer setMealCount = setmealMapper.selectCount(setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id));
        if (setMealCount > 0) {
            throw new CustomerException("该分类下还包含有套餐，无法删除！");
        }
        categoryMapper.deleteById(id);
    }

}




