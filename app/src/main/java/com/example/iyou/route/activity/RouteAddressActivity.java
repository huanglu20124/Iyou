package com.example.iyou.route.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.avos.avoscloud.AVUser;
import com.example.iyou.R;
import com.example.iyou.home.activity.SceneryCarActivity;
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.model.utils.FileUtils;
import com.example.iyou.route.model.bean.AMapUtil;
import com.example.iyou.route.model.bean.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteAddressActivity extends AppCompatActivity implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {

    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint = new LatLonPoint(39.90881451, 116.39757156);//起点，116.335891,39.942295
    private LatLonPoint mEndPoint = new LatLonPoint(39.90881451, 116.39757156);//终点，116.481288,39.995576
    private final int ROUTE_TYPE_DRIVE = 2;
    private LinearLayout mBusResultLayout;
    private RelativeLayout mBottomLayout;
    private TextView mRotueTimeDes, mRouteDetailDes;
    private ImageView mDrive;
    private ListView mBusResultList;
    private ProgressDialog progDialog = null;// 搜索时进度条

    //我的
    private List<Map<String, Object>> bean=null;
    private Spinner startSpinner;
    private Spinner endSpinner;
    private Marker geoMarker;
    private GeocodeSearch startGeoSearch;
    private GeocodeSearch endGeoSearch;
    private List<LatLonPoint> allPoints=new ArrayList<LatLonPoint>();
    private List<LatLonPoint> pressPoints=new ArrayList<LatLonPoint>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_route_address);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle(R.string.route_demo);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写

        //我的
        startGeoSearch = new GeocodeSearch(this);
        endGeoSearch = new GeocodeSearch(this);
        startGeoSearch.setOnGeocodeSearchListener(new RouteAddressActivity.startGeoSearchListener());
        endGeoSearch.setOnGeocodeSearchListener(new RouteAddressActivity.endGeoSearchListener());
        progDialog = new ProgressDialog(this);
        startSpinner=(Spinner)findViewById(R.id.start_spinner);
        endSpinner=(Spinner)findViewById(R.id.end_spinner);
        //获取传递数据bean对象：即listview指定item的相应的景点map对象
        bean=(List<Map<String, Object>>) getIntent().getSerializableExtra("bean");
        final String[] items=new String[bean.size()];//Spinner数据集
        for(int i=0;i<bean.size();i++){
            items[i]= (String) bean.get(i).get("sceneryName");
        }
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        startSpinner .setAdapter(adapter);
        endSpinner .setAdapter(adapter);
        endSpinner.setSelection(items.length-1);
        startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLatlon(items[position],(String) bean.get(position).get("city"),true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        endSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLatlon(items[position],(String) bean.get(position).get("city"),false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //先获取路线每一个点的经纬度值将他保存到一个list里面
        for(Map map:bean){
            getLatlon((String) map.get("sceneryName"),(String) map.get("city"),true);
        }
        //我的

        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
        registerListener();
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        mDrive = (ImageView)findViewById(R.id.route_drive);
        mBusResultList = (ListView) findViewById(R.id.bus_result_list);
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(RouteAddressActivity.this);
        aMap.setOnMarkerClickListener(RouteAddressActivity.this);
        aMap.setOnInfoWindowClickListener(RouteAddressActivity.this);
        aMap.setInfoWindowAdapter(RouteAddressActivity.this);

    }

    //点击事件
    public void onDriveClick(View view) {
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        mDrive.setImageResource(R.drawable.route_drive_select);
        mapView.setVisibility(View.VISIBLE);
        mBusResultLayout.setVisibility(View.GONE);
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "起点未设置");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            //搜寻途径点集合
            for(LatLonPoint latLngPoint:allPoints){
                if( (mStartPoint.getLatitude()==latLngPoint.getLatitude() && mStartPoint.getLongitude()==latLngPoint.getLongitude()) ||
                        (mEndPoint.getLatitude()==latLngPoint.getLatitude() && mEndPoint.getLongitude()==latLngPoint.getLongitude())){
                    continue;
                }
                pressPoints.add(latLngPoint);
            }
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, pressPoints,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        //画途径点坐标
        for(LatLonPoint latLngPoint:pressPoints){
            aMap.addMarker(new MarkerOptions().position(new LatLng(latLngPoint.getLatitude(),latLngPoint.getLongitude()))
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverLay drivingRouteOverlay = new DrivingRouteOverLay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+ AMapUtil.getFriendlyLength(dis)+")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约"+taxiCost+"元");
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    DriveRouteDetailActivity.class);
                            intent.putExtra("drive_path", drivePath);
                            intent.putExtra("drive_result",
                                    mDriveRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 响应地理编码
     * type=true:代表起点
     * type=false:代表终点
     */
    public void getLatlon(final String name,String city,boolean type){
        showDialog();

        GeocodeQuery query = new GeocodeQuery(name, city);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        if(type){
            startGeoSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
        }
        else{
            endGeoSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
        }
    }


    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    private class startGeoSearchListener implements GeocodeSearch.OnGeocodeSearchListener{
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        }

        @Override
        public void onGeocodeSearched(GeocodeResult result, int rCode) {
            dissmissProgressDialog();
            if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                if (result != null && result.getGeocodeAddressList() != null
                        && result.getGeocodeAddressList().size() > 0) {
                    GeocodeAddress address = result.getGeocodeAddressList().get(0);
                    mStartPoint=address.getLatLonPoint();
                    allPoints.add(address.getLatLonPoint());
                } else {
                    ToastUtil.show(RouteAddressActivity.this, R.string.no_result);
                }
            } else {
                ToastUtil.showerror(RouteAddressActivity.this, rCode);
            }
        }
    }

    private class endGeoSearchListener implements GeocodeSearch.OnGeocodeSearchListener{
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        }

        @Override
        public void onGeocodeSearched(GeocodeResult result, int rCode) {
            dissmissProgressDialog();
            if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                if (result != null && result.getGeocodeAddressList() != null
                        && result.getGeocodeAddressList().size() > 0) {
                    GeocodeAddress address = result.getGeocodeAddressList().get(0);
                    mEndPoint=address.getLatLonPoint();
                } else {
                    ToastUtil.show(RouteAddressActivity.this, R.string.no_result);
                }
            } else {
                ToastUtil.showerror(RouteAddressActivity.this, rCode);
            }
        }
    }

    //返回键事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                //list调换位置,修改终点和起点
                Map<String, Object> temp=new HashMap<String,Object>();
                temp=bean.get(0);
                bean.set(0,bean.get((int) startSpinner.getSelectedItemId()));
                bean.set((int) startSpinner.getSelectedItemId(),temp);
                Map<String, Object> temp2=new HashMap<String,Object>();
                temp2=bean.get(bean.size()-1);
                bean.set(bean.size()-1,bean.get((int) endSpinner.getSelectedItemId()));
                bean.set((int) endSpinner.getSelectedItemId(),temp2);

                boolean flag=getIntent().getBooleanExtra("flag",false);
                int position;

                //true:代表是修改路线（重新重写文件内容覆盖）；false:代表景点车来的（只添加一行文件内容）
                if(flag){
                    List<Object> lists= DAOFactory.getRouteServiceInstance().getData();
                    if(lists==null)
                        lists=new ArrayList<Object>();
                    position=getIntent().getIntExtra("position",-1);
                    //将更改后的内容覆盖到原来的数据内容
                    lists.set(position,bean);
                    //上传路线到服务器
                    AVUser.getCurrentUser().put("Routes",lists);
                }
                else{
                    //上传路线到服务器
                    List<Object> lists= DAOFactory.getRouteServiceInstance().getData();
                    if(lists==null)
                        lists=new ArrayList<Object>();
                    lists.add(bean);
                    AVUser.getCurrentUser().put("Routes",lists);
                    AVUser.getCurrentUser().saveInBackground();
                    //清除购景车数据
                    SceneryCarActivity.dataRecource.clear();
                    Intent intent =new Intent();
                    intent.setClass(RouteAddressActivity.this, RouteActivity.class);
                    RouteAddressActivity.this.startActivity(intent);
                }

                Toast.makeText(RouteAddressActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_map, menu);
        return true;
    }
}
