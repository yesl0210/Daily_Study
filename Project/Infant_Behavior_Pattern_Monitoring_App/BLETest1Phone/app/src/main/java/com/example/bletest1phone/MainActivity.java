package com.example.bletest1phone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "BLE_Main";

    // * UI * //
    public Button connBtn, level_1Btn, level_2Btn, level_3Btn, level_4Btn
            , lvStateBtn, mStateBtn, deviceIdBtn, mStartStopBtn;
    public ListView listView, listView2, listView3;
    public int level;

    // * Bluetooth * //
    private Controller controller;
    public BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    public Dialog dialog; // 블루투스 창
    private ArrayAdapter<String> discoveredDevicesAdapter;
    private ArrayAdapter<String> pairedDevicesAdapter;
    private SimpleAdapter connectedDevicesAdapter;
    private ArrayList<HashMap<String,String>> connectedDevicesList;

    // * Message code
    public static final int REQUEST_ENABLE_BT = 1; // 블루투스 활성화 요청 메시지

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_DISCONNECTED = 6;

    public static final String DEVICE_OBJECT = "device_name";
    public static final String TOAST = "toast";
    public static final String DEVICE_NAME_ADDRESS = "device_name_address";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 켜두기

        init();
        initBLE();

        // 블루투스 연결 창 보여주기
        connBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                // 초기화
                pairedDevicesAdapter.clear();
                discoveredDevicesAdapter.clear();

                if (bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();
                }
                // 조회 스캔과, 검색된 각 기기의 페이지 스캔을 통해 블루투스 이름을 가져오는 과정이 포함
                // 검색이 성공적으로 시작되었는지 나타내는 부울 값을 반환
                // 검색된 각 기기에 대한 정보를 수신하려면
                // ACTION_FOUND 인텐트에 대한 BroadcastReceiver를 등록해야함
                bluetoothAdapter.startDiscovery();

                // 페어링된 기기 집합 쿼리
                // 페어링된 기기를 나타내는 BluetoothDevice 객체 세트를 반환 -> name과 mac address를 얻을 수 있음.
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) { // 페어링된 기기가 있을 경우
                    for (BluetoothDevice device : pairedDevices) {
                        // 페어링된 기기의 이름 & 페어링된 기기의 MAC address
                        // 페어링된 기기들을 찾아 adapter에 추가
                        pairedDevicesAdapter.add(device.getName() + "\n"+ device.getAddress());
                    }
                } else { // 페어링된 기기가 없을 경우
                    pairedDevicesAdapter.add("No devices have been paired");
                }

                // Listview에 있는 아이템 클릭시 이벤트 핸들링 (Paring)
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        bluetoothAdapter.cancelDiscovery(); // 아이템 클릭시 Discovery 그만하기
                        // view 내용 = 기기 이름 + \n + mac 주소
                        String info = ((TextView) view).getText().toString(); // 해당 기기의 이름과
                        String address = info.substring(info.length() - 17); // 주소를 얻어오기

                        connectToDevice(address); // 선택한 장치의 mac 주소 넘김
                        dialog.dismiss(); // Dialog 사라지게
                    }

                });

                // Listview2에 있는 아이템 클릭시 이벤트 핸들링 (Discovering)
                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        bluetoothAdapter.cancelDiscovery(); // 아이템 클릭시 Discovery 그만하기
                        // view 내용 = 기기 이름 + \n + mac 주소
                        String info = ((TextView) view).getText().toString(); // 해당 기기의 이름과
                        String address = info.substring(info.length() - 17); // 주소를 얻어오기

                        connectToDevice(address); // 선택한 장치의 mac 주소 넘김
                        dialog.dismiss(); // Dialog 사라지게
                    }
                });

                dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });

        // Listview3에 있는 아이템 클릭시 이벤트 핸들링 (Connected device list)
        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 선택한 기기가 "Not connected" 상태일 경우 재연결하도록 해야함

                // 선택한 item 얻어옴
                HashMap<String, String> item = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                String info = item.get("nameAddress"); // 해당 기기의 이름과
                String address = info.substring(info.length() - 17); // 주소를 얻어오기
                String connState = item.get("connection"); // 해당 기기와의 연결 상태 얻어오기

                if(connState.equals("Not connected")){
                    connectToDevice(address); // 선택한 장치의 mac 주소 넘김
               }
                dialog.dismiss(); // Dialog 사라지게
            }
        });

        mStartStopBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                // 연결된 장치가 하나도 없을경우
                if(connectedDevicesList.get(0).get("connection").equals("None of the devices") &&
                        connectedDevicesList.size() == 1){
                    Toast.makeText(getApplicationContext(), "Unable to send command.\n"+"No device is connected.", Toast.LENGTH_SHORT).show();
                }
                else { // 연결된 장치가 하나라도 있을경우 -> start or stop 명령어 전송
                    if (mStartStopBtn.getText().equals("START")){
                        Toast.makeText(getApplicationContext(), "Start measuring...", Toast.LENGTH_SHORT).show();
                        mStateBtn.setText("MEASURING...");
                        mStartStopBtn.setText("STOP");
                        sendMessage("start-0");
                    }
                    else if (mStartStopBtn.getText().equals("STOP")){
                        Toast.makeText(getApplicationContext(), "Stop measuring.", Toast.LENGTH_SHORT).show();
                        mStateBtn.setText("NOT MEASURING");
                        mStartStopBtn.setText("START");
                        sendMessage("stop-1");
                    }
                }
            }
        });

        level_1Btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "level change to 1", Toast.LENGTH_SHORT).show();
                sendMessage("level-1");
                lvStateBtn.setText("Current Level : 1");
            }
        });

        level_2Btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "level change to 2", Toast.LENGTH_SHORT).show();
                sendMessage("level-2");
                lvStateBtn.setText("Current Level : 2");
            }
        });

        level_3Btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "level change to 3", Toast.LENGTH_SHORT).show();
                sendMessage("level-3");
                lvStateBtn.setText("Current Level : 3");
            }
        });

        level_4Btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "level change to 4", Toast.LENGTH_SHORT).show();
                sendMessage("level-4");
                lvStateBtn.setText("Current Level : 4");
            }
        });
    }

    public void init() { // initialize
        // for UI
        connBtn = (Button) findViewById(R.id.connect);
        level_1Btn = (Button) findViewById(R.id.level_1);
        level_2Btn = (Button) findViewById(R.id.level_2);
        level_3Btn = (Button) findViewById(R.id.level_3);
        level_4Btn = (Button) findViewById(R.id.level_4);
        lvStateBtn = (Button) findViewById(R.id.lvState);
        mStateBtn = (Button) findViewById(R.id.measureState);
        deviceIdBtn = (Button) findViewById(R.id.deviceID);
        mStartStopBtn = (Button) findViewById(R.id.startStop);
    }

    public void initBLE() {
        // BluetoothAdapter : 기기 자체 블루투스 송수신 장치. 이 객체를 이용해 상호작용 가능.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceIdBtn.setText("Device Name : "+ bluetoothAdapter.getName());

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

        /*
        * 검색 기능 활성화시 블루투스가 자동으로 활성화됨.
        * 블루투스 activity를 수행하기 전에 기기 검색 기능을 일관되게 활성화할 계획이면, 블루투스 활성화 단계를 스킵해도 됨
        * */

        // 블루투스 선택 dialog 창
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle("Bluetooth Devices");


//        if (bluetoothAdapter.isDiscovering()){
//            bluetoothAdapter.cancelDiscovery();
//        }
//        // 조회 스캔과, 검색된 각 기기의 페이지 스캔을 통해 블루투스 이름을 가져오는 과정이 포함
//        // 검색이 성공적으로 시작되었는지 나타내는 부울 값을 반환
//        // 검색된 각 기기에 대한 정보를 수신하려면
//        // ACTION_FOUND 인텐트에 대한 BroadcastReceiver를 등록해야함
//        bluetoothAdapter.startDiscovery();

        pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        connectedDevicesList = new ArrayList<HashMap<String,String>>();
        connectedDevicesAdapter = new SimpleAdapter(this, connectedDevicesList,
                android.R.layout.simple_list_item_2, new String[]{"nameAddress", "connection"},
                new int[] {android.R.id.text1, android.R.id.text2});

        // listview를 dialog에 위치시키기 & 어댑터 붙이기
        listView = (ListView) dialog.findViewById(R.id.pairedDeviceList); // List view for paired devices
        listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList); // List view for discovered devices
        listView3 = (ListView) findViewById(R.id.connDeviceList); // List view for connected devices

        listView.setAdapter(pairedDevicesAdapter); // 페어링된 기기들
        listView2.setAdapter(discoveredDevicesAdapter); // 페어링X, 발견된(discovered) 기기들
        listView3.setAdapter(connectedDevicesAdapter); // 연결된 장치들

        HashMap<String, String> item = new HashMap<>();
        item.put("nameAddress", "X");
        item.put("connection", "None of the devices");
        connectedDevicesList.add(item);
        connectedDevicesAdapter.notifyDataSetChanged();


        registerReceiver(); // receiver 등록

