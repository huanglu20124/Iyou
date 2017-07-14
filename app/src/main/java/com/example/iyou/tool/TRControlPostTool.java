package com.example.iyou.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by asus on 2017/2/24.
 */

public class TRControlPostTool {

    protected String tableName;
    protected String IdName;
    protected Integer Id;

    public TRControlPostTool(String tableName, String IdName, Integer Id){
        this.tableName=tableName;
        this.IdName=IdName;
        this.Id=Id;

        String url = "http://" + "119.29.121.44" +
                ":8349";
        String param = String.format("type=delete&deleteTable=%s&IdName=%s&Id=%d",
                tableName, IdName, Id);
        String result = sendGet(url, param);
    }

    public void Delete() {
        String url = "http://" + "119.29.121.44" +
                ":8349";
        String param = String.format("type=delete&deleteTable=%s&IdName=%s&Id=%d",
                this.tableName, this.IdName, this.Id);
//        String param = String.format("type=delete&deleteTable=%s&IdName=%s&Id=%d",
//                tableName, IdName, Id);
        String result = sendGet(url, param);
        // System.out.println(result);
    }

    public String sendGet(String url, String param) {
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
