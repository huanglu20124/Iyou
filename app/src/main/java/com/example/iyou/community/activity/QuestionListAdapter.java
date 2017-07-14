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
import com.example.iyou.tool.CustomImageView;
import com.example.iyou.tool.Image;
import com.example.iyou.tool.NineGridlayout;
import com.example.iyou.tool.ScreenTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017/1/14.
 */
public class QuestionListAdapter extends BaseAdapter {

    private List<QuestionListViewItem> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public QuestionListAdapter(Context context, List<QuestionListViewItem> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }


    public final class ViewHolderFirstType {
        public CircleImageView head;
        public TextView content;
        public TextView account;
        public TextView timestamp;
        public NineGridlayout ivMore;
        public CustomImageView ivOne;
    }

    public final class ViewHolderSecondType {
        public CircleImageView head;
        public TextView content;
        public TextView account;
        public TextView timestamp;
        public NineGridlayout ivMore;
        public CustomImageView ivOne;
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
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuestionListViewItem qusetionListItem=data.get(position);
        int Type = getItemViewType(position);

        ViewHolderFirstType qusetionViewHolder=null;
        ViewHolderSecondType answerViewHolder=null;

        if (convertView == null) {
            switch (Type) {
                case 0:
                    qusetionViewHolder=new ViewHolderFirstType();
                    convertView = layoutInflater.inflate(R.layout.question_list_item, null);

                    qusetionViewHolder.head=(CircleImageView) convertView.findViewById(R.id.community_question_head);
                    qusetionViewHolder.account=(TextView)convertView.findViewById(R.id.community_question_username);
                    qusetionViewHolder.timestamp=(TextView)convertView.findViewById(R.id.community_question_time);
                    qusetionViewHolder.content=(TextView)convertView.findViewById(R.id.community_question_text);
                    qusetionViewHolder.ivMore = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
                    qusetionViewHolder.ivOne = (CustomImageView) convertView.findViewById(R.id.iv_oneiamge);

                    bindQuestionData(qusetionViewHolder, qusetionListItem);
                    convertView.setTag(R.id.my_reset_tag,qusetionViewHolder);
                    break;

                case 1:
                    answerViewHolder=new ViewHolderSecondType();
                    convertView = layoutInflater.inflate(R.layout.comment_reply_list_item, null);

                    answerViewHolder.head=(CircleImageView) convertView.findViewById(R.id.community_comment_head);
                    answerViewHolder.account=(TextView)convertView.findViewById(R.id.community_comment_username);
                    answerViewHolder.timestamp=(TextView)convertView.findViewById(R.id.community_comment_time);
                    answerViewHolder.content=(TextView)convertView.findViewById(R.id.community_comment_text);
                    answerViewHolder.ivMore = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
                    answerViewHolder.ivOne = (CustomImageView) convertView.findViewById(R.id.iv_oneimage);

                    bindAnswerData(answerViewHolder,qusetionListItem);
                    convertView.setTag(R.id.action_car,answerViewHolder);
                    break;
            }



        } else {
            switch (Type){
                case 0:
                    qusetionViewHolder = (ViewHolderFirstType) convertView.getTag(R.id.my_reset_tag);
                    bindQuestionData(qusetionViewHolder, qusetionListItem);
                    break;
                case 1:
                    answerViewHolder = (ViewHolderSecondType) convertView.getTag(R.id.action_car);
                    bindAnswerData(answerViewHolder,qusetionListItem);
                    break;
            }
        }

        if(convertView == null){
            System.out.println("caonima");
        }
        else{
            System.out.println("caonima2hao");
            return convertView;
        }

        return convertView;
    }


