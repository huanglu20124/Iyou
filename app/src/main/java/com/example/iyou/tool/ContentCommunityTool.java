package com.example.iyou.tool;

import android.os.Handler;
import android.os.Message;

import com.example.iyou.community.activity.ContentCommunity;

import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/2/17.
 */

public class ContentCommunityTool {

    private List<Map<String, Object>> Data;                     //原有的list数据
    private ContentCommunity contentCommunity;

    private List<Map<String, Object>> result;             //总返回数据

    public ContentCommunityTool(ContentCommunity contentCommunity , List<Map<String, Object>> Data){
        this.Data = Data;
        this.contentCommunity = contentCommunity;
    }

    private Handler RouteHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                initRouteView(msg.obj);
            }
        }
    };
    private Handler QuestHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                initQuestView(msg.obj);
            }
        }
    };

    //回调函数
    private void initRouteView(Object obj){
        contentCommunity.doLoadRoutePostContent(obj);
    }
    private void initQuestView(Object obj){
        contentCommunity.doLoadQuestPostContent(obj);
    }

    //加载更多足迹贴信息
    public void doLoadRoute(){
        (new Thread() {
            @Override
            public void run() {
                TRIPTool IPtool = new TRIPTool();
                String ip = IPtool.getIp();
                String port = IPtool.getPort();
                TRNetRequest TRNet = new TRNetRequest();

                String id = (String) Data.get(0).get("noteId");
                for(int i = 0;i<Data.size();i++) {
                    if(Integer.parseInt(id) > Integer.parseInt((String)Data.get(i).get("noteId"))){
                        id = (String)Data.get(i).get("noteId");
                    }
                }

                String url = "http://" + ip +
                        ":" + port;
                String param = "type=GetNote&NoteType=Route&sinceId="+id;
                String res = TRNet.sendGet(url, param);
//                System.out.println(result);

                Map<String, Object> Routemap = (Map<String, Object>) JsonUtil.jsonToMap(res);
                result = (List<Map<String, Object>>) Routemap.get("Notes");

                /*
                 *  Hander 回调数据
                 */
                Message msg = RouteHander.obtainMessage();
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();
    }
    public void doLoadQuest(){
        (new Thread() {
            @Override
            public void run() {
                TRIPTool IPtool = new TRIPTool();
                String ip = IPtool.getIp();
                String port = IPtool.getPort();
                TRNetRequest TRNet = new TRNetRequest();

                String id = (String) Data.get(0).get("noteId");
                for(int i = 0;i<Data.size();i++) {
                    if(Integer.parseInt(id) > Integer.parseInt((String)Data.get(i).get("noteId"))){
                        id = (String)Data.get(i).get("noteId");
                    }
                }

                String url = "http://" + ip +
                        ":" + port;
                String param = "type=GetNote&NoteType=Question&sinceId="+id;
                String res = TRNet.sendGet(url, param);
//                System.out.println(result);

                Map<String, Object> Routemap = (Map<String, Object>) JsonUtil.jsonToMap(res);
                result = (List<Map<String, Object>>) Routemap.get("Notes");

                /*
                 *  Hander 回调数据
                 */
                Message msg = QuestHander.obtainMessage();
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();
    }
}
