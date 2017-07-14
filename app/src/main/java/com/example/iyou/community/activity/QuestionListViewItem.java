package com.example.iyou.community.activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 2017/1/16.
 */
public class QuestionListViewItem {

    //用于区分listview显示的不同item,告诉适配器我这是什么类型，listview适配器根据type决定怎么显示
    public int type;
    //将要显示的数据用HashMap包装好
    public Map<String,Object> map ;

    public QuestionListViewItem(int type, Map<String, Object> map){
        this.type=type;
        this.map=map;
    }
}
