package com.example.neon.deardiary.data.source

import com.example.neon.deardiary.data.Diary

import java.util.ArrayList
import java.util.Calendar

interface DataSource {
    interface LoadDiaryCallBack {
        fun onDiaryLoaded(diaryList: ArrayList<Diary>)

        fun onDataNotAvailable()
    }

    interface GetDiaryCallBack {
        fun onDiaryGot(diary: Diary)

        fun onDataNotAvailable()
    }

    interface UpdateDiaryCallback {
        fun onDiaryUpdated()
        fun onDiaryUpdateFailed()
    }

    fun insertDiary(diary: Diary)

    fun updateDiary(diary: Diary, callback: UpdateDiaryCallback)

    fun deleteAll()

    fun queryByDay(calendar: Calendar, callBack: GetDiaryCallBack)

    fun queryByMonth(calendar: Calendar, callBack: LoadDiaryCallBack)

    fun queryAndAddDefault(calendar: Calendar, callBack: LoadDiaryCallBack)

    fun queryByKeyword(keyword: String, callBack: LoadDiaryCallBack)

    fun validDairyCount(): Int
}
