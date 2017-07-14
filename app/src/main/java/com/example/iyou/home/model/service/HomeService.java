package com.example.iyou.home.model.service;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.example.iyou.home.activity.ContentHome;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public interface HomeService {

    //添加景点到购物车
    public boolean addCar(Context context,List<Map<String,Object>> data,int position);
    //获取服务器里景点信息，list的数据源
    public List<Map<String,Object>> getData(ContentHome contentHome);
    //跳转到定位页面的activity
    public void toLocationPage(Activity activity);
    //显示购物车的信息
    public void displayCar();
    //从购物车删除景点
    public boolean delSceneryFromCar();
    //跳转到景点购物车页面的activity
    public void toSceneryCarPage(Activity activity);
    //定制路线
    public void madeRoute();
    //显示详细的景点信息
    public void displayDetail(Activity activity,List<Map<String, Object>> listdata,int position);
    //跳转到搜索页面的activity
    public void toSearchPage(Activity activity);
}
