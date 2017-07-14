package com.example.iyou.home.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iyou.R;
import com.example.iyou.my.activity.CircleImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by cyhaha on 2017/1/16.
 */

public class SceneryCarListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public SceneryCarListAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class ViewHolder {
        public CircleImageView sceneryImage;
        public TextView sceneryName;
        public ImageButton sceneryDeleteBtn;
        public ImageView itemArrow;
    }
    //不同颜色箭头
    public static final int[] arrow = new int[]{
        R.drawable.arrow_one,R.drawable.arrow_two,
        R.drawable.arrow_three,R.drawable.arrow_four,
        R.drawable.arrow_five
    };
    //对应颜色
    public static final int[] arrow_color = new int[]{
       R.color.c_1,R.color.c_2,R.color.c_3,
            R.color.c_4,R.color.c_5
    };

    @Override
    public int getCount() {
        return data.size();
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

            convertView = layoutInflater.inflate(R.layout.scenery_car_list_item, null);
            //获得组件，实例化组件
            holder.sceneryImage = (CircleImageView) convertView.findViewById(R.id.scenery_place_icon);
            holder.sceneryName = (TextView) convertView.findViewById(R.id.scenery_name);
            holder.sceneryDeleteBtn = (ImageButton) convertView.findViewById(R.id.scenery_delete_btn);
            holder.sceneryDeleteBtn.setTag(position);
            holder.itemArrow = (ImageView) convertView.findViewById(R.id.itemArrow);
            holder.itemArrow.setTag(position);

            convertView.setTag(holder);
            CircleImageView imageView;

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //绑定箭头
        if(position != data.size() - 1)
        holder.itemArrow.setImageResource(arrow[position%5]);


        //绑定数据
        holder.sceneryImage.setImageBitmap(getBitmap((String) data.get(position).get("sceneryImageUrl")));
        holder.sceneryName.setText((String) data.get(position).get("sceneryName"));
        holder.sceneryName.setTextColor(arrow_color[position%5]);
        holder.sceneryImage.setBorderColor(R.color.colorBaseYellow);
//        holder.sceneryImage.setBorderWidth(10); //这里有毒，设置不了颜色
        holder.sceneryDeleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                new AlertDialog.Builder(context)
                        .setTitle("注意：")
                        .setMessage("你确定要从景点车中删除该景点吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //从景点车删除制定景点
                                int i=(Integer)v.getTag();
                                data.remove(i);
                                SceneryCarListAdapter.this.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
        return convertView;
    }

    public Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

}
