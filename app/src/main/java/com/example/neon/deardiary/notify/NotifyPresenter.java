package com.example.neon.deardiary.notify;

import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.data.source.DataSource;

import java.util.Calendar;


public class NotifyPresenter implements NotifyContract.Presenter {
    private DataSource mDataSource;
    private NotifyContract.View mView;

    NotifyPresenter(DataSource dataSource, NotifyContract.View view) {
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

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
