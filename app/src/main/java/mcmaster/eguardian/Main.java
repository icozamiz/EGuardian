package mcmaster.eguardian;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mcmaster.eguardian.domain.BluetoothLeService;
import mcmaster.eguardian.domain.HeartRate;

/**
 * Created by i on 2016-11-20.
 */

public class Main extends AppCompatActivity {
    private static final long SCAN_PERIOD = 1000 ;
    private int numSteps = 100;
    private int bpm = 76;
    private double sleepHrs = 7.5;
    private BluetoothLeService mBluetoothLeService = new BluetoothLeService();
    BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothAdapter mBluetoothAdapter;
    private String TAG = "Bluetooth";
    private String mDeviceAddress ="98:4F:EE:10:7C:CC";
    private String mDeviceName;
    private Handler mHandler;
    private boolean mScanning;
    private Context context = this;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress, mBluetoothAdapter);
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private boolean mConnected;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
               BluetoothGattService heartRateService =  mBluetoothLeService.getHeartRateService();
                if (heartRateService == null){
                    Log.e("BLUETOOTH", "services not found");
                    return;
                }
                BluetoothGattCharacteristic characteristic =
                        heartRateService.getCharacteristic(mBluetoothLeService.UUID_HEART_RATE_MEASUREMENT);
                if (characteristic != null && characteristic.getUuid().equals(mBluetoothLeService.UUID_HEART_RATE_MEASUREMENT)){
                    final int charaProp = characteristic.getProperties();

                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
                        if (mNotifyCharacteristic != null) {
                            mBluetoothLeService.setCharacteristicNotification(
                                    mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(characteristic);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);
                    }
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                int hR = Integer.valueOf(intent.getStringExtra(BluetoothLeService.HEART_RATE_DATA));
                mBluetoothLeService.saveHeartRate(hR);
                final TextView heartRate = (TextView) findViewById(R.id.heartRate);
                heartRate.setText("Heart Rate: " + hR + " bpm");

                if (intent.getStringExtra(BluetoothLeService.FALL_DATA).equals("FALL OCCURRED")){
                    Context ctx = getApplicationContext();
                    CharSequence text = "CAUTION: FALL MAY HAVE OCCURRED!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(ctx, text, duration);
                    toast.show();
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(mDeviceAddress, mBluetoothAdapter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        if (mBluetoothLeService != null){
            mBluetoothLeService.disconnect();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BLUETOOTH STUFF
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        scanLeDevice(true);

        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // INITIALISE DATABASE
        SugarContext.init(context);

        setContentView(R.layout.content_main);
        ImageButton alertsButton = (ImageButton) findViewById(R.id.AlertsButton);
        ImageButton activityButton = (ImageButton) findViewById(R.id.ActivityButton);
        ImageButton sleepButton = (ImageButton) findViewById(R.id.SleepActivityButton);
        ImageButton dashboardButton = (ImageButton) findViewById(R.id.dashBoardButton);
        final TextView sleepPattern = (TextView) findViewById(R.id.sleepPattern);
        final TextView activity = (TextView) findViewById(R.id.activity);
        final TextView heartRate = (TextView) findViewById(R.id.heartRate);


        HeartRate hr = new HeartRate();
        heartRate.setText("Heart Rate: No Connection");
        sleepPattern.setText("Sleep: " + hr.getHoursOfSleep(new Date()) + " hrs");
        activity.setText("Activity: " + hr.getSteps(new Date()) + " steps");
        /*
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                HeartRate hr = new HeartRate();
                                sleepPattern.setText("Sleep: " + hr.getHoursOfSleep(new Date()) + " hrs");
                                activity.setText("Activity: " + hr.getSteps(new Date()) + " steps");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
*/
        sleepButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Main.this, SleepActivity.class);
                startActivity(i);
            }
        });
        activityButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Main.this, PhysicalActivity.class);
                startActivity(i);
            }
        });
        dashboardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Main.this, Main.class);
                startActivity(i);
            }
        });
        alertsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Main.this, Alerts.class);
                startActivity(i);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (device.getAddress().equals("98:4F:EE:10:7C:CC")){
                                mDeviceName = device.getName();
                                mDeviceAddress = device.getAddress();
//                                Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
//                                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                            }
                        }
                    });
                }
            };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}