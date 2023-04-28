package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 菜品相关请求响应
 *
 * @Author Lyc
 * @Date 2023/4/24 10:51
 * @Description: 菜品相关请求响应
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    /**
     * 菜品页的分页查询+模糊查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page>  select(Integer page,Integer pageSize,String name){
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = Wrappers.lambdaQuery(Dish.class);
        dishLambdaQueryWrapper.like(StringUtils.isNotBlank(name),Dish::getName,name).orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,dishLambdaQueryWrapper);
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> dishDtoList = records.stream().map(
                dishRecord -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(dishRecord, dishDto);
                    Long categoryId = dishRecord.getCategoryId();
                    //这里这一步其实是不需要的，因为CategoryId字段是主键，不可能为空。但是写这里只是为了拓宽自己的代码思路
                    //如果对于其它的可以为空字段的话，那么是需要进行判断的
                    if (categoryId !=null) {
                        String categoryName = categoryService.getById(categoryId).getName();
                        dishDto.setCategoryName(categoryName);
                    }
                    return dishDto;
                }
        ).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品，此时需要操作的是两张表菜品表以及菜品口味表.
     * 并且前端所传输过来的数据是Dish一个类无法全部能接收的，所以使用一个新的类来继承自这个Dish类
     * 的同时扩展自己的属性，以确保能够将前端所传输过来的数据全部接收封装起来
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("提交的要添加的菜品信息：{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    /**
     * 修改菜品之回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> selectByID(@PathVariable Long id){
        log.info("id:{}",id);
        DishDto dishDto = dishService.selectByID(id);
        return  R.success(dishDto);
    }
    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
   @PutMapping
   public R<String> updateDish(@RequestBody DishDto dishDto){
        log.info("dishDto:{}",dishDto);
        dishService.updateDish(dishDto);
        return R.success("修改成功");
   }

    /**
     * 根据分类信息的id，查找出对应id下的所有菜品信息，这里是返回给新增/修改套餐 时所需要的菜品信息
     * @param categoryId
     * @return
     */
   @GetMapping("/list")
    public R<List<Dish>> select(Long categoryId){
       LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = Wrappers.lambdaQuery(Dish.class);
       dishLambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
       List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
       return R.success(dishList);
   }

    /**
     * 修改菜品的状态
     * @param s
     * @param ids
     * @return
     */
   @PostMapping("/status/{s}")
    public R<String> updateStatus(@PathVariable Integer s,@RequestParam List<Long> ids){
       List<Dish> dishList = dishService.listByIds(ids);
       List<Dish> dishes = dishList.stream().map(
               item -> {
                   item.setStatus(s);
                   return item;
               }
       ).collect(Collectors.toList());
       dishService.updateBatchById(dishes);
       return R.success("修改成功");
   }

   @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
       dishService.delete(ids);

       return R.success("修改成功");
   }

}
