package com.example.neon.deardiary.data.source.local.originaldb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DiaryDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "diaries.db";
    private static final int DB_VERSION = 1;

    static final String TABLE_NAME = "diary";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String INT_TYPE = " INTEGER ";
    public static final String OPERATOR_EQUAL = " = ";
    public static final String OPERATOR_NOT_EQUAL = " != ";
    public static final String OPERATOR_LIKE = " LIKE ";
    public static final String OPERATOR_AND = " AND ";
    public static final String OPERATOR_OR = " OR ";
    public static final String OPERATOR_ARGUMENT = "?";

    private static final String COLUMN_ID = "id";
    static final String COLUMN_YEAR = "year";
    static final String COLUMN_MONTH = "month";
    static final String COLUMN_DAY_OF_MONTH = "dayOfMonth";
    static final String COLUMN_DAY_OF_WEEK = "dayOfWeek";
    static final String COLUMN_HOUR = "hour";
    static final String COLUMN_MINUTE = "minute";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_CONTENT = "content";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " ("
            + COLUMN_ID + TEXT_TYPE + "PRIMARY kEY,"
            + COLUMN_YEAR + INT_TYPE + ","
            + COLUMN_MONTH + INT_TYPE + ","
            + COLUMN_DAY_OF_MONTH + INT_TYPE + ","
            + COLUMN_DAY_OF_WEEK + INT_TYPE + ","
            + COLUMN_HOUR + INT_TYPE + ","
            + COLUMN_MINUTE + INT_TYPE + ","
            + COLUMN_TITLE + TEXT_TYPE + ","
            + COLUMN_CONTENT + TEXT_TYPE
            + ")";


    DiaryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
