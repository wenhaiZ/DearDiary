package com.example.neon.deardiary;

import java.util.Calendar;

/**
 * 日记实体对象
 * Created by Neon on 2016/11/30.
 */

public class DiaryBean {
    private int year;//年份
    private int month;//月
    private int dayOfMonth;//日
    private String weekDay;//星期
    private int paperType;//纸张类型
    private String content;//日记内容
    public static final int DEFAULT_PAPER_TYPE = 0;
    private final String[] DAY_OF_WEEK={"","周日","周一","周二","周三","周四","周五","周六"};

    /**
     * 含参构造器
     * @param year
     * @param month
     * @param dayOfMonth
     * @param weekDay
     * @param paperType
     * @param content
     */
    public DiaryBean(int year, int month, int dayOfMonth, int weekDay, int paperType, String content) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.weekDay = DAY_OF_WEEK[weekDay];
        this.paperType = paperType;
        this.content = content;
    }

    public DiaryBean(int year, int month, int dayOfMonth, String weekDay, int paperType, String content) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.weekDay = weekDay;
        this.paperType = paperType;
        this.content = content;
    }

    public DiaryBean(Calendar c){

        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH)+1;
        this.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        this.weekDay = DAY_OF_WEEK[c.get(Calendar.DAY_OF_WEEK)];
        this.paperType = DEFAULT_PAPER_TYPE;
        this.content = "";
    }

    public DiaryBean(Calendar c,String content){

        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH)+1;
        this.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        this.weekDay = DAY_OF_WEEK[c.get(Calendar.DAY_OF_WEEK)];
        this.paperType = DEFAULT_PAPER_TYPE;
        this.content = content;
    }

    /**
     * 无参构造器
     */
    public DiaryBean(){

    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = DAY_OF_WEEK[weekDay];
    }

    public int getPaperType() {
        return paperType;
    }

    public void setPaperType(int paperType) {
        this.paperType = paperType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
