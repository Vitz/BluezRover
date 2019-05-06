package com.example.myapplication;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication.devices.BTDev;
import com.example.myapplication.devices.MyBluetoothService;

import static java.lang.Thread.sleep;

public class MainActivity extends Activity
{
    TextView tvProgressLabel1;
    TextView tvProgressLabel2;
    ListView listView;
    Switch switchConnectionState;
    static BTDev btDev;
    SliderController sController;

    public static BTDev getDevice() {
        return btDev;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeekBar seekBarLeft = findViewById(R.id.seekBarLeft);
        seekBarLeft.setOnSeekBarChangeListener(seekBarChangeListenerLeft);
        SeekBar seekBarRight = findViewById(R.id.seekBarRight);
        seekBarRight.setOnSeekBarChangeListener(seekBarChangeListenerRight);
        tvProgressLabel1 = findViewById(R.id.textViewLeft);
        tvProgressLabel1.setText(" " + Helpers.normalizeTo0(seekBarLeft.getProgress()));
        tvProgressLabel2 = findViewById(R.id.textViewRight);
        tvProgressLabel2.setText(" " + Helpers.normalizeTo0(seekBarRight.getProgress()));
        switchConnectionState = findViewById(R.id.switch1);
        switchConnectionState.setChecked(false);
        switchConnectionState.setOnCheckedChangeListener(switchListener);

        Log.i("BTDev", "try init");
        btDev =  new BTDev();
        btDev.initDevice();
        listView = (ListView) findViewById(R.id.listview);
        Log.i("BTDev", "inited");

        ArrayAdapter view_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, btDev.getAvailableDevicesReadAble());
        listView.setAdapter(view_adapter);
        listView.setOnItemClickListener(choseDeviceListener);   }


    ListView.OnItemClickListener choseDeviceListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String chosenElement = (String) parent.getItemAtPosition(position);
            Log.i("MainActivity", "chosen device:" + chosenElement);
            btDev.initConnection(position);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (btDev.isConnected()) {
                        btDev.sendData(PckManager.buildMovePck(1, 1));
                    } }
                }
            });
            thread.start();


            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    Switch.OnCheckedChangeListener switchListener = new Switch.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                btDev.cancel();
            }
        }
    };


    SeekBar.OnSeekBarChangeListener seekBarChangeListenerLeft = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i("Slider", Integer.toString(Helpers.normalizeTo0(progress)));
            tvProgressLabel1.setText(" " + Helpers.normalizeTo0(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setProgress(50);
        }
    };
    SeekBar.OnSeekBarChangeListener seekBarChangeListenerRight = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            tvProgressLabel2.setText(" " + Helpers.normalizeTo0(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setProgress(50);
        }
    };

}


