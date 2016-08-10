package com.wang.demo.bledemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * 要求在20以上。5.0，在18以上可以，蓝牙4.0
     */
    final String SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    UUID uuid = UUID.fromString(SPP_UUID);
    BluetoothAdapter btAdapter;
    BluetoothLeScanner scanner ;
    Button startScan;
    TextView showScanList;
    List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    String deviceName = "";
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                {
                    showScanList.setText(deviceName);
                    break;
                }
                case 2:
                {
                    showScanList.setText(deviceName+"扫描结束");
                    break;
                }

            }
        }
    };
    private ScanCallback mScanCallback =
            new ScanCallback(){
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device =result.getDevice();
                    if (device != null){
                        //过滤掉其他设备
                        if (device.getName() != null){
                            bluetoothDeviceList.add(device);
                            deviceName = deviceName + device.getName()+"\n";
                            handler.sendEmptyMessage(1);
                        }
                    }

                }
                @Override
                public void onBatchScanResults(List<ScanResult> results) {

                    Log.d("callback","onBatchScanResults");
                }


                @Override
                public void onScanFailed(int errorCode) {
                    Log.d("callback","onScanFailed");
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        scanner = btAdapter.getBluetoothLeScanner();
        openBLE();



        initLayout();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startscan:{
                scanLeDevice();
                break;
            }
        }
    }

    void initLayout(){
        startScan = (Button) findViewById(R.id.startscan);
        showScanList = (TextView) findViewById(R.id.show_scan_list);

        startScan.setOnClickListener(this);
    }
    void openBLE(){
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }
    void scanLeDevice(){
        scanner.startScan(mScanCallback);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopscanDevice();
            }
        },10000);
    }
    void stopscanDevice(){
        scanner.stopScan(mScanCallback);
        handler.sendEmptyMessage(2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopscanDevice();
    }
}
