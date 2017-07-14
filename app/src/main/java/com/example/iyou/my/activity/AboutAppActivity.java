package com.example.iyou.my.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.iyou.R;

public class AboutAppActivity extends AppCompatActivity {

    private TextView aboutAppText;

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
        setContentView(R.layout.activity_about_app);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("关于软件");
        actionBar.setDisplayHomeAsUpEnabled(true);

        aboutAppText=(TextView)findViewById(R.id.aboutapp_text);
        aboutAppText.setText(getString(R.string.about_app));
    }
}
