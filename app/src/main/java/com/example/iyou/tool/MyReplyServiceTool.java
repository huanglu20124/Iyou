package com.example.iyou.tool;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.iyou.R;
import com.example.iyou.my.activity.MyReplyActivity;
import com.example.iyou.my.model.bean.GetdataParamters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/1/16.
 */

public class MyReplyServiceTool {
    private GetdataParamters data;                          //输入数据
    private MyReplyActivity myReplyActivity;             //回调函数类
    private List<Map<String, Object>> result;               //返回数据
    private static final int MSG_CODE = 1001;               //没卵用的东西
    private Activity activity;

    public MyReplyServiceTool(MyReplyActivity myReplyActivity, GetdataParamters data){
        this.myReplyActivity = myReplyActivity;
        this.data = data;
    }

    public MyReplyServiceTool(Activity activity, GetdataParamters data){
        this.activity = activity;
        this.data = data;
    }

    private Handler contentHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                initView(msg.obj);
            }
            else{
                System.out.println("nimabi");
            }
        }
    };

    private Handler getNumHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView textView = (TextView) activity.findViewById(R.id.reply_num);
            if(null!=String.valueOf(result.size()))
                textView.setText(String.valueOf(result.size()));
        }
    };

    private void initView(Object obj){
        myReplyActivity.doContent(obj);
    }


    public void  getMyReplyData(){
        (new Thread() {
            @Override
            public void run() {
                String account = data.getAccount();
                try{
                    account = URLEncoder.encode(account,"utf-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String Routeurl = "http://" + ip +
                        ":" + port;
                String Routeparam = "type=GetComment&account=" + account;
                TRNetRequest RouteTRN = new TRNetRequest();
                String Routeres = RouteTRN.sendGet(Routeurl, Routeparam);

                System.out.println(Routeres);

                Map<String, Object> Routemap = (Map<String, Object>)JsonUtil.jsonToMap(Routeres);
                if(Routemap.containsKey("comments"))
                    result = (List<Map<String, Object>>) Routemap.get("comments");

                Message msg = contentHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();
    }

    public void getReplyNum(){
        (new Thread() {
            @Override
            public void run() {
                String account = data.getAccount();
                try{
                    account = URLEncoder.encode(account,"utf-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String Routeurl = "http://" + ip +
                        ":" + port;
                String Routeparam = "type=GetComment&account=" + account;
                TRNetRequest RouteTRN = new TRNetRequest();
                String Routeres = RouteTRN.sendGet(Routeurl, Routeparam);

                System.out.println(Routeres);

                Map<String, Object> Routemap = (Map<String, Object>)JsonUtil.jsonToMap(Routeres);
                result = (List<Map<String, Object>>) Routemap.get("comments");

                Message msg = getNumHandler.obtainMessage(MSG_CODE);
                msg.sendToTarget();
            }
        }).start();
    }
}
