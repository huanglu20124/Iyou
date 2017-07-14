package com.example.iyou.tool;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.iyou.community.activity.QuestionListViewItem;
import com.example.iyou.community.activity.QuestionPostContentActivity;
import com.example.iyou.community.model.bean.TRGetDataParamters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.iyou.R.id.my_question_reply;

/**
 * Created by cyhaha on 2017/1/15.
 */

public class QuestionPostServiceImplTool {
    private List<Map<String, Object>> result = null;
    private Map<String, Object> bean;
    private QuestionPostContentActivity activity;
    private TRGetDataParamters data;
    private static final int MSG_CODE = 1001;
    private View rootView;
    private int num;

    private Handler handlerPeplyNum = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView textView = (TextView) rootView.findViewById(my_question_reply);
            textView.setText(String.valueOf(num-1)+"回复");
        }
    };


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
        activity.doContent(obj);
    }

    public QuestionPostServiceImplTool(QuestionPostContentActivity activity, Map<String, Object> bean){
        this.activity = activity;
        this.bean = bean;
    }


    public QuestionPostServiceImplTool(View rootview, Map<String, Object> bean){
        this.rootView= rootview;
        this.bean = bean;
    }

    public QuestionPostServiceImplTool(Map<String, Object> bean){
        this.bean = bean;
    }

    public void getdata(){

        (new Thread() {
            @Override
            public void run() {

                //获取数据
                String noteId = (String) bean.get("noteId");

//                TRNetRequest trn = new TRNetRequest();
                TRIPTool iptool = new TRIPTool();
                String type = (String)bean.get("type");
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String url = "http://" + ip +
                        ":" + port;
                String param = "type="+ "GetComment" +"&noteId=" + noteId;
                String res = TRNetRequest.sendGet(url, param);

                Map<String, Object> map = (Map<String, Object>)JsonUtil.jsonToMap(res);
                result = (List<Map<String, Object>>) map.get("comments");

                List<QuestionListViewItem> lists = new ArrayList<QuestionListViewItem>();

                //设置问题的具体显示
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put("head", android.R.drawable.ic_menu_crop);
                m.put("account", bean.get("account"));
                m.put("timestamp", bean.get("timestamp"));
                m.put("content", bean.get("content"));
                m.put("imageUrl", bean.get("imageUrl"));
                lists.add(new QuestionListViewItem(0, m));
                result.size();
                //评论的内容
                for(int i = 0;i<result.size();i++){
                    Map<String, Object> index = result.get(i);
                    lists.add(new QuestionListViewItem(1, index));
                }

                Message msg = mHander.obtainMessage(MSG_CODE);
                msg.obj = (Object) lists;
                msg.sendToTarget();
            }
        }).start();
    }

    public void getCommentCount(){
        final List<QuestionListViewItem> lists = new ArrayList<QuestionListViewItem>();
        (new Thread() {
            @Override
            public void run() {

                //获取数据
                String noteId = (String) bean.get("noteId");

//                TRNetRequest trn = new TRNetRequest();
                TRIPTool iptool = new TRIPTool();
                String type = (String)bean.get("type");
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String url = "http://" + ip +
                        ":" + port;
                String param = "type="+ "GetComment" +"&noteId=" + noteId;
                String res = TRNetRequest.sendGet(url, param);

                Map<String, Object> map = (Map<String, Object>)JsonUtil.jsonToMap(res);
                result = (List<Map<String, Object>>) map.get("comments");


                //设置问题的具体显示
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put("head", android.R.drawable.ic_menu_crop);
                m.put("account", bean.get("account"));
                m.put("timestamp", bean.get("timestamp"));
                m.put("content", bean.get("content"));
                m.put("imageUrl", bean.get("imageUrl"));
                lists.add(new QuestionListViewItem(0, m));
                //评论的内容
                for(int i = 0;i<result.size();i++){
                    Map<String, Object> index = result.get(i);
                    lists.add(new QuestionListViewItem(1, index));
                }
                num = lists.size();
                handlerPeplyNum.sendEmptyMessage(0x123);
            }
        }).start();

    }
}
