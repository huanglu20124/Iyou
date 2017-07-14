package com.example.iyou.tool;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.iyou.community.activity.ContentCommunity;
import com.example.iyou.community.activity.ShareQuestionPostActivity;
import com.example.iyou.community.model.bean.SharePostActivityParamters;
import com.example.iyou.community.model.bean.TRGetDataParamters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/1/15.
 */

public class CommunityServiceTool {

    private ContentCommunity contentCommunity;                 //回调函数类
    private TRGetDataParamters data;                           //输入数据类
    private SharePostActivityParamters d;

    private List<Map<String, Object>> Routeresult = null;       //足迹贴返回数据
    private List<Map<String, Object>> Questionresult = null;    //问题贴返回数据

    private boolean Route = false;                              //判断是否有问题或者足迹贴内容返回
    private boolean Question = false;

    private List<List<Map<String, Object>>> result;             //总返回数据
    private static final int MSG_CODE = 1001;                   //没卵用的东西
    private static final int SEND_QUESTION_CODE=1002;

    private ShareQuestionPostActivity activity=null;


    public CommunityServiceTool (ShareQuestionPostActivity activity){
        this.activity=activity;
    }

    public CommunityServiceTool (ContentCommunity contentCommunity, TRGetDataParamters data){
        this.contentCommunity = contentCommunity;
        this.data = data;

    }

    private Handler RouteHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                result = (List<List<Map<String, Object>>>)msg.obj;

                Routeresult = result.get(0);

