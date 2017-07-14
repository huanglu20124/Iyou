package com.example.iyou.my.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.example.iyou.R;

public class MyForgetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_forget);
        final EditText editText = (EditText) findViewById(R.id.resetByEmail);
        findViewById(R.id.submitByEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyForgetActivity.this);
                builder.setTitle("提示信息").setMessage("点击“确定”后，系统将会发送一封邮件到您的邮箱用于修改密码")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AVUser.requestPasswordResetInBackground(editText.getText().toString(), new RequestPasswordResetCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            Toast.makeText(MyForgetActivity.this,"邮件已发送",Toast.LENGTH_SHORT).show();
                                            v.setClickable(false);
                                        } else {
                                            Toast.makeText(MyForgetActivity.this,"所填写的邮箱地址错误",Toast.LENGTH_SHORT).show();
                                            v.setClickable(false);
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("取消",null).show();
                try {
                    Thread.sleep(2000);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
