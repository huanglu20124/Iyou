package com.example.iyou.community.activity;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.example.iyou.model.utils.DAOFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/1/15.
 */

public class RoutePostContent {

    private Context context;
    private ListView listview;
    private Map<String, Object> bean;

    List<Map<String, Object>> listdata=null;
    CommentListAdapter commentListAdapter;

    public RoutePostContent(final Context context, final ListView listview,Map<String, Object> bean) {

        this.listview = listview;
        this.context = context;
        this.bean = bean;

        DAOFactory.getRoutePostServiceInstance().getCommentData(bean,this);
    }
        public void docontent(Object obj){

            //设置数据源
            listdata = (List<Map<String, Object>>)obj;
//            listdata = DAOFactory.getRoutePostServiceInstance().getCommentData();

            //设置适配器
            commentListAdapter=new CommentListAdapter(context, listdata);
            listview.setAdapter(commentListAdapter);
        }
}
