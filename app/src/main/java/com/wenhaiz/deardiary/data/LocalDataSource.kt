package com.wenhaiz.deardiary.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.util.ArrayList
import java.util.Calendar

/**
 *  SQLite 数据源
 */

class LocalDataSource private constructor(context: Context) : DataSource {
    private var mDbHelper: DiaryDbHelper = DiaryDbHelper(context)

    override fun updateDiary(diary: Diary, callback: DataSource.UpdateDiaryCallback) {
        val db = mDbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DiaryDbHelper.COLUMN_MINUTE, diary.minute)
            put(DiaryDbHelper.COLUMN_HOUR, diary.hour)
            put(DiaryDbHelper.COLUMN_TITLE, diary.title)
            put(DiaryDbHelper.COLUMN_CONTENT, diary.content)
        }
        val whereClause = "${DiaryDbHelper.COLUMN_YEAR} ${DiaryDbHelper.OPERATOR_EQUAL} ${DiaryDbHelper.OPERATOR_ARGUMENT} ${DiaryDbHelper.OPERATOR_AND} " +
                "${DiaryDbHelper.COLUMN_MONTH} ${DiaryDbHelper.OPERATOR_EQUAL} ${DiaryDbHelper.OPERATOR_ARGUMENT} ${DiaryDbHelper.OPERATOR_AND}  " +
                "${DiaryDbHelper.COLUMN_DAY_OF_MONTH} ${DiaryDbHelper.OPERATOR_EQUAL} ${DiaryDbHelper.OPERATOR_ARGUMENT}"
        val whereArgs = arrayOf(diary.year.toString(), diary.month.toString(), diary.dayOfMonth.toString())

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
        return Diary().apply {
            year = with(cursor) { getInt(getColumnIndex(DiaryDbHelper.COLUMN_YEAR)) }
            month = with(cursor) { getInt(getColumnIndex(DiaryDbHelper.COLUMN_MONTH)) }
            dayOfMonth = with(cursor) { getInt(getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_MONTH)) }
            dayOfWeek = with(cursor) { getInt(getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_WEEK)) }
            minute = with(cursor) { getInt(getColumnIndex(DiaryDbHelper.COLUMN_MINUTE)) }
            hour = with(cursor) { getInt(getColumnIndex(DiaryDbHelper.COLUMN_HOUR)) }
            title = with(cursor) { getString(getColumnIndex(DiaryDbHelper.COLUMN_TITLE)) }
            content = with(cursor) { getString(getColumnIndex(DiaryDbHelper.COLUMN_CONTENT)) }
        }
    }

    private fun getDiariesFromCursor(cursor: Cursor): ArrayList<Diary> {
        val diaries = ArrayList<Diary>()
        while (cursor.moveToNext()) {
            val diary = getDiaryFromCursor(cursor)
            diaries.add(diary)
        }
        return diaries
    }

    //按月查询时，如果有记录为空，就添加一个默认的日记记录
    override fun queryAndAddDefault(calendar: Calendar, callBack: DataSource.LoadDiariesCallBack) {
        val isBefore = isBefore(calendar)
        //设置查询边界
        val diaryNumbers =
                if (isBefore)
                //获取当月最大天数
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                else
                //获取当前日期
                    calendar.get(Calendar.DAY_OF_MONTH)

        queryByMonth(calendar, object : DataSource.LoadDiariesCallBack {
            override fun onDiaryLoaded(diaryList: ArrayList<Diary>) {
                //如果记录数小于天数，为没有记录的日期添加默认值
                if (diaryList.size < diaryNumbers) {
                    addEmptyDiary(diaryNumbers, calendar)
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

    //检查给定日期是否早于当前时间
    private fun isBefore(calendar: Calendar): Boolean {
        val now = Calendar.getInstance()//获取当前日期
        return calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR) || calendar.get(Calendar.MONTH) < now.get(Calendar.MONTH)

    }

    override fun queryByMonth(calendar: Calendar, callBack: DataSource.LoadDiariesCallBack) {
        val db = mDbHelper.readableDatabase

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val selection = "${DiaryDbHelper.COLUMN_YEAR} ${DiaryDbHelper.OPERATOR_EQUAL} $year ${DiaryDbHelper.OPERATOR_AND} ${DiaryDbHelper.COLUMN_MONTH} ${DiaryDbHelper.OPERATOR_EQUAL} $month"
        val cursor = db.query(DiaryDbHelper.TABLE_NAME, null, selection, null, null, null, DiaryDbHelper.COLUMN_DAY_OF_MONTH)
        if (cursor == null) {
            callBack.onDataNotAvailable()
            return
        }

        val diaries = getDiariesFromCursor(cursor)
        callBack.onDiaryLoaded(diaries)
        cursor.close()
        db.close()
    }

    override fun queryByDay(calendar: Calendar, callBack: DataSource.LoadDiaryCallBack) {
        val db = mDbHelper.readableDatabase

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val selection = "${DiaryDbHelper.COLUMN_YEAR} ${DiaryDbHelper.OPERATOR_EQUAL} $year ${DiaryDbHelper.OPERATOR_AND} ${DiaryDbHelper.COLUMN_MONTH} ${DiaryDbHelper.OPERATOR_EQUAL} $month ${DiaryDbHelper.OPERATOR_AND} ${DiaryDbHelper.COLUMN_DAY_OF_MONTH} ${DiaryDbHelper.OPERATOR_EQUAL} $dayOfMonth"
        val cursor = db.query(DiaryDbHelper.TABLE_NAME, null, selection, null, null, null, DiaryDbHelper.COLUMN_DAY_OF_MONTH)
        if (cursor == null || cursor.count == 0) {
            callBack.onDataNotAvailable()
            return
        }
        //notice:没有这句代码，cursor 的 mPos 初始为-1，
        //会抛出 CursorIndexOutOfBoundsException: Index -1 requested, with a size of 1
        cursor.moveToFirst()

        val diary = getDiaryFromCursor(cursor)
        callBack.onDiaryGot(diary)
        cursor.close()
        db.close()
    }

    private fun addEmptyDiary(diaryNumbers: Int, calendar: Calendar) {
        for (i in 1..diaryNumbers) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            queryByDay(calendar, object : DataSource.LoadDiaryCallBack {
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
        val values = ContentValues().apply {
            put(DiaryDbHelper.COLUMN_YEAR, diary.year)
            put(DiaryDbHelper.COLUMN_MONTH, diary.month)
            put(DiaryDbHelper.COLUMN_DAY_OF_MONTH, diary.dayOfMonth)
            put(DiaryDbHelper.COLUMN_DAY_OF_WEEK, diary.dayOfWeek)
            put(DiaryDbHelper.COLUMN_MINUTE, diary.minute)
            put(DiaryDbHelper.COLUMN_HOUR, diary.hour)
            put(DiaryDbHelper.COLUMN_TITLE, diary.title)
            put(DiaryDbHelper.COLUMN_CONTENT, diary.content)
        }
        db.insert(DiaryDbHelper.TABLE_NAME, null, values)
        db.close()
    }

    override fun queryByKeyword(keyword: String, callBack: DataSource.LoadDiariesCallBack) {
        val db = mDbHelper.readableDatabase
        val whereClause = "${DiaryDbHelper.COLUMN_TITLE} ${DiaryDbHelper.OPERATOR_LIKE} ${DiaryDbHelper.OPERATOR_ARGUMENT} ${DiaryDbHelper.OPERATOR_OR} ${DiaryDbHelper.COLUMN_CONTENT} ${DiaryDbHelper.OPERATOR_LIKE} ${DiaryDbHelper.OPERATOR_ARGUMENT}"
        val whereArgs = arrayOf("%$keyword%", "%$keyword%")

        val cursor = db.query(DiaryDbHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null)
        if (cursor == null || cursor.count == 0) {
            callBack.onDataNotAvailable()
        } else {
            callBack.onDiaryLoaded(getDiariesFromCursor(cursor))
        }
        cursor.close()
        db.close()
    }

    override fun validDairyCount(): Int {
        val db = mDbHelper.writableDatabase
        val columns = arrayOf(DiaryDbHelper.COLUMN_TITLE, DiaryDbHelper.COLUMN_CONTENT)
        val whereClause = "${DiaryDbHelper.COLUMN_CONTENT} ${DiaryDbHelper.OPERATOR_NOT_EQUAL} ${DiaryDbHelper.OPERATOR_ARGUMENT} ${DiaryDbHelper.OPERATOR_OR} ${DiaryDbHelper.COLUMN_TITLE} ${DiaryDbHelper.OPERATOR_NOT_EQUAL} ${DiaryDbHelper.OPERATOR_ARGUMENT}"
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
        private var mInstance: LocalDataSource? = null

        @JvmStatic
        fun getInstance(context: Context): LocalDataSource? {
            if (mInstance == null) {
                // no need to use synchronized lock
                mInstance = LocalDataSource(context)
            }
            return mInstance
        }
    }
}
