package com.example.iyou.home.model.service.impl;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import com.example.iyou.home.activity.ContentHome;
import com.example.iyou.home.activity.LocationActivity;
import com.example.iyou.home.activity.SceneryActivity;
import com.example.iyou.home.activity.SceneryCarActivity;
import com.example.iyou.home.activity.SearchActivity;
import com.example.iyou.home.model.bean.Paramters.TRGetDataParamters;
import com.example.iyou.home.model.service.HomeService;
import com.example.iyou.tool.HomeServiceTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.iyou.Constants.CURRENT_CITY;

/**
 * Created by asus on 2017/1/4.
 */
public class HomeServiceImpl implements HomeService {

    /*
     * 添加景点到购物车处理
     */
    @Override
    public boolean addCar(Context context,List<Map<String,Object>> data,int position) {

        Intent intent=new Intent();
        intent.putExtra("bean", (Serializable) data.get(position));
        intent.setAction(Intent.ACTION_EDIT);
        context.sendBroadcast(intent);
        return false;
    }

    /*
     *从服务器获取景点信息作为listview数据源
     */
    @Override
    public List<Map<String, Object>> getData(ContentHome contentHome) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        TRGetDataParamters data = new TRGetDataParamters();

        data.setKeyWordType("city");
        data.setKeyWord(CURRENT_CITY);

        HomeServiceTool tool = new HomeServiceTool(contentHome,data);
        tool.getData();;


        return list;
    }

    /*
     * 跳转到定位界面的activity
     */
    @Override
    public void toLocationPage(Activity activity) {
        Intent intent=new Intent();
        intent.setClass(activity,LocationActivity.class);
        activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }


    @Override
    public void displayCar() {

    }

    @Override
    public boolean delSceneryFromCar() {
        return false;
    }

    /*
     * 跳转到景点车界面的activity
     */
    @Override
    public void toSceneryCarPage(Activity activity) {
        Intent intent=new Intent();
        intent.setClass(activity,SceneryCarActivity.class);
        activity.startActivity(intent);
    }


    @Override
    public void madeRoute() {

    }

    /*
     * 点击listview的item栏跳转到景点详细介绍界面
     */
    @Override
    public void displayDetail(Activity activity,List<Map<String, Object>> listdata,int position) {
        Intent intent=new Intent();
        intent.setClass(activity,SceneryActivity.class);
        intent.putExtra("bean", (Serializable) listdata.get(position));
        activity.startActivity(intent);
    }

    /*
     * 跳转到搜索界面的activity
     */
    @Override
    public void toSearchPage(Activity activity) {
        Intent intent=new Intent();
        intent.setClass(activity,SearchActivity.class);
        activity.startActivity(intent);
    }
}
