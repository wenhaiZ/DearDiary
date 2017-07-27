//package com.example.neon.deardiary.dao;
//
//import android.content.Context;
//
//import java.util.Calendar;
//import java.util.List;
//
///**
// * 封装一系列需要的数据库操作
// * Created by Neon on 2016/12/10.
// */
//
//public class DaoOpsHelper {
//
//    private DiaryDao mDiaryDao;
//
//    public DaoOpsHelper(Context context) {
//        mDiaryDao = DaoManager.INSTANCE.getDaoSession(context).getDiaryDao();
//    }
//
//    //插入一条记录
//    public void insertDairy(com.example.neon.deardiary.dao.Diary diary) {
//        mDiaryDao.insert(diary);
//    }
//
//    //更新指定日记内容
//
//    public void updateDiary(com.example.neon.deardiary.dao.Diary diary) {
//        mDiaryDao.update(diary);
//    }
//
//    // 删除所有数据
//
//    public void deleteAll() {
//        mDiaryDao.deleteAll();
//    }
//
//
//    //根据指定日期按天搜索数据库返回一个 Diary 对象
//
//    public com.example.neon.deardiary.dao.Diary queryByDay(Calendar c) {
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH) + 1;
//        int day = c.get(Calendar.DAY_OF_MONTH);
//        return mDiaryDao.queryBuilder().where(DiaryDao.Properties.Year.eq(year),
//                DiaryDao.Properties.Month.eq(month),
//                DiaryDao.Properties.DayOfMonth.eq(day)).unique();
//    }
//
//
//    //查找一个月的记录
//    public List<Diary> queryByMonth(Calendar c) {
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH) + 1;
//        return mDiaryDao.queryBuilder().where(DiaryDao.Properties.Year.eq(year), DiaryDao.Properties.Month.eq(month))
//                .orderAsc(DiaryDao.Properties.DayOfMonth)
//                .list();
//    }
//
//
//    /*
//     * 按月查询，
//     * 如果当月没有记录或记录小于当前天数，为没有记录的日期添加默认值，
//     * 以便listView的适配器决定视图
//     */
//    public List<Diary> queryAddDefault(Calendar c) {
//        boolean isBefore = true;//标识是否是之前的月份,默认为true
//        Calendar now = Calendar.getInstance();//获取当前日期
//        //如果是当前月
//        if ((c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) && (c.get(Calendar.MONTH) == now.get(Calendar.MONTH))) {
//            isBefore = false;
//        }
//        //设置查询边界
//        int end = isBefore ? c.getActualMaximum(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH);
//        List<Diary> diaries = queryByMonth(c);
//
//        //当月没有记录，为每日按添加默认值
//        if (diaries.size() == 0) {
//            for (int i = 1; i <= end; i++) {
//                c.set(Calendar.DAY_OF_MONTH, i);
//                Diary diary = new Diary(c);
//                insertDairy(diary);
//            }
//
//        }
//
//        //如果记录数小于天数，为没有记录的日期添加默认值
//        if (diaries.size() < end) {
//            //为没有记录的日期添加空记录
//            for (int i = 1; i <= end; i++) {
//                c.set(Calendar.DAY_OF_MONTH, i);
//                Diary diary = queryByDay(c);
//                //如果没有记录，就添加一条空记录
//                if (diary == null) {
//                    Diary newDiary = new Diary(c);
//                    insertDairy(newDiary);
//                }
//            }
//        }
//        return queryByMonth(c);//此时虽然calendar的日期以改变，但月份没变
//    }
//
//
//    //按内容搜索
//    public List<Diary> queryByContent(String key) {
//        return mDiaryDao.queryBuilder()
//                .where(DiaryDao.Properties.Content.like(key))
//                .orderDesc(DiaryDao.Properties.Year)
//                .orderDesc(DiaryDao.Properties.Month)
//                .orderDesc(DiaryDao.Properties.DayOfMonth)
//                .list();
//    }
//
//
//    //查询所有日记内容不为空的记录，返回记录数量
//    public int getValidDiaryCount() {
//        return mDiaryDao.queryBuilder().where(DiaryDao.Properties.Content.notEq("")).list().size();
//    }
//}
