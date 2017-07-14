package com.example.iyou.tool;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.iyou.R;
import com.example.iyou.my.activity.MyQuestionActivity;
import com.example.iyou.my.model.bean.GetdataParamters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/1/16.
 */

public class MyQuestionPostServiceTool {
    private GetdataParamters data;                          //输入数据
    private MyQuestionActivity myQuestionActivity;          //回调函数类
    private List<Map<String, Object>> result;               //返回数据
    private static final int MSG_CODE = 1001;               //没卵用的东西

    //用于更新mainActivity的问题贴数目
    private Activity activity;

    public MyQuestionPostServiceTool(MyQuestionActivity myQuestionActivity, GetdataParamters data){
        this.data = data;
        this.myQuestionActivity = myQuestionActivity;
    }

    public MyQuestionPostServiceTool(Activity activity, GetdataParamters data){
        this.activity = activity;
        this.data = data;
    }

    private  Handler getQuetionNumHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView textView = (TextView) activity.findViewById(R.id.quetion_num);
            textView.setText(String.valueOf(result.size()));
        }
    };

    public void getQuetionNum(){
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
                String Routeparam = "type=GetNote&NoteType=Question&account=" + account;
                TRNetRequest RouteTRN = new TRNetRequest();
                String Routeres = RouteTRN.sendGet(Routeurl, Routeparam);

                if(Routeres!=null && !Routeres.equals("")){
                    Map<String, Object> Routemap = (Map<String, Object>) JsonUtil.jsonToMap(Routeres);
                    result = (List<Map<String, Object>>) Routemap.get("Notes");
                }


                Message msg = getQuetionNumHandler.obtainMessage(MSG_CODE);
                msg.sendToTarget();

            }
        }).start();
    }


    private Handler contentHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                initView(msg.obj);
            }
        }
    };

    private void initView(Object obj){
        myQuestionActivity.doContent(obj);
    }


    public void getMyQuestionPostData(){
                new Thread() {
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
                String Routeparam = "type=GetNote&NoteType=Question&account=" + account;
                TRNetRequest RouteTRN = new TRNetRequest();
                String Routeres = RouteTRN.sendGet(Routeurl, Routeparam);

                System.out.println(Routeres);

                Map<String, Object> Routemap = (Map<String, Object>) JsonUtil.jsonToMap(Routeres);
                if(Routemap.containsKey("Notes"))
                    result = (List<Map<String, Object>>) Routemap.get("Notes");

                Message msg = contentHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) result;
                msg.sendToTarget();

            }
        }.start();

    }

}
