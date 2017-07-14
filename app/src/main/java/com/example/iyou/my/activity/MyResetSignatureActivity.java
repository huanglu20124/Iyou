package com.example.iyou.my.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVUser;
import com.example.iyou.R;

public class MyResetSignatureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reset_signature);
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("修改签名");
        actionBar.setDisplayHomeAsUpEnabled(true);

        final EditText editText = (EditText) findViewById(R.id.changeSignature);
        if(AVUser.getCurrentUser().get(ContentMy.USER_SIGNATURE) != null)
        editText.setText(AVUser.getCurrentUser().get(ContentMy.USER_SIGNATURE).toString());
        findViewById(R.id.submitSignature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText() != null)
                AVUser.getCurrentUser().put(ContentMy.USER_SIGNATURE,editText.getText().toString());
                finish();
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