//        // 페어링된 기기 집합 쿼리
//        // 페어링된 기기를 나타내는 BluetoothDevice 객체 세트를 반환 -> name과 mac address를 얻을 수 있음.
//        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//
//        if (pairedDevices.size() > 0) { // 페어링된 기기가 있을 경우
//            for (BluetoothDevice device : pairedDevices) {
//                // 페어링된 기기의 이름 & 페어링된 기기의 MAC address
//                // 페어링된 기기들을 찾아 adapter에 추가
//                pairedDevicesAdapter.add(device.getName() + "\n"+ device.getAddress());
//            }
//        } else { // 페어링된 기기가 없을 경우
//            pairedDevicesAdapter.add("No devices have been paired");
//        }

//        // Listview에 있는 아이템 클릭시 이벤트 핸들링 (Paring)
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                bluetoothAdapter.cancelDiscovery(); // 아이템 클릭시 Discovery 그만하기
//                // view 내용 = 기기 이름 + \n + mac 주소
//                String info = ((TextView) view).getText().toString(); // 해당 기기의 이름과
//                String address = info.substring(info.length() - 17); // 주소를 얻어오기
//
//                connectToDevice(address); // 선택한 장치의 mac 주소 넘김
//                dialog.dismiss(); // Dialog 사라지게
//            }
//
//        });
//
//        // Listview2에 있는 아이템 클릭시 이벤트 핸들링 (Discovering)
//        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                bluetoothAdapter.cancelDiscovery(); // 아이템 클릭시 Discovery 그만하기
//                // view 내용 = 기기 이름 + \n + mac 주소
//                String info = ((TextView) view).getText().toString(); // 해당 기기의 이름과
//                String address = info.substring(info.length() - 17); // 주소를 얻어오기
//
//                connectToDevice(address); // 선택한 장치의 mac 주소 넘김
//                dialog.dismiss(); // Dialog 사라지게
//            }
//        });
//
//        // connBtn 눌렀을때 얘네 뜨게해야함.
//        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setCancelable(false);
//        dialog.show();



    } // end initBLE()

    public void registerReceiver(){
        // 기기 검색
        // 검색된 각 기기에 대한 정보를 수신할 수 있도록 ACTION_FOUND에 대한 intent 생성
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        // discovery가 완료되었을때, 브로드캐스트를 위해 등록
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

//        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED); // 블루투스 상태변화 액션
//        registerReceiver(receiver, filter);

        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED); // 연결 끊김 확인
        registerReceiver(receiver, filter);
    }

    private void connectToDevice(String deviceAddress){
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        // paired device 혹은 discovered device 에서 선택한 장치의 mac 주소를 이용해
        // 해당 장치를 얻어 controller에 넘김
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        controller.connect(device);
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
                case MESSAGE_WRITE: // for phone

//                    if(stateBtn.getText().equals("Stop")){ // 측정 중일때만 전송
//                        byte[] writeBuf = (byte[]) msg.obj;
//                        String writeMessage = new String(writeBuf);
//                    }

                    break;
                case MESSAGE_DEVICE_OBJECT:
                    BluetoothDevice connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();

                    // 지금 연결하는 워치가 첫번째 연결 장치인 경우 -> None of the devices 없애야 함.
                    if(connectedDevicesList.get(0).get("connection").equals("None of the devices") &&
                            connectedDevicesList.size() == 1){
//                        connectedDevicesList.remove(0);
                        connectedDevicesList.clear();
                    }

                    // 연결 끊김 상태인 것을 재연결하는 것이면 -> connectedDevicesList에서 찾아서 지운후 다시 추가
                    // 어차피 "not connected"는 맨 위쪽에 위치하여 루프 자체는 많이 돌지 않음.
                    // 만약 현재 index 기기의 "connection" 상태가 "connected"이면 루프 stop
                    for (int i=0; i<connectedDevicesList.size(); i++){
                        HashMap<String, String> tmp = connectedDevicesList.get(i);
                        if (tmp.get("connection").equals("connected")) {
                            break;
                        }
                        else { // 연결 상태가 "not connected"일 경우 -> 현재 index에 해당하는 기기 list에서 제거
                            if(tmp.get("nameAddress").equals(connectingDevice.getName() + "\n"+ connectingDevice.getAddress())){
                                connectedDevicesList.remove(i);
                                break;
                            }
                        }
                    }

                    // 연결된 장치 이름, mac 주소, 연결 상태가 필요함
                    // 연결된 장치 리스트에 해당 장치 상태 업데이트
                    HashMap<String, String> item = new HashMap<>();
                    item.put("nameAddress", connectingDevice.getName() + "\n"+ connectingDevice.getAddress());
                    item.put("connection", "connected");
                    connectedDevicesList.add(item);
                    connectedDevicesAdapter.notifyDataSetChanged();

                    break;
                case MESSAGE_DISCONNECTED : // 연결 끊겼을때
                    String disconnectedDevice = msg.getData().getString(DEVICE_NAME_ADDRESS);
                    Toast.makeText(getApplicationContext(), "Device connection was lost\n"+disconnectedDevice
                            , Toast.LENGTH_SHORT).show();

                    // connected device list view 에서 연결끊긴 장치의 index 찾기
                    for (int i=0; i<connectedDevicesList.size(); i++){
                        HashMap<String, String> tmp2 = connectedDevicesList.get(i);
                        Log.d(TAG,tmp2.get("nameAddress"));
                        if(tmp2.get("nameAddress").equals(disconnectedDevice)){
                            Log.d(TAG, "MESSAGE_DISCONNECTED: find disconnected device in connected device list");

                            // 해당 장치 connected -> not connected로
                            // 연결 끊긴 장치가 맨 위에 뜨도록 하기위해 remove후 첫번째 자리에 add
                            // index가 하나씩 밀리기 때문에 성능에 악영향 -> 하지만, 연결되는 장치 개수가 적어서 괜찮을듯.
                            connectedDevicesList.remove(i);
                            HashMap<String, String> item2 = new HashMap<>();
                            item2.put("nameAddress", disconnectedDevice);
                            item2.put("connection", "Not connected");
                            connectedDevicesList.add(0,item2);
                            connectedDevicesAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    break;

                case MESSAGE_TOAST:
                    // Connection lost 혹은 connection failed에 활용
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });



    private void sendMessage(String message) {

        if (message.length() > 0) {
            Log.d(TAG, message);
            byte[] send = message.getBytes();
            controller.write(send);
        }
    }

    // ACTION_FOUND에 대한 BroadcastReceiver
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) { // Discovery가 a device를 찾음.
                // Intent로부터 BluetoothDevice 객체와 정보를 얻음
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // device.getBondState() : 해당 기기가 페어링된 장치인지 상태를 얻어옴
                    // -> BOND_NONE(페어링 되어있지 않음), BOND_BONDING(페어링이 진행중), BOND_BONDED(페어링이 되어있음)
                    // BluetoothDevice.BOND_BONDED : Remote device와 페어링되어있다는 상태를 의미함
                    // 해당 블루투스 기기가 페어링된 상태가 아닐경우 !! -> discovered adapter에 추가

                    // discoveredDevices adapter에 선택한 discovered 장치의 이름과 mac 주소를 넘김
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add("no found");
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){ // "블루투스" 자체 연결 끊겼을때
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // intent에서 device를 얻음.
                Toast.makeText(getApplicationContext(), device.getName() + "BLE 연결 끊김",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Receiver - ACL: Disconnected with "+device.getName());

//                // connected device list view 에서 연결끊긴 장치의 index 찾기
//                for (int i=0; i<connectedDevicesList.size(); i++){
//                    HashMap<String, String> tmp = connectedDevicesList.get(i);
//                    Log.d(TAG,tmp.get("nameAddress"));
//                    if(tmp.get("nameAddress").equals(device.getName() + "\n"+ device.getAddress())){
//                        Log.d(TAG, "Receiver - ACL: find disconnected device in connected device list");
//
//                        // 해당 장치 connected -> not connected로
//                        // 연결 끊긴 장치가 맨 위에 뜨도록 하기위해 remove후 첫번째 자리에 add
//                        // index가 하나씩 밀리기 때문에 성능에 악영향 -> 하지만, 연결되는 장치 개수가 적어서 괜찮을듯.
//                        connectedDevicesList.remove(i);
//                        HashMap<String, String> item = new HashMap<>();
//                        item.put("nameAddress", device.getName() + "\n"+ device.getAddress());
//                        item.put("connection", "Not connected");
//                        connectedDevicesList.add(0,item);
//                        connectedDevicesAdapter.notifyDataSetChanged();
//                        break;
//                    }
//                }

            }
        }
    };

    // 블루투스 활성화 요청에 대한 액션
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    controller = new Controller(this, handler);
                } else {
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (controller != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (controller.getState() == controller.STATE_NONE) {
                // Start the Bluetooth chat services
                controller.start();
                Toast.makeText(this, "Controller start", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver); // BroadcastReceiver 해제
        if (controller != null) controller.stop(); // controller 멈추기
    }


}