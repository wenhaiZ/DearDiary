package com.example.neon.deardiary.diaryquery;


import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.data.source.DataSource;

import java.util.ArrayList;

class DiaryQueryPresenter implements DiaryQueryContract.Presenter {
    private DiaryQueryContract.View mView;
    private DataSource mDataSource;

    DiaryQueryPresenter(DiaryQueryContract.View view, DataSource dataSource) {
        mView = view;
        mView.setPresenter(this);
        mDataSource = dataSource;
    }

    @Override
    public void start() {

    }

    @Override
    public void diaryQueryByKeyword(String keyword) {
        mDataSource.queryByKeyword(keyword, new DataSource.LoadDiaryCallBack() {
            @Override
            public void onDiaryLoaded(ArrayList<Diary> diaryList) {
                mView.querySuccessfully(diaryList);
            }

            @Override
            public void onDataNotAvailable() {
                mView.queryFailed();
            }
        });

    }
}
