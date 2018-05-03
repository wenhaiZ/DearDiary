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

    fun queryByMonth(calendar: Calendar): List<Diary>

    fun queryAndAddDefault(calendar: Calendar, callBack: LoadDiariesCallBack) {
        val actualDiaryCount = getActualDiaryCount(calendar)
        val savedDiaries = queryByMonth(calendar)
        val queryResult = getEmptyDiaryList(actualDiaryCount, calendar)
        if (savedDiaries.size < actualDiaryCount) {
            replaceEmptyRecord(savedDiaries, queryResult)
        }
        callBack.onDiaryLoaded(queryResult)
    }

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

    fun getEmptyDiaryList(count: Int, calendar: Calendar): ArrayList<Diary> {
        val diaryList = ArrayList<Diary>(count)
        //add empty diary
        for (i in 1..count) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            diaryList.add(Diary.emptyDiary(calendar))
        }
        return diaryList
    }

    fun getActualDiaryCount(calendar: Calendar): Int {
        return if (isBefore(calendar))
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        else
            calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun replaceEmptyRecord(savedDiaries: List<Diary>, queryResult: ArrayList<Diary>) {
        for (i in 0 until savedDiaries.size) {
            if (queryResult.contains(savedDiaries[i])) {
                queryResult[savedDiaries[i].dayOfMonth - 1] = savedDiaries[i]
            }
        }
    }
}
