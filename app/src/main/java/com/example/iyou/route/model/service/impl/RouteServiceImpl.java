package com.example.iyou.route.model.service.impl;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.AVUser;
import com.example.iyou.home.activity.SceneryCarActivity;
import com.example.iyou.model.utils.FileUtils;
import com.example.iyou.route.activity.EditShareRouteActivity;
import com.example.iyou.route.model.service.RouteService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public class RouteServiceImpl implements RouteService {

    /*
     * 从本地获取listview的初始数据源
     */
    @Override
    public List<Object> getData() {

//        List<Map<String, Object>> list=null;
//        FileUtils fileUtils=new FileUtils();
//        List<Object> datas=  fileUtils.load(fileUtils.getSDPATH()+"MyRoute.txt");
        List<Object> list = ( List<Object> ) AVUser.getCurrentUser().get("Routes");
        return list;
    }

    @Override
    public List<Map<String, Object>> searchRoute(String namekey) {
        return null;
    }

    @Override
    public void toEditRoutePage(Activity activity, List<Object> listdata, int position) {
        Intent intent=new Intent();
        intent.setClass(activity, SceneryCarActivity.class);
        intent.putExtra("bean", (Serializable) listdata.get(position));
        intent.putExtra("position",position);
        activity.startActivity(intent);
    }

    @Override
    public void toEidtShareRoutePage(Activity activity, List<Object> listdata, int position) {
        Intent intent=new Intent();
        intent.setClass(activity,EditShareRouteActivity.class);
        intent.putExtra("bean", (Serializable) listdata.get(position));
        activity.startActivity(intent);
    }


    @Override
    public boolean delRoute() {
        return false;
    }

    @Override
    public void toRouteMapPage(Activity activity, List<Object> listdata, int position) {
//        Intent intent=new Intent();
//        intent.setClass(activity,RouteMapActivity.class);
//        intent.putExtra("bean", (Serializable) listdata.get(position));
//        activity.startActivity(intent);
    }


}
