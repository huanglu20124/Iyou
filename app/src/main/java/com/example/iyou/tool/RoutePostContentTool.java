package com.example.iyou.tool;

import android.os.Handler;
import android.os.Message;

import com.example.iyou.community.activity.RoutePostContent;
import com.example.iyou.community.model.bean.TRGetDataParamters;

import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/1/15.
 */

public class RoutePostContentTool {

    private List<Map<String, Object>> result = null;
    private Map<String, Object> bean;
    private RoutePostContent routePostContent;
    private TRGetDataParamters data;
    private static final int MSG_CODE = 1001;


    public RoutePostContentTool(TRGetDataParamters data, RoutePostContent routePostContent){
        this.routePostContent = routePostContent;
        this.data = data;
    }

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                initView(msg.obj);
            }
            else{
                System.out.println("namabi");
            }
        }
    };

    private void initView(Object obj){
        routePostContent.docontent(obj);
    }

    public void getdata(){


        (new Thread() {
            @Override
            public void run() {

                //获取数据
                String noteId = data.getNoteId();

//                TRNetRequest trn = new TRNetRequest();
                TRIPTool iptool = new TRIPTool();
                String type = data.getType();
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String url = "http://" + ip +
                        ":" + port;
                String param = "type="+ type +"&noteId=" + noteId;
                String res = TRNetRequest.sendGet(url, param);

                Map<String, Object> map = (Map<String, Object>)JsonUtil.jsonToMap(res);
                result = (List<Map<String, Object>>) map.get("comments");


                Message msg = mHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();

    }

}
