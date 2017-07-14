package com.example.iyou.route.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.iyou.model.utils.DAOFactory;
import com.example.iyou.model.utils.FileUtils;
import com.example.iyou.route.model.bean.AMapUtil;
import com.example.iyou.route.model.bean.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteMapActivity extends AppCompatActivity implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener{

    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint;//起点，116.335891,39.942295
    private LatLonPoint mEndPoint;//终点，116.481288,39.995576
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
    private GeocodeSearch startGeoSearch;//用于设置起点的地理编码
    private GeocodeSearch endGeoSearch;//用于设置途径点的地理编码
    private GeocodeSearch geoSearch;//用于设置终点的地理编码
    private List<LatLonPoint> allPoints=new ArrayList<LatLonPoint>();
    private List<LatLonPoint> pressPoints=new ArrayList<LatLonPoint>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_route_map);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写

        startGeoSearch = new GeocodeSearch(this);
        geoSearch = new GeocodeSearch(this);
        endGeoSearch = new GeocodeSearch(this);
        startGeoSearch.setOnGeocodeSearchListener(new RouteMapActivity.startGeoSearchListener());
        geoSearch.setOnGeocodeSearchListener(new RouteMapActivity.geoSearchListener());
        endGeoSearch.setOnGeocodeSearchListener(new RouteMapActivity.endGeoSearchListener());
        progDialog = new ProgressDialog(this);
        //获取传递数据bean对象：即listview指定item的相应的景点map对象
        bean=(List<Map<String, Object>>) getIntent().getSerializableExtra("bean");
        //设置标题
        StringBuffer routeName = new StringBuffer();
        for(Map map:bean){
            routeName.append(map.get("sceneryName") + "->");
        }
        actionBar.setTitle((String) routeName.toString().substring(0,routeName.toString().length()-2));

        //先获取路线每一个点的经纬度值将他保存到一个list里面
        for(Map map:bean){
            getLatlon((String) map.get("sceneryName"),(String) map.get("city"),2);
        }

        //设置起点
        getLatlon((String) bean.get(0).get("sceneryName"),(String)bean.get(0).get("city"),1);
        //设置终点
        getLatlon((String) bean.get(bean.size()-1).get("sceneryName"),(String)bean.get(bean.size()-1).get("city"),3);
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
        aMap.setOnMapClickListener(RouteMapActivity.this);
        aMap.setOnMarkerClickListener(RouteMapActivity.this);
        aMap.setOnInfoWindowClickListener(RouteMapActivity.this);
        aMap.setInfoWindowAdapter(RouteMapActivity.this);

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

    /*
     *  路程规划回调函数
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        //画途经点坐标
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
     * 响应地理编码
     * 1:代表起点
     * 2:代表添加到allPoint集中
     * 3:代表终点
     */
    public void getLatlon(final String name,String city,int type){
        showDialog();

        GeocodeQuery query = new GeocodeQuery(name, city);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        switch (type){
            case 1:
                startGeoSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
                break;
            case 2:
                geoSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
                break;
            case 3:
                endGeoSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
                break;
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
                } else {
                    ToastUtil.show(RouteMapActivity.this, R.string.no_result);
                }
            } else {
                ToastUtil.showerror(RouteMapActivity.this, rCode);
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
                    ToastUtil.show(RouteMapActivity.this, R.string.no_result);
                }
            } else {
                ToastUtil.showerror(RouteMapActivity.this, rCode);
            }
        }
    }

    private class geoSearchListener implements GeocodeSearch.OnGeocodeSearchListener{
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
                    allPoints.add(address.getLatLonPoint());
                } else {
                    ToastUtil.show(RouteMapActivity.this, R.string.no_result);
                }
            } else {
                ToastUtil.showerror(RouteMapActivity.this, rCode);
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
                //上传路线到服务器
                List<Object> lists= DAOFactory.getRouteServiceInstance().getData();
                lists.add(bean);
                AVUser.getCurrentUser().put("Routes",lists);
                AVUser.getCurrentUser().saveInBackground();
                ToastUtil.show(this,"已添加到我的路线");
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
        //获取Flag值如果是true证明是从我的路线跳转过来不需要添加保存按钮
        //如果是false证明是从社区的足迹贴中传递过来的，需要添加保存按钮给用户添加到本机我的路线
        boolean flag=getIntent().getBooleanExtra("Flag",false);
        if(flag)
            getMenuInflater().inflate(R.menu.menu_edit_map, menu);
        return true;
    }
}
