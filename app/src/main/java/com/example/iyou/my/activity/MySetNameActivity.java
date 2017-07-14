package com.example.iyou.my.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.iyou.R;

public class MySetNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_set_name);
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("修改昵称");
        actionBar.setDisplayHomeAsUpEnabled(true);


        final EditText editText = (EditText) findViewById(R.id.changeName);
        editText.setText(AVUser.getCurrentUser().getUsername());

        findViewById(R.id.submitName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText() == null){
                    Toast.makeText(MySetNameActivity.this,"用户名不能为空！",Toast.LENGTH_SHORT).show();
                }else{
                    AVUser.getCurrentUser().setUsername(editText.getText().toString());
                    AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e != null){
                                Toast.makeText(MySetNameActivity.this,"用户名已经存在",Toast.LENGTH_SHORT).show();
                            }else{
                                finish();
                            }
                        }
                    });
                }
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


}
