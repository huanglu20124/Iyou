package com.example.iyou.home.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iyou.R;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.tool.SearchActivityTool;

import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private TextView sceneryTextView;
    private TextView routePostTextView;
    private TextView questionPostTextView;
    private ListView listView;
    private SearchView searchView;
    private Button searchBtn;

    protected List<Map<String,Object>> sceneryList;
    protected List<Map<String,Object>> routePostList;
    protected List<Map<String,Object>> questionPostList;

    private String keyWord;

    /*
     * 用于标起选择了那种搜索
     * 景点搜索代表：1
     * 足迹贴搜索代表：2
     * 问题贴搜索代表：3
     */
    private int flag=1;

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
        setContentView(R.layout.activity_search);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("搜索景点");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //初始获取数据源
        //loadDatas();

        //初始化界面控件
        initView();
//        sceneryTextView.setTextColor(getResources().getColor(R.color.colorMyFontNum));

        setEvents();


    }

    private void loadDatas(){

    }

    private void initView(){
//        sceneryTextView=(TextView)findViewById(R.id.scenery_textview);
        searchBtn=(Button)findViewById(R.id.search_btn);
//        routePostTextView=(TextView)findViewById(R.id.routepost_textview);
//        questionPostTextView=(TextView)findViewById(R.id.questionpost_textview);
        searchView=(SearchView)findViewById(R.id.searchView);
        listView=(ListView)findViewById(R.id.search_listview);
    }

    private void setEvents(){
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);

        //设置搜索按钮事件
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyWord==null || keyWord.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "请输入关键字", Toast.LENGTH_SHORT).show();
                }
                else {
                    //从服务器中获取数据源
//                    loadDatas();
//                    List<Map<String, Object>> obj = searchItem(keyWord, flag);
//                    updateLayout(obj);
                    searchItem(keyWord, flag);
                }


            }
        });

//        sceneryTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sceneryTextView.setTextColor(getResources().getColor(R.color.colorMyFontNum));
//                routePostTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                questionPostTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                flag=1;
//            }
//        });
//
//        routePostTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sceneryTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                routePostTextView.setTextColor(getResources().getColor(R.color.colorMyFontNum));
//                questionPostTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                flag=2;
//            }
//        });
//
//        questionPostTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sceneryTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                routePostTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                questionPostTextView.setTextColor(getResources().getColor(R.color.colorMyFontNum));
//                flag=3;
//            }
//        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        List<Map<String,Object>> obj = searchItem(newText,flag);
//        updateLayout(obj);
        keyWord=newText;
        return false;
    }

    public void searchItem(String keyword,int flag) {
        switch (flag){
            case 1:
                //获取景点搜索信息
                SearchActivityTool tool = new SearchActivityTool(keyword,this);
                tool.loadDatas();
                break;
            case 2:
                //直接访问服务器
                break;
            case 3:
                //直接访问服务器
                break;
        }
    }

    // 更新数据
    public void updateLayout(final List<Map<String,Object>> obj) {
        SimpleAdapter adapter = new SimpleAdapter(this,obj,R.layout.search_list_item,
                new String[]{"image","sceneryName"},
                new int[]{R.id.search_item_imageview,R.id.search_item_textview});
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (flag){
                    case 1:
                        //景点点击列表事件
                        DAOFactory.getHomeServiceInstance().displayDetail(SearchActivity.this,obj,position);
                        break;
                    case 2:
                        //足迹贴点击列表事件
                        break;
                    case 3:
                        //问题贴点击列表事件
                        break;

                }
            }
        });
    }

    //景点搜索回调方法
    public void doSceneryContent(Object obj){
        //将访问服务器获取新的数据保存在一个list里面
        sceneryList=(List<Map<String, Object>>)obj;
        updateLayout(sceneryList);
    }

}