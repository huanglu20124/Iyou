package com.example.iyou;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.example.iyou.tourpal.model.bean.CustomUserProvider;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by cyhaha on 2017/1/16.
 */

public class MyLeanCloudApp extends Application {

    private final String APP_ID = "wkWL7Ej6O7uUHk6Stba1Wcfc-gzGzoHsz";
    private final String APP_KEY = "g9G682YCfJl0L4BAixYSFHEc";

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"wkWL7Ej6O7uUHk6Stba1Wcfc-gzGzoHsz","g9G682YCfJl0L4BAixYSFHEc");
        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
        LCChatKit.getInstance().init(getApplicationContext(), APP_ID, APP_KEY);
    }
}