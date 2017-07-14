package com.example.iyou.route.model.service;

import android.app.Activity;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public interface RouteService {

    //从本地获取所有初始listview数据源
    public List<Object> getData();
    //搜索自定义路线
    public List<Map<String,Object>> searchRoute(String namekey);
    //跳转到编辑路线
    public void toEditRoutePage(Activity activity, List<Object> listdata, int position);
    //跳转到分享路线界面
    public void toEidtShareRoutePage(Activity activity, List<Object> listdata, int position);
    //删除路线
    public boolean delRoute();
    //跳转到路线高德地图显示界面
    public void toRouteMapPage(Activity activity,List<Object> listdata,int position);
}
