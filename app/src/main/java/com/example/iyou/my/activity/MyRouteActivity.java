package com.example.iyou.my.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.iyou.R;
import com.example.iyou.community.activity.RoutePostListAdapter;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.route.model.bean.ToastUtil;
import com.example.iyou.tool.TRControlPostTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyRouteActivity extends AppCompatActivity {

    private ListView listView;
    private List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();

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
        setContentView(R.layout.activity_my_route);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("我的足迹贴");
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView=(ListView)findViewById(R.id.my_route_listView);
        DAOFactory.getMyRoutePostServiceInstance().getMyRoutePostData(this);

    }

    public void doContent(Object obj){
        data=(List<Map<String,Object>>)obj;
        final RoutePostListAdapter adapter=new RoutePostListAdapter(MyRouteActivity.this,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DAOFactory.getCommunityServiceInstance().toArticlePostPage(MyRouteActivity.this,data,position);
//                ToastUtil.show(MyRouteActivity.this,data.get(position).get("routeId").toString());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyRouteActivity.this);
                builder.setItems(new String[]{"删除该记录"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final Integer routeid=Integer.parseInt( data.get(position).get("routeId").toString());
                        new Thread(){
                            @Override
                            public void run() {
                                TRControlPostTool controlPostTool=new TRControlPostTool("route","routeId",
                                        routeid);
                                controlPostTool.Delete();
                            }
                        }.start();
                        data.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }
}
