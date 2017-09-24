package com.wenhaiz.deardiary.splash

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.diarylist.DiaryListActivity

class SplashActivity : AppCompatActivity() {
    @BindView(R.id.tv_version)
    internal lateinit var mTvVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ButterKnife.bind(this)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            val showVersionName = "V " + versionName
            mTvVersion.text = showVersionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val handler = Handler()
        val finish = Runnable {
            val main = Intent(this@SplashActivity, DiaryListActivity::class.java)
            startActivity(main)
            finish()
        }
        handler.postDelayed(finish, SPLASH_DURATION.toLong())
    }

    companion object {
        private val SPLASH_DURATION = 1500
    }
}
