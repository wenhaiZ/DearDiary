package com.example.neon.deardiary.splash;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.diarylist.DiaryListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500;
    @BindView(R.id.tv_version)
    TextView mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            String versionName = packageInfo.versionName;
            String showVersionName = "V "+versionName;
            mTvVersion.setText(showVersionName);
        }

        Handler handler = new Handler();
        Runnable finish = new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashActivity.this, DiaryListActivity.class);
                startActivity(main);
                finish();
            }
        };
        handler.postDelayed(finish, SPLASH_DURATION);
    }
}
