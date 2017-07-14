package com.example.iyou.tool;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.example.iyou.community.activity.EditAnswerActivity;
import com.example.iyou.community.activity.EditCommentActivity;
import com.example.iyou.route.activity.EditShareRouteActivity;
import com.example.iyou.route.model.bean.EditShareRouteParamters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.example.iyou.tool.TRNetRequest.socketData;

/**
 * Created by cyhaha on 2017/2/17.
 */

public class EditShareRouteActivityTool {

    private EditShareRouteParamters data;
    private EditShareRouteActivity shareRouteActivity;

    public EditShareRouteActivityTool(EditShareRouteParamters data,EditShareRouteActivity shareRouteActivity)
    {
        this.data = data;
        this.shareRouteActivity=shareRouteActivity;
        String account,title,content;
        account = data.getAccount();
        title = data.getTitle();
        content = data.getContent();

        try{
            account = URLEncoder.encode(account,"utf-8");
            this.data.setAccount(account);
            title = URLEncoder.encode(title,"utf-8");
            this.data.setTitle(title);
            content = URLEncoder.encode(content,"utf-8");
            this.data.setContent(content);
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
        shareRouteActivity.doCheckMessage();

    }

    public void upload(){
        (new Thread() {
            @Override
            public void run() {
                try {
                    String paramString =
                            "type=upRoute&account="+data.getAccount()+"&timestamp="+ data.getTimestamp()+"&title="+data.getTitle()+"&NoteType="+
                                    data.getNoteType()+"&content="+data.getContent()+"&sceneries="+data.getSceneries();
                    TRIPTool ip = new TRIPTool();
                    String IP = ip.getIp();
                    int port = Integer.parseInt(ip.getPort());
                    if(data.getImageArr() != null){
                        String imageStr = ip.byte2hex(data.getImageArr());
                        paramString = paramString + "&imageUrl=" + data.getImageUrl();
                        paramString = paramString + "&image=" + imageStr;
                    }
                    socketData(IP, port, paramString);
                }
                catch (Exception e){

                }

                Message msg = mHander.obtainMessage();
                msg.sendToTarget();
            }
        }).start();
    }
}
