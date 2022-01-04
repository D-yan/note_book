package com.example.finaltext01;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MapActivity extends AppCompatActivity {


    private MapView mMapView;

    private LocationClient mLocClient;
    private BaiduMap mBaiduMap;

    private BitmapDescriptor bitmap;//标点的图标
    private double markerLatitude = 0;//标点纬度
    private double markerLongitude = 0;//标点经度
    private ImageButton ibLocation;//重置定位按钮
    private Marker marker;//标点也可以说是覆盖物

    private Button closebutton;

    public static MapActivity mapactivity;//这个静态变量用来关闭这个activity


    LocationManager mLocationManager;
    Location mlocation;
    TextView mTextView;

    String loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        initView();//视图初始化

        checkVersion();//检查版本

        mapOnClick();//地图点击

        //返回定位信息的按钮
        closebutton = (Button) findViewById(R.id.ReInfoBotton);
        closebutton.setOnClickListener(new ReturnButtonListener());

        //loc=getLocation().getAddrStr().toString();
    }

    private class ReturnButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            //setClass函数的第一个参数是一个Context对象
            //Context是一个类,Activity是Context类的子类,也就是说,所有的Activity对象都可以向上转型为Context对象
            //setClass函数的第二个参数是Class对象,在当前场景下,应该传入需要被启动的Activity的class对象
            intent.setClass(MapActivity.this, new_cost.class);
            intent.putExtra("LocationInfo", loc);
            startActivity(intent);
            finish();
        }
    }

    //    //获取location这个系统类
//    public Location getLocation() {
//        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            //return TODO;
//        }
//        mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (mlocation == null) {
//            mlocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        }
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, mLocationListener);
//        //String Loc=mlocation.getAddrStr();
//        return mlocation;
//    }
//
//    LocationListener mLocationListener = new LocationListener() {
//        @TargetApi(17)
//        @Override
//        public void onLocationChanged(Location mlocal) {
//            if(mlocal == null) return;
//            String strResult = "getAccuracy:" + mlocal.getAccuracy() + "\r\n"
//                    + "getAltitude:" + mlocal.getAltitude() + "\r\n"
//                    + "getBearing:" + mlocal.getBearing() + "\r\n"
//                    + "getElapsedRealtimeNanos:" + String.valueOf(mlocal.getElapsedRealtimeNanos()) + "\r\n"
//                    + "getLatitude:" + mlocal.getLatitude() + "\r\n"
//                    + "getLongitude:" + mlocal.getLongitude() + "\r\n"
//                    + "getProvider:" + mlocal.getProvider()+ "\r\n"
//                    + "getSpeed:" + mlocal.getSpeed() + "\r\n"
//                    + "getTime:" + mlocal.getTime() + "\r\n";
//            Log.i("Show", strResult);
//            if (mTextView != null) {
//                mTextView.setText(strResult);
//            }
//        }
//
//        @Override
//        public void onProviderDisabled(String arg0) {
//        }
//
//        @Override
//        public void onProviderEnabled(String arg0) {
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int event, Bundle extras) {
//        }
//    };

    /**
     * 检查版本
     */
    private void checkVersion() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            RxPermissions rxPermissions = new RxPermissions(this);
            //permission request
            rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {//申请成功
                            //发起连续定位请求
                            initLocation();// 定位初始化
                        } else {//申请失败
                            Toast.makeText(MapActivity.this,"权限未开启",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            initLocation();// 定位初始化
        }
    }

    private void initView() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        //回到当前定位
        ibLocation = (ImageButton) findViewById(R.id.ib_location);
        mMapView.showScaleControl(true);  // 设置比例尺是否可见（true 可见/false不可见）
        //mMapView.showZoomControls(false);  // 设置缩放控件是否可见（true 可见/false不可见）
        mMapView.removeViewAt(1);// 删除百度地图Logo

        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final String info = (String) marker.getExtraInfo().get("info");
                Toast.makeText(MapActivity.this, info, Toast.LENGTH_SHORT).show();
                return true;

            }
        });
    }

    /**
     * 地图点击
     */
    private void mapOnClick() {
        // 设置marker图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }

            //此方法就是点击地图监听
            @Override
            public void onMapClick(LatLng latLng) {
                //获取经纬度
                markerLatitude = latLng.latitude;
                markerLongitude = latLng.longitude;
                //先清除图层
                mBaiduMap.clear();
                // 定义Maker坐标点
                LatLng point = new LatLng(markerLatitude, markerLongitude);
                // 构建MarkerOption，用于在地图上添加Marker
                MarkerOptions options = new MarkerOptions().position(point)
                        .icon(bitmap);
                // 在地图上添加Marker，并显示
                //mBaiduMap.addOverlay(options);
                marker = (Marker) mBaiduMap.addOverlay(options);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", "纬度：" + markerLatitude + "   经度：" + markerLongitude);
                marker.setExtraInfo(bundle);//将bundle值传入marker中，给baiduMap设置监听时可以得到它

                //点击地图之后重新定位
                initLocation();
            }
        });

    }


    /**
     * 定位初始化
     */
    public void initLocation() {

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        MyLocationListener myListener = new MyLocationListener();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setOpenGps(true);// 打开gps
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置高精度定位
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocClient.setLocOption(option);
        mLocClient.start();//开始定位
    }

    /**
     * 点切换到其他标点位置时，重置定位显示，点击之后回到自动定位
     *
     * @param view
     */
    public void resetLocation(View view) {
        markerLatitude = 0;
        initLocation();
        marker.remove();//清除标点
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //show locate info
            Toast.makeText(MapActivity.this,location.getAddrStr(),Toast.LENGTH_SHORT).show();
            loc=location.getAddrStr();
            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            double resultLatitude;
            double resultLongitude;

            if (markerLatitude == 0) {//自动定位
                resultLatitude = location.getLatitude();
                resultLongitude = location.getLongitude();
                ibLocation.setVisibility(View.GONE);
            } else {//标点定位
                resultLatitude = markerLatitude;
                resultLongitude = markerLongitude;
                ibLocation.setVisibility(View.VISIBLE);
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(resultLatitude)
                    .longitude(resultLongitude)
                    .build();

            mBaiduMap.setMyLocationData(locData);// 设置定位数据, 只有先允许定位图层后设置数据才会生效
            LatLng latLng = new LatLng(resultLatitude, resultLongitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(20.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }
}
