package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.SetmealDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐
 *
 * @Author Lyc
 * @Date 2023/4/27 15:57
 * @Description: 套餐
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 菜品分页展示+ 模糊查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @RequestMapping("/page")
    public R<Page> select(Integer page,Integer pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = Wrappers.lambdaQuery(Setmeal.class);
        setmealLambdaQueryWrapper.like(StringUtils.isNotBlank(name),Setmeal::getName,name);
        setmealService.page(setmealPage,setmealLambdaQueryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = records.stream().map(record -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record,setmealDto);
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoList);
        return R.success(setmealDtoPage);
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto:{}",setmealDto);
        setmealService.saveSetMeal(setmealDto);
        return R.success("添加成功");
    }

    /**
     * 修改功能回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> selectById(@PathVariable Long id){
       SetmealDto setmealDto =  setmealService.selectById(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto:{}",setmealDto);
        setmealService.updateWithSetMeal(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.delete(ids);
        return R.success("删除成功");
    }

    /**
     * 修改套餐状态
     * @param ids
     * @param s
     * @return
     */
    @PostMapping("/status/{s}")
    public R<String> updateStatus(@RequestParam List<Long> ids, @PathVariable Integer s){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = Wrappers.lambdaQuery(Setmeal.class);
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);
        List<Setmeal> setmeals = setmealList.stream().map(
                setmeal -> {
                    setmeal.setStatus(s);
                    return setmeal;
                }
        ).collect(Collectors.toList());
        setmealService.updateBatchById(setmeals);
        return R.success("状态修改成功");
    }
}
