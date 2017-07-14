package com.example.iyou.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.iyou.R;
import com.example.iyou.route.activity.RouteAddressActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SceneryCarActivity extends AppCompatActivity {


    private SceneryCarListAdapter listAdapter;
    private ListView listView;
    private List<Map<String,Object>> data;

    private LinearLayout makeRouteButton;

    private SceneryCarReceiver receiver=new SceneryCarReceiver();

    public static List<Map<String,Object>> dataRecource=new ArrayList<Map<String,Object>>();

    //用于获取修改路线储存的本地路线数据集
    private List<Map<String, Object>> bean;

    @Override
    protected void onResume() {
        super.onResume();
        if(bean!=null){
            data=bean;
            listAdapter=new SceneryCarListAdapter(this,data);
            listView.setAdapter(listAdapter);
        }
        else {
            data=dataRecource;
            listAdapter=new SceneryCarListAdapter(this,data);
            listView.setAdapter(listAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerycar);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
//        actionBar.setTitle("景点车");
        actionBar.setDisplayHomeAsUpEnabled(true);

        bean = (List<Map<String, Object>>) getIntent().getSerializableExtra("bean");
        if(bean!=null){
            actionBar.setTitle("修改路线");
        }
        else{
            actionBar.setTitle("景点车");
        }


        makeRouteButton=(LinearLayout) this.findViewById(R.id.make_route_btn);
        makeRouteButton.setOnClickListener(new MakeRouteBtnListener());


        listView=(ListView)this.findViewById(R.id.scenerycar_listView);
        LayoutAnimationController layoutAnimationController= new    LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.anim_one));
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        listView.setLayoutAnimation(layoutAnimationController);
//        data=dataRecource;
//        listAdapter=new SceneryCarListAdapter(this,data);
//        listView.setAdapter(listAdapter);
    }


    //点击定制路线按钮生成路线事件
    public class MakeRouteBtnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //生成路线标志
            StringBuffer routeName = new StringBuffer();
            for(Map<String,Object> map:data){
                routeName.append(map.get("sceneryName") + "->");
            }

            if(routeName==null || routeName.toString().equals("")){
                //Toast.makeText(SceneryCarActivity.this, "请设计你的旅游路线", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(SceneryCarActivity.this)
                        .setTitle("温馨提示：")
                        .setMessage("请设计你的旅游路线")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }else{
                //保存我的路线在本地，位置在storage-emulated-0-MyRoute.txt文件里面
//                FileUtils fileUtils=new FileUtils();
//                fileUtils.save(data,fileUtils.getSDPATH()+"MyRoute.txt",true);
                Intent intent =new Intent();
//                intent.setClass(SceneryCarActivity.this, RouteActivity.class);
                intent.setClass(SceneryCarActivity.this, RouteAddressActivity.class);
                intent.putExtra("bean", (Serializable) data);
                //如果是修改景点就设置传一个判断值
                if(bean!=null){
                    intent.putExtra("flag",true);
                    intent.putExtra("position",getIntent().getIntExtra("position",-1));
                }
                SceneryCarActivity.this.startActivity(intent);
                //清除景点车
//                data.clear();
                //刷新列表
                listAdapter.notifyDataSetChanged();
            }

        }
    }
}
