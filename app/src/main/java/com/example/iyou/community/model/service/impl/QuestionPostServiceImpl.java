package com.example.iyou.community.model.service.impl;

import com.example.iyou.community.activity.QuestionListViewItem;
import com.example.iyou.community.activity.QuestionPostContentActivity;
import com.example.iyou.community.model.service.QuestionPostService;
import com.example.iyou.tool.Image;
import com.example.iyou.tool.QuestionPostServiceImplTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public class QuestionPostServiceImpl implements QuestionPostService {

    /*
     * 从点击问题贴的栏目返回选定的问题贴信息，通过bean传递数据过来
     * 它相应的回答内容就通过根据id访问服务器
     *
     * Map<String,Object> bean   bean是问题贴的内同容
     *
     *
     * ****************************************这是你写的方法***********************************************
     */
    @Override
    public List<QuestionListViewItem> getReplyData(QuestionPostContentActivity activity, Map<String, Object> bean) {

        QuestionPostServiceImplTool tool = new QuestionPostServiceImplTool(activity,bean);
        tool.getdata();
        return null;
    }



    @Override
    public void giveReply() {

    }

    @Override
    public void giveAnswer() {

    }
}
