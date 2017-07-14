package com.example.iyou.community.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.iyou.MainActivity;
import com.example.iyou.R;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.my.activity.CircleImageView;
import com.example.iyou.my.activity.ContentMy;
import com.example.iyou.tool.GoodView;
import com.example.iyou.tool.Image;
import com.example.iyou.tool.JsonUtil;
import com.example.iyou.tool.ScreenTools;
import com.example.iyou.tool.TRAddagree;
import com.example.iyou.tool.TRIPTool;
import com.example.iyou.tool.TRNetRequest;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.iyou.R.id.zan;

/**
 * Created by asus on 2017/1/9.
 * 足迹贴listview的适配器
 */
public class RoutePostListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public JSONArray goodNum;
    private Handler handlerUpZan = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            data = ( List<Map<String, Object>>)msg.obj;
        }
    };


    //表名字
//    public static final String AGREE_COUNT_NAME = "agree_count_name";

    public RoutePostListAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);


        new Thread(){
            @Override
            public void run() {
                super.run();
                //本人的在此贴的点赞情况
                AVUser currentuser = AVUser.getCurrentUser();
                if(currentuser != null){
                    if(currentuser.get(ContentMy.USER_HAVE_GOOD) != null){
                        if((int)currentuser.get(ContentMy.IF_NEW_LOG) == 1){
                            goodNum = new JSONArray();
                            ArrayList<String> temp = (ArrayList<String>) currentuser.get(ContentMy.USER_HAVE_GOOD);
                            for(int i = 0; i < temp.size(); i++){
                                goodNum.add((String) temp.get(i));
                            }
                        }
                        else
                        goodNum = (JSONArray) currentuser.get(ContentMy.USER_HAVE_GOOD);
                    }
                    else
                        goodNum = new JSONArray();
                }else{
                    goodNum = new JSONArray();
                }
            }
        }.start();
    }

    public final class ViewHolder {
        public CircleImageView head;
        public TextView username;
        public TextView time;
        //public LinearLayout textlinlayout;
        public TextView text;
        public ImageView routepic;
        public TextView routename;
        public FrameLayout viewfralayout;
//        public NineGridlayout ivMore;
        public ImageView ivOne;
        private FrameLayout comment_fL;
        private FrameLayout givezan_fL;
        public TextView agreeCount;
        private boolean isGood;
        private ImageView zan;

        public TextView comentCount;

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
        List<Image> itemList=null;

        //获取图片数据
        if(data.get(position).get("imageUrl") != null) {
            if(itemList == null) {
                itemList = new ArrayList<Image>();
            }
            Image newI = new Image(data.get(position).get("imageUrl").toString(), 200, 150);
            itemList.add(newI);
        }

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.my_route_list_item, null);

            //获得组件，实例化组件
            holder.head=(CircleImageView) convertView.findViewById(R.id.my_route_head);
            holder.username = (TextView) convertView.findViewById(R.id.my_route_username);
            holder.time = (TextView) convertView.findViewById(R.id.my_route_time);
            //holder.textlinlayout=(LinearLayout)convertView.findViewById(R.id.my_route_text_linlayout);
            holder.text=(TextView)convertView.findViewById(R.id.my_route_text);
            holder.routepic=(ImageView)convertView.findViewById(R.id.my_route_post_head);
            holder.routename=(TextView)convertView.findViewById(R.id.my_route_post_name);
//            holder.ivMore = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
            holder.ivOne = (ImageView) convertView.findViewById(R.id.iv_oneimage);
            holder.comment_fL=(FrameLayout)convertView.findViewById(R.id.route_post_comment_fl);
            holder.comment_fL.setTag(position);
            holder.givezan_fL=(FrameLayout)convertView.findViewById(R.id.route_post_givezan_fl);
            holder.agreeCount = (TextView)convertView.findViewById(R.id.agreeCount);
            holder.zan = (ImageView) convertView.findViewById(zan);
            holder.comentCount = (TextView) convertView.findViewById(R.id.commentCount);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
