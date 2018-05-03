package com.wenhaiz.deardiary.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.getBroadcast
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.widget.TimePicker
import android.widget.Toast
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.utils.LogUtil
import com.wenhaiz.deardiary.utils.StringUtil
import com.wenhaiz.deardiary.widget.CustomTimePickerDialog
import java.util.Calendar

internal class SettingsFragment : PreferenceFragment(), SettingsContract.View {
    private lateinit var mPresenter: SettingsContract.Presenter
    private lateinit var mPreferences: SharedPreferences
    private var mTriggerMills: Long = 0//设置的提醒时间
    private lateinit var timeSet: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        initPreferences()
    }

    private fun initPreferences() {
        //已写日记天数
        val diaryCountPre = findPreference(KEY_DIARY_COUNT)
        val diaryCount = mPreferences.getString(KEY_DIARY_COUNT, "0")
        diaryCountPre.summary = "${diaryCount !!} ${getString(R.string.day)}"

        //删除日记
        val deleteAll = findPreference(KEY_DELETE_ALL)
        deleteAll.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            showDeleteConfirmDialog()
            true
        }

        //设置提醒
        val remindSet = findPreference(KEY_REMIND_SET)
        remindSet.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            val isRemindSet = newValue as Boolean
            if (isRemindSet) {
                setRemind()
            }
            true
        }

        //提醒时间
        timeSet = findPreference(KEY_REMIND_TIME)
        val time = mPreferences.getString(KEY_REMIND_TIME, "00:00")
        val displayStr = getString(R.string.every_day) + time !!
        timeSet.summary = displayStr
        timeSet.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            showTimePicker()
            false
        }
    }


    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(activity)
                .setCancelable(true)
                .setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.ask_to_delete_all))
                .setNegativeButton(getString(R.string.no), null)
                .setPositiveButton(getString(R.string.ok)) { _, _ -> mPresenter.deleteAll() }
                .create()
                .show()
    }

    private fun setRemind() {
        setTriggerMills()
        val oldRemindId = mPreferences.getInt(KEY_REMIND_ID, 0)
        val newRemindId = oldRemindId + 1//每次设置，id增1，用于与旧的提醒区分
        mPreferences.edit()
                .putInt(KEY_REMIND_ID, newRemindId)
                .apply()

        //启动BroadcastReceiver的Intent
        val intent = Intent()
        intent.action = REMIND_ACTION
        intent.putExtra(KEY_LAST_TIME, mTriggerMills)
        intent.putExtra(KEY_REMIND_ID, newRemindId)

        //用于发送广播的PendingIntent
        val alarmIntent = getBroadcast(activity,
                REMIND_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTriggerMills, alarmIntent)
        Toast.makeText(activity, getString(R.string.setting_success), Toast.LENGTH_SHORT).show()
        printLog(newRemindId)
    }

    private fun setTriggerMills() {
        val trigger = Calendar.getInstance()

        val remindTime = mPreferences.getString(KEY_REMIND_TIME, "09:00")
        val time = remindTime.split(":".toRegex())

        val hourOfDay = Integer.valueOf(time[0])
        val minute = Integer.valueOf(time[1])

        trigger.set(Calendar.HOUR_OF_DAY, hourOfDay)
        trigger.set(Calendar.MINUTE, minute)
        trigger.set(Calendar.SECOND, 0)

        //供AlarmManager设置定时任务
        mTriggerMills = trigger.timeInMillis
        //如果设置时间小于当前时间，则后延一天(即明天的这个时刻)
        if (mTriggerMills < System.currentTimeMillis()) {
            mTriggerMills += INTERVAL_DAY
        }
    }

    private fun showTimePicker() {
        val c = Calendar.getInstance()
        val chooseTimeDialog = CustomTimePickerDialog(activity,
                RemindTimeSetListener(),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true)
        chooseTimeDialog.setTitle(getString(R.string.set_remind_time))
        chooseTimeDialog.show()
    }

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        mPresenter = presenter
    }

    override fun onDiaryDeleted() {
        mPreferences.edit()
                .putString(KEY_DIARY_COUNT, "0").apply()
        val zero = 0.toString() + getString(R.string.day)
        findPreference(KEY_DIARY_COUNT).summary = zero
        Toast.makeText(activity, getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
    }


    private fun printLog(newRemindId: Int) {
        val c = Calendar.getInstance()
        c.timeInMillis = mTriggerMills
        LogUtil.d(TAG, "onClick:  remindId:" + newRemindId + ",通知时间：" + c.get(Calendar.YEAR) + "年"
                + (c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日"
                + c.get(Calendar.HOUR_OF_DAY) + "时" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒")
    }

    private inner class RemindTimeSetListener : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            val remindHour = StringUtil.formatNumber(hourOfDay)
            val remindMinute = StringUtil.formatNumber(minute)
            val strRemindTime = "$remindHour:$remindMinute"
            mPreferences.edit()
                    .putString(KEY_REMIND_TIME, strRemindTime)
                    .apply()
            timeSet.summary = getString(R.string.every_day) + strRemindTime
            setRemind()
        }

    }

    companion object {
        private const val TAG = "SettingActivity"
        private const val KEY_DELETE_ALL = "delete_all"
        const val KEY_DIARY_COUNT = "diary_count"
        const val KEY_REMIND_SET = "remind_set"
        const val KEY_REMIND_TIME = "remind_time"
        const val KEY_REMIND_ID = "remind_id"
        const val KEY_LAST_TIME = "lastTime"

        //发送通知的间隔24小时
        const val INTERVAL_DAY = (24 * 60 * 60 * 1000).toLong()
        const val REMIND_CODE = 0x123
        const val REMIND_ACTION = "com.wenhaiz.deardiary.Receiver"
    }
}
