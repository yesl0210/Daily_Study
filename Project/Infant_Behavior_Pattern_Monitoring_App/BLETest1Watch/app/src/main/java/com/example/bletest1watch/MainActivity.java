package com.example.bletest1watch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
    // Debugging
    private static final String TAG = "BluetoothMain";

    // * UI * //
    public Button scanBtn, connBtn, levelBtn, measureBtn, saveNumBtn;
    public int level,saveN;

    // * Time * //
    private boolean thread_state;
    private TimeThread timeT;

    // * Bleutooth * //
    private Controller controller;
    public BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터

    // * Sensor * //
    private SensorManager manager;
    private Sensor mHeartRate, mGyro, mAccel, mStep;
    private ArrayList<String> sensorData, copyData;
    public String fileName;
    private int fileVer;

    // * Message code *
    public static final int REQUEST_ENABLE_BT = 1; // 블루투스 활성화 요청 메시지
    public static final int DISCOVERY_REQUEST = 2; // 기기가 검색될 수 있도록 활성화 요청 메시지

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3; // watch는 필요없음. 오직 read만 함.
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_OBJECT = "device_name";
    public static final String TOAST = "toast";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 켜두기

        init();
        initBLE();

        // 검색 기능 활성화 - 로컬 기기를 다른 기기가 검색할 수 있도록
        scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); // 5분간 검색가능하도록
                startActivity(discoverableIntent);
            }
        });
    }

    public void init() { // initialize & get sensor
        // for UI
        scanBtn = (Button) findViewById(R.id.discoverable);
        connBtn = (Button) findViewById(R.id.connectionState);
        levelBtn = (Button) findViewById(R.id.level);
        measureBtn = (Button) findViewById(R.id.measureState);
        saveNumBtn = (Button) findViewById(R.id.saveNumber);
        saveN = 0;

        // for sensor
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRate = manager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mGyro = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAccel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mStep = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorData = new ArrayList<String>();
        copyData = new ArrayList<String>();

        timeT = new TimeThread();
        thread_state = false;
        timeT.start();
        Log.d(TAG, "Time thread : timeT.start");
        fileVer = 1;
    }

    public void initBLE() {
        // BluetoothAdapter : 기기 자체 블루투스 송수신 장치. 이 객체를 이용해 상호작용 가능.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // BLE 지원하는지 확인
        if (bluetoothAdapter == null) { // BLE 지원하지 않을경우, 앱 종료
            Toast.makeText(this, "블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish(); // 앱 종료. 백그라운드에는 남아있음. (어떻게 할건지는 더 생각)
        }

        // 현재 블루투스가 활성화되어있는지 확인.
        if (!bluetoothAdapter.isEnabled()) { // False -> 현재 블루투스 비활성화되어있음.
            // 블루투스 활성화 요청
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else { // True -> 현재 블루투스 활성화되어있음.
            controller = new Controller(this, handler);
        }
    }

    // 블루투스 활성화 요청에 대한 액션
    public void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    controller = new Controller(this, handler);
                } else {
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case DISCOVERY_REQUEST:
                Toast.makeText(this, "It can be scanned for 5 minutes.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Controller.STATE_CONNECTED:
//                            setStatus("Connected to: " + connectingDevice.getName());
//                            connectBtn.setEnabled(false);
                            break;
                        case Controller.STATE_CONNECTING:
//                            setStatus("Connecting...");
//                            connectBtn.setEnabled(false);
                            break;
                        case Controller.STATE_LISTEN:
                        case Controller.STATE_NONE:
//                            setStatus("Not connected");
                            break;
                    }
                    break;
                case MESSAGE_READ: // 메시지 Read는 watch 만
                    Log.d(TAG,"MESSAGE_READ");

                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String[] msg_split = readMessage.split("-");

                    Log.d("watch","Command : "+msg_split[0]);
                    Toast.makeText(getApplicationContext(), "Command : "+msg_split[0], Toast.LENGTH_SHORT).show();

//                    if (msg_split[0].equals("start") && (controller.getState() == Controller.STATE_CONNECTED)){
//                        start_sensor();
//                    }
                    if (msg_split[0].equals("start") && (measureBtn.getText().toString().equals("NOT MEASURING"))){
                        // Button.getText -> to String으로 변환을 해야 equals 인가??????
                        // *****************************************************************************
                        if (connBtn.getText().toString().equals("NOT CONNECTED")){ // 연결된 상태가 아닐때
                            Toast.makeText(getApplicationContext(), "Not connected.\n"+"No start", Toast.LENGTH_SHORT).show();
                        }
                        else if (measureBtn.getText().toString().equals("NOT MEASURING")){ // 연결된 상태일때 & 측정중이 아닐때
                            start_sensor();
                        }
                    }
                    else if (msg_split[0].equals("stop") && (measureBtn.getText().toString().equals("MEASURING..."))){

                        if (connBtn.getText().toString().equals("NOT CONNECTED")){ // 연결된 상태가 아닐때
                            Toast.makeText(getApplicationContext(), "Not connected.\n"+"No stop", Toast.LENGTH_SHORT).show();
                        }
                        else if (measureBtn.getText().toString().equals("MEASURING...")){ // 연결된 상태일때 & 측정중일때
                            stop_sensor();
                        }
                    }
                    else if (msg_split[0].equals("level")){
                        level = Integer.parseInt(msg_split[1]);
                        levelBtn.setText("Level : " + level);
                    }
                    break;
                case MESSAGE_DEVICE_OBJECT: // ~기기와 연결되었다.
                    BluetoothDevice connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    connBtn.setText("Connected to "+connectingDevice.getName());
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST: // connection lost
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    public void saveData(){ // 파일 기기 내에 저장
        Log.d(TAG, "Time thread : saveData()");
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File path = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS);
            try {
                // Save total Data

//                fileName = deviceID + "-" + time + "-" + fileVer + ".txt";
                File f = new File(path, fileName);
                Log.d(TAG, "Time thread : saveData - fileName : "+fileName);
                FileWriter fw = new FileWriter(f, true); // 기존 파일에 append
                Log.d(TAG, "Time thread : saveData() - fileWriter fw");
//                FileWriter fw = new FileWriter(f, false);
                PrintWriter out = new PrintWriter(fw);

                // Deep copy (동시 수정을 피하기 위한)
                copyData.addAll(sensorData);
                sensorData.clear();
                Log.d(TAG, "Time thread : saveData - copyData : "+copyData.size());
                out.println(copyData);
                out.close();
//                fileVer ++;
//                Toast.makeText(getApplicationContext(), "Your data has been saved.",Toast.LENGTH_SHORT).show();
                copyData.clear();
                Log.d(TAG, "Time thread : saveData end");

            } catch(IOException e) {
                Log.e(TAG, "save data failed", e);
            }

        }
        else {
            Toast.makeText(getApplicationContext(), "외부 메모리 읽기 쓰기 불가능",Toast.LENGTH_SHORT).show();
        }
    }

    public void setFileName(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String time = mFormat.format(date);
//            String deviceID = Build.ID;
//            deviceID = deviceID.replace('.','_');
//            Log.d("watch","device ID : "+deviceID);
        String deviceID = bluetoothAdapter.getName();
        Log.d(TAG,"Device name : "+deviceID);
        // File name = device id + time
        fileName = deviceID + "-" + time + ".txt";
    }


    public void start_sensor(){
        measureBtn.setText("MEASURING...");
        saveN = 0;
//        filever = 1;
        register();
        setFileName();
        timeT.re_start();

    }

    public void stop_sensor(){
        measureBtn.setText("NOT MEASURING");
        timeT.cancel();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i(TAG, "Main thread sleep interrupted");
        }
        saveData();
        unregister();
    }

    public void register() { // register listener
        manager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, mStep, SensorManager.SENSOR_DELAY_GAME);

        if(mHeartRate == null) {
            Toast.makeText(getApplicationContext(),"HeartRate sensor X",Toast.LENGTH_SHORT).show();
        }
        if(mGyro == null) {
            Toast.makeText(getApplicationContext(),"Gyroscope sensor X",Toast.LENGTH_SHORT).show();
        }
        if(mAccel == null) {
            Toast.makeText(getApplicationContext(),"Accelerometer sensor X",Toast.LENGTH_SHORT).show();
        }
        if(mStep == null) {
            Toast.makeText(getApplicationContext(),"Step counter sensor X",Toast.LENGTH_SHORT).show();
        }
    }

    public void unregister() { // unregister listener
        manager.unregisterListener(this);
        sensorData.clear();
    }

    private class TimeThread extends Thread {
        private int timeValue = 0;

        public void run() {
//            Log.d(TAG, "Time thread : run()");
            while (true) {
//                timeValue++;  // 작업스레드 값 증가.
//                Log.d(TAG, "Time thread : run()_while");
                try {
                    timeValue++;
                    Thread.sleep(1000);   // 1000ms, 즉 1초 단위로 작업스레드 실행
//                    Log.d(TAG, "Time thread : time value ++ : "+timeValue);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.i(TAG, "Time thread interrupted");
                }
                if ((timeValue == 600) && thread_state){ // time = 10m(600)
                    Log.d(TAG, "Time thread : 10m");
                    timeValue =0;
                    if(sensorData.size() != 0){
                        saveData();

                        saveN ++;
//                        saveNumBtn.setText("Data save Number : "+saveN);
                        Log.d(TAG, "Time thread : save N -"+saveN);
//                        sensorData.clear();
                    }

                }
            }
        }

        public void re_start(){ // thread start -> 다시 측정 start 버튼 눌렀을때
            fileVer = 0;
            timeValue = 0;
            thread_state = true;
            Log.d(TAG, "Time thread : timeT.re_start("+timeValue+","+thread_state+")");
        }

        public void cancel(){ // thread stop -> 측정 stop 버튼 눌렀을때
            Log.d(TAG, "Time thread : timeT.cancel");
            thread_state = false;
        }
    }

    // Sensor work //
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }
    public final void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        int tag = level;

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_SSS");
        String time = mFormat.format(date);

        switch (sensor.getType()) { // send signal to firebase
            case Sensor.TYPE_HEART_RATE :
                sensorData.add(tag+"+"+"heartR+"+time+"+"+event.values[0]);
                break;

            case Sensor.TYPE_GYROSCOPE :
                sensorData.add(tag+"+"+"gyroX+"+time+"+"+event.values[0]);
                sensorData.add(tag+"+"+"gyroY+"+time+"+"+event.values[1]);
                sensorData.add(tag+"+"+"gyroZ+"+time+"+"+event.values[2]);
                break;

            case Sensor.TYPE_ACCELEROMETER :
                sensorData.add(tag+"+"+"accelX+"+time+"+"+event.values[0]);
                sensorData.add(tag+"+"+"accelY+"+time+"+"+event.values[1]);
                sensorData.add(tag+"+"+"accelZ+"+time+"+"+event.values[2]);
                break;

            case Sensor.TYPE_STEP_COUNTER :
                sensorData.add(tag+"+"+"stepC+"+time+"+"+event.values[0]);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(timeT != null){
//            timeT = null;
//        }

        if (controller != null) {
            if (controller.getState() == Controller.STATE_NONE) {
                // controller를 아직 시작하지 않았다고 판단 -> start
                controller.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (controller != null)
            controller.stop();
        unregister();
        if(timeT != null){
            timeT = null;
        }
    }

}