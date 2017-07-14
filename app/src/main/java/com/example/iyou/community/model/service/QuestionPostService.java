package com.example.iyou.community.model.service;

import android.app.Activity;

import com.example.iyou.community.activity.QuestionListViewItem;
import com.example.iyou.community.activity.QuestionPostContentActivity;
import com.example.iyou.community.activity.RoutePostContent;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public interface QuestionPostService {

    //获取问题信息内容以及相应的回答（给陈跃数据连接层测试使用的重写方法，实现后要删除上面的方法）
    public List<QuestionListViewItem> getReplyData(QuestionPostContentActivity activity, Map<String,Object> bean);
    //回复帖子
    public void giveReply();
    //回答帖子
    public void giveAnswer();
}
