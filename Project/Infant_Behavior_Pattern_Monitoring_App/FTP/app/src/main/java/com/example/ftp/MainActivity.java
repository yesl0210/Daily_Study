package com.example.ftp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Main";
    private ConnectFTP ConnectFTP;
    private FtpConnectThread mFtpConnectThread;
    private FtpDisconnectThread mFtpDisconnectThread;
    private FtpUploadThread mFtpUploadThread;

    public static final int PERMISSIONS_REQUEST = 1; // 권한 요청

    // * UI * //
    private ArrayAdapter<String> fileListAdapter;
    public ListView listView;

    // * Network Information * //
    private String ip, userId, userPw;
    private int port;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        networkSet();
        doCheckPermission();
        init();
        fileSet();
    }

    public void doCheckPermission() {
        // 권한 X
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
        }
    }

    private void init() {
        ConnectFTP = new ConnectFTP();
        mFtpConnectThread = new FtpConnectThread();
//        mFtpConnectThread.start();
        fileListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView = (ListView) findViewById(R.id.fileList);
        listView.setAdapter(fileListAdapter);

        // Listview에 있는 아이템 클릭시 이벤트 핸들링 (Paring)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName = ((TextView) view).getText().toString(); // 파일 확장자도 포함되어 있음.
                if (mFtpUploadThread != null) {
                    mFtpUploadThread = null;
                }
                mFtpUploadThread = new FtpUploadThread(fileName);
                mFtpUploadThread.start();
            }

        });
    }

    private void fileSet() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File list[] = path.listFiles();
        Log.d(TAG, "file list len:" + list.length + ", list :" + list);

        for (int i = 0; i < list.length; i++) {
            fileListAdapter.add(list[i].getName());
        }
    }

    private void networkSet() {
        // Layout xml resource를 view 객체로 inflate(부풀림)하는 객체
        LayoutInflater inflater = getLayoutInflater();
        // View 객체 생성
        final View dialogView = inflater.inflate(R.layout.network_info, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network Info");
        builder.setView(dialogView); // 생성한 view 객체 세팅

        // dialogView 객체 안에서 EditText 객체 찾기
        EditText editIp = (EditText) dialogView.findViewById(R.id.ipInfo);
        EditText editPort = (EditText) dialogView.findViewById(R.id.portInfo);
        EditText editId = (EditText) dialogView.findViewById(R.id.userId);
        EditText editPw = (EditText) dialogView.findViewById(R.id.userPw);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { // positive 버튼 눌렀을때
                // 입력한 network 정보 가져오기
                ip = editIp.getText().toString();
                port = Integer.parseInt(editPort.getText().toString());
                userId = editId.getText().toString();
                userPw = editPw.getText().toString();


                // SharedPreferences를 sFile이름, 기본모드로 설정
                SharedPreferences sharedPreferences = getSharedPreferences("FTP_network_info", MODE_PRIVATE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("ip", ip); // key, value를 이용하여 저장하는 형태
                editor.putInt("port", port);
                editor.putString("userId", userId);
                editor.putString("userPw", userPw);
                editor.commit(); // 최종 commit

                mFtpConnectThread.start();
            }
        });

        builder.setNegativeButton("Set info", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { // Negative 버튼 눌렀을때
                SharedPreferences sf = getSharedPreferences("FTP_network_info", MODE_PRIVATE);
                ip = sf.getString("ip", "ip"); // key가 ip인 것이 없으면 "" 반환
                userId = sf.getString("userId", "userId");
                userPw = sf.getString("userPw", "userPw");
                port = sf.getInt("port", -1);

                editIp.setText(ip);
                editPort.setText(Integer.toString(port));
                editId.setText(userId);
                editPw.setText(userPw);

                Log.d(TAG, "ip:" + ip);
                Log.d(TAG, "userId:" + userId);
                Log.d(TAG, "userPw:" + userPw);
                Log.d(TAG, "port:" + port);

                mFtpConnectThread.start();
            }
        });

        // 설정한 값으로 AlertDialog 객체 생성
        AlertDialog dialog = builder.create();
        // Dialog 바깥을 터치했을때 dialog를 없애지 않도록
        dialog.setCanceledOnTouchOutside(false);
        // dialog 보이게하기
        dialog.show();
    }

    private class FtpConnectThread extends Thread {
        public void run() {
            boolean status = false;
            status = ConnectFTP.ftpConnect(ip, userId, userPw, port);
//            status = ConnectFTP.ftpConnect("mhealth.gachon.ac.kr","mhlab","mhlab118",23423);
//            status = ConnectFTP.ftpConnect("211.109.215.201","yeseul","0210",23423);
            if (status == true) {
                Log.d(TAG, "Connection Success");
            } else {
                Log.d(TAG, "Connection failed");
            }
        }
    }

    private class FtpDisconnectThread extends Thread {
        public void run() {
            boolean result = ConnectFTP.ftpDisConnect();
            if (result == true) {
                Log.d(TAG, "Disconnection Success");
            } else {
                Log.d(TAG, "Discconection failed");
            }
        }
    }

    private class FtpUploadThread extends Thread {
        private String mName;

        public FtpUploadThread(String name) {
            this.mName = name;
        }

        public void run() {
            boolean result = ConnectFTP.ftpUploadFile(mName);
            if (result == true) {
                Log.d(TAG, "File Upload Success");
            } else {
                Log.d(TAG, "File Upload failed");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}