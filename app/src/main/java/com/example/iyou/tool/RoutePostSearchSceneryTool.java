package com.example.iyou.tool;

import android.os.Handler;
import android.os.Message;

import com.example.iyou.community.activity.RoutePostContentActivity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/2/23.
 */

public class RoutePostSearchSceneryTool {

    List<Map<String, Object>> result;//返回的结果list
    RoutePostContentActivity searchActivity;   //改成你的回掉函数的类
    String keyword;                  //景点id串

    //里面的activity也要改
    public RoutePostSearchSceneryTool(String keyword, RoutePostContentActivity searchActivity){
        this.searchActivity = searchActivity;
        this.keyword=keyword;
    }

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                initView(msg.obj);
            }
        }
    };

    public void initView(Object obj){
        //改成调用你的回调函数
        searchActivity.doSceneryContent(obj);
    }

    //啊铨调用函数
    public void loadDatas(){
        (new Thread() {
            @Override
            public void run() {

                result = Datafunction(keyword);

                /*
                 *  Hander 回调数据
                 */
                Message msg = mHander.obtainMessage();
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();
    }

    //根据id串获取景点信息
    private  List<Map<String, Object>> Datafunction(String id){

        List<Map<String, Object>> index = null;

        TRIPTool iptool = new TRIPTool();
        String ip = iptool.getIp();
        String port = iptool.getPort();

        String urlString = "http://" + ip +
                ":"+port+"?type=queryScenery&limit=20&keyWordType=sceneryIds&keyWord="+ id;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "TRClient 1.0");
            connection.setRequestMethod("GET");

            connection.connect();

            int code = connection.getResponseCode();
//                    System.out.println("响应头： " + code);

            InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
            int tem;
            StringBuffer boBuffer = new StringBuffer();
            while (true) {
                tem = reader.read();
                if (tem == -1) {
                    break;
                }
                boBuffer.append((char) tem);
            }

            if(boBuffer == null || boBuffer.equals("")){
                return index;
            }

            Map<String, Object> map = (Map<String, Object>)JsonUtil.jsonToMap(boBuffer.toString());
            index = (List<Map<String, Object>>) map.get("scenerys");

//                    System.out.println("服务器数据" + boBuffer);
            reader.close();
            return  index;
        }
        catch (IOException e){

        }
        finally {
            return index;
        }
    }
}
