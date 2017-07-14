package com.example.iyou.tourpal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.NearbyInfo;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchResult;
import com.amap.api.services.nearby.UploadInfo;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.example.iyou.MainActivity;
import com.example.iyou.R;
import com.example.iyou.route.model.bean.ToastUtil;
import com.example.iyou.tourpal.model.bean.CustomUserProvider;

import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;

/**
 * Created by asus on 2017/2/20.
 */

public class ContentTourpal implements LocationSource,
        AMapLocationListener, NearbySearch.NearbyListener,AMap.OnMarkerClickListener {

    private Context context;
    private MapView mapView;
    private AMap aMap;
    //定位服务类。此类提供单次定位、持续定位、地理围栏、最后位置相关功能
    private AMapLocationClient aMapLocationClient;
    private com.amap.api.maps.LocationSource.OnLocationChangedListener listener;
    //定位参数设置
    private AMapLocationClientOption aMapLocationClientOption;
    private Marker mLocMarker;
//    public SensorEventHelper mSensorHelper;
    private Circle mCircle;
    public AMapLocationClient mlocationClient;
    public boolean mFirstFix = false;
    private OnLocationChangedListener mListener;
    private TextView mLocationErrText;
    private View rootView;
    private AMapLocationClientOption mLocationOption;
    public static final String LOCATION_MARKER_FLAG = "mylocation";
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    public LatLonPoint locationPoint=null;
    NearbySearch mNearbySearch=null;
    private final double RADIUS=2000;
    private final int MAKER_SIZE=150;
    private Switch locationSwitch=null;
    private ImageButton tourpalBtn=null;
    private boolean mShareFlag=false;//判断是否开启共享位置

    public ContentTourpal(Context context, MapView mapView, View rootView, Bundle savedInstanceState ){
        this.context=context;
        this.rootView=rootView;
        this.mapView=mapView;
        this.mapView.onCreate(savedInstanceState);// 此方法必须重写

        //获取附近实例（单例模式）
        mNearbySearch = NearbySearch.getInstance(this.context.getApplicationContext());
        //设置附近监听
        NearbySearch.getInstance(this.context.getApplicationContext()).addNearbyListener(this);

        //开启聊天链接
        if(null!=AVUser.getCurrentUser()){
            LCChatKit.getInstance().open(AVUser.getCurrentUser().getObjectId(), new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                }
            });
        }

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mLocationErrText = (TextView)this.rootView.findViewById(R.id.location_errInfo_text);
        //开发设置事件
        locationSwitch=(Switch)this.rootView.findViewById(R.id.share_location_switch);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(null!=AVUser.getCurrentUser()){
                        new AlertDialog.Builder(context).setTitle("注意").setMessage("确定共享位置吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mShareFlag=true;
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        locationSwitch.setChecked(false);
                                    }
                                })
                                .show();
                    }
                    else{
                        //显示请登陆窗体
                        showDialogForCheckLogin();
                    }
                } else {
                    if(null!=AVUser.getCurrentUser()){
                        //清除附近记录
                        clearUserInfo();
                    }
                    else{
                        //显示请登陆窗体
                        showDialogForCheckLogin();
                    }
                }
            }
        });
        //通讯录按钮及其事件
        tourpalBtn=(ImageButton)this.rootView.findViewById(R.id.tourpal_btn);
        tourpalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=AVUser.getCurrentUser()) {
                    Intent intent = new Intent();
                    intent.setClass((Activity) context, TourpalConversationListActivity.class);
                    context.startActivity(intent);
                }
                else {
                    //显示请登陆窗体
                    showDialogForCheckLogin();
                }
            }
        });
        mLocationErrText.setVisibility(View.GONE);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setOnMarkerClickListener(this);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle();
    }

    private void setupLocationStyle(){
        // 自定义系统定位蓝点
        final MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
//                fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(R.color.white);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(3);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mLocationErrText.setVisibility(View.GONE);
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                this.locationPoint=new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());

                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, RADIUS);//添加定位精度圆

                    //获取当前用户信息
                    AVUser currentuser = AVUser.getCurrentUser();
                    addMarker(currentuser,location);//添加定位图标
                    //将自己添加到通讯人物列表
                    CustomUserProvider.getInstance().addUserData(currentuser);

                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(RADIUS);
