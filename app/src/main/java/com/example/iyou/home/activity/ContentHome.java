package com.example.iyou.home.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.iyou.R;
import com.example.iyou.community.activity.QuestionPostListAdapter;
import com.example.iyou.community.activity.RoutePostListAdapter;
import com.example.iyou.home.model.bean.Paramters.TRGetDataParamters;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.tool.HomeServiceTool;
import com.example.iyou.tool.RefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.iyou.Constants.CURRENT_CITY;

/**
 * Created by asus on 2017/1/13.
 */
public class ContentHome {

    private Context context;
    private View rootView;
    private ListView listview;
    List<Map<String, Object>> listdatas=null;
    private ContentHome contentHome = this;

    //主页listview适配器
    private HomeListAdapter homeListAdapter;

    protected RefreshLayout refreshLayout;

    public ContentHome(final Context context,View rootView,ListView listview,List<Map<String, Object>> listdata) {
        this.rootView = rootView;
        this.listview = listview;
        this.context = context;

        refreshLayout = (RefreshLayout) rootView.findViewById(R.id.contact_fragment_srl_list);

        //访问数据连接层
//        DAOFactory.getHomeServiceInstance().getData(contentHome);
        visitServer();

        //下拉刷新无操作，只是取消下拉刷新控件
        refreshLayout.setOnRefreshListener(new
            SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 refreshLayout.setRefreshing(false);
                }
             });


        //上拉刷新，用于获取之后的更多信息
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                if(listdatas == null ){return;}

                String size = (String) listdatas.get(0).get("sceneryId");
                for(int i = 0; i < listdatas.size(); i++){
                    if(Integer.parseInt((String)listdatas.get(i).get("sceneryId")) > Integer.parseInt(size)){
                        size = (String)listdatas.get(i).get("sceneryId");
                    }
                }

                TRGetDataParamters data = new TRGetDataParamters();
                data.setSinceId(Integer.parseInt(size)+1);
                data.setKeyWordType("city");
                data.setKeyWord(CURRENT_CITY);

                HomeServiceTool tool = new HomeServiceTool(contentHome,data);
                tool.getLoadData();

                Toast.makeText(((Activity)context), "刷新",
                        Toast.LENGTH_SHORT).show();
                refreshLayout.setLoading(false);

            }
        });

    }

    //上拉刷新回调方法
     public void doLoadContent(Object obj){
         //将访问服务器获取新的数据保存在一个list里面
          List<Map<String, Object>> newlist=(List<Map<String, Object>>)obj;
          //遍历新的20条list添加到旧的原始list里面，listdatas为原始数据集
          for(Map map:newlist){
            if(map!=null || !map.isEmpty()){
                listdatas.add(map);
            }
          }
          //刷新ui层的listview数据
         homeListAdapter.notifyDataSetChanged();
         refreshLayout.setLoading(false);
     }

    public void doContent(Object obj){
        //设置数据源
        listdatas = (List<Map<String, Object>>)obj;
        //设置适配器
        homeListAdapter=new HomeListAdapter(context, listdatas);
        listview.setAdapter(homeListAdapter);
        //设置事件
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到景点介绍Activity
                DAOFactory.getHomeServiceInstance().displayDetail( (Activity) context,listdatas,position);

            }
        });

    }
    public void visitServer(){
        //访问数据连接层
        DAOFactory.getHomeServiceInstance().getData(this);
    }
}
