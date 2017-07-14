package com.example.iyou.community.model.service.impl;

import com.example.iyou.community.activity.RoutePostContent;
import com.example.iyou.community.model.bean.TRGetDataParamters;
import com.example.iyou.community.model.service.RoutePostService;
import com.example.iyou.route.model.service.RouteService;
import com.example.iyou.tool.Image;
import com.example.iyou.tool.RoutePostContentTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 */
public class RoutePostServiceImpl implements RoutePostService {

    /*
     * 从服务器获取评论数据源
     */
    @Override
    public List<Map<String, Object>> getCommentData(Map<String, Object> bean,RoutePostContent routePostContent) {

        //输入的数据，如帖子ID等
        TRGetDataParamters data = new TRGetDataParamters();
        data.setNoteId((String) bean.get("noteId"));

        RoutePostContentTool tool = new RoutePostContentTool(data,routePostContent);

        tool.getdata();


        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        /*
        //图片集合
        List<Image> imgs=new ArrayList<Image>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("head", android.R.drawable.ic_menu_crop);
        map.put("account", "user 1");
        map.put("timestamp","2017-01-07 17:13");
        map.put("content","我很开心！");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("head", android.R.drawable.ic_menu_crop);
        map.put("account", "user 2");
        map.put("timestamp","2017-01-07 17:13");
        map.put("content", "我很开心！\n fsdafds");
        imgs.(new Image("https://imgsa.baidu.com/baike/c0%3Dbaike150%2C5%2C5%2C150%2C50/sign=1dc843d3" +
                "02087bf469e15fbb93ba3c49/6a63f6246b600c338719a2501a4c510fd8f9a1c1.jpg", 0, 0));
        imgs.add(new Image("https://imgsa.baidu.com/baike/c0%3Dbaike150%2C5%2C5%2C150%2C50/sign=1dc843d3" +
                "02087bf469e15fbb93ba3c49/6a63f6246b600c338719a2501a4c510fd8f9a1c1.jpg", 1280, 800));
        imgs.add(new Image("https://imgsa.baidu.com/baike/c0%3Dbaike150%2C5%2C5%2C150%2C50/sign=1dc843d3" +
                "02087bf469e15fbb93ba3c49/6a63f6246b600c338719a2501a4c510fd8f9a1c1.jpg", 1280, 800));
        imgs.add(new Image("https://imgsa.baidu.com/baike/c0%3Dbaike150%2C5%2C5%2C150%2C50/sign=1dc843d3" +
                "02087bf469e15fbb93ba3c49/6a63f6246b600c338719a2501a4c510fd8f9a1c1.jpg", 1280, 800));
        imgs.add(new Image("https://imgsa.baidu.com/baike/c0%3Dbaike150%2C5%2C5%2C150%2C50/sign=1dc843d3" +
                "02087bf469e15fbb93ba3c49/6a63f6246b600c338719a2501a4c510fd8f9a1c1.jpg", 1280, 800));
        map.put("imgaeUrl",imgs);
        list.add(map);
        */

        return list;
    }

    /*
     * 从服务器获取点赞记录信息
     */
    @Override
    public List<Map<String, Object>> getZanData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("head", android.R.drawable.ic_menu_crop);
        map.put("username", "google 3");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("head", android.R.drawable.ic_menu_crop);
        map.put("username", "google 3");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("head", android.R.drawable.ic_menu_crop);
        map.put("username", "google 3");
        list.add(map);

        return list;
    }

    @Override
    public boolean giveZan() {
        return false;
    }

    @Override
    public void giveReply() {

    }
}
