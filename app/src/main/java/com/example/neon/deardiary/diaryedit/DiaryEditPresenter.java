package com.example.neon.deardiary.diaryedit;

import android.util.Log;

import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.data.source.DataSource;


class DiaryEditPresenter implements DiaryEditContract.Presenter {
    private DiaryEditContract.View mView;
    private DataSource mDataSource;
    private static final String TAG = "DiaryEditPresenter";

    DiaryEditPresenter(DataSource dataSource, DiaryEditContract.View view) {
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void saveDiary(Diary diary) {
        mDataSource.updateDiary(diary, new DataSource.UpdateDiaryCallback() {
            @Override
            public void onDiaryUpdated() {
                mView.onDiaryUpdated();
                Log.d(TAG, "onDiaryUpdated: 日记更新成功");
            }

            @Override
            public void onDiaryUpdateFailed() {
                mView.onDiaryUpdateFailed();
                Log.d(TAG, "onDiaryUpdated: 日记更新失败");
            }
        });

    }

    @Override
    public int getValidDiaryCount() {
        return mDataSource.getValidDairyCount();
    }

}
