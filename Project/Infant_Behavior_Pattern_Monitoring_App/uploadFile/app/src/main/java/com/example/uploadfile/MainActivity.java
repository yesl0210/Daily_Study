package com.example.uploadfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> fileListAdapter;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 켜두기

        init();
        fileSet();
    }

    public void init(){

        fileListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView = (ListView) findViewById(R.id.fileList);
        listView.setAdapter(fileListAdapter);

                // Listview에 있는 아이템 클릭시 이벤트 핸들링 (Paring)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName = ((TextView) view).getText().toString();
                uploadFile(fileName);
            }

        });
    }

    public void fileSet(){
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File list[] = path.listFiles();

        for (int i = 0; i<list.length;i++){
            fileListAdapter.add(list[i].getName());
        }
    }

    public void uploadFile(String name){
        FirebaseApp.initializeApp(getApplicationContext());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("test/"+name);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Uri uri = Uri.fromFile(new File(path+"/"+name));


        // Meta data 1 - 업로드 시각
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss_SSS");
        Date date = new Date();
        String uploadTime = mFormat.format(date);

        // Meta data 2 - 업로더 이름
        String uploaderName = "yeseul"; // 나중에 입력하도록

        // Meta data 3 - 센서 리스트
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> list = sm.getSensorList(Sensor.TYPE_ALL);
        String sensorList = "<센서목록>\n센서 총개수: " + list.size();

        for (int i = 0; i < list.size(); i++) {
            Sensor s = list.get(i);
            sensorList += ","+s.getName();
            if(i == 0) {
                sensorList = sensorList.substring(1);
            }
        }

        // Meta data 4 - 측정 기기
        String model = Build.MODEL;

        // Create file metadata including the content type
        // 메타데이터 포함 내용 : 측정된 날짜, 이름이랑, 센서 리스트, 측정된 기기
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("txt")
                .setCustomMetadata("Date",uploadTime)
                .setCustomMetadata("Name",uploaderName)
                .setCustomMetadata("Sensor list",sensorList)
                .setCustomMetadata("Model",model).build();


//        InputStream stream = null;
//        try {
//            stream = new FileInputStream(new File(path, name));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        Log.d("upload","file upload try : "+name);
        UploadTask uploadTask = mountainsRef.putFile(uri, metadata);
//        UploadTask uploadTask = mountainsRef.putStream(stream);


        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(),"업로드 실패",Toast.LENGTH_SHORT).show();
                Log.d("upload","file upload failure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(getApplicationContext(),"업로드 완료",Toast.LENGTH_SHORT).show();
                Log.d("upload","file upload success");
            }
        });
    }
}