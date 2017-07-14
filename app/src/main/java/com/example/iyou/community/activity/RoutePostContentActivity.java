package com.example.iyou.community.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.example.iyou.route.activity.RouteMapActivity;
import com.example.iyou.tool.Image;
import com.example.iyou.tool.RoutePostSearchSceneryTool;
import com.example.iyou.tool.ScreenTools;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RoutePostContentActivity extends AppCompatActivity {


    private LinearLayout bottomBtn;

    private Map<String, Object> bean;
    private TextView routeName;
    private TextView fullAtical;
    private FrameLayout routeFraLayout;

    private ListView listview;
    //存放路线景点的list
    protected List<Map<String,Object>> sceneryList;
    private String sceneriesIDs=null;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_route_content);


        //获取传递过来的bean对象  (新加的根据点击获取指定item的对象，然后传值)
        bean= (Map<String, Object>) getIntent().getSerializableExtra("bean");
        sceneriesIDs= (String) bean.get("sceneryIds");
        routeName=(TextView)this.findViewById(R.id.routename);
        routeName.setText((String)bean.get("title"));
        fullAtical = (TextView) findViewById(R.id.fullArtical);
        fullAtical.setText((String)bean.get("content"));
        CircleImageView imageView = (CircleImageView) findViewById(R.id.textHead);
        setHead(imageView,(String)bean.get("account"));

        //绑定图片
        Image image = new Image(bean.get("imageUrl").toString(), 400, 300);
        ImageView  route_image = (ImageView) findViewById(R.id.route_content_image);
        int totalWidth;
        int imageWidth;
        int imageHeight;
        ScreenTools screentools = ScreenTools.instance(RoutePostContentActivity.this);
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
        ViewGroup.LayoutParams layoutparams = route_image.getLayoutParams();
        layoutparams.height = imageHeight;
        layoutparams.width = imageWidth;
        route_image.setLayoutParams(layoutparams);
        route_image.setClickable(true);
        route_image.setScaleType(ImageView.ScaleType.FIT_XY);



        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(RoutePostContentActivity.this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config.build());
        imageLoader.displayImage((String)bean.get("imageUrl"),route_image);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("足迹贴正文");
        actionBar.setDisplayHomeAsUpEnabled(true);

        listview = (ListView) this.findViewById(R.id.comment_listView);
        new RoutePostContent(this,listview,bean);

        //点击足迹贴标题事件：根据景点串id从服务器获取景点详细信息，并跳转到地图显示
        routeFraLayout=(FrameLayout)findViewById(R.id.route_post_name_fralayout);
        routeFraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutePostSearchSceneryTool tool=new RoutePostSearchSceneryTool(sceneriesIDs,RoutePostContentActivity.this);
                tool.loadDatas();
            }
        });

        bottomBtn=(LinearLayout) findViewById(R.id.community_comment_btn);


        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(RoutePostContentActivity.this,EditCommentActivity.class);
                //发送Flag用来判断跳转的来处，使RoutePostContentActivity根据Flag值来确定是否添加右上角保存按钮
                intent.putExtra("bean", (Serializable) bean);
                RoutePostContentActivity.this.startActivity(intent);
            }
        });
    }

    //返回键事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //返回到本界面就会调用这个方法
    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this,"sfdsfsda",Toast.LENGTH_LONG).show();
        new RoutePostContent(this,listview,bean);
    }

    public static void setHead(final CircleImageView imageView, String userName){
        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        userQuery.whereEqualTo(ContentMy.USER_NAME,userName); //找到对应的用户
        userQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if(e == null){
                    AVUser user= list.get(0);
                    AVFile file = user.getAVFile(ContentMy.USER_HEAD_IMAGE);
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            imageView.setImageBitmap(MainActivity.Bytes2Bimap(bytes));
                        }
                    });
                }
            }
        });
    }

    //景点搜索回调方法
    public void doSceneryContent(Object obj){
        //将访问服务器获取新的数据保存在一个list里面
        sceneryList=(List<Map<String, Object>>)obj;
        if(null!=sceneryList){
            Intent intent=new Intent();
            intent.setClass(RoutePostContentActivity.this,RouteMapActivity.class);
            intent.putExtra("bean", (Serializable) sceneryList);
            intent.putExtra("Flag",true);
            RoutePostContentActivity.this.startActivity(intent);
        }
    }
}
