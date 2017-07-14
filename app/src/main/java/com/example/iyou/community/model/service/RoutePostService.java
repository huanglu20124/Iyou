package com.example.iyou.community.model.service;

import com.example.iyou.community.activity.RoutePostContent;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public interface RoutePostService {

    //获取足迹贴回复信息
    public List<Map<String,Object>> getCommentData(Map<String, Object> bean,RoutePostContent routePostContent);
    //获取足迹贴点赞数据记录信息
    public List<Map<String,Object>> getZanData();
    //点赞
    public boolean giveZan();
    //评论
    public void giveReply();

}
