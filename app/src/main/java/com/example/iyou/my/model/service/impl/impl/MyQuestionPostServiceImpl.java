package com.example.iyou.my.model.service.impl.impl;

import com.avos.avoscloud.AVUser;
import com.example.iyou.my.activity.MyQuestionActivity;
import com.example.iyou.my.model.bean.GetdataParamters;
import com.example.iyou.my.model.service.MyQuestionPostService;
import com.example.iyou.tool.MyQuestionPostServiceTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public class MyQuestionPostServiceImpl implements MyQuestionPostService {
    @Override
    public List<Map<String, Object>> getMyQuestionPostData(MyQuestionActivity myQuestionActivity) {

        GetdataParamters data = new GetdataParamters();
        data.setAccount(AVUser.getCurrentUser().getUsername());

        MyQuestionPostServiceTool tool = new MyQuestionPostServiceTool(myQuestionActivity,data);
        tool.getMyQuestionPostData();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();


        return list;
    }
}
