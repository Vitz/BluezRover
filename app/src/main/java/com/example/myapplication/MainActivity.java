package com.example.myapplication;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.devices.BTDev;

import static java.lang.Thread.sleep;

public class MainActivity extends Activity
{
    TextView tvProgressLabel1;
    TextView tvProgressLabel2;
    TextView tvStateLabel;
    ListView listView;
    Button buttonStopConnection;
    static BTDev btDev;
    Thread thread;
    boolean stopThread;


    public static BTDev getDevice() {
        return btDev;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SeekBar seekBarLeft = findViewById(R.id.seekBarLeft);
        seekBarLeft.setOnSeekBarChangeListener(seekBarChangeListenerLeft);
        SeekBar seekBarRight = findViewById(R.id.seekBarRight);
        seekBarRight.setOnSeekBarChangeListener(seekBarChangeListenerRight);
        tvProgressLabel1 = findViewById(R.id.textViewLeft);
        tvProgressLabel1.setText(" " + Helpers.normalizeTo0(seekBarLeft.getProgress()));
        tvProgressLabel2 = findViewById(R.id.textViewRight);
        tvProgressLabel2.setText(" " + Helpers.normalizeTo0(seekBarRight.getProgress()));
        tvStateLabel = findViewById(R.id.textViewState);
        tvStateLabel.setText("Hello!");

        buttonStopConnection = findViewById(R.id.button1);
        buttonStopConnection.setOnClickListener(buttonListener);
        stopThread = false;
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
            stopThread = false;
            String chosenElement = (String) parent.getItemAtPosition(position);
            Log.i("MainActivity", "chosen device:" + chosenElement);
            btDev.initConnection(position);

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        if (stopThread) {
                            try {
                                sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        try {
                            sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (btDev.isConnected()) {
                            int left = Integer.parseInt(((String) tvProgressLabel1.getText()).replaceAll("\\s",""));
                            int right = Integer.parseInt(((String) tvProgressLabel2.getText()).replaceAll("\\s",""));
                            byte[] data  = PckManager.buildMovePck(left, right);
                            btDev.sendData(data);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvStateLabel.setText(btDev.getLastSentString());
                                }
                            });
                        }
                    else{
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
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

    Button.OnClickListener buttonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View buttonView) {
            stopThread = true;
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            btDev.cancel();
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


