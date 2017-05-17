package com.example.neon.deardiary.settings;

import com.example.neon.deardiary.base.BasePresenter;
import com.example.neon.deardiary.base.BaseView;


interface SettingsContract {
    interface View extends BaseView<Presenter> {
        void onDiaryDeleted();
    }

    interface Presenter extends BasePresenter {
        void deleteAll();
    }
}
