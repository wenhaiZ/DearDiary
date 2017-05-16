package com.example.neon.deardiary.diarylist;

import com.example.neon.deardiary.base.BasePresenter;
import com.example.neon.deardiary.base.BaseView;
import com.example.neon.deardiary.data.Diary;

import java.util.ArrayList;
import java.util.Calendar;


interface DiaryListContract {
    interface View extends BaseView<Presenter> {
        void showDiary(ArrayList<Diary> diaries);

        void editDiary(Diary diary);

    }

    interface Presenter extends BasePresenter {

        void loadDiaries(Calendar c);

        Diary getDiary(Calendar c);

        void insertDiary(Diary diary);
    }
}
