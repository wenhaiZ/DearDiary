package com.wenhaiz.deardiary.data.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.wenhaiz.deardiary.data.DataSource
import com.wenhaiz.deardiary.data.Diary
import java.util.ArrayList
import java.util.Calendar

@Suppress("unused")
/**
 *  SQLite 数据源
 */

class SQLDataSource private constructor(context: Context) : DataSource{
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
        db.close()
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

    override fun queryByMonth(calendar: Calendar): List<Diary> {
        val db = mDbHelper.readableDatabase

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val selection = "${DiaryDbHelper.COLUMN_YEAR} ${DiaryDbHelper.OPERATOR_EQUAL} $year ${DiaryDbHelper.OPERATOR_AND} ${DiaryDbHelper.COLUMN_MONTH} ${DiaryDbHelper.OPERATOR_EQUAL} $month"
        val cursor = db.query(DiaryDbHelper.TABLE_NAME, null, selection, null, null, null, DiaryDbHelper.COLUMN_DAY_OF_MONTH)
                ?: return ArrayList()
        val diaries = getDiariesFromCursor(cursor)
        cursor.close()
        db.close()
        return diaries
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
        val db = mDbHelper.readableDatabase
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
        private var sMInstance: SQLDataSource? = null

        @JvmStatic
        fun getInstance(context: Context): SQLDataSource? {
            if (sMInstance == null) {
                // no need to use synchronized lock
                sMInstance = SQLDataSource(context)
            }
            return sMInstance
        }
    }
}
