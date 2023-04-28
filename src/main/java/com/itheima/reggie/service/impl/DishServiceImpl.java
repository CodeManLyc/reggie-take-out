package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishDto;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 26541
 * @description 针对表【dish(菜品管理)】的数据库操作Service实现
 * @createDate 2023-04-24 09:59:27
 */
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 添加新的菜品
     * @param dishDto
     */
    @Transactional  //操作了多张表，需要开启事务来保证数据的一致性
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //先将里面属于dish的数据添加到dish表中
        this.save(dishDto);
        Dish dish = new Dish();
        dish.getId();
        //然后得到添加的dish的id，因为在对dish_flavor表进行添加的时候需要使用到
        Long dishId = dishDto.getId();
        List<DishFlavor> flavorsList = dishDto.getFlavors();
        log.info("flavorsList: {}", flavorsList);
        flavorsList.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavorsList);
    }

    /**
     * 修改菜品信息时候的回显菜品信息
     * @param id
     * @return
     */
    @Override
    public DishDto selectByID(Long id) {
        //先从菜品表查询，菜品信息。
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //再从菜品口味表查询菜品口味的信息
        LambdaQueryWrapper<DishFlavor> dishDtoLambdaQueryWrapper = Wrappers.lambdaQuery(DishFlavor.class);
        dishDtoLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(dishDtoLambdaQueryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    /**
     * 修改菜品信息之提交
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateDish(DishDto dishDto) {
        //更新菜品表的信息
        this.updateById(dishDto);
        //int i = 1 / 0;
        //更新菜品口味表的信息
        //构造删除口味信息的操作,先将当前菜品下的所有口味数据，然后再去重新加入当前提交的菜品口味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = Wrappers.lambdaQuery(DishFlavor.class);
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        log.info("flavors:{}", dishDto.getFlavors());
        //添加当前提交过来的口味信息  此时的flavors中是不包括dish_id的
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> dishFlavors = flavors.stream().map(
                flavor -> {
                    flavor.setDishId(dishDto.getId());
                    return flavor;
                }
        ).collect(Collectors.toList());
        log.info("flavors:{}", flavors);
        dishFlavorService.saveBatch(dishFlavors);
    }

    /**
     * 删除菜品，要同时删除两张表下的数据
     * @param ids
     */
    @Transactional
    @Override
    public void delete(List<Long> ids) {
        //删除菜品表中的菜品信息,先根据ids查找该id对应的菜品状态是启用的话是不能够删除的
        //  以及setmealdish表中的数据
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = Wrappers.lambdaQuery(Dish.class);
        dishLambdaQueryWrapper.in(Dish::getId, ids).eq(Dish::getStatus, 1);
        int count = this.count(dishLambdaQueryWrapper);
        if (count > 0) {
            throw new CustomerException("所选菜品在起售状态，无法删除");
        }
        //当if成立，便不会走到这一步，如果走到这一步说明已经可以删除了
        //删除dish表
        this.removeByIds(ids);
        //删除setmealdish表中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}