//                    mLocMarker.setPosition(location);
                }
                //判断是否上传位置信息和搜索附近，只有上传位置才能搜索附近
                if(mFirstFix && mShareFlag){
                    mShareFlag=false;
                    //构造上传位置信息
                    upUserInfo(amapLocation);
                    //设置搜索条件，调用搜索附近信息
                    searchUserInfo();
                }
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
                mLocationErrText.setVisibility(View.VISIBLE);
                mLocationErrText.setText(errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this.context);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    //画圆范围
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    //根据用户添加用户头像到地图定位
    private void addMarker(final AVUser user, final LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        //根据登录情况初始化，显示头像
//        AVUser currentuser = AVUser.getCurrentUser();
        if(user != null) {
            AVFile file = user.getAVFile("user_head_image");
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    Bitmap bMap= MainActivity.Bytes2Bimap(bytes);
                    BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(makeRoundCorner(resizeImage(bMap,MAKER_SIZE,MAKER_SIZE)));
                    MarkerOptions options = new MarkerOptions();
                    options.icon(des);
                    options.anchor(0.5f, 0.5f);
                    options.position(latlng);
                    Marker marker = aMap.addMarker(options);
                    //设置用户的id到title中
                    marker.setTitle(user.getObjectId());
                }
            });
        }
    }

    @Override
    public void onUserInfoCleared(int i) {
//        ToastUtil.show(this.context,"清除回调结果："+i);
    }

    /*
     * 查询附近回调函数
     */
    @Override
    public void onNearbyInfoSearched(NearbySearchResult nearbySearchResult, int resultCode) {
        //搜索周边附近用户回调处理
        if(resultCode == 1000){
            if (nearbySearchResult != null
                    && nearbySearchResult.getNearbyInfoList() != null
                    && nearbySearchResult.getNearbyInfoList().size() > 0) {
                NearbyInfo nearbyInfo = nearbySearchResult.getNearbyInfoList().get(0);
                ToastUtil.show(this.context,"周边搜索结果数为： "+ nearbySearchResult.getNearbyInfoList().size());

                //查询搜索到附近的用户进行用户定位放置maker和添加通讯任务列表
                for(final NearbyInfo info:nearbySearchResult.getNearbyInfoList()){
                    if( !info.getUserID().equals(AVUser.getCurrentUser().getObjectId())) {
                        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
                        userQuery.whereEqualTo("objectId",info.getUserID() ); //找到对应的用户
                        userQuery.findInBackground(new FindCallback<AVUser>() {
                            @Override
                            public void done(List<AVUser> list, AVException e) {
                                if (e == null) {
                                    if (list.size() != 0) {
                                        AVUser user = list.get(0);
                                        //得到用户添加到地图上
                                        addMarker(user,new LatLng(info.getPoint().getLatitude(), info.getPoint().getLongitude()));
                                        //添加用户信息
                                        CustomUserProvider.getInstance().addUserData(user);
                                    }
                                }
                            }
                        });
                    }
                }
                LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
            } else {
                ToastUtil.show(this.context,"周边搜索结果为空");
            }
        }
        else{
            ToastUtil.show(this.context,"周边搜索出现异常，异常码为："+resultCode);
        }
    }

    /*
     * 上传地理位置到高德地图后回调方法
     */
    @Override
    public void onNearbyInfoUploaded(int i) {
//        ToastUtil.show(this.context,"上传回调结果："+i);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (aMap != null) {
            //开启聊天窗体
            Intent intent = new Intent((Activity)context, LCIMConversationActivity.class);
            // 传入对方的 Id 即可
            intent.putExtra(LCIMConstants.PEER_ID, marker.getTitle());
            ((Activity)context).startActivity(intent);
        }
//        Toast.makeText(this.context, "您点击了Marker"+marker.getId(), Toast.LENGTH_LONG).show();
        return true;
    }

    //使用Bitmap加Matrix来缩放
    public  Bitmap resizeImage(Bitmap bitmap, int w, int h)
    {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    //将矩形图片变成圆形
    public Bitmap makeRoundCorner(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    //显示请登录弹框
    private void showDialogForCheckLogin(){
        new AlertDialog.Builder(context).setTitle("注意").setMessage("请用户先登录。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
        locationSwitch.setChecked(false);
    }

    //搜索用户信息
    private void searchUserInfo(){
        //设置搜索条件
        NearbySearch.NearbyQuery query = new NearbySearch.NearbyQuery();
        //设置搜索的中心点
        query.setCenterPoint(this.locationPoint);
        //设置搜索的坐标体系
        query.setCoordType(NearbySearch.AMAP);
        //设置搜索半径
        query.setRadius((int) RADIUS);
        //设置查询的时间
        query.setTimeRange(10000);
        //设置查询的方式驾车还是距离
//                    query.setType(NearbySearchFunctionType.DRIVING_DISTANCE_SEARCH);
        //调用异步查询接口
        NearbySearch.getInstance(this.context.getApplicationContext())
                .searchNearbyInfoAsyn(query);
    }


    //上传用户信息
    private void upUserInfo(AMapLocation amapLocation){
        //构造上传位置信息
        UploadInfo loadInfo = new UploadInfo();
        //设置上传位置的坐标系支持AMap坐标数据与GPS数据
        loadInfo.setCoordType(NearbySearch.AMAP);
        //设置上传数据位置,位置的获取推荐使用高德定位sdk进行获取
        loadInfo.setPoint(new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude()));
        //根据登录情况初始化
        AVUser currentuser = AVUser.getCurrentUser();
        //设置上传用户id
        loadInfo.setUserID(currentuser.getObjectId());
        //调用异步上传接口
        NearbySearch.getInstance(this.context.getApplicationContext())
                .uploadNearbyInfoAsyn(loadInfo);
    }

    //清除用户信息
    public void clearUserInfo(){
        //获取附近实例，并设置要清楚用户的id
        NearbySearch.getInstance(this.context.getApplicationContext()).setUserID(AVUser.getCurrentUser().getObjectId());
        //调用异步清除用户接口
        NearbySearch.getInstance(this.context.getApplicationContext())
                .clearUserInfoAsyn();
    }
}
