package com.example.neon.deardiary.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 日记实体对象
 *
 * Created by Neon on 2016/11/30.
 */

@Entity
public class Diary implements Serializable {
    private static final long serialVersionUID = 0L;

    //字段
    @Id
    private Long id;//数据库id
    private int year;//年
    private int month;//月
    private int dayOfMonth;//日
    private int dayOfWeek;//星期
    private int hour;//当天最后一次编辑的小时
    private int minute;//当天最后一次编辑的分钟
    private String title;//日记标题
    private String content;//日记内容


    @Generated(hash = 455145805)
    public Diary(Long id, int year, int month, int dayOfMonth, int dayOfWeek, int hour,
                 int minute, String title, String content) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.minute = minute;
        this.title = title;
        this.content = content;
    }


    @Generated(hash = 112123061)
    public Diary() {
    }

    public Diary(Calendar c) {
        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH) + 1;
        this.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        this.dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        this.content = "";
        this.title = "";
        this.hour = 0;
        this.minute = 0;
    }


    //getters and setters
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public int getDayOfWeek() {
        return this.dayOfWeek;
    }


    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


    public int getHour() {
        return this.hour;
    }


    public void setHour(int hour) {
        this.hour = hour;
    }


    public int getMinute() {
        return this.minute;
    }


    public void setMinute(int minute) {
        this.minute = minute;
    }


    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }
}
