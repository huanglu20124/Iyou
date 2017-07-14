package com.example.iyou.my.model.service;

import android.app.Activity;

/**
 * Created by asus on 2017/1/4.
 */
public interface MyService {

    //跳转到修改个人信息
    public void toResetPage(Activity activity);
    //跳转到我的足迹贴
    public void toMyRoutePostPage(Activity activity);
    //跳转到我的问题贴
    public void toMyQuestionPostPage(Activity activity);
    //跳转到我的回复
    public void toMyReplyPage(Activity activity);
    //跳转到修改个人信息界面
    public void toExchangePage(Activity activity);
    //跳转到软件详情界面
    public void toAboutAppPage(Activity activity);
    //退出登录
    public boolean logOff(Activity activity);
    //跳转到我的自定义本地保存路线界面
    public void toRoutePage(Activity activity);

}
