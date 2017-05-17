package com.example.neon.deardiary.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.source.DataSource;
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource;
import com.example.neon.deardiary.util.ActivityUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SettingsFragment settingsFragment = (SettingsFragment) getFragmentManager().findFragmentById(R.id.settings_container);
        if(settingsFragment==null){
            settingsFragment = new SettingsFragment();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),settingsFragment,R.id.settings_container);
        }
        DataSource dataSource = DiaryLocalDataSource.getInstance(this);
        //create the presenter
        new SettingsPresenter(dataSource,settingsFragment);
    }
}
