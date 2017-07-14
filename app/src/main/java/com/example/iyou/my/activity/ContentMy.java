package com.example.iyou.my.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetDataCallback;
import com.example.iyou.MainActivity;
import com.example.iyou.R;
import com.example.iyou.model.utils.DAOFactory;

/**
 * Created by asus on 2017/1/13.
 */
public class ContentMy {

    private Context context;
    private View rootView;

    //AVuser存储的字段
    public static final String USER_NAME = "username";
    public static final String USER_SIGNATURE = "signature";
    public static final String USER_HEAD_IMAGE = "user_head_image";
    public static final String USER_HAVE_GOOD = "user_have_good";
//    public static final String USER_AGGREE_COUNT = "user_agree_count";
    public static final String USER_ROUTE_NUM = "user_route_num";
    public static final String USER_QUESTION_NUM = "user_question_num";
    public static final String USER_COMMENT_COUNT= "user_comment_count";
    public static final String IF_NEW_LOG= "if_new_log";

    public ContentMy(final Activity activity, final Context context, final View rootView) {
        this.context = context;


        //根据登录情况初始化
        final AVUser currentuser = AVUser.getCurrentUser();
        TextView tv = (TextView) rootView.findViewById(R.id.accountName);
        final ImageView iv = (ImageView) rootView.findViewById(R.id.userHead1);
        if(currentuser != null){
            tv.setText(currentuser.get(USER_NAME).toString());
            AVFile file = currentuser.getAVFile("user_head_image");
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    iv.setImageBitmap(MainActivity.Bytes2Bimap(bytes));
                }
            });

            //设置帖子数量
            AVUser avUser = AVUser.getCurrentUser();
            TextView textViewRoute = (TextView) rootView.findViewById(R.id.route_num);
            TextView textViewQuestion = (TextView)rootView.findViewById(R.id.quetion_num);
            TextView textViewReply = (TextView) rootView.findViewById(R.id.reply_num);
            textViewRoute.setText(String.valueOf(avUser.get(ContentMy.USER_ROUTE_NUM)));
            textViewQuestion.setText(String.valueOf(avUser.get(ContentMy.USER_QUESTION_NUM)));
            textViewReply.setText(String.valueOf(avUser.get(ContentMy.USER_COMMENT_COUNT)));


        }else{
            tv.setText("点击登录");
        }
        FrameLayout foot_fL=(FrameLayout)rootView.findViewById(R.id.my_footprint_fl);
        foot_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AVUser.getCurrentUser() == null){
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                }else
                DAOFactory.getMyServiceInstance().toMyRoutePostPage(activity);
            }
        });
        FrameLayout question_fL=(FrameLayout)rootView.findViewById(R.id.my_question_fl);
        question_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AVUser.getCurrentUser() == null){
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                }else
                DAOFactory.getMyServiceInstance().toMyQuestionPostPage(activity);
            }
        });
        FrameLayout reply_fL=(FrameLayout)rootView.findViewById(R.id.my_reply_fl);
        reply_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AVUser.getCurrentUser() == null){
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                }else
                DAOFactory.getMyServiceInstance().toMyReplyPage(activity);
            }
        });
        FrameLayout login_fL=(FrameLayout)rootView.findViewById(R.id.userData_fl);
        login_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAOFactory.getMyServiceInstance().toExchangePage(activity);
            }
        });
        FrameLayout route_fL=(FrameLayout)rootView.findViewById(R.id.route_fl);
        route_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null==AVUser.getCurrentUser()){
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                }
                DAOFactory.getMyServiceInstance().toRoutePage(activity);
            }
        });
        LinearLayout reset_lL=(LinearLayout)rootView.findViewById(R.id.my_reset_ll);
        reset_lL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAOFactory.getMyServiceInstance().toResetPage(activity);
            }
        });
        FrameLayout aboutapp_fL=(FrameLayout)rootView.findViewById(R.id.my_aboutapp_fl);
        aboutapp_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAOFactory.getMyServiceInstance().toAboutAppPage(activity);
            }
        });
        FrameLayout exit_fL=(FrameLayout)rootView.findViewById(R.id.my_exit_fl);
        exit_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAOFactory.getMyServiceInstance().logOff(activity);
            }
        });
    }

}
