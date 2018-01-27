package com.softkey.mazno2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.softkey.mazno2.util.Utils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        navigate();
    }

    private void navigate(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Utils.navigateTo(SplashActivity.this, MapsActivity.class);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
