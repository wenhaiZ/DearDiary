package com.example.neon.deardiary.data.source.local.originaldb

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.data.source.DataSource

import java.util.ArrayList
import java.util.Calendar

/**
 * 本地 SQLite 数据源
 */

class DiaryLocalDataSource private constructor(context: Context) : DataSource {


    private val mDbHelper: DiaryDbHelper = DiaryDbHelper(context)


    override fun updateDiary(diary: Diary, callback: DataSource.UpdateDiaryCallback) {
        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(DiaryDbHelper.COLUMN_MINUTE, diary.minute)
        values.put(DiaryDbHelper.COLUMN_HOUR, diary.hour)
        values.put(DiaryDbHelper.COLUMN_TITLE, diary.title)
        values.put(DiaryDbHelper.COLUMN_CONTENT, diary.content)

        val whereClause = DiaryDbHelper.COLUMN_YEAR + DiaryDbHelper.OPERATOR_EQUAL + DiaryDbHelper.OPERATOR_ARGUMENT + DiaryDbHelper.OPERATOR_AND + DiaryDbHelper.COLUMN_MONTH + DiaryDbHelper.OPERATOR_EQUAL + DiaryDbHelper.OPERATOR_ARGUMENT + DiaryDbHelper.OPERATOR_AND + DiaryDbHelper.COLUMN_DAY_OF_MONTH + DiaryDbHelper.OPERATOR_EQUAL + DiaryDbHelper.OPERATOR_ARGUMENT
        val whereArgs = arrayOf(diary.year.toString() + "", diary.month.toString() + "", diary.dayOfMonth.toString() + "")

        val result = db.update(DiaryDbHelper.TABLE_NAME, values, whereClause, whereArgs)

        if (result > 0) {
            callback.onDiaryUpdated()
        } else {
            callback.onDiaryUpdateFailed()
        }
        db.close()
    }

    override fun deleteAll() {
        val db = mDbHelper.writableDatabase
        db.delete(DiaryDbHelper.TABLE_NAME, null, null)
    }


