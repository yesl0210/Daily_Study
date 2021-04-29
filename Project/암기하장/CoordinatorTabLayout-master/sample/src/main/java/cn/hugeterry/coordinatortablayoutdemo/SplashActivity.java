package cn.hugeterry.coordinatortablayoutdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstaneState){
        super.onCreate(savedInstaneState);

        try{
            Thread.sleep(1000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
