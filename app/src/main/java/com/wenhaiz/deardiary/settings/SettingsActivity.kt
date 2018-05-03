package com.wenhaiz.deardiary.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.data.ObjectBoxDataSource
import com.wenhaiz.deardiary.utils.ActivityUtil

internal class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        var settingsFragment: SettingsFragment? = fragmentManager.findFragmentById(R.id.settings_container) as? SettingsFragment
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment()
            ActivityUtil.addFragmentToActivity(fragmentManager, settingsFragment, R.id.settings_container)
        }
//        val dataSource = LocalDataSource.getInstance(this)
        val dataSource = ObjectBoxDataSource(this)
        //create the presenter
        SettingsPresenter(dataSource, settingsFragment)
    }
}
