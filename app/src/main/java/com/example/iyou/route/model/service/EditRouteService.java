package com.example.iyou.route.model.service;

/**
 * Created by asus on 2017/1/4.
 */
public interface EditRouteService {

    //从自定义景点表中删除景点
    public boolean delSceneryFromRoute();
    //移动购物车的景点游览顺序
    public boolean resetOrder();
    //设置保存的自定义路线名
    public boolean setRouteName();
}
