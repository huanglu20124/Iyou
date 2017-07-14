package com.example.iyou.community.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.iyou.R;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.route.activity.RouteActivity;
import com.example.iyou.tool.CircularAnim;
import com.example.iyou.tool.ContentCommunityTool;
import com.example.iyou.tool.RefreshLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/1/13.
 * 作用于主页托转到社区的内容设置
 */
public class ContentCommunity {


    //新增两个fabButton
    private FloatingActionButton buttonToRoute;
    private FloatingActionButton buttonToQuestion;

    private Context context;
    private View rootView;
    private ListView listview;
    List<Map<String, Object>> routeListData=null;
    List<Map<String, Object>> questionListData=null;

    //社区问题贴的listview适配器
    private QuestionPostListAdapter questionPostListAdapter;
    //社区问题贴的listview适配器
    private RoutePostListAdapter routePostListAdapter;

    //布局按钮
    private FrameLayout routePost_fL;
    private FrameLayout questionPost_fL;
    private FrameLayout share_fL;

    //下拉刷新控件
//    private RefreshableView refreshableView;
    protected RefreshLayout refreshLayout;


    private static int flag=1;

    private ContentCommunity contentCommunity = this;

    public ContentCommunity(final Context context,View rootView, final ListView listview){


        // Simulate network access.

        this.rootView=rootView;
        this.listview=listview;
        this.context=context;


        routePost_fL=(FrameLayout)rootView.findViewById(R.id.community_route_post_fl);
        questionPost_fL=(FrameLayout)rootView.findViewById(R.id.community_question_post_fl);
        share_fL=(FrameLayout)rootView.findViewById(R.id.community_share_button);
        refreshLayout = (RefreshLayout) rootView.findViewById(R.id.contact_fragment_srl_list);

        buttonToRoute = (FloatingActionButton) rootView.findViewById(R.id.fab_to_route);
        buttonToQuestion = (FloatingActionButton) rootView.findViewById(R.id.fab_to_question);


        //足迹贴分享按钮
        buttonToRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AVUser.getCurrentUser() == null){
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                }else{

                    CircularAnim.fullActivity((Activity) context, v)
                            .colorOrImageRes(R.color.colorBaseYellow)
                            .go(new CircularAnim.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    context.startActivity(new Intent(context, RouteActivity.class));
                                }
                            });
                }

            }
        });
        //问题贴分享按钮
        buttonToQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AVUser.getCurrentUser() == null){
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                }else{

                    CircularAnim.fullActivity((Activity) context, v)
                            .colorOrImageRes(R.color.colorBaseYellow)
                            .go(new CircularAnim.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    context.startActivity(new Intent(context, ShareQuestionPostActivity.class));
                                }
                            });
                }
            }
        });


        /*
         *  构造方法先通过DAOFactory访问各自的服务器信息传递给相应的list保存数据
         *  然后通过数据层回调给这个类相应的list，和配置listview适配器
         *  最后根据点击按钮事件来设置listview选择哪个适配器
         */

        //调用一次getdata获取全部帖子数据（）
        visitServer();


        //初始化
        ImageView imageView1 = (ImageView) routePost_fL.findViewById(R.id.route_image);
        imageView1.setImageResource(R.drawable.route_title_selected);
        listview.setAdapter(routePostListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DAOFactory.getCommunityServiceInstance().toArticlePostPage((Activity) context,routeListData,position);

            }
        });
        routePost_fL.setBackground(context.getResources().getDrawable(R.drawable.button_bac));
        flag = 1;

