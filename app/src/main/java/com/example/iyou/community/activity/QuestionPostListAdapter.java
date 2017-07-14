package com.example.iyou.community.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.example.iyou.MainActivity;
import com.example.iyou.R;
import com.example.iyou.my.activity.CircleImageView;
import com.example.iyou.my.activity.ContentMy;
import com.example.iyou.tool.QuestionPostServiceImplTool;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/5.
 * 问题贴listview的适配器
 */
public class QuestionPostListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;



    public QuestionPostListAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class ViewHolder {
        public TextView reply;
        public TextView question;
        public TextView username;
        public TextView time;
        public CircleImageView question_show_head;
        public TextView time_detail;
    }
    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.my_question_list_item, null);
            //获得组件，实例化组件
            holder.reply=(TextView)convertView.findViewById(R.id.my_question_reply);
            holder.question = (TextView) convertView.findViewById(R.id.my_question_name);
            holder.username = (TextView) convertView.findViewById(R.id.my_question_user_name);
            holder.time = (TextView) convertView.findViewById(R.id.my_question_time);
            holder.question_show_head = (CircleImageView) convertView.findViewById(R.id.question_show_head);
            holder.time_detail = (TextView) convertView.findViewById(R.id.my_question_time_two);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        if(data != null){
            QuestionPostServiceImplTool tool = new QuestionPostServiceImplTool(convertView ,data.get(position));
//            List<QuestionListViewItem> temp2 = DAOFactory.getQuestionPostServiceInstance().getReplyData(data.get(position));
            tool.getCommentCount();
        }


        //绑定数据
//        holder.reply.setText("回复数量"+(String) data.get(position).get("commentCount"));
//        String x = (String) data.get(position).get("commentCount");
        holder.question.setText((String) data.get(position).get("title"));
        holder.username.setText((String) data.get(position).get("account"));
        String timeTemp1 = (String) data.get(position).get("timestamp");
        holder.time.setText(timeTemp1.substring(0,10));
        holder.time_detail.setText(timeTemp1.substring(11,13)+"时"+timeTemp1.substring(14,16)+"分");
        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        userQuery.whereEqualTo(ContentMy.USER_NAME,holder.username.getText().toString()); //找到对应的用户
        final ViewHolder finalHolder = holder;
        userQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if(e == null){
                    if(list.size() != 0){
                        AVUser user= list.get(0);
                        AVFile file = user.getAVFile(ContentMy.USER_HEAD_IMAGE);
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, AVException e) {
                                finalHolder.question_show_head.setImageBitmap(MainActivity.Bytes2Bimap(bytes));
                            }
                        });
                    }
                }
            }
        });
        return convertView;
    }
}
