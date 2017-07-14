package com.example.iyou.community.model.service;

/**
 * Created by asus on 2017/1/4.
 */
public interface ReplyService {

    //发送编辑信息
    public boolean sendMsg();
    //添加表情
    public Object addExpression();
    //添加图片
    public Object addPicture();
}
