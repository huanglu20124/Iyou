package com.example.iyou.community.model.service;

import android.app.Activity;
import android.view.View;

import com.example.iyou.community.activity.ContentCommunity;
import com.example.iyou.community.model.bean.SharePostActivityParamters;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public interface CommunityService {

    //从服务器获取帖子数据
    public List<Map<String,Object>> getcontentPostData(ContentCommunity contentCommunity);

    //从服务器获取足迹贴数据，list的数据源
    public List<Map<String,Object>> getRoutePostData(ContentCommunity contentCommunity);
    //从服务器获取问题贴数据，list的数据源
    public List<Map<String,Object>> getQuestionPostData(ContentCommunity contentCommunity);
    //跳转到评论点赞界面
    public void toRoutePostCoGiPage(Activity activity,List<Map<String, Object>> data,View v);
    //跳转到问题详细介绍和回答界面
    public void toQuestionPostAnswerPage(Activity activity,List<Map<String, Object>> data,int position);
    //搜索足迹贴关键字
    public List<Map<String,Object>> searchRoutePost(String key);
    //搜索问题贴关键字
    public List<Map<String,Object>> searchQuestionPost(String key);
    //分享足迹贴
    public void shareRoute();
    //发布问题贴
    public void sendQuestion(SharePostActivityParamters data);
    //跳转显示足迹贴的连接，地图显示路线
    public void displayRouteOnAddr();
    //点赞
    public boolean giveZan();
    //跳转到评论界面
    public void togiveReplyPage();
    //跳转到正文显示界面
    public void toArticlePostPage(Activity activity,List<Map<String, Object>> data,int position);
}