//        CircularAnim.show(listview).triggerView(routePost_fL).go();
        //足迹贴事件处理
        routePost_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //判断如果重复点击不会刷新listview
                if(flag != 1){
                    listview.setAdapter(routePostListAdapter);
                    CircularAnim.show(listview).triggerView(routePost_fL).go();
                    ImageView imageView2 = (ImageView) routePost_fL.findViewById(R.id.route_image);
                    imageView2.setImageResource(R.drawable.route_title_selected);
                    ImageView imageView3 = (ImageView) questionPost_fL.findViewById(R.id.quetion_image);
                    imageView3.setImageResource(R.drawable.quetion_title_normal);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            DAOFactory.getCommunityServiceInstance().toArticlePostPage((Activity) context,routeListData,position);

                        }
                    });
                    routePost_fL.setBackground(context.getResources().getDrawable(R.drawable.button_bac));
                    questionPost_fL.setBackground(context.getResources().getDrawable(R.color.avoscloud_feedback_white));

                    flag = 1;
                }

            }
        });

        //问题贴事件处理
        questionPost_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断如果重复点击不会刷新listview
                if(flag != 2){
                    listview.setDividerHeight(15);
                    listview.setAdapter(questionPostListAdapter);
                    CircularAnim.show(listview).triggerView(questionPost_fL).go();
                    ImageView imageView4 = (ImageView) questionPost_fL.findViewById(R.id.quetion_image);
                    imageView4.setImageResource(R.drawable.quetion_tittle_selected);
                    ImageView imageView5 = (ImageView) routePost_fL.findViewById(R.id.route_image);
                    imageView5.setImageResource(R.drawable.route_title_normal);
                    listview.setDividerHeight(10);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //跳转到景点介绍Activity
                            DAOFactory.getCommunityServiceInstance().toQuestionPostAnswerPage((Activity) context,questionListData,position);

                        }
                    });
                    questionPost_fL.setBackground(context.getResources().getDrawable(R.drawable.button_bac));
                    routePost_fL.setBackground(context.getResources().getDrawable(R.color.avoscloud_feedback_white));
                    flag = 2;
                }

            }
        });



        //下拉刷新栏控件事件、监听器
        //填写刷新数据
        //下拉刷新，用于获取更新的帖子信息
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshMembers();
                if(flag == 2){
                    DAOFactory.getCommunityServiceInstance().getQuestionPostData(contentCommunity);
                }
                else {
                    DAOFactory.getCommunityServiceInstance().getRoutePostData(contentCommunity);
                }


            }
        });
        //上拉刷新，用于获取之后的更多信息
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                if(flag == 1){
                    //足迹贴上拉刷新
                    ContentCommunityTool tool = new ContentCommunityTool(contentCommunity,routeListData);
                    tool.doLoadRoute();
                }
                else{
                    //问题贴上拉刷新
                    ContentCommunityTool tool = new ContentCommunityTool(contentCommunity,questionListData);
                    tool.doLoadQuest();
                }

            }
        });

    }


    //设置足迹贴的listview数据源和适配器
    public void doRoutePostContent(Object obj){
        routeListData= (List<Map<String, Object>>)obj;
        routePostListAdapter=new RoutePostListAdapter(context, routeListData);
        if(flag==1)
            listview.setAdapter(routePostListAdapter);
        refreshLayout.setRefreshing(false);
    }

    //设置问题贴的listview数据源和适配器
    public void doQuestionPostContent(Object obj){
        questionListData= (List<Map<String, Object>>)obj;
        questionPostListAdapter=new QuestionPostListAdapter(context, questionListData);
        if(flag==2)
            listview.setAdapter(questionPostListAdapter);
        refreshLayout.setRefreshing(false);
    }

    //上拉刷新足迹贴回调方法
    public void doLoadRoutePostContent(Object obj){
        //将访问服务器获取新的数据保存在一个list里面
        List<Map<String, Object>> newlist=(List<Map<String, Object>>)obj;
        //遍历新的20条list添加到旧的原始list里面，listdatas为原始数据集
        for(Map map:newlist){
            if(map!=null || !map.isEmpty()){
                routeListData.add(map);
            }
        }
        //刷新ui层的listview数据
        routePostListAdapter.notifyDataSetChanged();
        refreshLayout.setLoading(false);
    }

    //上拉刷新问题贴回调方法
    public void doLoadQuestPostContent(Object obj){
        //将访问服务器获取新的数据保存在一个list里面
        List<Map<String, Object>> newlist=(List<Map<String, Object>>)obj;
        //遍历新的20条list添加到旧的原始list里面，listdatas为原始数据集
        for(Map map:newlist){
            if(map!=null || !map.isEmpty()){
                questionListData.add(map);
            }
        }
        //刷新ui层的listview数据
        questionPostListAdapter.notifyDataSetChanged();
        refreshLayout.setLoading(false);
    }

    public void visitServer(){
        DAOFactory.getCommunityServiceInstance().getcontentPostData(contentCommunity);
    }
}
