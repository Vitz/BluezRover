package com.example.myapplication;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import com.example.myapplication.devices.BTDev;

public class SliderController extends Activity {
    Switch switchConnectionState;
    BTDev btDev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("SliderController", "Init");


        switchConnectionState = findViewById(R.id.switch1);
        btDev =  MainActivity.getDevice();

        try {
            if (btDev.isConnected()) switchConnectionState.setChecked(true);
            else switchConnectionState.setChecked(false);
            Log.i("SliderController", Boolean.toString(btDev.isConnected()));
        } catch (Exception e) {
            Log.i("SliderController:Err", e.toString());

        }


    }

}



