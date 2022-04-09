package com.example.ucontrolcompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceScanActivity extends BluetoothTest {

    private Button startBtn, connectBtn;
    private BluetoothLeScanner bluetoothLeScanner = btAdapter.getBluetoothLeScanner();
    private boolean scanning;
    private Handler handler = new Handler();
    private String address;
    // Stops scanning after 10 seconds
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.BLUETOOTH, BT_PERM_CODE);
                if (btGranted)
                    scanLeDevice();
                else
                    Toast.makeText(DeviceScanActivity.this, "Scan perm not granted.", Toast.LENGTH_SHORT).show();
            }
        });

        connectBtn = (Button) findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.BLUETOOTH, BT_PERM_CODE);
                if (btGranted)
                {
                    //Log.w("1","Test");
                    Intent myIntent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
                    myIntent.putExtra("Address", address);
                    startActivityForResult(myIntent, 0);
                }
                else
                    Toast.makeText(DeviceScanActivity.this, "Please give BT perm.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scanLeDevice() {
        if (!scanning) {
            //Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    checkPermission(Manifest.permission.BLUETOOTH, BT_PERM_CODE);
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            checkPermission(Manifest.permission.BLUETOOTH, BT_PERM_CODE);
            bluetoothLeScanner.startScan(leScanCallback);
        }
        else
        {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            checkPermission(Manifest.permission.BLUETOOTH, BT_PERM_CODE);
            Log.w("Scanner", result.getDevice().getName());
            if (result.getDevice().getName() != null && result.getDevice().getName().contains("Adafruit")) {
                Toast.makeText(DeviceScanActivity.this, "Address: " + result.getDevice().getAddress(), Toast.LENGTH_LONG).show();
                address = result.getDevice().getAddress();
            }
        }
    };
}