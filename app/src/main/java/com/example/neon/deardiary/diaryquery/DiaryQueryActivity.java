package com.example.neon.deardiary.diaryquery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.source.DataSource;
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource;
import com.example.neon.deardiary.util.ActivityUtils;

public class DiaryQueryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_query);
        DiaryQueryFragment diaryQueryFragment = (DiaryQueryFragment) getFragmentManager().findFragmentById(R.id.query_container);
        if (diaryQueryFragment == null) {
            diaryQueryFragment = new DiaryQueryFragment();
            ActivityUtils.addFragmentToActivity(getFragmentManager(), diaryQueryFragment, R.id.query_container);
        }
        DataSource dataSource = DiaryLocalDataSource.getInstance(this);
        DiaryQueryContract.Presenter presenter = new DiaryQueryPresenter(diaryQueryFragment, dataSource);

    }
}
