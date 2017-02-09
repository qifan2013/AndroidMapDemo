package com.fan.map.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fan.map.demo.amap.Amap2DActivity;
import com.fan.map.demo.amap.Amap3DActivity;
import com.fan.map.demo.baidu.BmapLocationMark;
import com.fan.map.demo.gps.GPSService;
import com.fan.map.demo.tecent.Tmap2DActivity;
import com.fan.map.demo.tecent.Tmap3DActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnGps, btnBaidu,btnTecent2D,btnTecent3D ,btnAmap2D,btnAmap3D;
    private TextView tvGpsResult;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvGpsResult = (TextView) findViewById(R.id.gps_result);
        btnGps = (Button) findViewById(R.id.gps);
        btnBaidu = (Button) findViewById(R.id.baidu);
        btnTecent2D = (Button) findViewById(R.id.tecent2d);
        btnTecent3D = (Button) findViewById(R.id.tecent3d);
        btnAmap2D = (Button) findViewById(R.id.gaode2d);
        btnAmap3D = (Button) findViewById(R.id.gaode3d);
        btnGps.setOnClickListener(this);
        btnBaidu.setOnClickListener(this);
        btnTecent2D.setOnClickListener(this);
        btnTecent3D.setOnClickListener(this);
        btnAmap2D.setOnClickListener(this);
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
                intent = new Intent(MainActivity.this, BmapLocationMark.class);
                startActivity(intent);
                break;
            case R.id.tecent2d:
                intent = new Intent(MainActivity.this, Tmap2DActivity.class);
                startActivity(intent);
                break;
            case R.id.tecent3d:
                intent = new Intent(MainActivity.this, Tmap3DActivity.class);
                startActivity(intent);
                break;
            case R.id.gaode2d:
                intent = new Intent(MainActivity.this, Amap2DActivity.class);
                startActivity(intent);
                break;
            case R.id.gaode3d:
                intent = new Intent(MainActivity.this, Amap3DActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


}
