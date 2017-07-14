package com.example.iyou.home.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iyou.R;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.tool.CustomImageView;
import com.example.iyou.tool.GoodView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 * 作为主页景点列表显示的listview的适配器
 */
public class HomeListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public HomeListAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class ViewHolder {
        public CustomImageView img;
        public TextView name;
        public TextView place;
        public TextView money;
        public ImageView viewBtn;
    }

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

            convertView = layoutInflater.inflate(R.layout.home_view_list, null);
            //获得组件，实例化组件
            holder.img = (CustomImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
//            holder.place = (TextView) convertView.findViewById(R.id.place);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.viewBtn = (ImageView) convertView.findViewById(R.id.view_btn);
            holder.viewBtn.setTag(position);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //绑定数据
        holder.img.setImageUrl((String)data.get(position).get("sceneryImageUrl"));
        holder.name.setText((String) data.get(position).get("sceneryName"));
//        holder.place.setText((String) data.get(position).get("city"));
        String temp = (String) data.get(position).get("scenerySummary");
        if(temp.length() > 120)
        holder.money.setText(temp.substring(0,120)+"...");
        else
        holder.money.setText(temp);

        holder.viewBtn.setOnClickListener(new MyBtnOnClickListener(position));
        return convertView;
    }

    class MyBtnOnClickListener implements View.OnClickListener {

        private int position;

        public MyBtnOnClickListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            boolean addflag=false;
            //点击“+”添加景点到购物车
            addflag = DAOFactory.getHomeServiceInstance().addCar(context,data,position);
                    GoodView goodView = new GoodView((Activity)context);
                    goodView.setImage(loadImageFromNetwork((String)data.get(position).get("sceneryImageUrl")));
                    goodView.setDuration(2000);
                    goodView.show(v);
        }
    }

    //imageUrl转drawable
    public static Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }

        return drawable ;
    }
}

