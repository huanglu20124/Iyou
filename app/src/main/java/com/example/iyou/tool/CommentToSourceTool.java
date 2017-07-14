package com.example.iyou.tool;

import android.os.Handler;
import android.os.Message;

import com.example.iyou.my.activity.MyReplyActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/2/25.
 */

public class CommentToSourceTool {

    protected String infoType;
    protected String ids;
    protected MyReplyActivity activity;

    public CommentToSourceTool(String infoType, String ids,MyReplyActivity activity){
        this.infoType=infoType;
        this.ids=ids;
        this.activity=activity;
    }

    public void GetInfoById(){

        (new Thread() {
            @Override
            public void run() {
                String url = "http://" + "119.29.121.44" + ":8349";
                String param = String.format("type=InfoForId&infoType=%s&ids=%s", infoType, ids);
                String result = sendGet(url, param);

                Map<String, Object> map = (Map<String, Object>)JsonUtil.jsonToMap(result);

                /*
                 *  Hander 回调数据
                 */
                Message msg = mHander.obtainMessage();
                msg.obj = (Object) map;
                msg.sendToTarget();
            }
        }).start();
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
        activity.doContentReplySource(obj);
    }

    private String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString;
            URL realUrl;
            if (param != null) {
                urlNameString = url + "?" + param;
                realUrl = new URL(urlNameString);
            } else{
                urlNameString = url;
                realUrl = new URL(urlNameString);
            }

            // �򿪺�URL֮�������
            URLConnection connection = realUrl.openConnection();
            // ����ͨ�õ���������
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ����ʵ�ʵ�����
            connection.connect();

            // ���� BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;

            // ���������Ժ�һֱ��ȡ���͹�������� ��Ϊ�յĻ�
            // �����̣߳�ʹ��CPU�����߳�ʹ�ã���Ӱ���û���UI����

            while ((line = in.readLine()) != null) { // ���������Ժ�һֱ��ȡ���͹��������
                result += line;
            }
        } catch (Exception e) {
            System.out.println("����GET��������쳣��" + e);
            e.printStackTrace();
        }
        // ʹ��finally�����ر�������
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
