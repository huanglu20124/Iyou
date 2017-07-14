package com.example.iyou.my.model.service;

import com.example.iyou.my.activity.MyRouteActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public interface MyRoutePostService {

    //查询本机数据库获取我分享过的路线数据源
    public List<Map<String,Object>> getMyRoutePostData(MyRouteActivity myRouteActivity);
}
