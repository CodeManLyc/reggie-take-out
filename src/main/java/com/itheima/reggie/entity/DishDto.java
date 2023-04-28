package com.itheima.reggie.entity;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于接收添加菜品时候前端所发送回来的数据
 * 由于里面含有一项是flavors是Dish实体类里面所没有的，所以新建一个类来继承自Dish的同时又新添加三项属相
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