//            holder.zan = (ImageView) convertView.findViewById(zan);
//            holder.zan.setImageResource(R.drawable.good_left);
        }




        final ViewHolder finalHolder2 = holder;



        //足迹贴的评论按钮事件
        holder.comment_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到景点介绍Activity
                DAOFactory.getCommunityServiceInstance().toRoutePostCoGiPage((Activity)context,data,v);
            }
        });

        //足迹贴的点赞按钮设置
        final ViewHolder finalHolder1 = holder;
        holder.givezan_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AVUser.getCurrentUser() == null) Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                //增加赞
                if(finalHolder1.isGood == false && AVUser.getCurrentUser() != null){
                    GoodView goodView = new GoodView((Activity)context);
                    goodView.setText("+1");
                    goodView.show(v);
                    finalHolder1.zan.setImageResource(R.drawable.have_good_left);
                    //将用户赞过的帖子保存到leanCloud
                    if(goodNum == null) goodNum = new JSONArray();
                    goodNum.add((String)data.get(position).get("routeId"));
                    AVUser.getCurrentUser().put(ContentMy.USER_HAVE_GOOD,goodNum);
                    AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e!=null) Log.d("xx",e.toString());
                        }
                    });

                    //更新赞的数量
                    int numTemp = Integer.valueOf(finalHolder1.agreeCount.getText().toString());
                    finalHolder1.agreeCount.setText(String.valueOf(numTemp+1));
                    TRAddagree trAddagree = new TRAddagree((String)data.get(position).get("noteId"));
                    trAddagree.AddAgree();
                    finalHolder1.isGood = true;
                    //更新数据 重新绑定数据
                    upLoad();
                }
                //减少赞
                else if(finalHolder1.isGood == true && AVUser.getCurrentUser() != null){
                    finalHolder1.zan.setImageResource(R.drawable.good_left);
                    int numTemp = Integer.valueOf(finalHolder1.agreeCount.getText().toString());
                    finalHolder1.agreeCount.setText(String.valueOf(numTemp-1));
                    goodNum.remove((String)data.get(position).get("routeId"));
                    AVUser.getCurrentUser().put(ContentMy.USER_HAVE_GOOD,goodNum);
                    AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e!=null) Log.d("xx",e.toString());
                        }
                    });

                    TRAddagree trAddagree = new TRAddagree((String)data.get(position).get("noteId"));
                    trAddagree.subAgree();
                    finalHolder1.isGood = false;
                    //更新数据 重新绑定数据
                    upLoad();


                }
            }
        });


        //绑定数据
//        holder.head.setBackgroundResource((Integer) data.get(position).get("head"));
        holder.username.setText((String) data.get(position).get("account"));
        String timeTemp = (String) data.get(position).get("timestamp");
        holder.time.setText(timeTemp.substring(0,16));
        String tempText = (String) data.get(position).get("content");
        if(tempText != null){
            if(tempText.length() >= 12)
                holder.text.setText(tempText.substring(0,12)+"...");
            else
                holder.text.setText(tempText);
        }


//        holder.routepic.setBackgroundResource((Integer) data.get(position).get("routehead"));
        holder.routename.setText((String) data.get(position).get("title"));

        holder.agreeCount.setText((String) data.get(position).get("agreeCount"));
        holder.comentCount.setText(String.valueOf(data.get(position).get("commentCount")).substring(0,1));


        //从leanCloud得到头像数据
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
                                finalHolder.head.setImageBitmap(MainActivity.Bytes2Bimap(bytes));
                            }
                        });
                    }
                }
            }
        });

        //绑定是否点赞
        if(goodNum!=null) {
            if (goodNum.contains((String) data.get(position).get("routeId"))) {
                holder.zan.setImageResource(R.drawable.have_good_left);
                holder.isGood = true;
            } else {
                holder.zan.setImageResource(R.drawable.good_left);
                holder.isGood = false;
            }
        }




        if (itemList==null || itemList.isEmpty()) {
//            holder.ivMore.setVisibility(View.GONE);
            holder.ivOne.setVisibility(View.GONE);
        } else if (itemList.size() == 1) {
//            holder.ivMore.setVisibility(View.GONE);
            holder.ivOne.setVisibility(View.VISIBLE);



            handlerOneImage(holder, itemList.get(0));
        } else {
//            holder.ivMore.setVisibility(View.VISIBLE);
            holder.ivOne.setVisibility(View.GONE);

//            holder.ivMore.setImagesData(itemList);
        }

        return convertView;
    }

    //处理一张图片时的布局情况
    private void handlerOneImage(final ViewHolder viewHolder, Image image) {
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
        viewHolder.ivOne.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config.build());
        imageLoader.displayImage(image.getUrl(),viewHolder.ivOne);
//        viewHolder.ivOne.setImageUrl(image.getUrl());


//        imageLoader.loadImage(image.getUrl(), new ImageSize(80, 50), new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    viewHolder.ivOne.setImageBitmap(loadedImage);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//
//            }
//        });
    }

    public void upLoad(){
        new Thread(){
            @Override
            public void run() {
                List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();

                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String Routeurl = "http://" + ip +
                        ":"+port;
                String Routeparam = "type=GetNote&NoteType=Route";
                TRNetRequest RouteTRN = new TRNetRequest();
                String Routeres = RouteTRN.sendGet(Routeurl, Routeparam);
                if(Routeres!=null && !Routeres.equals("")){
                    Map<String, Object> Routemap = (Map<String, Object>) JsonUtil.jsonToMap(Routeres);
                    r = (List<Map<String, Object>>) Routemap.get("Notes");
                }
                super.run();
                Message message = new Message();
                message.obj = (Object)r;
                handlerUpZan.sendMessage(message);
            }
        }.start();
    }

}