                if(Routeresult != null) {
                    Object o1 = (Object)Routeresult;
                    initRouteView(o1);
                }
            }
        }
    };

    private Handler QuestionHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                result = (List<List<Map<String, Object>>>)msg.obj;

                //进行结果大小判断，防止下标越界
                Questionresult = result.get(0);

                if(Questionresult != null){
                    Object o2 = (Object)Questionresult;
                    initQuestionView(o2);
                }
            }
            if(msg.arg1==SEND_QUESTION_CODE){
                initSendQuestion();
            }
        }
    };

    private Handler contentHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null ) {
                result = (List<List<Map<String, Object>>>)msg.obj;

                //进行结果大小判断，防止下标越界
                Routeresult = result.get(0);
                Questionresult = result.get(1);

                if(Routeresult != null) {
                    Object o1 = (Object)Routeresult;
                    initRouteView(o1);
                }
                if(Questionresult != null){
                    Object o2 = (Object)Questionresult;
                    initQuestionView(o2);
                }
            }
        }
    };

    public void initRouteView(Object obj){
        contentCommunity.doRoutePostContent(obj);
    }


    public void initQuestionView(Object obj){
        contentCommunity.doQuestionPostContent(obj);
    }

    public void initSendQuestion(){
        if(activity!=null)
            activity.doCheckMessage();
    }


    //浩铨调用的函数，并调用回调函数，获取所有帖子信息
    public void getcontentPostData(ContentCommunity contentCommunity){

        this.contentCommunity = contentCommunity;
        (new Thread() {
            @Override
            public void run() {
                result = new ArrayList<List<Map<String, Object>>>();

                /*
                 *  足迹贴数据获取
                 */
                Routeresult = GetRouteresult();
                result.add(Routeresult);

                /*
                 *  问题贴数据获取
                 */
                Questionresult = GetQuestionresult();
                result.add(Questionresult);

                /*
                 *  Hander 回调数据
                 */
                Message msg = contentHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();
    }

    public void getRouteData(ContentCommunity contentCommunity){
        this.contentCommunity = contentCommunity;
        (new Thread() {
            @Override
            public void run() {
                result = new ArrayList<List<Map<String, Object>>>();

                /*
                 *  足迹贴数据获取
                 */
                Routeresult = GetRouteresult();
                result.add(Routeresult);

                /*
                 *  Hander 回调数据
                 */
                Message msg = RouteHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();
    }

    public void getQuestionData(ContentCommunity contentCommunity){
        this.contentCommunity = contentCommunity;
        (new Thread() {
            @Override
            public void run() {
                result = new ArrayList<List<Map<String, Object>>>();

                /*
                 *  问题贴数据获取
                 */
                Questionresult = GetQuestionresult();
                result.add(Questionresult);

                /*
                 *  Hander 回调数据
                 */
                Message msg = QuestionHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) result;
                msg.sendToTarget();
            }
        }).start();
    }

//    private Handler QuestionHander = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if(msg.obj != null ) {
//                initRouteView(msg.obj);
//            }
//        }
//    };



    //获取足迹贴内容并返回，给线程函数调用
    public List<Map<String, Object>> GetRouteresult(){
        List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();

        TRIPTool iptool = new TRIPTool();
        String ip = iptool.getIp();
        String port = iptool.getPort();
        String Routeurl = "http://" + ip +
                ":"+port;
        String Routeparam = "type=GetNote&NoteType=Route";
        TRNetRequest RouteTRN = new TRNetRequest();
        String Routeres = RouteTRN.sendGet(Routeurl, Routeparam);
        if(Routeres!=null && !Routeres.equals("")){
            Map<String, Object> Routemap = (Map<String, Object>)JsonUtil.jsonToMap(Routeres);
            r = (List<Map<String, Object>>) Routemap.get("Notes");
        }
        System.out.println(Routeres);
//        Map<String, Object> Routemap = (Map<String, Object>)net.sf.json.JSONObject.fromObject(Routeres);
        return r;
    }

    //获取问题贴内容并返回，给线程函数调用
    public List<Map<String, Object>> GetQuestionresult(){
        List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
        TRIPTool iptool = new TRIPTool();
        String ip = iptool.getIp();
        String port = iptool.getPort();
        String Questionurl = "http://" + ip +
                ":"+port;
        String Questionparam = "type=GetNote&NoteType=Question";
        TRNetRequest QuestionTRN = new TRNetRequest();
        String Questionres = QuestionTRN.sendGet(Questionurl, Questionparam);

        if(Questionres==null || Questionres.equals("")){
            return null;
        }
        System.out.println(Questionres);

        Map<String, Object> Questionmap =(Map<String, Object>)JsonUtil.jsonToMap(Questionres);
        r = (List<Map<String, Object>>) Questionmap.get("Notes");
        return r;
    }

    //上传问题贴
    public void sendQuestion(final SharePostActivityParamters d){
        (new Thread() {
            @Override
            public void run() {
                try{
                    d.setContent(URLEncoder.encode(d.getContent(),"utf-8"));
                    d.setAccount(URLEncoder.encode(d.getAccount(),"utf-8"));
                    d.setTitle(URLEncoder.encode(d.getTitle(),"utf-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

                String paramString =
                        String.format("type=upQuestion&account=%s&timestamp=%s&title=%s&NoteType=%s&content=%s",
                                d.getAccount(),d.getTimestamp() ,d.getTitle(), "Question", d.getContent());

                TRIPTool iptool = new TRIPTool();
                int port = Integer.parseInt(iptool.getPort());
                String IP = iptool.getIp();

                if(d.getImageArr() != null)
                {
                    String imageStr = iptool.byte2hex(d.getImageArr());
                    paramString = paramString + "&imageUrl=" + d.getImageUrl();
                    paramString = paramString + "&image=" + imageStr;
                }

                TRNetRequest sendTRN = new TRNetRequest();
                try {
                    sendTRN.socketData(IP, port, paramString);
                }
                catch(Exception e) {

                }

                /*
                 *  Hander 回调数据
                 */
                Message msg = QuestionHander.obtainMessage(SEND_QUESTION_CODE);
                msg.arg1=SEND_QUESTION_CODE;
                msg.sendToTarget();
            }
        }).start();
    }

    public List<Map<String, Object>> getQuestionDataForNum(){
        (new Thread() {
            @Override
            public void run() {
                result = new ArrayList<List<Map<String, Object>>>();

                /*
                 *  问题贴数据获取
                 */
                Questionresult = GetQuestionresult();
                result.add(Questionresult);
                Log.d("xx","xx");
            }
        }).start();
        return result.get(0);
    }

}
