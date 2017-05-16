package com.example.neon.deardiary.settings;

import com.example.neon.deardiary.data.source.DataSource;

class SettingsPresenter implements SettingsContract.Presenter {
    private DataSource mDataSource;
    private SettingsContract.View mView;

    SettingsPresenter(DataSource dataSource, SettingsContract.View view) {
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void deleteAll() {
        mDataSource.deleteAll();
        mView.onDiaryDeleted();
    }
}
