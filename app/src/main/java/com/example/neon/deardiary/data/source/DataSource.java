package com.example.neon.deardiary.data.source;

import com.example.neon.deardiary.data.Diary;

import java.util.ArrayList;
import java.util.Calendar;

public interface DataSource {
    interface LoadDiaryCallBack {
        void onDiaryLoaded(ArrayList<Diary> diaryList);

        void onDataNotAvailable();
    }

    interface GetDiaryCallBack {
        void onDiaryGot(Diary diary);

        void onDataNotAvailable();
    }

    interface UpdateDiaryCallback{
        void onDiaryUpdated();
        void onDiaryUpdateFailed();
    }

    void insertDiary(Diary diary);


    void updateDiary(Diary diary,UpdateDiaryCallback callback);

    void deleteAll();

    void queryByDay(Calendar calendar, GetDiaryCallBack callBack);

    void queryByMonth(Calendar calendar, LoadDiaryCallBack callBack);

    void queryAndAddDefault(Calendar calendar, LoadDiaryCallBack callBack);

    void queryByKeyword(String content, LoadDiaryCallBack callBack);

    int getValidDairyCount();
}
