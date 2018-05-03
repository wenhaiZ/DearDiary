package com.wenhaiz.deardiary.data.sql

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wenhaiz.deardiary.utils.LogUtil


internal class DiaryDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        //create table
        LogUtil.d("test", CREATE_TABLE_SQL)
        db.execSQL(CREATE_TABLE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // database update

    }

    companion object {
        //const
        private val DB_NAME = "diaries.db"
        private val DB_VERSION = 1

        val TABLE_NAME = "diary"
        private val TEXT_TYPE = "TEXT"
        private val INT_TYPE = "INTEGER"
        val OPERATOR_EQUAL = "="
        val OPERATOR_NOT_EQUAL = "!="
        val OPERATOR_LIKE = "LIKE"
        val OPERATOR_AND = "AND"
        val OPERATOR_OR = "OR"
        val OPERATOR_ARGUMENT = "?"

        private val COLUMN_ID = "id"
        val COLUMN_YEAR = "year"
        val COLUMN_MONTH = "month"
        val COLUMN_DAY_OF_MONTH = "dayOfMonth"
        val COLUMN_DAY_OF_WEEK = "dayOfWeek"
        val COLUMN_HOUR = "hour"
        val COLUMN_MINUTE = "minute"
        val COLUMN_TITLE = "title"
        val COLUMN_CONTENT = "content"

        private val CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} " +
                "(${COLUMN_ID} ${INT_TYPE} PRIMARY kEY AUTOINCREMENT," +
                "${COLUMN_YEAR} ${INT_TYPE},${COLUMN_MONTH} ${INT_TYPE}," +
                "${COLUMN_DAY_OF_MONTH} ${INT_TYPE},${COLUMN_DAY_OF_WEEK} ${INT_TYPE}," +
                "${COLUMN_HOUR} ${INT_TYPE},${COLUMN_MINUTE} ${INT_TYPE}," +
                "${COLUMN_TITLE} ${TEXT_TYPE},${COLUMN_CONTENT} ${TEXT_TYPE})"
    }
}
