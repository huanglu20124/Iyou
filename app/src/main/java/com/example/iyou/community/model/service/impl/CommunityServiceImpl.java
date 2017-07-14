package com.example.iyou.community.model.service.impl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.iyou.community.activity.ContentCommunity;
import com.example.iyou.community.activity.QuestionPostContentActivity;
import com.example.iyou.community.activity.RoutePostContentActivity;
import com.example.iyou.community.model.bean.SharePostActivityParamters;
import com.example.iyou.community.model.bean.TRGetDataParamters;
import com.example.iyou.community.model.service.CommunityService;
import com.example.iyou.tool.CommunityServiceTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public class CommunityServiceImpl implements CommunityService {
    /*
     * 从服务器获取贴子数据
     */
    @Override
    public List<Map<String, Object>> getcontentPostData(ContentCommunity contentCommunity) {
        TRGetDataParamters data = new TRGetDataParamters();
        data.setNoteId("1");

        CommunityServiceTool tool = new CommunityServiceTool(contentCommunity,data);
        tool.getcontentPostData(contentCommunity);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        return list;
    }

    /*
     *  从服务器获取足迹贴数据
     */
    @Override
    public List<Map<String, Object>> getRoutePostData(ContentCommunity contentCommunity) {
        TRGetDataParamters data = new TRGetDataParamters();
        data.setNoteId("1");

        CommunityServiceTool tool = new CommunityServiceTool(contentCommunity,data);
        tool.getRouteData(contentCommunity);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        return list;
    }

    //正文界面
    @Override
    public void toArticlePostPage(Activity activity, List<Map<String, Object>> data, int position) {
        Intent intent=new Intent();
        intent.setClass(activity,RoutePostContentActivity.class);
        intent.putExtra("bean", (Serializable) data.get(position));
        activity.startActivity(intent);
    }

    /*
     * 从服务器过去问题贴信息
     */
    @Override
    public List<Map<String, Object>> getQuestionPostData(ContentCommunity contentCommunity) {

        TRGetDataParamters data = new TRGetDataParamters();
        data.setNoteId("1");

        CommunityServiceTool tool = new CommunityServiceTool(contentCommunity,data);
        tool.getQuestionData(contentCommunity);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();


        return list;
    }


    /*
    * 跳转到足迹贴评论和点赞界面
    */
    @Override
    public void toRoutePostCoGiPage(Activity activity,List<Map<String, Object>> data,View v) {
        Intent intent=new Intent();
        intent.setClass(activity,RoutePostContentActivity.class);
        intent.putExtra("bean", (Serializable) data.get((Integer)v.getTag()));
        activity.startActivity(intent);
    }

    /*
     * 跳转到问题贴回答界面
     */

    @Override
    public void toQuestionPostAnswerPage(Activity activity,List<Map<String, Object>> data,int position)   {
        Intent intent=new Intent();
        intent.setClass(activity,QuestionPostContentActivity.class);
        intent.putExtra("bean", (Serializable) data.get(position));
        activity.startActivity(intent);
    }


    @Override
    public List<Map<String, Object>> searchRoutePost(String key) {
        return null;
    }

    @Override
    public List<Map<String, Object>> searchQuestionPost(String key) {
        return null;
    }

    @Override
    public void shareRoute() {

    }

    @Override
    public void sendQuestion(SharePostActivityParamters data) {
//        CommunityServiceTool tool =new CommunityServiceTool();
//        tool.sendQuestion(data);

    }


    @Override
    public void displayRouteOnAddr() {

    }

    @Override
    public boolean giveZan() {
        return false;
    }


    @Override
    public void togiveReplyPage() {

    }

}
