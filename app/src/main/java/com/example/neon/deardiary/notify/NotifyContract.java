package com.example.neon.deardiary.notify;

import com.example.neon.deardiary.base.BasePresenter;
import com.example.neon.deardiary.base.BaseView;
import com.example.neon.deardiary.data.Diary;

import java.util.Calendar;

public interface NotifyContract {
    interface Presenter extends BasePresenter {
        Diary getDiary(Calendar c);

        void insertDiary(Diary diary);
    }
    interface View extends BaseView<Presenter>{

    }
}
