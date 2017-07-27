package com.example.neon.deardiary.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource
import com.example.neon.deardiary.utils.ActivityUtil

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        var settingsFragment: SettingsFragment? = fragmentManager.findFragmentById(R.id.settings_container) as? SettingsFragment
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment()
            ActivityUtil.addFragmentToActivity(fragmentManager, settingsFragment, R.id.settings_container)
        }
        val dataSource = DiaryLocalDataSource.getInstance(this)
        //create the presenter
        SettingsPresenter(dataSource, settingsFragment)
    }
}
