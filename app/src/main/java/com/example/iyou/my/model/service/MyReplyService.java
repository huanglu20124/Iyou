package com.example.iyou.my.model.service;

import com.example.iyou.my.activity.MyReplyActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public interface MyReplyService {

    //查询本机数据库获取我参与过的回复数据源
    public List<Map<String,Object>> getMyReplyData(MyReplyActivity myReplyActivity);
    //查询源处Y页面
    public void toResoursePage();
}
