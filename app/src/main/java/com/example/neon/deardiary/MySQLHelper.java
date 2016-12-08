package com.example.neon.deardiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created by Neon on 2016/11/30.
 */

public class MySQLHelper extends SQLiteOpenHelper {

    public MySQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public MySQLHelper(Context context) {
        super(context, DBConstant.DATABASE_NAME, null, DBConstant.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //建立数据表
        String createTable = "create table if not exists " + DBConstant.TABLE_NAME + " ("
                + DBConstant.ID + " Integer primary key autoincrement,"
                + DBConstant.YEAR + " Integer not null,"
                + DBConstant.MONTH + " Integer not null,"
                + DBConstant.DAY + " Integer not null,"
                + DBConstant.WEEKDAY + " Integer not null,"
                + DBConstant.PAPER_TYPE + " Integer,"
                + DBConstant.CONTENT + " text)";
        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }


    public boolean insertDiaryToTable(DiaryBean diaryBean) {
        //打开数据库
        SQLiteDatabase database = this.getReadableDatabase();
        //将 DiaryBean 数据写入 ContentValues
        ContentValues values = new ContentValues();
        values.put(DBConstant.CONTENT, diaryBean.getContent());
        values.put(DBConstant.DAY, diaryBean.getDayOfMonth());
        values.put(DBConstant.MONTH, diaryBean.getMonth());
        values.put(DBConstant.PAPER_TYPE, diaryBean.getPaperType());
        values.put(DBConstant.WEEKDAY, diaryBean.getWeekDay());
        values.put(DBConstant.YEAR, diaryBean.getYear());
        //在数据表中插入数据，并对结果进行判定
        long result = database.insert(DBConstant.TABLE_NAME, null, values);
//        Log.d(TAG, "insertDiaryToTable() called with: result = [" + result + "]");
        database.close();
        return result > 0;
    }


    /**
     * 清空数据表中所有记录
     */
    public void clearTable() {
        SQLiteDatabase database = this.getReadableDatabase();
        database.delete(DBConstant.TABLE_NAME, null, null);
        database.close();
    }


    /**
     * 由时间搜索数据库，并返回返回一个DiaryBean对象
     *
     * @param calendar
     * @return
     */
    public DiaryBean getDiaryBean(final Calendar calendar) {

        DiaryBean diaryBean = null;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + DBConstant.TABLE_NAME + " where " + DBConstant.YEAR + "=? and " + DBConstant.MONTH + "=?"
                + " and " + DBConstant.DAY + "=?", new String[]{year + "", month + "", day + ""});
        if (cursor.moveToNext()) {
            String mContent = cursor.getString(cursor.getColumnIndex(DBConstant.CONTENT));
            //创建DiaryBean对象
            diaryBean = new DiaryBean(calendar, mContent);
        }
        cursor.close();
        database.close();
        return diaryBean;

    }


    /**
     * 更新指定日记的内容
     *
     * @param diaryBean
     * @return
     */
    public boolean updateDiaryContent(DiaryBean diaryBean) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstant.CONTENT, diaryBean.getContent());
        long result = database.update(DBConstant.TABLE_NAME, values, DBConstant.YEAR + "=" + diaryBean.getYear() + " and " + DBConstant.MONTH + "=" + diaryBean.getMonth() +
                " and " + DBConstant.DAY + "=" + diaryBean.getDayOfMonth(), null);
        database.close();
        return result > 0;
    }


    /**
     * 填充数据，用于测试
     */
//    public void fillTable() {
//        //打开数据库
//        Calendar test = Calendar.getInstance();
//        for (int i = 1; i < 30; i++) {
//            test.set(Calendar.YEAR, 2016);
//            test.set(Calendar.MONTH, 10);
//            test.set(Calendar.DAY_OF_MONTH, i);
//            DiaryBean diaryBean = new DiaryBean(test, "zhaowennnvshdionnvnjmohhf ");
//            this.insertDiaryToTable(diaryBean);
//        }
//
//    }


    /**
     * 根据 Calendar 对象按天查询数据
     *
     * @param calendar
     * @return
     */
    public Cursor queryByDay(Calendar calendar) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + DBConstant.TABLE_NAME
                        + " where " + DBConstant.YEAR + "=? and "
                        + DBConstant.MONTH + " = ? and " + DBConstant.DAY + "= ? order by "
                        + DBConstant.DAY
                , new String[]{calendar.get(Calendar.YEAR) + "", (calendar.get(Calendar.MONTH) + 1) + "", calendar.get(Calendar.DAY_OF_MONTH) + ""});

        return cursor;


    }


    /**
     * 查询一个月的记录
     *
     * @param calendar
     * @return
     */
    public Cursor queryByMonth(Calendar calendar) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + DBConstant.TABLE_NAME
                        + " where " + DBConstant.YEAR + "=? and "
                        + DBConstant.MONTH + " = ? order by "
                        + DBConstant.DAY
                , new String[]{calendar.get(Calendar.YEAR) + "", (calendar.get(Calendar.MONTH) + 1) + ""});

        return cursor;


    }

    /**
     * 按日期查询，并为没有日记的日期添加默认值添加默认值
     *
     * @param calendar
     * @return
     */
    public Cursor queryAddDefault(Calendar calendar) {
        boolean isBefore = true;//是否是之前的月份,默认为true

        Calendar now = Calendar.getInstance();//获取当前日期

        //如果是当前月
        if ((calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) && (calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH))) {
            isBefore = false;
        }
        //设置查询边界
        int end = isBefore ? calendar.getActualMaximum(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH);

        Cursor cursor = this.queryByMonth(calendar);
        //当月没有记录，直接插入默认值
        if (cursor.getCount() == 0) {
            for (int i = 1; i <= end; i++) {
                calendar.set(Calendar.DAY_OF_MONTH, i);
                DiaryBean diaryBean = new DiaryBean(calendar);
                this.insertDiaryToTable(diaryBean);
            }

        }
        if (cursor.getCount() < end) {
            //为没有记录的日期添加空记录
            for (int i = 1; i <= end; i++) {
                calendar.set(Calendar.DAY_OF_MONTH, i);
                Cursor dayCursor = this.queryByDay(calendar);
                //如果没有记录，就添加一条空记录
                if (!dayCursor.moveToNext()) {
                    DiaryBean diaryBean = new DiaryBean(calendar);
                    this.insertDiaryToTable(diaryBean);
                }
                dayCursor.close();
            }
        }
        cursor.close();
        //此时虽然calendar的日期以改变，但月份没变
        return this.queryByMonth(calendar);

    }


    public Cursor getValidRecord() {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("select * from " + DBConstant.TABLE_NAME + " where " + DBConstant.CONTENT + "!=''", null);
    }
}
