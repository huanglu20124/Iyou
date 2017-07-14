package com.example.iyou.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.iyou.R;
import com.example.iyou.model.utils.DAOFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class QuestionPostContentActivity extends AppCompatActivity {

    private ListView listView;
    private List<QuestionListViewItem> data;

    //判断回答刷新
    public static boolean updateFlag=false;

    private LinearLayout bottomBtn;

    //传过来的bean数据
    private Map<String,Object> bean;

    @Override
    protected void onResume() {
        super.onResume();
        if(updateFlag){
            DAOFactory.getQuestionPostServiceInstance().getReplyData(this,bean);
            updateFlag=false;
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
        setContentView(R.layout.activity_answ_question_content);


        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("问题正文");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //获取传递数据bean对象：即listview指定item的相应的景点map对象
        bean = (Map<String, Object>) getIntent().getSerializableExtra(
                "bean");


        listView=(ListView)findViewById(R.id.community_answer_question_listview);
        DAOFactory.getQuestionPostServiceInstance().getReplyData(this,bean);


        bottomBtn=(LinearLayout) findViewById(R.id.community_answer_btn);
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(QuestionPostContentActivity.this, EditAnswerActivity.class);
                intent.putExtra("bean", (Serializable) bean);
                QuestionPostContentActivity.this.startActivity(intent);
            }
        });
    }

    public void doContent(Object object){
        //设置数据源
        data=(List<QuestionListViewItem>)object;
        //设置布局
        QuestionListAdapter adapter=new QuestionListAdapter(getBaseContext(),data);
        listView.setDividerHeight(5);
        listView.setDivider(getResources().getDrawable(R.color.divider_color));
        listView.setAdapter(adapter);
    }
}
