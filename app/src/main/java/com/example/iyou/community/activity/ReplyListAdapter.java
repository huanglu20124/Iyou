package com.example.iyou.community.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.iyou.R;
import com.example.iyou.my.activity.CircleImageView;
import com.example.iyou.tool.CustomImageView;
import com.example.iyou.tool.Image;
import com.example.iyou.tool.NineGridlayout;
import com.example.iyou.tool.ScreenTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/11.
 */
public class ReplyListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public ReplyListAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class ViewHolder {
        public CircleImageView head;
        public TextView username;
        public TextView time;
        //public LinearLayout textlinlayout;
        public TextView text;
        public FrameLayout viewfralayout;
        public TextView floor;
        public Button replybtn;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //获取图片数据
        String imageUrl= (String) data.get(position).get("imageUrl");
        List<Image> itemList=null;
        if(imageUrl==null || imageUrl.equals("NULL")){

        }
        else {
            Image iamge=new Image(imageUrl,150, 150);
            itemList = new ArrayList<>();
            itemList.add(iamge);
        }

        if (convertView == null ) {

            holder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.my_reply_list_item, null);
            //获得组件，实例化组件
            holder.username = (TextView) convertView.findViewById(R.id.my_reply_username);
            holder.time = (TextView) convertView.findViewById(R.id.my_reply_time);
            //holder.textlinlayout=(LinearLayout)convertView.findViewById(R.id.my_route_text_linlayout);
            holder.text=(TextView)convertView.findViewById(R.id.my_reply_text);
            holder.floor=(TextView)convertView.findViewById(R.id.my_reply_floor);
//            holder.replybtn=(Button)convertView.findViewById(R.id.my_reply_btn);
            holder.ivMore = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
            holder.ivOne = (CustomImageView) convertView.findViewById(R.id.iv_oneimage);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //绑定数据
        //holder.head.setBackgroundResource((Integer) data.get(position).get("head"));
        holder.username.setText((String) data.get(position).get("account"));
        holder.time.setText((String) data.get(position).get("timestamp"));
        holder.text.setText((String) data.get(position).get("content"));
        holder.floor.setText((String)data.get(position).get("floor"));



        if (itemList==null || itemList.isEmpty()) {
            holder.ivMore.setVisibility(View.GONE);
            holder.ivOne.setVisibility(View.GONE);
        } else if (itemList.size() == 1) {
            holder.ivMore.setVisibility(View.GONE);
            holder.ivOne.setVisibility(View.VISIBLE);

            handlerOneImage(holder, itemList.get(0));
        } else {
            holder.ivMore.setVisibility(View.VISIBLE);
            holder.ivOne.setVisibility(View.GONE);

            holder.ivMore.setImagesData(itemList);
        }

        return convertView;
    }

    //处理一张图片时的布局情况
    private void handlerOneImage(ViewHolder viewHolder, Image image) {
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
