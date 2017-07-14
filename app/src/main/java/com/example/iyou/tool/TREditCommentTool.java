package com.example.iyou.tool;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.example.iyou.community.activity.EditAnswerActivity;
import com.example.iyou.community.activity.EditCommentActivity;
import com.example.iyou.community.model.bean.EditParamters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by cyhaha on 2017/1/16.
 */

public class TREditCommentTool {
    private EditParamters par;
    private Activity activity;

    public TREditCommentTool(EditParamters par,Activity activity)
    {
        this.activity=activity;
        this.par = par;
        try{
            par.setAccount(URLEncoder.encode(par.getAccount(),"utf-8"));
            par.setContent(URLEncoder.encode(par.getContent(),"utf-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initView();
        }
    };

    public void initView(){
        if(this.activity instanceof EditAnswerActivity){
            ((EditAnswerActivity) this.activity).doCheckMessage();
        }
        if(this.activity instanceof EditCommentActivity){
            ((EditCommentActivity) this.activity).doCheckMessage();
        }

    }


    //上传评论()
    public void senddata() {
        (new Thread() {
            @Override
            public void run() {
                sendfunction();
            }
        }).start();
    }

    //调用函数
    private void sendfunction(){

        String paramString ="type=addComment&account="+par.getAccount()
                +"&timestamp=" + par.getTimestamp()
                +"&commentType=" + par.getCommentType()
                +"&content=" + par.getContent()
                +"&noteId=" + par.getNoteId();

        if(par.getImageArr() != null ){
            paramString = paramString + "&imageUrl=" + par.getImageUrl();
            String image = new TRIPTool().byte2hex(par.getImageArr());
            paramString = paramString + "&image=" + image;
        }
//                        String.format(
//                                "type=addComment&account=%s&timestamp=%s&commentType=%s" +
//                                        "&content=%s&imageUrl=%s&noteId=%d",
//                                par.getAccount(), par.getTimestamp(), par.getCommentType(), par.getContent(), "NULL", par.getNoteId());

        String IP = new TRIPTool().getIp();
        int port = Integer.parseInt(new TRIPTool().getPort());

        try {
            TRNetRequest.socketData(IP, port, paramString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg = mHander.obtainMessage();
        msg.sendToTarget();
    }
}
