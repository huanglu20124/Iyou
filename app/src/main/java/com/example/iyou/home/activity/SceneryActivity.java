package com.example.iyou.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.iyou.R;
import com.example.iyou.tool.CustomImageView;
import com.example.iyou.tool.GoodView;

import java.io.Serializable;
import java.util.Map;

import static com.example.iyou.home.activity.HomeListAdapter.loadImageFromNetwork;

public class SceneryActivity extends AppCompatActivity {

    private Map<String, Object> bean;

    private CustomImageView sceneryImageView;
    private TextView sceneryNameTextView;
    private TextView sceneryAddressTextView;
    private TextView sceneryTicketTextView;
    private TextView sceneryDetailTextView;

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
        setContentView(R.layout.activity_scenery);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("景点介绍");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //获取传递数据bean对象：即listview指定item的相应的景点map对象
        bean = (Map<String, Object>) getIntent().getSerializableExtra(
                "bean");
        initView();

        //设置浮动加入购物车按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoodView goodView = new GoodView(SceneryActivity.this);
                goodView.setImage(loadImageFromNetwork((String) bean.get("sceneryImageUrl")));
                goodView.setDuration(3000);
                goodView.show(view);

                Intent intent=new Intent();
                intent.putExtra("bean", (Serializable) bean);
                intent.setAction(Intent.ACTION_EDIT);
                view.getContext().sendBroadcast(intent);
            }
        });
    }

    private void initView(){
        sceneryImageView=(CustomImageView)findViewById(R.id.scenery_imageview);
        sceneryNameTextView=(TextView)findViewById(R.id.scenery_name_textview);
        sceneryAddressTextView=(TextView)findViewById(R.id.scenery_address_textview);
        sceneryTicketTextView=(TextView)findViewById(R.id.scenery_ticket_textview);
        sceneryDetailTextView=(TextView)findViewById(R.id.scenery_detail_textview);

        sceneryImageView.setImageUrl((String) bean.get("sceneryImageUrl"));
        sceneryNameTextView.setText(bean.get("sceneryName").toString());
        sceneryAddressTextView.setText(bean.get("sceneryAddress").toString());
        sceneryTicketTextView.setText(bean.get("scenerySummary").toString());
        sceneryDetailTextView.setText(bean.get("ticketSummary").toString());
    }
}
