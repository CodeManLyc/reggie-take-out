package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.SetmealDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import com.itheima.reggie.mapper.SetmealMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 26541
 * @description 针对表【setmeal(套餐)】的数据库操作Service实现
 * @createDate 2023-04-24 09:59:42
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 添加新的套餐
     * @param setmealDto
     */

    @Transactional
    @Override
    public void saveSetMeal(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> setmealDishList = setmealDishes.stream().map(
                setmealDish -> {
                    setmealDish.setSetmealId(setmealId);
                    return setmealDish;
                }
        ).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishList);
    }

    /**
     * 修改套餐功能回显数据
     * @param id
     * @return
     */
    @Override
    public SetmealDto selectById(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        setmealDishLambdaQueryWrapper.eq(setmeal.getId() != null, SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishLambdaQueryWrapper);
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }

    /**
     * 修改套餐的信息
     * @param setmealDto
     */
    @Transactional
    @Override
    public void updateWithSetMeal(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        //删除此套餐下已经选择的菜品
        setmealDishLambdaQueryWrapper.eq(setmealDto.getId() != null, SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //重新添加所提交的菜品信息
        List<SetmealDish> setmealDishList = setmealDishes.stream().map(
                setmealDish -> {
                    setmealDish.setSetmealId(setmealDto.getId());
                    return setmealDish;
                }
        ).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishList);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void delete(List<Long> ids) {
    //先查看该套餐id的状态，符合停售状态才能删除，否则抛出一个自定义的业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = Wrappers.lambdaQuery(Setmeal.class);
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        //查看此时套餐状态为起售的个数  如果时大于0，则删除失败，抛出业务异常
        int count = this.count(setmealLambdaQueryWrapper);
        if (count > 0){
            throw new CustomerException("套餐正在售卖，不可以删除");
        }
      //如果if判断成立，那么下面的代码将不会执行
       //删除setmeal表信息
       this.removeByIds(ids);
    //   删除setmeal_dish表下的信息
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}




