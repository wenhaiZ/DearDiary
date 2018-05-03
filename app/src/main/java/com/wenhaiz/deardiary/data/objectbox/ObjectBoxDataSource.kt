package com.wenhaiz.deardiary.data.objectbox

import android.content.Context
import com.wenhaiz.deardiary.MyApp
import com.wenhaiz.deardiary.data.DataSource
import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.data.Diary_
import com.wenhaiz.deardiary.utils.LogUtil
import io.objectbox.Box
import java.util.Calendar

/**
 * ObjectBox DataSource
 */

class ObjectBoxDataSource(context: Context) : DataSource {

    private val box: Box<Diary> = (context.applicationContext as MyApp).getMyBoxStore().boxFor(Diary::class.java)
    override fun insertDiary(diary: Diary) {
        box.put(diary)
    }

    override fun updateDiary(diary: Diary, callback: DataSource.UpdateDiaryCallback) {
        if (box.put(diary) > 0) {
            callback.onDiaryUpdated()
        } else {
            callback.onDiaryUpdateFailed()
        }
    }

    override fun deleteAll() {
        box.removeAll()
    }

    override fun queryByDay(calendar: Calendar, callBack: DataSource.LoadDiaryCallBack) {
        val diary = box.query().equal(Diary_.year, calendar.get(Calendar.YEAR).toLong())
                .and()
                .equal(Diary_.month, calendar.get(Calendar.MONTH).toLong() + 1)
                .and()
                .equal(Diary_.dayOfMonth, calendar.get(Calendar.DAY_OF_MONTH).toLong())
                .build()
                .findFirst()
        if (diary != null) {
            callBack.onDiaryGot(diary)
        } else {
            callBack.onDataNotAvailable()
        }
    }

    override fun queryByMonth(calendar: Calendar, callBack: DataSource.LoadDiariesCallBack) {
        val result = ArrayList<Diary>(calendar.getMaximum(Calendar.DAY_OF_MONTH))
        LogUtil.d(TAG, "diary count:" + result.size)
    }

    override fun queryAndAddDefault(calendar: Calendar, callBack: DataSource.LoadDiariesCallBack) {
        val actualDiaryCount = if (isBefore(calendar))
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        else
            calendar.get(Calendar.DAY_OF_MONTH)
        LogUtil.d(TAG, "actualCount=$actualDiaryCount")
        val data = box.query().equal(Diary_.year, calendar.get(Calendar.YEAR).toLong())
                .and().equal(Diary_.month, calendar.get(Calendar.MONTH).toLong() + 1)
                .build().find()
        LogUtil.d(TAG, "data size = ${data.size}")
        //add empty diary
        val result = ArrayList<Diary>(actualDiaryCount)
        for (i in 1..actualDiaryCount) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            result.add(Diary.emptyDiary(calendar))
        }
        LogUtil.d(TAG, "result size= ${result.size}")
        if (data.isNotEmpty()) {
            for (i in 0 until data.size) {
                if (result.contains(data[i])) {
                    result[data[i].dayOfMonth - 1] = data[i]
                }
            }
        }
        callBack.onDiaryLoaded(result)
    }

    override fun queryByKeyword(keyword: String, callBack: DataSource.LoadDiariesCallBack) {
        val list: List<Diary> = box.query()
                .contains(Diary_.content, keyword)
                .or()
                .contains(Diary_.title, keyword)
                .build()
                .find()
        val result = ArrayList<Diary>()
        result.addAll(list)
        if (list.isNotEmpty()) {
            callBack.onDiaryLoaded(result)
        } else {
            callBack.onDataNotAvailable()
        }
    }

    override fun validDairyCount(): Int {
        val list = box.query()
                .notNull(Diary_.title)
                .or()
                .notNull(Diary_.content)
                .build().find()
        return list.size
    }

    companion object {
        const val TAG = "ObjectBoxDataSource"
    }
}