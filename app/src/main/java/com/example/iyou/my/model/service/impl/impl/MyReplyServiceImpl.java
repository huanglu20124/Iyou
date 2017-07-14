package com.example.iyou.my.model.service.impl.impl;

import com.avos.avoscloud.AVUser;
import com.example.iyou.my.activity.MyReplyActivity;
import com.example.iyou.my.model.bean.GetdataParamters;
import com.example.iyou.my.model.service.MyReplyService;
import com.example.iyou.tool.MyReplyServiceTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public class MyReplyServiceImpl implements MyReplyService {
    @Override
    public List<Map<String, Object>> getMyReplyData(MyReplyActivity myReplyActivity) {

        GetdataParamters data = new GetdataParamters();
        data.setAccount(AVUser.getCurrentUser().getUsername());

        MyReplyServiceTool tool = new MyReplyServiceTool(myReplyActivity, data);
        tool.getMyReplyData();



        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }

    @Override
    public void toResoursePage() {

    }
}
