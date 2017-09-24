package com.wenhaiz.deardiary.data

import java.util.ArrayList
import java.util.Calendar

interface DataSource {
    interface LoadDiariesCallBack {
        fun onDiaryLoaded(diaryList: ArrayList<Diary>)

        fun onDataNotAvailable()
    }

    interface LoadDiaryCallBack {
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

    fun queryByDay(calendar: Calendar, callBack: LoadDiaryCallBack)

    fun queryByMonth(calendar: Calendar, callBack: LoadDiariesCallBack)

    fun queryAndAddDefault(calendar: Calendar, callBack: LoadDiariesCallBack)

    fun queryByKeyword(keyword: String, callBack: LoadDiariesCallBack)

    fun validDairyCount(): Int
}
