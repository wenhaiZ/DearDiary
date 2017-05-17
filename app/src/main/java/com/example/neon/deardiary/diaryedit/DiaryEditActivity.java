package com.example.neon.deardiary.diaryedit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource;
import com.example.neon.deardiary.util.ActivityUtils;

public class DiaryEditActivity extends AppCompatActivity {
    public static final String DATA_DIARY = "diary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_edit);

        DiaryEditFragment diaryEditFragment = (DiaryEditFragment) getFragmentManager().findFragmentById(R.id.edit_container);
        if (diaryEditFragment == null) {
            diaryEditFragment = new DiaryEditFragment();
            ActivityUtils.addFragmentToActivity(getFragmentManager(), diaryEditFragment, R.id.edit_container);
        }

        Diary diary = (Diary) getIntent().getSerializableExtra(DATA_DIARY);
        Bundle data = new Bundle();
        data.putSerializable(DATA_DIARY, diary);
        diaryEditFragment.setArguments(data);
        //create dataSource
        DiaryLocalDataSource dataSource = DiaryLocalDataSource.getInstance(this);
        //create presenter
        new DiaryEditPresenter(dataSource, diaryEditFragment);
    }

}
