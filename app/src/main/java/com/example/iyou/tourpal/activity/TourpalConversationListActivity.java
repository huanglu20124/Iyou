package com.example.iyou.tourpal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.iyou.R;

public class TourpalConversationListActivity extends AppCompatActivity {

    //返回键事件
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
        setContentView(R.layout.activity_tourpal_conversation_list);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("通讯录");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
