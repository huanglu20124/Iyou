package com.example.iyou.my.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.iyou.R;
import com.example.iyou.community.activity.QuestionPostContentActivity;
import com.example.iyou.community.activity.ReplyListAdapter;
import com.example.iyou.community.activity.RoutePostContentActivity;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.route.model.bean.ToastUtil;
import com.example.iyou.tool.CommentToSourceTool;
import com.example.iyou.tool.TRControlPostTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyReplyActivity extends AppCompatActivity {

    private ListView listView;
    private List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
    private List<Map<String,Object>> sourceData=new ArrayList<Map<String,Object>>();

    private String replyType;

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
        setContentView(R.layout.activity_my_reply);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("我的回复");
        actionBar.setDisplayHomeAsUpEnabled(true);


        listView=(ListView)findViewById(R.id.my_reply_listView);
        data= DAOFactory.getMyReplyServiceInstance().getMyReplyData(this);
    }

    public void doContent(Object obj){
        data=(List<Map<String,Object>>)obj;
        final ReplyListAdapter adapter=new ReplyListAdapter(getBaseContext(),data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                replyType=data.get(position).get("type").toString();
                if(replyType.equals("Route")){
                    CommentToSourceTool tool=new CommentToSourceTool("routeForCommentId",
                            data.get(position).get("commentId").toString(),MyReplyActivity.this);
                    tool.GetInfoById();
                }
                else{
                    CommentToSourceTool tool=new CommentToSourceTool("questionForCommentId",
                            data.get(position).get("commentId").toString(),MyReplyActivity.this);
                    tool.GetInfoById();
                }


            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyReplyActivity.this);
                builder.setItems(new String[]{"删除该记录"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final Integer replyid=Integer.parseInt( data.get(position).get("commentId").toString());
                        new Thread(){
                            @Override
                            public void run() {
                                TRControlPostTool controlPostTool=new TRControlPostTool("comment","commentId",
                                        replyid);
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

    public void doContentReplySource(Object obj){
        Map<String,Object> map= (Map<String, Object>) obj;
        Intent intent=new Intent();
        if(replyType.equals("Route")){
            sourceData= (List<Map<String, Object>>) map.get("routeForCommentId");
            intent.setClass(MyReplyActivity.this, RoutePostContentActivity.class);
            intent.putExtra("bean", (Serializable) sourceData.get(0));
            MyReplyActivity.this.startActivity(intent);
        }
        if(replyType.equals("Question")) {
            sourceData = (List<Map<String, Object>>) map.get("questionForCommentId");
            intent.setClass(MyReplyActivity.this, QuestionPostContentActivity.class);
            intent.putExtra("bean", (Serializable) sourceData.get(0));
            MyReplyActivity.this.startActivity(intent);
        }
    }
}
