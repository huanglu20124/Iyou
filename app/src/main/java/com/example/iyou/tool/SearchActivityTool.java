package com.example.iyou.tool;

import android.os.Handler;
import android.os.Message;

import com.example.iyou.home.activity.SearchActivity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/2/16.
 */

public class SearchActivityTool {

    List<Map<String, Object>> result;
    SearchActivity searchActivity;
    String keyword;

    public SearchActivityTool(String keyword,SearchActivity searchActivity){
        this.searchActivity = searchActivity;
        try{
            this.keyword = URLEncoder.encode(keyword,"utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
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
        searchActivity.doSceneryContent(obj);
    }

    public void loadDatas(){
        (new Thread() {
            @Override
            public void run() {
                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String urlString = "http://" + ip +
                        ":"+ port +"?type=queryScenery&limit=20&keyWordType=scenery&keyWord="+ keyword;
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

                    Map<String, Object> map = (Map<String, Object>)JsonUtil.jsonToMap(boBuffer.toString());
                    result = (List<Map<String, Object>>) map.get("scenerys");

//                    System.out.println("服务器数据" + boBuffer);
                    reader.close();
                }
                catch (IOException e){

                }
                /*
                 *  Hander 回调数据
                 */
                Message msg = mHander.obtainMessage();
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();
    }

}
