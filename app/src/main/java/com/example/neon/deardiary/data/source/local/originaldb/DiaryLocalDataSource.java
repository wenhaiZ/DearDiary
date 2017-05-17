package com.example.neon.deardiary.data.source.local.originaldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.data.source.DataSource;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 本地数据源SQLite
 * <p>
 * Created by Neon on 2017/5/15.
 */

public class DiaryLocalDataSource implements DataSource {
    private DiaryDbHelper mDbHelper;
    private static DiaryLocalDataSource mInstance;

    public static DiaryLocalDataSource getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DiaryLocalDataSource.class) {
                if (mInstance == null) {
                    mInstance = new DiaryLocalDataSource(context);
                }
            }
        }
        return mInstance;
    }

    private DiaryLocalDataSource(Context context) {
        mDbHelper = new DiaryDbHelper(context);
    }


    @Override
    public void updateDiary(Diary diary, UpdateDiaryCallback callback) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DiaryDbHelper.COLUMN_MINUTE, diary.getMinute());
        values.put(DiaryDbHelper.COLUMN_HOUR, diary.getHour());
        values.put(DiaryDbHelper.COLUMN_TITLE, diary.getTitle());
        values.put(DiaryDbHelper.COLUMN_CONTENT, diary.getContent());

        String whereClause = DiaryDbHelper.COLUMN_YEAR
                + DiaryDbHelper.OPERATOR_EQUAL + DiaryDbHelper.OPERATOR_ARGUMENT + DiaryDbHelper.OPERATOR_AND
                + DiaryDbHelper.COLUMN_MONTH + DiaryDbHelper.OPERATOR_EQUAL + DiaryDbHelper.OPERATOR_ARGUMENT
                + DiaryDbHelper.OPERATOR_AND
                + DiaryDbHelper.COLUMN_DAY_OF_MONTH + DiaryDbHelper.OPERATOR_EQUAL + DiaryDbHelper.OPERATOR_ARGUMENT;
        String[] whereArgs = {diary.getYear() + "",
                diary.getMonth() + "",
                diary.getDayOfMonth() + ""};

        int result = db.update(DiaryDbHelper.TABLE_NAME, values, whereClause, whereArgs);

        if (result > 0) {
            callback.onDiaryUpdated();
        } else {
            callback.onDiaryUpdateFailed();
        }
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DiaryDbHelper.TABLE_NAME, null, null);
    }


    @NonNull
    private Diary getDiaryFromCursor(Cursor cursor) {
        cursor.moveToFirst();
        Diary diary = new Diary();
        diary.setYear(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_YEAR)));
        diary.setMonth(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_MONTH)));
        diary.setDayOfMonth(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_MONTH)));
        diary.setDayOfWeek(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_WEEK)));
        diary.setMinute(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_MINUTE)));
        diary.setHour(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_HOUR)));
        diary.setTitle(cursor.getString(cursor.getColumnIndex(DiaryDbHelper.COLUMN_TITLE)));
        diary.setContent(cursor.getString(cursor.getColumnIndex(DiaryDbHelper.COLUMN_CONTENT)));
        return diary;
    }

    private ArrayList<Diary> getDiariesFromCursor(Cursor cursor) {
        ArrayList<Diary> diaries = new ArrayList<>();
        while (cursor.moveToNext()) {
            Diary diary = new Diary();
            diary.setYear(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_YEAR)));
            diary.setMonth(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_MONTH)));
            diary.setDayOfMonth(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_MONTH)));
            diary.setDayOfWeek(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_DAY_OF_WEEK)));
            diary.setMinute(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_MINUTE)));
            diary.setHour(cursor.getInt(cursor.getColumnIndex(DiaryDbHelper.COLUMN_HOUR)));
            diary.setTitle(cursor.getString(cursor.getColumnIndex(DiaryDbHelper.COLUMN_TITLE)));
            diary.setContent(cursor.getString(cursor.getColumnIndex(DiaryDbHelper.COLUMN_CONTENT)));
            diaries.add(diary);
        }
        return diaries;

    }

    @Override
    public void queryAndAddDefault(final Calendar calendar, final LoadDiaryCallBack callBack) {
        boolean isBefore = isBefore(calendar);
        //设置查询边界
        final int diaryNumbers = isBefore ? calendar.getActualMaximum(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH);

        queryByMonth(calendar, new LoadDiaryCallBack() {
            @Override
            public void onDiaryLoaded(ArrayList<Diary> diaryList) {
                //如果记录数小于天数，为没有记录的日期添加默认值
                if (diaryList.size() < diaryNumbers) {
                    addDefault(diaryNumbers, calendar);
                    queryByMonth(calendar, callBack);
                } else {
                    callBack.onDiaryLoaded(diaryList);
                }
            }

            @Override
            public void onDataNotAvailable() {
                callBack.onDataNotAvailable();
            }
        });
    }

    private boolean isBefore(Calendar calendar) {
        Calendar now = Calendar.getInstance();//获取当前日期
        return (calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR)) || (calendar.get(Calendar.MONTH) < now.get(Calendar.MONTH));

    }

    @Override
    public void queryByMonth(Calendar calendar, LoadDiaryCallBack callBack) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        Cursor cursor = db.query(DiaryDbHelper.TABLE_NAME, null,
                DiaryDbHelper.COLUMN_YEAR + " = " + year + " and " + DiaryDbHelper.COLUMN_MONTH + " = " + month,
                null, null, null, DiaryDbHelper.COLUMN_DAY_OF_MONTH);
        if (cursor == null) {
            callBack.onDataNotAvailable();
            return;
        }

        ArrayList<Diary> diaries = getDiariesFromCursor(cursor);
        callBack.onDiaryLoaded(diaries);

        cursor.close();
        db.close();
    }

    @Override
    public void queryByDay(Calendar calendar, GetDiaryCallBack callBack) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        Cursor cursor = db.query(DiaryDbHelper.TABLE_NAME, null,
                DiaryDbHelper.COLUMN_YEAR + " = " + year + " and " + DiaryDbHelper.COLUMN_MONTH + " = " + month + " and " + DiaryDbHelper.COLUMN_DAY_OF_MONTH + " = " + dayOfMonth,
                null, null, null, DiaryDbHelper.COLUMN_DAY_OF_MONTH);
        if (cursor == null || cursor.getCount() == 0) {
            callBack.onDataNotAvailable();
            return;
        }

        Diary diary = getDiaryFromCursor(cursor);
        callBack.onDiaryGot(diary);

        cursor.close();
        db.close();
    }

    private void addDefault(int diaryNumbers, final Calendar calendar) {
        for (int i = 1; i <= diaryNumbers; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            queryByDay(calendar, new GetDiaryCallBack() {
                @Override
                public void onDiaryGot(Diary diary) {

                }

                @Override
                public void onDataNotAvailable() {
                    Diary defaultDiary = new Diary(calendar);
                    insertDiary(defaultDiary);
                }
            });

        }
    }

    @Override
    public void insertDiary(Diary diary) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DiaryDbHelper.COLUMN_YEAR, diary.getYear());
        values.put(DiaryDbHelper.COLUMN_MONTH, diary.getMonth());
        values.put(DiaryDbHelper.COLUMN_DAY_OF_MONTH, diary.getDayOfMonth());
        values.put(DiaryDbHelper.COLUMN_DAY_OF_WEEK, diary.getDayOfWeek());
        values.put(DiaryDbHelper.COLUMN_MINUTE, diary.getMinute());
        values.put(DiaryDbHelper.COLUMN_HOUR, diary.getHour());
        values.put(DiaryDbHelper.COLUMN_TITLE, diary.getTitle());
        values.put(DiaryDbHelper.COLUMN_CONTENT, diary.getContent());
        db.insert(DiaryDbHelper.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void queryByKeyword(String keyword, LoadDiaryCallBack callBack) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause = DiaryDbHelper.COLUMN_TITLE
                + DiaryDbHelper.OPERATOR_LIKE + "?"
                + DiaryDbHelper.OPERATOR_OR
                + DiaryDbHelper.COLUMN_CONTENT
                + DiaryDbHelper.OPERATOR_LIKE + "?";

        String[] whereArgs = {"%" + keyword + "%", "%" + keyword + "%"};
        Cursor cursor = db.query(DiaryDbHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            callBack.onDataNotAvailable();
            return;
        }
        ArrayList<Diary> diaries = getDiariesFromCursor(cursor);
        callBack.onDiaryLoaded(diaries);
        cursor.close();
        db.close();
    }

    @Override
    public int getValidDairyCount() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] columns = {DiaryDbHelper.COLUMN_TITLE, DiaryDbHelper.COLUMN_CONTENT};
        String whereClause = DiaryDbHelper.COLUMN_CONTENT
                + DiaryDbHelper.OPERATOR_NOT_EQUAL
                + DiaryDbHelper.OPERATOR_ARGUMENT
                + DiaryDbHelper.OPERATOR_OR
                + DiaryDbHelper.COLUMN_TITLE
                + DiaryDbHelper.OPERATOR_NOT_EQUAL
                + DiaryDbHelper.OPERATOR_ARGUMENT;
        String[] whereArgs = {"", ""};
        Cursor cursor = db.query(DiaryDbHelper.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        db.close();
        return count;
    }
}
