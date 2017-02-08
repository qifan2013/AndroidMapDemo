package com.fan.map.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fan.map.demo.baidu.LocationMark;
import com.fan.map.demo.gps.GPSService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnGps, btnBaidu, btnGaode, btnTecent;
    private TextView tvGpsResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvGpsResult = (TextView) findViewById(R.id.gps_result);
        btnGps = (Button) findViewById(R.id.gps);
        btnBaidu = (Button) findViewById(R.id.baidu);
        btnTecent = (Button) findViewById(R.id.tecent);
        btnGaode = (Button) findViewById(R.id.gaode);

        btnGps.setOnClickListener(this);
        btnBaidu.setOnClickListener(this);
        btnGaode.setOnClickListener(this);
        btnTecent.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gps:
                Log.d("qifan","click" );

                Intent gpsIntent = new Intent(MainActivity.this, GPSService.class);
                startService(gpsIntent);
                break;
            case R.id.baidu:
                Intent intent = new Intent(MainActivity.this, LocationMark.class);
                startActivity(intent);
                break;
            case R.id.tecent:
                break;
            case R.id.gaode:
                break;
            default:
                break;
        }
    }


}