    private fun getDiaryFromCursor(cursor: Cursor): Diary {
        cursor.moveToFirst()
        val diary = Diary()
        diary.year = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_YEAR))
        diary.month = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_MONTH))
        diary.dayOfMonth = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_MONTH))
        diary.dayOfWeek = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_WEEK))
        diary.minute = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_MINUTE))
        diary.hour = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_HOUR))
        diary.title = cursor.getString(cursor.getColumnIndex(DiaryDbHelper.COLUMN_TITLE))
        diary.content = cursor.getString(cursor.getColumnIndex(DiaryDbHelper.COLUMN_CONTENT))
        return diary
    }

    private fun getDiariesFromCursor(cursor: Cursor): ArrayList<Diary> {
        val diaries = ArrayList<Diary>()
        while (cursor.moveToNext()) {
            val diary = Diary()
            diary.year = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_YEAR))
            diary.month = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_MONTH))
            diary.dayOfMonth = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_MONTH))
            diary.dayOfWeek = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_WEEK))
            diary.minute = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_MINUTE))
            diary.hour = cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_HOUR))
            diary.title = cursor.getString(cursor.getColumnIndex(DiaryDbHelper.COLUMN_TITLE))
            diary.content = cursor.getString(cursor.getColumnIndex(DiaryDbHelper.COLUMN_CONTENT))
            diaries.add(diary)
        }
        return diaries

    }

    override fun queryAndAddDefault(calendar: Calendar, callBack: DataSource.LoadDiaryCallBack) {
        val isBefore = isBefore(calendar)
        //设置查询边界
        val diaryNumbers = if (isBefore) calendar.getActualMaximum(Calendar.DAY_OF_MONTH) else calendar.get(Calendar.DAY_OF_MONTH)

        queryByMonth(calendar, object : DataSource.LoadDiaryCallBack {
            override fun onDiaryLoaded(diaryList: ArrayList<Diary>) {
                //如果记录数小于天数，为没有记录的日期添加默认值
                if (diaryList.size < diaryNumbers) {
                    addDefault(diaryNumbers, calendar)
                    queryByMonth(calendar, callBack)
                } else {
                    callBack.onDiaryLoaded(diaryList)
                }
            }

            override fun onDataNotAvailable() {
                callBack.onDataNotAvailable()
            }
        })
    }

    private fun isBefore(calendar: Calendar): Boolean {
        val now = Calendar.getInstance()//获取当前日期
        return calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR) || calendar.get(Calendar.MONTH) < now.get(Calendar.MONTH)

    }

    override fun queryByMonth(calendar: Calendar, callBack: DataSource.LoadDiaryCallBack) {
        val db = mDbHelper.writableDatabase

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        val cursor = db.query(DiaryDbHelper.TABLE_NAME, null,
                DiaryDbHelper.COLUMN_YEAR + " = " + year + " and " + DiaryDbHelper.COLUMN_MONTH + " = " + month, null, null, null, DiaryDbHelper.COLUMN_DAY_OF_MONTH)
        if (cursor == null) {
            callBack.onDataNotAvailable()
            return
        }

        val diaries = getDiariesFromCursor(cursor)
        callBack.onDiaryLoaded(diaries)

        cursor.close()
        db.close()
    }

    override fun queryByDay(calendar: Calendar, callBack: DataSource.GetDiaryCallBack) {
        val db = mDbHelper.writableDatabase

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val cursor = db.query(DiaryDbHelper.TABLE_NAME, null,
                DiaryDbHelper.COLUMN_YEAR + " = " + year + " and " + DiaryDbHelper.COLUMN_MONTH + " = " + month + " and " + DiaryDbHelper.COLUMN_DAY_OF_MONTH + " = " + dayOfMonth, null, null, null, DiaryDbHelper.COLUMN_DAY_OF_MONTH)
        if (cursor == null || cursor.count == 0) {
            callBack.onDataNotAvailable()
            return
        }

        val diary = getDiaryFromCursor(cursor)
        callBack.onDiaryGot(diary)

        cursor.close()
        db.close()
    }

    private fun addDefault(diaryNumbers: Int, calendar: Calendar) {
        for (i in 1..diaryNumbers) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            queryByDay(calendar, object : DataSource.GetDiaryCallBack {
                override fun onDiaryGot(diary: Diary) {

                }

                override fun onDataNotAvailable() {
                    val defaultDiary = Diary(calendar)
                    insertDiary(defaultDiary)
                }
            })

        }
    }

    override fun insertDiary(diary: Diary) {
        val db = mDbHelper.writableDatabase
        val values = ContentValues()
        values.put(DiaryDbHelper.COLUMN_YEAR, diary.year)
        values.put(DiaryDbHelper.COLUMN_MONTH, diary.month)
        values.put(DiaryDbHelper.COLUMN_DAY_OF_MONTH, diary.dayOfMonth)
        values.put(DiaryDbHelper.COLUMN_DAY_OF_WEEK, diary.dayOfWeek)
        values.put(DiaryDbHelper.COLUMN_MINUTE, diary.minute)
        values.put(DiaryDbHelper.COLUMN_HOUR, diary.hour)
        values.put(DiaryDbHelper.COLUMN_TITLE, diary.title)
        values.put(DiaryDbHelper.COLUMN_CONTENT, diary.content)
        db.insert(DiaryDbHelper.TABLE_NAME, null, values)
        db.close()
    }

    override fun queryByKeyword(keyword: String, callBack: DataSource.LoadDiaryCallBack) {
        val db = mDbHelper.readableDatabase

        val whereClause = DiaryDbHelper.COLUMN_TITLE + DiaryDbHelper.OPERATOR_LIKE + "?" + DiaryDbHelper.OPERATOR_OR + DiaryDbHelper.COLUMN_CONTENT + DiaryDbHelper.OPERATOR_LIKE + "?"

        val whereArgs = arrayOf("%$keyword%", "%$keyword%")
        val cursor = db.query(DiaryDbHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null)

        if (cursor == null || cursor.count == 0) {
            callBack.onDataNotAvailable()
            return
        }
        val diaries = getDiariesFromCursor(cursor)
        callBack.onDiaryLoaded(diaries)
        cursor.close()
        db.close()
    }

    override fun validDairyCount(): Int {
        val db = mDbHelper.writableDatabase
        val columns = arrayOf(DiaryDbHelper.COLUMN_TITLE, DiaryDbHelper.COLUMN_CONTENT)
        val whereClause = "${DiaryDbHelper.COLUMN_CONTENT}${DiaryDbHelper.OPERATOR_NOT_EQUAL}${DiaryDbHelper.OPERATOR_ARGUMENT}${DiaryDbHelper.OPERATOR_OR}${DiaryDbHelper.COLUMN_TITLE}${DiaryDbHelper.OPERATOR_NOT_EQUAL}${DiaryDbHelper.OPERATOR_ARGUMENT}"
        val whereArgs = arrayOf("", "")
        val cursor = db.query(DiaryDbHelper.TABLE_NAME, columns, whereClause, whereArgs, null, null, null)
        var count = 0
        if (cursor != null) {
            count = cursor.count
            cursor.close()
        }
        db.close()
        return count
    }

    companion object {
        private var mInstance: DiaryLocalDataSource? = null

        fun getInstance(context: Context): DiaryLocalDataSource? {
            if (mInstance == null) {
                synchronized(DiaryLocalDataSource::class.java) {
                    if (mInstance == null) {
                        mInstance = DiaryLocalDataSource(context)
                    }
                }
            }
            return mInstance
        }
    }
}
