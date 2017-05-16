package com.example.neon.deardiary.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.example.neon.deardiary.diaryedit.DiaryEditActivity;
import com.example.neon.deardiary.util.Constant;
import com.example.neon.deardiary.dao.DaoOpsHelper;
import com.example.neon.deardiary.dao.Diary;
import com.example.neon.deardiary.R;

import java.util.Calendar;

public class NotifyReceiver extends BroadcastReceiver {
    private static final String TAG = "NotifyReceiver";

    public NotifyReceiver() {
    }

    /**
     * 收到消息后先进行判定是否响应
     * 若是则发送通知，提醒用户写日记
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //从配置文件中查看是否设置了提醒
        boolean isRemind = context.getSharedPreferences(Constant.SHARED_PREFERENCE, Context.MODE_PRIVATE)
                .getBoolean(Constant.IS_REMIND, false);
        //从配置文件查看提醒对应的id(如果重新设置了提醒时间，那么该id会更新)
        int remindIdFromPreference = context.getSharedPreferences(Constant.SHARED_PREFERENCE, Context.MODE_PRIVATE).getInt(Constant.REMIND_ID, 65535);
        //从intent中读取id，该intent可能是上次设置的提醒中的，所以要进行判断
        int remindIdFromIntent = intent.getIntExtra(Constant.REMIND_ID, 0);
        Log.d(TAG, "onReceive: isRemind?" + isRemind + " remindIdFromPreference:"
                + remindIdFromPreference + ",remindIdFromIntent: " + remindIdFromIntent);
        //如果开启了提醒，并且当前intent中的remind_id与配置文件中的相等，那么发送通知
        if (isRemind && (remindIdFromIntent == remindIdFromPreference)) {
            //发送通知
            sendNotification(context);
            //设置下一次通知时间
            setNextBroadCast(context, intent);
        } else {
            Log.d(TAG, "onReceive: 收到广播");
        }

    }

    /**
     * 发送通知栏通知提醒用户写日记
     *
     * @param context
     */
    private void sendNotification(Context context) {
        //用于启动 EditActivity 的Intent
        Intent mIntent = new Intent(context, DiaryEditActivity.class);
        Bundle b = new Bundle();
        //放入今天日期
        Calendar today = Calendar.getInstance();
        DaoOpsHelper helper = new DaoOpsHelper(context);
        Diary diary = helper.queryByDay(today);
        if (diary == null) {
            diary = new Diary(today);
            helper.insertDairy(diary);
        }
        b.putSerializable(Constant.DIARY, diary);
        mIntent.putExtras(b);
        //包装上面Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_ONE_SHOT);

        //创建一个新的通知
        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)//用户点击后，通知自动取消
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Dear Diary")
                .setContentText("该写日记了")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        //获得通知管理服务
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //发送通知
        manager.notify((int) System.currentTimeMillis(), notification);
    }


    /**
     * 设置下次通知时间，间隔24小时
     * @param context
     * @param intent
     */
    private void setNextBroadCast(Context context, Intent intent) {
        //设置本次通知的时间
        long lastTime = intent.getLongExtra(Constant.LAST_TIME, 0);
        //下次发送通知的时间
        long nextTime = lastTime + Constant.INTERVAL;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(nextTime);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //复用上次的Intent，只更改 lastTime 的数据,确保remind_id不变，可以进行重复通知
        intent.putExtra(Constant.LAST_TIME, nextTime);
        int remind_id = intent.getIntExtra(Constant.REMIND_ID, 0);
        //用于发送广播的PendingIntent
        PendingIntent sender = PendingIntent.getBroadcast(context, Constant.REMIND_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        am.setExact(AlarmManager.RTC, nextTime, sender);

        //log信息
        Log.d(TAG, "onClick:  remindId:" + remind_id + ",下次通知时间：" + calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
                + calendar.get(Calendar.DAY_OF_MONTH) + "日" + calendar.get(Calendar.HOUR_OF_DAY) + "时" + calendar.get(Calendar.MINUTE) + "分" + calendar.get(Calendar.SECOND) + "秒");

    }


}
