package com.example.iyou.my.model.service;

import com.example.iyou.my.activity.MyQuestionActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public interface MyQuestionPostService {

    //查询本机数据库获取我发布过的问题数据源
    public List<Map<String,Object>> getMyQuestionPostData(MyQuestionActivity myQuestionActivity);
}
