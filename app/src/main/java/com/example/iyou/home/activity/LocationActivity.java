package com.example.iyou.home.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iyou.Constants;
import com.example.iyou.R;
import com.example.iyou.home.model.bean.Friend;
import com.example.iyou.home.model.bean.MyLetterView;
import com.example.iyou.tool.CharacterParser;
import com.example.iyou.tool.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private ListView list_friends;
    private TextView dialog;
    private MyLetterView right_letter;

    private List<Friend> friends;

    private PinyinComparator pinyinComparator;

    private CharacterParser characterParser;

    public android.support.v7.app.ActionBar actionBar;
    public Friend currentLocation=new Friend();

    @Override
    protected void onPause() {
        super.onPause();
        //将地位信息保存在缓存里面
        SharedPreferences.Editor sharedata = getSharedPreferences("data", 0).edit();
        sharedata.putString("locationcity",currentLocation.getName());
        sharedata.commit();
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
        setContentView(R.layout.activity_location);

        //设置工具栏，返回按钮
        actionBar=this.getSupportActionBar();
        actionBar.setTitle("定位");
        //设置当前位置
        currentLocation.setName(Constants.CURRENT_CITY);
        actionBar.setSubtitle(currentLocation.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);

        initData();
        initView();
    }

    //获取城市数据
    private void initData() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        //strings文件里直接获取
        String[] names = getResources().getStringArray(R.array.names);
        friends = new ArrayList<Friend>();
        for (int i = 0; i < names.length; i++) {
            Friend f = new Friend();
            f.setName(names[i]);
            String pinyin = characterParser.getSelling(names[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                f.setSortLetters(sortString.toUpperCase());
            } else {
                f.setSortLetters("#");
            }
            friends.add(f);
        }
        Collections.sort(friends, pinyinComparator);
    }

    //定义布局视图
    private void initView() {
        list_friends = (ListView) findViewById(R.id.list_friends);
        dialog = (TextView) findViewById(R.id.dialog);
        right_letter = (MyLetterView) findViewById(R.id.right_letter);
        right_letter.setTextDialog(dialog);
        final FriendsAdapter adapter = new FriendsAdapter(this,friends);
        right_letter
                .setOnTouchingLetterChangedListener(new MyLetterView.OnTouchingLetterChangedListener() {
                    @Override
                    public void onTouchingLetterChanged(String s) {
                        // 该字母首次出现的位置
                        int position = adapter.getPositionForSection(s.charAt(0));
                        if (position != -1) {
                            list_friends.setSelection(position);
                        }
                    }
                });

        list_friends.setAdapter(adapter);

        list_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentLocation=friends.get(position);
                Toast.makeText(LocationActivity.this, currentLocation.getName(), Toast.LENGTH_SHORT).show();
                Constants.CURRENT_CITY=currentLocation.getName();
                actionBar.setSubtitle(Constants.CURRENT_CITY);
                finish();
            }
        });
    }
}
