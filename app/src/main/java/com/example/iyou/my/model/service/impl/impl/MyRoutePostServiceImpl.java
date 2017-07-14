package com.example.iyou.my.model.service.impl.impl;

import com.avos.avoscloud.AVUser;
import com.example.iyou.my.activity.MyRouteActivity;
import com.example.iyou.my.model.bean.GetdataParamters;
import com.example.iyou.my.model.service.MyRoutePostService;
import com.example.iyou.tool.MyRoutePostServiceTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public class MyRoutePostServiceImpl implements MyRoutePostService {
    @Override
    public List<Map<String, Object>> getMyRoutePostData(MyRouteActivity myRouteActivity) {

        GetdataParamters data = new GetdataParamters();
        data.setAccount(AVUser.getCurrentUser().getUsername());

        MyRoutePostServiceTool tool = new MyRoutePostServiceTool(myRouteActivity,data);
        tool.getMyRoutePostData();


        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        return list;
    }
}
