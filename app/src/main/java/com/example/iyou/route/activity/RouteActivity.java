package com.example.iyou.route.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.iyou.R;
import com.example.iyou.model.utils.DAOFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteActivity extends AppCompatActivity {

    private ListView listView;
    //    private List<Map<String,Object>> data;
    private List<Object> data;
    private RouteListAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        if(null!= AVUser.getCurrentUser()){
            data= DAOFactory.getRouteServiceInstance().getData();
            if(null==data){
                data=new ArrayList<Object>();
            }
        }
        else {
            data=new ArrayList<Object>();
        }
        adapter=new RouteListAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.setClass(RouteActivity.this,RouteMapActivity.class);
                intent.putExtra("bean", (Serializable) data.get(position));
                RouteActivity.this.startActivity(intent);
            }
        });
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
        setContentView(R.layout.activity_route);

        //设置工具栏
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("我的路线");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //设置listview
        listView=(ListView)findViewById(R.id.route_listview);
    }

}