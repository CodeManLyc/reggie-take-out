package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品套餐相关请求响应
 *
 * @Author Lyc
 * @Date 2023/4/23 21:00
 * @Description: 菜品套餐相关请求响应
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController{
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> select(int page,int pageSize){
        log.info("接收到的page:{},pageSize:{}",page,pageSize);
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = Wrappers.lambdaQuery(Category.class);
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort)
                        .orderByDesc(Category::getUpdateTime);
        categoryService.page(categoryPage,categoryLambdaQueryWrapper);
        return R.success(categoryPage);
    }

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("接收到的category：{}",category.toString());
        categoryService.save(category);
        return R.success("添加成功");
    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("接收到的category:{}",category.toString());
        categoryService.updateById(category);
        return R.success("修改成功");
    }
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("接收到的id:{}",ids);
        /*
        此时并不能直接使用通用Service给我们提供的remove方法，因为在删除category的时候，要确保在该category下
        不再含有对应的dish、setmeal。所以直接删除是错误的，在此需要我们在Service层自定义一个remove方法，然后再去调用我们自定义的
        remove方法。要确保在删除的时候，如果该分类下仍然含有套餐或者菜品，那么就报出异常，然后响应给前端，前端再将此显示再页面当中。
         */
        //categoryService.removeById(id);
        //调用在service层自定义的remove方法
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 根据条件来查询所有的菜品分类，此时的结果是响应给，新增菜品页面中的菜品分类的下拉框中供用户进行选择的
     * @param type
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>>  select (Integer type){
        log.info("type:",type);
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = Wrappers.lambdaQuery(Category.class);
        //这里查找了全部的字段，使用了select进行只查找指定的字段的时候，前端获取出错了，不知原因。
        categoryLambdaQueryWrapper.eq(Category::getType,type)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        List<Category> categoriesList = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(categoriesList);
    }
}
