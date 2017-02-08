package com.fan.map.demo.baidu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fan.map.demo.LocationApplication;
import com.fan.map.demo.R;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;

/***
 * 定位滤波demo，实际定位场景中，可能会存在很多的位置抖动，此示例展示了一种对定位结果进行的平滑优化处理
 * 实际测试下，该平滑策略在市区步行场景下，有明显平滑效果，有效减少了部分抖动，开放算法逻辑，希望能够对开发者提供帮助
 * 注意：该示例场景仅用于对定位结果优化处理的演示，里边相关的策略或算法并不一定适用于您的使用场景，请注意！！！
 * 
 * @author baidu
 * 
 */
public class LocationMark extends Activity {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private Button reset;
	private LocationService locService;
	private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationfilter);
		mMapView = (MapView) findViewById(R.id.bmapView);
		reset = (Button) findViewById(R.id.clear);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
		locService = ((LocationApplication) getApplication()).locationService;
		LocationClientOption mOption = locService.getDefaultLocationClientOption();
		mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
		mOption.setCoorType("bd09ll");
		//locService.setLocationOption(mOption);
		//locService.registerListener(listener);
		//locService.start();

		LocationClient mClient = new LocationClient(this);
		mClient.setLocOption(mOption);
		mClient.registerLocationListener(listener);
		mClient.start();
	}

	/***
	 * 定位结果回调，在此方法中处理定位结果
	 */
	BDLocationListener listener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub

			if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
				Message locMsg = locHander.obtainMessage();
				Bundle locData;
				locData = Algorithm(location);
				if (locData != null) {
					locData.putParcelable("loc", location);
					locMsg.setData(locData);
					locHander.sendMessage(locMsg);
				}
			}
		}
	};

	/***
	 * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
	 * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
	 * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
	 * 
	 * @return Bundle
	 */
	private Bundle Algorithm(BDLocation location) {
		Bundle locData = new Bundle();
		double curSpeed = 0;
		if (locationList.isEmpty() || locationList.size() < 2) {
			LocationEntity temp = new LocationEntity();
			temp.location = location;
			temp.time = System.currentTimeMillis();
			locData.putInt("iscalculate", 0);
			locationList.add(temp);
		} else {
			if (locationList.size() > 5)
				locationList.removeFirst();
			double score = 0;
			for (int i = 0; i < locationList.size(); ++i) {
				LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
						locationList.get(i).location.getLongitude());
				LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
			}
			if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
				location.setLongitude(
						(locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
								/ 2);
				location.setLatitude(
						(locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
								/ 2);
				locData.putInt("iscalculate", 1);
			} else {
				locData.putInt("iscalculate", 0);
			}
			LocationEntity newLocation = new LocationEntity();
			newLocation.location = location;
			newLocation.time = System.currentTimeMillis();
			locationList.add(newLocation);

		}
		return locData;
	}

	/***
	 * 接收定位结果消息，并显示在地图上
	 */
	private Handler locHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			try {
				BDLocation location = msg.getData().getParcelable("loc");
				ArrayList<LatLng> points = new ArrayList<>();
				int iscal = msg.getData().getInt("iscalculate");
				if (location != null) {
					Log.d("qifan","location.getLatitude() "+location.getLatitude());
					LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
					points.add(point);
					points.add(new LatLng(location.getLatitude()+0.001, location.getLongitude()));
					points.add(new LatLng(location.getLatitude(), location.getLongitude()+0.001));
					// 构建Marker图标
					BitmapDescriptor bitmap = null;
					BitmapDescriptor bitmap2 = null;
					BitmapDescriptor bitmap3 = null;

//					if (iscal == 0) {
//						bitmap = BitmapDescriptorFactory.fromResource(R.drawable.huaji); // 非推算结果
//					} else {
//						bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark); // 推算结果
//					}

//					bitmap = BitmapDescriptorFactory.fromResource(R.drawable.avatar1);
//					bitmap2 = BitmapDescriptorFactory.fromResource(R.drawable.avatar2);
//					bitmap3 = BitmapDescriptorFactory.fromResource(R.drawable.avatar3);
					bitmap = BitmapDescriptorFactory.fromBitmap(toRoundBitmap(getResources().getDrawable(R.drawable.avatar1)));
					bitmap2 = BitmapDescriptorFactory.fromBitmap(toRoundBitmap(getResources().getDrawable(R.drawable.avatar2)));
					bitmap3 = BitmapDescriptorFactory.fromBitmap(toRoundBitmap(getResources().getDrawable(R.drawable.avatar3)));

					// 构建MarkerOption，用于在地图上添加Marker
					OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
					// 在地图上添加Marker，并显示
					mBaiduMap.addOverlay(option);

					mBaiduMap.addOverlay(new MarkerOptions().position(points.get(1)).icon(bitmap2));
					mBaiduMap.addOverlay(new MarkerOptions().position(points.get(2)).icon(bitmap3));

					mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//		WriteLog.getInstance().close();
		locService.unregisterListener(listener);
		locService.stop();
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mBaiduMap != null)
					mBaiduMap.clear();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();

	}

	/**
	 * 封装定位结果和时间的实体类
	 * 
	 * @author baidu
	 *
	 */
	class LocationEntity {
		BDLocation location;
		long time;
	}


	public Bitmap toRoundBitmap(Drawable drawable)
	{

		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap.Config config =drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w,h,config);
		Canvas canvas1 = new Canvas(bitmap);// 建立对应bitmap的画布
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas1);// 把drawable内容画到画布中
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
		if (width <= height) {
			roundPx = width / 2 -5;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2 -5;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width,
				height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
		final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
		final RectF rectF = new RectF(dst_left+15, dst_top+15, dst_right-20, dst_bottom-20);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
}