    //缁戝畾闂鏁版嵁
    private void bindQuestionData(final ViewHolderFirstType qusetionViewHolder, QuestionListViewItem qusetionListItem){
        //qusetionViewHolder.head.setBackgroundResource((Integer) qusetionListItem.map.get("head"));
        qusetionViewHolder.account.setText((String) qusetionListItem.map.get("account"));
        String temp = (String) qusetionListItem.map.get("timestamp");
        qusetionViewHolder.timestamp.setText(temp.substring(0,16));
        qusetionViewHolder.content.setText((String) qusetionListItem.map.get("content"));

        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        userQuery.whereEqualTo(ContentMy.USER_NAME,qusetionViewHolder.account.getText().toString()); //找到对应的用户
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
                                qusetionViewHolder.head.setImageBitmap(MainActivity.Bytes2Bimap(bytes));
                            }
                        });
                    }
                }
            }
        });


        List<Image> itemList=null;
        //获取图片数据
        if(qusetionListItem.map.get("imageUrl") != null) {
            if(itemList == null) {
                itemList = new ArrayList<Image>();
            }
            Image newI = new Image(qusetionListItem.map.get("imageUrl").toString(), 150, 150);
            itemList.add(newI);
        }

        if (itemList==null || itemList.isEmpty()) {
            qusetionViewHolder.ivMore.setVisibility(View.GONE);
            qusetionViewHolder.ivOne.setVisibility(View.GONE);
        } else if (itemList.size() == 1) {
            qusetionViewHolder.ivMore.setVisibility(View.GONE);
            qusetionViewHolder.ivOne.setVisibility(View.VISIBLE);

            handlerQuestionOneImage(qusetionViewHolder, itemList.get(0));
        } else {
            qusetionViewHolder.ivMore.setVisibility(View.VISIBLE);
            qusetionViewHolder.ivOne.setVisibility(View.GONE);

            qusetionViewHolder.ivMore.setImagesData(itemList);
        }


    }

    //缁戝畾鍥炵瓟鏁版嵁
    private void bindAnswerData(final ViewHolderSecondType answerViewHolder, QuestionListViewItem qusetionListItem){
        //answerViewHolder.head.setBackgroundResource((Integer) qusetionListItem.map.get("head"));
        answerViewHolder.account.setText((String) qusetionListItem.map.get("account"));
        String temp = (String) qusetionListItem.map.get("timestamp");
        answerViewHolder.timestamp.setText(temp.substring(0,16));
        answerViewHolder.content.setText((String) qusetionListItem.map.get("content"));

        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        userQuery.whereEqualTo(ContentMy.USER_NAME,answerViewHolder.account.getText().toString()); //找到对应的用户
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
                                answerViewHolder.head.setImageBitmap(MainActivity.Bytes2Bimap(bytes));
                            }
                        });
                    }
                }
            }
        });


        List<Image> itemList=null;
        //获取图片数据
        if(qusetionListItem.map.get("imageUrl") != null) {
            if(itemList == null) {
                itemList = new ArrayList<Image>();
            }
            Image newI = new Image(qusetionListItem.map.get("imageUrl").toString(), 150, 150);
            itemList.add(newI);
        }


        if (itemList==null || itemList.isEmpty()) {
            answerViewHolder.ivMore.setVisibility(View.GONE);
            answerViewHolder.ivOne.setVisibility(View.GONE);
        } else if (itemList.size() == 1) {
            answerViewHolder.ivMore.setVisibility(View.GONE);
            answerViewHolder.ivOne.setVisibility(View.VISIBLE);

            handlerAnswerOneImage(answerViewHolder, itemList.get(0));
        } else {
            answerViewHolder.ivMore.setVisibility(View.VISIBLE);
            answerViewHolder.ivOne.setVisibility(View.GONE);

            answerViewHolder.ivMore.setImagesData(itemList);
        }
    }


    //澶勭悊闂鐨勪竴寮犲浘鐗囨椂鐨勫竷灞€鎯呭喌
    private void handlerQuestionOneImage(ViewHolderFirstType viewHolder, Image image) {
        int totalWidth;
        int imageWidth;
        int imageHeight;
        ScreenTools screentools = ScreenTools.instance(context);
        totalWidth = screentools.getScreenWidth() - screentools.dip2px(80);
        imageWidth = screentools.dip2px(image.getWidth());
        imageHeight = screentools.dip2px(image.getHeight());
        if (image.getWidth() <= image.getHeight()) {
            if (imageHeight > totalWidth) {
                imageHeight = totalWidth;
                imageWidth = (imageHeight * image.getWidth()) / image.getHeight();
            }
        } else {
            if (imageWidth > totalWidth) {
                imageWidth = totalWidth;
                imageHeight = (imageWidth * image.getHeight()) / image.getWidth();
            }
        }
        ViewGroup.LayoutParams layoutparams = viewHolder.ivOne.getLayoutParams();
        layoutparams.height = imageHeight;
        layoutparams.width = imageWidth;
        viewHolder.ivOne.setLayoutParams(layoutparams);
        viewHolder.ivOne.setClickable(true);
        viewHolder.ivOne.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        viewHolder.ivOne.setImageUrl(image.getUrl());

    }

    //澶勭悊鍥炵瓟鐨勪竴寮犲浘鐗囨椂鐨勫竷灞€鎯呭喌
    private void handlerAnswerOneImage(ViewHolderSecondType viewHolder, Image image) {
        int totalWidth;
        int imageWidth;
        int imageHeight;
        ScreenTools screentools = ScreenTools.instance(context);
        totalWidth = screentools.getScreenWidth() - screentools.dip2px(80);
        imageWidth = screentools.dip2px(image.getWidth());
        imageHeight = screentools.dip2px(image.getHeight());
        if (image.getWidth() <= image.getHeight()) {
            if (imageHeight > totalWidth) {
                imageHeight = totalWidth;
                imageWidth = (imageHeight * image.getWidth()) / image.getHeight();
            }
        } else {
            if (imageWidth > totalWidth) {
                imageWidth = totalWidth;
                imageHeight = (imageWidth * image.getHeight()) / image.getWidth();
            }
        }
        ViewGroup.LayoutParams layoutparams = viewHolder.ivOne.getLayoutParams();
        layoutparams.height = imageHeight;
        layoutparams.width = imageWidth;
        viewHolder.ivOne.setLayoutParams(layoutparams);
        viewHolder.ivOne.setClickable(true);
        viewHolder.ivOne.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        viewHolder.ivOne.setImageUrl(image.getUrl());

    }

}

