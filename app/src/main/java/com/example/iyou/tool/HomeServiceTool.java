package com.example.iyou.tool;

import android.os.Handler;
import android.os.Message;

import com.example.iyou.home.activity.ContentHome;
import com.example.iyou.home.model.bean.Paramters.TRGetDataParamters;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/1/15.
 */

public class HomeServiceTool {

    private ContentHome contentHome;
    private TRGetDataParamters data;

    List<Map<String, Object>> resultList;
    private static final int MSG_CODE = 1001;

    public HomeServiceTool(com.example.iyou.home.activity.ContentHome contentHome,TRGetDataParamters data){
        this.contentHome = contentHome;
        this.data = data;
    }

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                initView(msg.obj);
            }
        }
    };

    private Handler LoadHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                initLoadView(msg.obj);
            }
        }
    };

    public void initView(Object obj){
        contentHome.doContent(obj);
    }
    public void initLoadView(Object obj){
        contentHome.doLoadContent(obj);
    }

    public void getData(){
        (new Thread() {
            @Override
            public void run() {


                Integer limit = data.getLimit();
                String keyWordType = data.getKeyWordType();
                String keyWord = data.getKeyWord();
                try {
                    keyWord = URLEncoder.encode(keyWord,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String type = data.getType();

                //获取本机ip
                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String urlString = "http://" + ip +
                        ":"+ port + "?type="+ type +"&limit="+ limit +"&keyWordType="+ keyWordType +"&keyWord="+ keyWord +"";
                URL url = null;
                try {
                    url = new URL(urlString);

                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    connection.setRequestProperty("user-agent",
                            "TRClient 1.0");
                    connection.setRequestMethod("GET");

                    connection.connect();

                    int code = connection.getResponseCode();
                    System.out.println("响应头： " + code);

                    InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                    int tem;
                    StringBuffer boBuffer = new StringBuffer();
                    while (true) {
                        tem = reader.read();
                        if (tem == -1) {
                            break;
                        }
                        boBuffer.append((char)tem);
                    }


                    System.out.println("服务器数据" + boBuffer);

                    Map<String, Object> map = (Map<String, Object>) JsonUtil.jsonToMap(boBuffer.toString());
                    resultList = (List<Map<String, Object>>) map.get("scenerys");

                    System.out.println("==============wowowo==============;'];].].]./]/;][;']======="+resultList);
                    System.out.println();
                    reader.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Message msg = mHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) resultList;
                msg.sendToTarget();

            }
        }).start();
    }

    //获取新的数据调用线程函数
    public void getLoadData(){
        (new Thread() {
            @Override
            public void run() {


                Integer limit = data.getLimit();
                String keyWordType = data.getKeyWordType();
                String keyWord = data.getKeyWord();
                String type = data.getType();
                Integer sinceId = data.getSinceId();


                //获取本机ip
                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String urlString = "http://" + ip +
                        ":"+ port +"?type="+ type +"&limit="+ limit +"&keyWordType="+ keyWordType +"&keyWord="+ keyWord +""+ "&sinceId=" + sinceId;
                URL url = null;
                try {
                    url = new URL(urlString);

                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    connection.setRequestProperty("user-agent",
                            "TRClient 1.0");
                    connection.setRequestMethod("GET");

                    connection.connect();

                    int code = connection.getResponseCode();
                    System.out.println("响应头： " + code);

                    InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                    int tem;
                    StringBuffer boBuffer = new StringBuffer();
                    while (true) {
                        tem = reader.read();
                        if (tem == -1) {
                            break;
                        }
                        boBuffer.append((char)tem);
                    }

//                    System.out.println("服务器数据" + boBuffer);

                    Map<String, Object> map = (Map<String, Object>) JsonUtil.jsonToMap(boBuffer.toString());
                    resultList = (List<Map<String, Object>>) map.get("scenerys");

                    reader.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Message msg = LoadHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) resultList;
                msg.sendToTarget();

            }
        }).start();
    }
}
