package com.wenhaiz.deardiary.notify

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.data.ObjectBoxDataSource
import com.wenhaiz.deardiary.diaryedit.DiaryEditActivity
import com.wenhaiz.deardiary.settings.SettingsFragment
import com.wenhaiz.deardiary.utils.Constant
import com.wenhaiz.deardiary.utils.LogUtil
import java.util.Calendar

internal class NotifyReceiver : BroadcastReceiver(), NotifyContract.View {

    private lateinit var mPresenter: NotifyContract.Presenter

    override fun onReceive(context: Context, intent: Intent) {
//        val dataSource = LocalDataSource.getInstance(context)
        val dataSource = ObjectBoxDataSource(context)
        mPresenter = NotifyPresenter(dataSource, this)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isRemindSet = preferences.getBoolean(SettingsFragment.KEY_REMIND_SET, false)
        val remindIdFromPreference = preferences.getInt(SettingsFragment.KEY_REMIND_ID, 65535)
        val remindIdFromIntent = intent.getIntExtra(SettingsFragment.KEY_REMIND_ID, 0)

        LogUtil.d(TAG, "onReceive: isRemind?" + isRemindSet + " remindIdFromPreference:"
                + remindIdFromPreference + ",remindIdFromIntent: " + remindIdFromIntent)

        //设置提醒并且 id 相符，发送通知
        if (isRemindSet && remindIdFromIntent == remindIdFromPreference) {
            sendNotification(context)
            setNextBroadCastTime(context, intent)
        }

    }

    private fun sendNotification(context: Context) {
        val today = Calendar.getInstance()
        var diary = mPresenter.getDiary(today)
        if (diary == null) {
            diary = Diary(today)
            mPresenter.insertDiary(diary)
        }

        val data = Bundle().apply {
            putSerializable(Constant.DIARY, diary)
        }

        val startEdit = Intent(context, DiaryEditActivity::class.java)
        startEdit.putExtras(data)

        val pendingIntent = PendingIntent.getActivity(context, 0, startEdit, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(context)
                .setAutoCancel(true)//用户点击后，通知自动取消
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.time_write_diary))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    private fun setNextBroadCastTime(context: Context, intent: Intent) {
        val lastTime = intent.getLongExtra(SettingsFragment.KEY_LAST_TIME, 0)
        val nextTime = lastTime + SettingsFragment.INTERVAL_DAY
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = nextTime
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //复用上次的Intent，更改 lastTime 的数据,确保remindId不变，可以进行重复通知
        intent.putExtra(SettingsFragment.KEY_LAST_TIME, nextTime)
        //用于发送广播的PendingIntent
        val alarmIntent = PendingIntent.getBroadcast(context, SettingsFragment.REMIND_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTime, alarmIntent)
        //log信息
        LogUtil.d(TAG, "onClick:" + ",下次通知时间：" + calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
                + calendar.get(Calendar.DAY_OF_MONTH) + "日" + calendar.get(Calendar.HOUR_OF_DAY) + "时" + calendar.get(Calendar.MINUTE) + "分" + calendar.get(Calendar.SECOND) + "秒")
    }

    override fun setPresenter(presenter: NotifyContract.Presenter) {
        mPresenter = presenter
    }

    companion object {
        private val TAG = "NotifyReceiver"
        private val NOTIFICATION_ID = 6
    }
}
