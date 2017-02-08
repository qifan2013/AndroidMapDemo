package com.fan.map.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fan.map.demo.amap.CustomLocation2DActivity;
import com.fan.map.demo.amap.CustomLocationActivity;
import com.fan.map.demo.baidu.LocationMark;
import com.fan.map.demo.gps.GPSService;
import com.fan.map.demo.tecent.ShowMyLocationActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnGps, btnBaidu, btnGaode, btnTecent,btnAmap3D;
    private TextView tvGpsResult;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvGpsResult = (TextView) findViewById(R.id.gps_result);
        btnGps = (Button) findViewById(R.id.gps);
        btnBaidu = (Button) findViewById(R.id.baidu);
        btnTecent = (Button) findViewById(R.id.tecent);
        btnGaode = (Button) findViewById(R.id.gaode);
        btnAmap3D = (Button) findViewById(R.id.gaode3d);
        btnGps.setOnClickListener(this);
        btnBaidu.setOnClickListener(this);
        btnGaode.setOnClickListener(this);
        btnTecent.setOnClickListener(this);
        btnAmap3D.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.gps:
                Log.d("qifan","click" );

                intent = new Intent(MainActivity.this, GPSService.class);
                startService(intent);
                break;
            case R.id.baidu:
                intent = new Intent(MainActivity.this, LocationMark.class);
                startActivity(intent);
                break;
            case R.id.tecent:
                intent = new Intent(MainActivity.this, ShowMyLocationActivity.class);
                startActivity(intent);
                break;
            case R.id.gaode:
                intent = new Intent(MainActivity.this, CustomLocation2DActivity.class);
                startActivity(intent);
                break;
            case R.id.gaode3d:
                intent = new Intent(MainActivity.this, CustomLocationActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


}
