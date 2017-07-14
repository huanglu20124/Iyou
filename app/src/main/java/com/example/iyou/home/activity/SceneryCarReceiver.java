package com.example.iyou.home.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by cyhaha on 2017/1/16.
 */

public class SceneryCarReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Map<String,Object> bean=(Map<String,Object>)intent.getSerializableExtra("bean");
        Toast.makeText(context,bean.get("sceneryName")+"已添加到景点车",Toast.LENGTH_SHORT).show();
        SceneryCarActivity.dataRecource.add(bean);

    }
}
