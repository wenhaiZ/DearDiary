package com.example.neon.deardiary.diarylist;

import android.util.Log;

import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.data.source.DataSource;
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource;

import java.util.ArrayList;
import java.util.Calendar;


class DiaryListPresenter implements DiaryListContract.Presenter {
    private static final String TAG = "DiaryListPresenter";
    private DataSource mDataSource;
    private DiaryListContract.View mView;

    DiaryListPresenter(DiaryLocalDataSource dataSource, DiaryListContract.View view) {
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadDiaries(Calendar.getInstance());
    }

    public void loadDiaries(Calendar c) {
        mDataSource.queryAndAddDefault(c, new DataSource.LoadDiaryCallBack() {
            @Override
            public void onDiaryLoaded(ArrayList<Diary> diaryList) {
                mView.showDiary(diaryList);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "onDataNotAvailable: 数据读取失败");

            }
        });

    }

    @Override
    public Diary getDiary(Calendar c) {
        final Diary[] diary = new Diary[1];
        mDataSource.queryByDay(c, new DataSource.GetDiaryCallBack() {
            @Override
            public void onDiaryGot(Diary foundDiary) {
                diary[0] = foundDiary;
            }

            @Override
            public void onDataNotAvailable() {
                diary[0] = null;
            }
        });
        return diary[0];

    }

    @Override
    public void insertDiary(Diary diary) {
        mDataSource.insertDiary(diary);
    }
}
