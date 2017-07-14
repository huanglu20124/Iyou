package com.example.iyou.route.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.example.iyou.R;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.model.utils.FileUtils;
import com.example.iyou.tool.CustomImageView;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/4.
 * 作为自定义路线列表显示的listview的适配器
 */
public class RouteListAdapter extends BaseAdapter {

    //    private List<Map<String, Object>> data;
    private List<Object> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public RouteListAdapter(Context context, List<Object> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class ViewHolder{
        TextView routeName;
        ImageButton editBtn;
        ImageButton shareBtn;
        ImageButton deleteBtn;
        CustomImageView routOneImage;
        TextView listNumFlag;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder=null;
        if(convertView==null){

            viewholder=new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.route_list_item, null);
            viewholder.routeName=(TextView)convertView.findViewById(R.id.route_name);
            viewholder.editBtn=(ImageButton)convertView.findViewById(R.id.route_edit);
            viewholder.editBtn.setTag(position);
            viewholder.shareBtn=(ImageButton)convertView.findViewById(R.id.route_share);
            viewholder.shareBtn.setTag(position);
            viewholder.deleteBtn=(ImageButton)convertView.findViewById(R.id.route_delete);
            viewholder.routOneImage = (CustomImageView) convertView.findViewById(R.id.routeOneImage);
            viewholder.routOneImage.setTag(position);
            viewholder.listNumFlag = (TextView) convertView.findViewById(R.id.listNumFlag);
            viewholder.listNumFlag.setTag(position);
            convertView.setTag(viewholder);
        }
        else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        //获取路线连线的各个名字连成串再赋值给控件
        List<Map<String,Object>> listdata=null;
        listdata= (List<Map<String, Object>>) data.get(position);
        StringBuffer routeName = new StringBuffer();
        for(Map map:listdata){
            routeName.append(map.get("sceneryName") + "->");
        }
        viewholder.routeName.setText((String) routeName.toString().substring(0,routeName.toString().length()-2));
        viewholder.routOneImage.setImageUrl((String) listdata.get(0).get("sceneryImageUrl"));
        viewholder.listNumFlag.setText("路线 "+String.valueOf(position+1));

//        viewholder.routeName.setText((String) data.get(position).get("route"));
        //设置删除按钮事件
        viewholder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("注意")
                        .setMessage("你确定要删除你的路线吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.remove(position);
                                //上传路线到服务器
                                AVUser.getCurrentUser().put("Routes",data);
                                AVUser.getCurrentUser().saveInBackground();
//                                //保存我的路线在本地，位置在storage-emulated-0-MyRoute.txt文件里面
//                                FileUtils fileUtils=new FileUtils();
//                                //如果数据集是空的话，直接删除文件
//                                if(data.size()==0){
//                                    fileUtils.deleteFile(fileUtils.getSDPATH()+"MyRoute.txt");
//                                }
//                                //删除文件后就更新覆盖文件内容
//                                for(int i=0;i<data.size();i++){
//                                    //第一个条内容是覆盖文件
//                                    if(i==0)
//                                        fileUtils.save(data.get(i),fileUtils.getSDPATH()+"MyRoute.txt",false);
//                                    else
//                                        fileUtils.save(data.get(i),fileUtils.getSDPATH()+"MyRoute.txt",true);
//                                }
                                RouteListAdapter.this.notifyDataSetChanged();
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

        //设置分享按钮事件
        viewholder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAOFactory.getRouteServiceInstance().toEidtShareRoutePage((Activity) context, data, (Integer) v.getTag());
            }
        });

        //设置修改按钮事件
        viewholder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAOFactory.getRouteServiceInstance().toEditRoutePage((Activity) context, data, (Integer) v.getTag());
            }
        });
        return convertView;
    }
}