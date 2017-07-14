package com.example.iyou;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.services.nearby.NearbySearch;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.iyou.community.activity.ContentCommunity;
import com.example.iyou.home.activity.ContentHome;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.my.activity.ContentMy;
import com.example.iyou.my.model.service.impl.impl.MyServiceImpl;
import com.example.iyou.tool.CustomViewPager;
import com.example.iyou.tourpal.activity.ContentTourpal;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;
    private TabLayout tabLayoutMenu;
    public Toolbar toolbar;


    //改变定位城市的请求吗
//    public static final int REQUEST_TO_CHANGE_CITY = 2;

    /**
     * 上一次点击 back 键的时间
     * 用于双击退出的判断
     */
    private static long lastBackTime = 0;

    /**
     * 当双击 back 键在此间隔内是直接触发 onBackPressed
     */
    private final int BACK_INTERVAL = 1000;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AVUser.getCurrentUser().put(ContentMy.IF_NEW_LOG,0);
        AVUser.getCurrentUser().saveInBackground();

        //设置动画效果

        setContentView(R.layout.activity_main);
        //设置toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView textView = (TextView) toolbar.findViewById(R.id.now_city);
        textView.setText(Constants.CURRENT_CITY);
//        toolbar.setLogo(getResources().getDrawable(R.drawable.title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorBaseYellow));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //想使用自己的布局就得 通过 监听进行关联,  设置底部导航栏
        tabLayoutMenu = (TabLayout) findViewById(R.id.tab_layout_menu);
        tabLayoutMenu.setBackgroundColor(getResources().getColor(R.color.avoscloud_feedback_white));
        bindPagerAndTab();
        setupTabIcon();


        // 测试 SDK 是否正常工作的代码
        AVObject testObject = new AVObject("TestObject");
        testObject.put("words","Hello World!");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Log.d("saved","success!");
                }
            }
        });

        //从本地缓存获取当前设置城市信息
        SharedPreferences sharedata = getSharedPreferences("data", 0);
        String city = sharedata.getString("locationcity", null);
        if(city!=null){
            Constants.CURRENT_CITY=city;
        }

    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackTime < BACK_INTERVAL) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "双击 back 退出", Toast.LENGTH_SHORT).show();
        }
        lastBackTime = currentTime;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView textView = (TextView) toolbar.findViewById(R.id.now_city);
        textView.setText(Constants.CURRENT_CITY);
    }


    //设置底部导航栏的布局控件
    private void setupTabIcon(){
        Resources res = getResources();
        tabLayoutMenu.addTab(tabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.scenery_normal), getString(R.string.tab_scenery))));
        tabLayoutMenu.addTab(tabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.community_normal), getString(R.string.tab_community))));
        tabLayoutMenu.addTab(tabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.route_normal), getString(R.string.tab_tourpal))));
        tabLayoutMenu.addTab(tabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.my_normal), getString(R.string.tab_my))));
    }

    //设置滑动事件
    private void bindPagerAndTab() {
        tabLayoutMenu.setSelectedTabIndicatorHeight(0);//去除指示器
        tabLayoutMenu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             * 选中tab后触发
             * @param tab 选中的tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //与pager 关联
//                mViewPager.setCurrentItem(tab.getPosition(), true);
                changeTabSelect(tab);
            }

            /**
             * 退出选中状态时触发
             * @param tab 退出选中的tab
             */
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            /**
             * 重复选择时触发
             * @param tab 被 选择的tab
             */
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    //设置选择tab图标
    private void changeTabSelect(TabLayout.Tab tab) {
        Resources res = getResources();
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.icon);
        TextView txt_title = (TextView) view.findViewById(R.id.title);
        txt_title.setTextColor(getResources().getColor(R.color.colorBaseYellow));
        if (tab.getPosition()==Constants.HOME_PAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.scenery_selected));
            mViewPager.setCurrentItem(Constants.HOME_PAGE-1,false);
        } else if (tab.getPosition()==Constants.COMMUNITY_PAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.community_selected));
            mViewPager.setCurrentItem(Constants.COMMUNITY_PAGE-1,false);
        }else if (tab.getPosition()==Constants.TOUTPAL_PAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.route_selected));
            mViewPager.setCurrentItem(Constants.TOUTPAL_PAGE-1,false);
        } else {
            img_title.setImageDrawable(res.getDrawable(R.drawable.my_selected));
            mViewPager.setCurrentItem(Constants.MY_PAGE-1,false);
        }
    }

    //设置还原tab图标
    private void changeTabNormal(TabLayout.Tab tab) {
        Resources res = getResources();
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.icon);
        TextView txt_title = (TextView) view.findViewById(R.id.title);
        txt_title.setTextColor(Color.GRAY);
        if (tab.getPosition()==Constants.HOME_PAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.scenery_normal));
        }  else if (tab.getPosition()==Constants.COMMUNITY_PAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.community_normal));
        }else if (tab.getPosition()==Constants.TOUTPAL_PAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.route_normal));
        }else {
            img_title.setImageDrawable(res.getDrawable(R.drawable.my_normal));
        }
    }

    private View createView(Drawable icon, String tab) {
        View view = getLayoutInflater().inflate(R.layout.fragment_tab_discover, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        TextView title = (TextView) view.findViewById(R.id.title);
        imageView.setImageDrawable(icon);

        title.setText(tab);
        return view;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    //设置工具栏右上角的item按钮事件
    //item的控件按钮在res/menu/menu_main.xml文件中定义
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_location:
                Toast.makeText(MainActivity.this, "定位", Toast.LENGTH_SHORT).show();
                DAOFactory.getHomeServiceInstance().toLocationPage(this);
                return true;
            case R.id.action_car:
                Toast.makeText(MainActivity.this, "景点车", Toast.LENGTH_SHORT).show();
                DAOFactory.getHomeServiceInstance().toSceneryCarPage(this);
                return true;
            case R.id.action_search:
                Toast.makeText(MainActivity.this, "搜索", Toast.LENGTH_SHORT).show();
                DAOFactory.getHomeServiceInstance().toSearchPage(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return Constants.PAGE_SIZE;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        @Override
        public void onResume() {
            super.onResume();
            if(contentHome!=null && getArguments().getInt(ARG_SECTION_NUMBER)==Constants.HOME_PAGE){
                contentHome.visitServer();
            }
            if(getArguments().getInt(ARG_SECTION_NUMBER)==Constants.TOUTPAL_PAGE){
                mMapView.onResume();
            }
            if(getArguments().getInt(ARG_SECTION_NUMBER)==Constants.COMMUNITY_PAGE){
                contentCommunity.visitServer();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            if(contentHome!=null && getArguments().getInt(ARG_SECTION_NUMBER)==Constants.HOME_PAGE){
                contentHome.visitServer();
            }
            if(getArguments().getInt(ARG_SECTION_NUMBER)==Constants.TOUTPAL_PAGE){
                mMapView.onPause();
                contentTourpal.deactivate();
                contentTourpal.mFirstFix=false;
                //调用销毁功能，在应用的合适生命周期需要销毁附近功能
                NearbySearch.destroy();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if(getArguments().getInt(ARG_SECTION_NUMBER)==Constants.TOUTPAL_PAGE){
                mMapView.onDestroy();
                if(null != contentTourpal.mlocationClient){
                    contentTourpal.mlocationClient.onDestroy();
                }
                //调用销毁功能，在应用的合适生命周期需要销毁附近功能
                NearbySearch.destroy();
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if(mMapView!=null)
                mMapView.onSaveInstanceState(outState);
        }

        private static final String ARG_SECTION_NUMBER = "section_number";

        private ContentHome contentHome=null;
        private ContentCommunity contentCommunity=null;

        private ContentTourpal contentTourpal=null;

        MapView mMapView = null;
        AMap aMap;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView =null;
            ListView listview=null;
            List<Map<String, Object>> listdata=null;
            //通过getArguments().getInt(ARG_SECTION_NUMBER)来判断页面
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case Constants.HOME_PAGE:
                    rootView = inflater.inflate(R.layout.content_home, container, false);
                    listview = (ListView) rootView.findViewById(R.id.home_listview);
                    //景点介绍主页页面的container的具体内容实现
                    contentHome=new ContentHome(getContext(),rootView,listview,listdata);
                    break;
                case  Constants.COMMUNITY_PAGE:
                    rootView = inflater.inflate(R.layout.content_community, container, false);
                    listview = (ListView) rootView.findViewById(R.id.community_listView);
                    //社区页面的container的具体内容实现
                    contentCommunity=new ContentCommunity(getContext(),rootView,listview);
                    break;
                case Constants.TOUTPAL_PAGE:
                    rootView = inflater.inflate(R.layout.activity_tourpal, container, false);
                    mMapView = (com.amap.api.maps2d.MapView) rootView.findViewById(R.id.gao_de_map);
                    contentTourpal=new ContentTourpal(getContext(),mMapView,rootView,savedInstanceState);
                    break;
                case Constants.MY_PAGE:
                    rootView=inflater.inflate(R.layout.content_my,container,false);
                    //我的页面的container的具体内容实现
                    new ContentMy(getActivity(),getContext(),rootView);
                    break;
            }

            return rootView;

        }

    }

    //处理一些我的界面的更新
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录成功，更新界面
        int x = resultCode;
        if((requestCode == MyServiceImpl.REQUESET_LOGIN_CODE )
                || requestCode == MyServiceImpl.REQUEST_CHANGE_DATA){

            AVUser user = AVUser.getCurrentUser();
            if(user != null){
                TextView tv = (TextView) findViewById(R.id.accountName);
                tv.setText(user.getUsername());

                final ImageView imageView = (ImageView) findViewById(R.id.userHead1);

                AVFile file = user.getAVFile("user_head_image");
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, AVException e) {
                        imageView.setImageBitmap(Bytes2Bimap(bytes));
                    }
                });

                //更新三种帖子的数目
                AVUser avUser = AVUser.getCurrentUser();
                TextView textViewRoute = (TextView) findViewById(R.id.route_num);
                TextView textViewQuestion = (TextView) findViewById(R.id.quetion_num);
                TextView textViewReply = (TextView) findViewById(R.id.reply_num);
                textViewRoute.setText(String.valueOf(avUser.get(ContentMy.USER_ROUTE_NUM)));
                textViewQuestion.setText(String.valueOf(avUser.get(ContentMy.USER_QUESTION_NUM)));
                textViewReply.setText(String.valueOf(avUser.get(ContentMy.USER_COMMENT_COUNT)));

                avUser.put(ContentMy.IF_NEW_LOG,1);
                avUser.saveInBackground();
            }

        }

    }


    //数据流转bitmap
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVUser.getCurrentUser().put(ContentMy.IF_NEW_LOG,0);
        AVUser.getCurrentUser().saveInBackground();
    }
}
