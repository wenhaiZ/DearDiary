package com.example.neon.deardiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.neon.deardiary.R;

/**
 * Splash Activity
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        setContentView(R.layout.activity_splash);
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //启动 MainActivity
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    //结束当前 Activity
                                    finish();
                                }
                            },
                1200);
    }
}
