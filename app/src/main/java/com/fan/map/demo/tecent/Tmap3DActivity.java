package com.fan.map.demo.tecent;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.fan.map.demo.ImageUtils;
import com.fan.map.demo.R;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.Circle;
import com.tencent.tencentmap.mapsdk.maps.model.CircleOptions;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;

public class Tmap3DActivity extends FragmentActivity implements TencentLocationListener,SensorEventListener {


    private TencentMap tencentMap;
    private ImageButton btnShowLocation ;
    private Marker myLocation;
    private Circle accuracy;
    private TencentLocationManager locationManager;
    private TencentLocationRequest locationRequest;
    private SensorManager sensorManager;
    private Sensor oritationSensor;
    private int error;
    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_tmap3d);
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment =
                (SupportMapFragment)fm.findFragmentById(R.id.frag_map);
        tencentMap = mapFragment.getMap();
        tencentMap.getUiSettings().setZoomControlsEnabled(false);
        btnShowLocation = (ImageButton)findViewById(R.id.btn_show_location);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        oritationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        locationManager = TencentLocationManager.getInstance(this);
        locationRequest = TencentLocationRequest.create();
        bindListener();
    }

    protected void bindListener() {
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int error = locationManager.requestLocationUpdates(
                        locationRequest, Tmap3DActivity.this);
                switch (error) {
                    case 0:
                        Log.e("location", "成功注册监听器");
                        break;
                    case 1:
                        Log.e("location", "设备缺少使用腾讯定位服务需要的基本条件");
                        break;
                    case 2:
                        Log.e("location", "manifest 中配置的 key 不正确");
                        break;
                    case 3:
                        Log.e("location", "自动加载libtencentloc.so失败");
                        break;

                    default:
                        break;
                }
                sensorManager.registerListener(Tmap3DActivity.this,
                        oritationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (tencentMap.isMyLocationEnabled()) {
            tencentMap.setMyLocationEnabled(false);
        }
        locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onLocationChanged(TencentLocation arg0, int arg1, String arg2) {
        if (arg1 == TencentLocation.ERROR_OK) {
            LatLng latLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
            LatLng latLng1 = new LatLng(arg0.getLatitude()+0.001, arg0.getLongitude());
            LatLng latLng2 = new LatLng(arg0.getLatitude(), arg0.getLongitude()+0.001);
            if (myLocation == null) {
                myLocation = tencentMap.addMarker(new MarkerOptions().
                        position(latLng).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point)).
                        anchor(0.5f, 0.5f));
                tencentMap.addMarker(new MarkerOptions().position(latLng1).icon(BitmapDescriptorFactory.fromBitmap(ImageUtils.toRoundBitmap(getResources().getDrawable(R.drawable.avatar1)))));
                tencentMap.addMarker(new MarkerOptions().position(latLng2).icon(BitmapDescriptorFactory.fromBitmap(ImageUtils.toRoundBitmap(getResources().getDrawable(R.drawable.avatar2)))));
            }
            if (accuracy == null) {
                accuracy = tencentMap.addCircle(new CircleOptions().
                        center(latLng).
                        radius((double)arg0.getAccuracy()).
                        fillColor(0x440000ff).
                        strokeWidth(0f));
            }
            myLocation.setPosition(latLng);
//			myLocation.setRotation(arg0.getBearing()); //仅当定位来源于gps有效，或者使用方向传感器
            accuracy.setCenter(latLng);
            accuracy.setRadius(arg0.getAccuracy());
            tencentMap.animateToNaviPosition(latLng,0,0);
        } else {
            Log.e("location", "location failed:" + arg2);
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (myLocation != null) {
            myLocation.setRotation(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
}
