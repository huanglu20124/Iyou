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
import com.example.iyou.community.activity.QuestionPostListAdapter;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.tool.TRControlPostTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyQuestionActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_my_question);


        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("我的问题");
        actionBar.setDisplayHomeAsUpEnabled(true);


        listView=(ListView)findViewById(R.id.my_question_listview);
        DAOFactory.getMyQuestionPostServiceInstance().getMyQuestionPostData(this);
        QuestionPostListAdapter adapter=new QuestionPostListAdapter(getBaseContext(),data);
        listView.setAdapter(adapter);
    }

    public void doContent(Object obj){

        data=(List<Map<String,Object>>)obj;

        final QuestionPostListAdapter adapter=new QuestionPostListAdapter(getBaseContext(),data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DAOFactory.getCommunityServiceInstance().toQuestionPostAnswerPage(MyQuestionActivity.this,data,position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyQuestionActivity.this);
                builder.setItems(new String[]{"删除该记录"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final Integer questionid=Integer.parseInt( data.get(position).get("questionId").toString());
                        new Thread(){
                            @Override
                            public void run() {
                                TRControlPostTool controlPostTool=new TRControlPostTool("question","questionId",
                                        questionid);
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
