package com.example.iyou.tool;

/**
 * Created by cyhaha on 2017/2/22.
 */

//点赞工具类
public class TRAddagree {
    private String noteId;      //被赞贴ID

    public TRAddagree(String noteId){
        this.noteId = noteId;
    }

    //点赞上传服务器调用函数
    public void AddAgree(){
        (new Thread(){
            @Override
            public void run(){
                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();

                String url = "http://" + ip +
                        ":" + port;
                String param = "type=agree&agreeType=in&noteId=" + noteId;
                String result = new TRNetRequest().sendGet(url, param);
            }
        }).start();
    }

    //点赞取消
    public void subAgree(){
        (new Thread(){
            @Override
            public void run(){
                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();

                String url = "http://" + ip +
                        ":" + port;
                String param = "type=agree&agreeType=sub&noteId=" + noteId;
                String result = new TRNetRequest().sendGet(url, param);
            }
        }).start();
    }
}
