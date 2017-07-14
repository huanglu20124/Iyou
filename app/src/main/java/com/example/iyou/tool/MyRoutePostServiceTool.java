package com.example.iyou.tool;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.iyou.R;
import com.example.iyou.my.activity.MyRouteActivity;
import com.example.iyou.my.model.bean.GetdataParamters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/1/16.
 */

public class MyRoutePostServiceTool {
    private MyRouteActivity myRouteActivity;    //回调函数类
    private GetdataParamters data;              //输入数据


    //该rootView用于更新mainActvity
    private Activity activity;

    List<Map<String, Object>> result;           //返回数据
    private static final int MSG_CODE = 1001;                   //没卵用的东西

    public MyRoutePostServiceTool(MyRouteActivity myRouteActivity, GetdataParamters data){
        this.myRouteActivity = myRouteActivity;
        this.data = data;
    }

    public MyRoutePostServiceTool(Activity activity, GetdataParamters data){
        this.data = data;
        this.activity = activity;
    }

    private Handler contentHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                //数据在这里传入
                initView(msg.obj);
            }
        }
    };

    //用于更新mainActvity
    private Handler getNumHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView tv = (TextView) activity.findViewById(R.id.route_num);
            int num = result.size();
            tv.setText(String.valueOf(num));
        }
    };


    public void  getRouteNum(){
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
                String Routeparam = "type=GetNote&NoteType=Route&account=" + account;
                TRNetRequest RouteTRN = new TRNetRequest();
                String Routeres = RouteTRN.sendGet(Routeurl, Routeparam);

                if(Routeres!=null && !Routeres.equals("")){
                    Map<String, Object> Routemap = (Map<String, Object>)JsonUtil.jsonToMap(Routeres);
                    result = (List<Map<String, Object>>) Routemap.get("Notes");
                }
                System.out.println(Routeres);


                Message msg = getNumHandler.obtainMessage(1);
                msg.sendToTarget();
            }
        }).start();
    }

    private void initView(Object obj){
        myRouteActivity.doContent(obj);
    }

    public void getMyRoutePostData() {
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
                String Routeparam = "type=GetNote&NoteType=Route&account=" + account;
                TRNetRequest RouteTRN = new TRNetRequest();
                String Routeres = RouteTRN.sendGet(Routeurl, Routeparam);

                System.out.println(Routeres);

                Map<String, Object> Routemap = (Map<String, Object>)JsonUtil.jsonToMap(Routeres);
                if(Routemap.containsKey("Notes"))
                    result = (List<Map<String, Object>>) Routemap.get("Notes");

                Message msg = contentHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) result;
                msg.sendToTarget();

            }
        }).start();
    }


}
