package com.example.neon.deardiary.diaryedit;

import com.example.neon.deardiary.base.BasePresenter;
import com.example.neon.deardiary.base.BaseView;
import com.example.neon.deardiary.data.Diary;


interface DiaryEditContract {
    interface View extends BaseView<Presenter> {
        void onDiaryUpdated();

        void onDiaryUpdateFailed();
    }

    interface Presenter extends BasePresenter {
        void saveDiary(Diary diary);

        int getValidDiaryCount();

    }


}
