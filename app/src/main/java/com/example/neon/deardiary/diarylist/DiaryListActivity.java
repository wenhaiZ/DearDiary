package com.example.neon.deardiary.diarylist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource;
import com.example.neon.deardiary.util.ActivityUtils;

public class DiaryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        DiaryListFragment diaryListFragment = (DiaryListFragment) getFragmentManager().findFragmentById(R.id.list_container);
        if (diaryListFragment == null) {
            diaryListFragment = new DiaryListFragment();
            ActivityUtils.addFragmentToActivity(getFragmentManager(), diaryListFragment, R.id.list_container);
        }

        DiaryLocalDataSource dataSource = DiaryLocalDataSource.getInstance(this);
        //create presenter
        new DiaryListPresenter(dataSource, diaryListFragment);
    }

}
