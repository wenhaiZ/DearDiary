package com.example.neon.deardiary.diaryedit;

import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.data.source.DataSource;


class DiaryEditPresenter implements DiaryEditContract.Presenter {
    private DiaryEditContract.View mView;
    private DataSource mDataSource;
//    private static final String TAG = "DiaryEditPresenter";

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
            }

            @Override
            public void onDiaryUpdateFailed() {
                mView.onDiaryUpdateFailed();
            }
        });

    }

    @Override
    public int getValidDiaryCount() {
        return mDataSource.getValidDairyCount();
    }

}
