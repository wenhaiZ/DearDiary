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

    /**
     * 检查给定日期是否早于当前时间
     */
    fun isBefore(calendar: Calendar): Boolean {
        val now = Calendar.getInstance()//获取当前日期
        return calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR)
                || calendar.get(Calendar.MONTH) < now.get(Calendar.MONTH)
    }
}
