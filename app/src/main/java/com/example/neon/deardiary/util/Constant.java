package com.example.neon.deardiary.util;

/**
 * 用到的常量
 * Created by Neon on 2016/12/3.
 */

public class Constant {
    public static final long INTERVAL =24*60*60*1000;//发送通知的间隔24小时
    public static final String SHARED_PREFERENCE = "activity_setting";
    public static final String IS_REMIND = "is_remind";//是否设置提醒
    public static final String REMIND_ID = "remind_id";//用于标识当前设置的提醒，供BroadcastReceiver进行判断，是否相应
    public static final String REMIND_TIME = "remind_time";//提醒时间
    public static final String TEXT_SIZE = "text_size";//字体大小
    public static final String HAS_PASSWORD = "has_password";//是否设置了密码
    public static final String PASSWORD = "password";//密码
    public static final String DIARY_COUNT = "diary_count";//日记数量
    public static final int REMIND_CODE = 0x123;
    public static final String LAST_TIME = "lastTime";
    public static final String PUSH_ACTION= "com.example.neon.deardiary.Receiver";
    public static final String DIARY = "diary";
}
