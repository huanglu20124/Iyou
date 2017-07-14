package com.example.iyou.model.utils;

import com.example.iyou.community.model.service.CommunityService;
import com.example.iyou.community.model.service.QuestionPostService;
import com.example.iyou.community.model.service.RoutePostService;
import com.example.iyou.community.model.service.impl.CommunityServiceImpl;
import com.example.iyou.community.model.service.impl.QuestionPostServiceImpl;
import com.example.iyou.community.model.service.impl.RoutePostServiceImpl;
import com.example.iyou.home.model.service.HomeService;
import com.example.iyou.home.model.service.impl.HomeServiceImpl;
import com.example.iyou.my.model.service.MyQuestionPostService;
import com.example.iyou.my.model.service.MyReplyService;
import com.example.iyou.my.model.service.MyRoutePostService;
import com.example.iyou.my.model.service.MyService;
import com.example.iyou.my.model.service.impl.impl.MyQuestionPostServiceImpl;
import com.example.iyou.my.model.service.impl.impl.MyReplyServiceImpl;
import com.example.iyou.my.model.service.impl.impl.MyRoutePostServiceImpl;
import com.example.iyou.my.model.service.impl.impl.MyServiceImpl;
import com.example.iyou.route.model.service.RouteService;
import com.example.iyou.route.model.service.impl.RouteServiceImpl;

/**
 * Created by asus on 2017/1/4.
 */
public class DAOFactory {

    //生产主页服务功能实现类
    public static HomeService getHomeServiceInstance(){
        return new HomeServiceImpl();
    }
    //生产路线定制页面服务功能实现类
    public static RouteService getRouteServiceInstance(){
        return new RouteServiceImpl();
    }
    //生产社区页面服务功能实现类
    public static CommunityService getCommunityServiceInstance(){
        return new CommunityServiceImpl();
    }
    //生产足迹贴页面服务功能实现类
    public static RoutePostService getRoutePostServiceInstance(){
        return new RoutePostServiceImpl();
    }
    //生产问题贴item点击页面服务功能实现类
    public static QuestionPostService getQuestionPostServiceInstance(){
        return new QuestionPostServiceImpl();
    }
    //生产我的页面服务功能实现类
    public static MyService getMyServiceInstance(){
        return new MyServiceImpl();
    }
    //生产我的问题贴页面服务功能实现类
    public static MyQuestionPostService getMyQuestionPostServiceInstance(){
        return new MyQuestionPostServiceImpl();
    }
    //生产我的足迹贴页面服务功能实现类
    public static MyRoutePostService getMyRoutePostServiceInstance(){
        return new MyRoutePostServiceImpl();
    }
    //生产我的回复页面服务功能实现类
    public static MyReplyService getMyReplyServiceInstance(){
        return new MyReplyServiceImpl();
    }


}
