package com.example.iyou.my.model.service.impl.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.iyou.R;
import com.example.iyou.my.activity.AboutAppActivity;
import com.example.iyou.my.activity.LoginActivity;
import com.example.iyou.my.activity.MyQuestionActivity;
import com.example.iyou.my.activity.MyReplyActivity;
import com.example.iyou.my.activity.MyResetActivity;
import com.example.iyou.my.activity.MyRouteActivity;
import com.example.iyou.my.model.service.MyService;
import com.example.iyou.route.activity.RouteActivity;
import com.example.iyou.tool.CircularAnim;

/**
 * Created by asus on 2017/1/4.
 */

public class MyServiceImpl implements MyService {

    public static  final int REQUESET_LOGIN_CODE = 0; //登录的请求码
    public static final int REQUEST_CHANGE_DATA = 1;//修改个人信息的请求码
    /*
     * 跳转到我的个人信息修改界面的Activity
     */
    @Override
    public void toResetPage(final Activity activity) {
        AVUser currentuser = AVUser.getCurrentUser();
        if(currentuser != null){

            LinearLayout reset_lL=(LinearLayout)activity.findViewById(R.id.my_reset_ll);
            CircularAnim.fullActivity(activity, reset_lL)
                    .colorOrImageRes(R.color.white)
                    .go(new CircularAnim.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd() {
                            Intent intent=new Intent();
                            intent.setClass(activity,MyResetActivity.class);
                            activity.startActivityForResult(intent,REQUEST_CHANGE_DATA);
                        }
                    });


        }else{
            Intent intent2Login = new Intent(activity.getApplicationContext(),LoginActivity.class);
            activity.startActivityForResult(intent2Login,REQUESET_LOGIN_CODE);
        }

    }

    /*
     * 跳转到我的足迹贴的Activity
     */
    @Override
    public void toMyRoutePostPage(Activity activity) {
        Intent intent=new Intent();
        intent.setClass(activity,MyRouteActivity.class);
        activity.startActivity(intent);

    }

    /*
     * 跳转到我的问题贴的Activity
     */
    @Override
    public void toMyQuestionPostPage(Activity activity) {
        Intent intent=new Intent();
        intent.setClass(activity,MyQuestionActivity.class);
        activity.startActivity(intent);
    }

    /*
     * 跳转到我的回复的Activity
     */
    @Override
    public void toMyReplyPage(Activity activity) {
        Intent intent=new Intent();
        intent.setClass(activity,MyReplyActivity.class);
        activity.startActivity(intent);
    }


    /*
     * 跳转到我的账号（账号登陆、切换）的Activity
     */
    @Override
    public void toExchangePage(Activity activity) {
        AVUser currentuser = AVUser.getCurrentUser();
        if(currentuser != null){
            Intent intent=new Intent();
            intent.setClass(activity,MyResetActivity.class);
            activity.startActivityForResult(intent,REQUEST_CHANGE_DATA);
        }else{
            Intent intent2Login = new Intent(activity.getApplicationContext(),LoginActivity.class);
            activity.startActivityForResult(intent2Login,REQUESET_LOGIN_CODE);
        }
    }

    /*
     * 跳转到关于软件介绍界面的Activity
     */
    @Override
    public void toAboutAppPage(Activity activity) {
        Intent intent=new Intent();
        intent.setClass(activity,AboutAppActivity.class);
        activity.startActivity(intent);
    }



    /*
    * 退出程序处理
    */
    @Override
    public boolean logOff(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("注意")
                .setMessage("你确定要退出账号吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AVUser.logOut();// 清除缓存用户对象
                        Toast.makeText(activity,"退出登录成功",Toast.LENGTH_SHORT).show();
                        TextView textView = (TextView) activity.findViewById(R.id.accountName);
                        textView.setText("请登录");

                        ImageView imageView = (ImageView) activity.findViewById(R.id.userHead1);
                        imageView.setImageResource(R.drawable.default_head_pic);

                        TextView textView1 = (TextView) activity.findViewById(R.id.route_num);
                        textView1.setText("0");
                        TextView textView2 = (TextView) activity.findViewById(R.id.quetion_num);
                        textView2.setText("0");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        return false;
    }


    /*
     * 跳转到我的自定义本地路线的Activity
     */
    @Override
    public void toRoutePage(Activity activity) {
        Intent intent=new Intent();
        intent.setClass(activity,RouteActivity.class);
        activity.startActivity(intent);
    }
}
