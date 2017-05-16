package com.example.neon.deardiary.diaryquery;

import com.example.neon.deardiary.base.BasePresenter;
import com.example.neon.deardiary.base.BaseView;
import com.example.neon.deardiary.data.Diary;

import java.util.ArrayList;


 interface DiaryQueryContract {
    interface View extends BaseView<Presenter>{
        void querySuccessfully(ArrayList<Diary> diaries);
        void queryFailed();

    }

    interface Presenter extends BasePresenter{

        void diaryQueryByKeyword(String keyword);

    }
}
