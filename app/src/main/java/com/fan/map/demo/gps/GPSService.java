package com.fan.map.demo.gps;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
public class GPSService extends Service {
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 1.获取位置的管理者
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 2.获取定位方式
        // 2.1获取所有的定位方式，true:表示返回所有可用定位方式
        List<String> providers = locationManager.getProviders(true);
        for (String string : providers) {
            System.out.println(string);
        }
        // 2.2获取最佳的定位方式
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(true);// 设置是否可以定位海拔,如果设置定位海拔，返回一定是gps
        // criteria : 设置定位属性
        // enabledOnly : true如果定位可用就返回
        String bestProvider = locationManager.getBestProvider(criteria, true);
        System.out.println("最佳的定位方式:" + bestProvider);
        // 3.定位
        myLocationListener = new MyLocationListener();
        // provider : 定位的方式
        // minTime : 定位的最小时间间隔
        // minDistance : 定位最小的间隔距离
        // LocationListener : 定位监听
        locationManager.requestLocationUpdates(bestProvider, 0, 0,
                myLocationListener);
        Log.d("qifan","start" );
    }
    private class MyLocationListener implements LocationListener{
        //当定位位置改变的调用的方法
        //Location : 当前的位置
        @Override
        public void onLocationChanged(Location location) {
            location.getAccuracy();//获取精确位置
            location.getAltitude();//获取海拔
            double latitude = location.getLatitude();//获取纬度，平行
            double longitude = location.getLongitude();//获取经度，垂直

            //发送坐标给指定手机号码
            Log.d("qifan","onLocationChanged" + latitude);
            //停止服务,但是必须得是startservice开启
            stopSelf();

        }
        //当定位状态发生改变的时候调用的方式
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            Log.d("qifan","onStatusChanged" );
        }
        //当定位可用的时候调用的方法
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }
        //当定位不可用的时候调用的方法
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(myLocationListener);//关闭gps,但是高版本中规定打开和关闭gps必须由用户自己主观的去实现，代码已经不允许进行操作
    }

}