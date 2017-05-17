package com.example.neon.deardiary.notify;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.data.source.DataSource;
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource;
import com.example.neon.deardiary.diaryedit.DiaryEditActivity;
import com.example.neon.deardiary.util.Constant;

import java.util.Calendar;

public class NotifyReceiver extends BroadcastReceiver implements NotifyContract.View {
    private static final String TAG = "NotifyReceiver";
    private static final int NOTIFICATION_ID = 6;

    private NotifyContract.Presenter mPresenter;

    public NotifyReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DataSource dataSource = DiaryLocalDataSource.getInstance(context);
        mPresenter = new NotifyPresenter(dataSource, this);

        SharedPreferences preferences = context.getSharedPreferences(Constant.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        boolean isRemindSet = preferences.getBoolean(Constant.IS_REMIND, false);
        int remindIdFromPreference = preferences.getInt(Constant.REMIND_ID, 65535);
        int remindIdFromIntent = intent.getIntExtra(Constant.REMIND_ID, 0);

        Log.d(TAG, "onReceive: isRemind?" + isRemindSet + " remindIdFromPreference:"
                + remindIdFromPreference + ",remindIdFromIntent: " + remindIdFromIntent);

        if (isRemindSet && (remindIdFromIntent == remindIdFromPreference)) {
            setNextBroadCastTime(context, intent);
            sendNotification(context);
        } else {
            //广播中断
        }

    }

    private void sendNotification(Context context) {
        Calendar today = Calendar.getInstance();
        Diary diary = mPresenter.getDiary(today);
        if (diary == null) {
            diary = new Diary(today);
            mPresenter.insertDiary(diary);
        }
        Bundle data = new Bundle();
        data.putSerializable(Constant.DIARY, diary);

        Intent startEdit = new Intent(context, DiaryEditActivity.class);
        startEdit.putExtras(data);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startEdit, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)//用户点击后，通知自动取消
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Dear Diary")
                .setContentText("该写日记了")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    private void setNextBroadCastTime(Context context, Intent intent) {
        long lastTime = intent.getLongExtra(Constant.LAST_TIME, 0);
        long nextTime = lastTime + Constant.INTERVAL;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(nextTime);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //复用上次的Intent，更改 lastTime 的数据,确保remindId不变，可以进行重复通知
        intent.putExtra(Constant.LAST_TIME, nextTime);
        //用于发送广播的PendingIntent
        PendingIntent sender = PendingIntent.getBroadcast(context, Constant.REMIND_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        am.setExact(AlarmManager.RTC, nextTime, sender);

        int remind_id = intent.getIntExtra(Constant.REMIND_ID, 0);
        //log信息
        Log.d(TAG, "onClick:  remindId:" + remind_id + ",下次通知时间：" + calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
                + calendar.get(Calendar.DAY_OF_MONTH) + "日" + calendar.get(Calendar.HOUR_OF_DAY) + "时" + calendar.get(Calendar.MINUTE) + "分" + calendar.get(Calendar.SECOND) + "秒");
    }


    @Override
    public void setPresenter(NotifyContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
