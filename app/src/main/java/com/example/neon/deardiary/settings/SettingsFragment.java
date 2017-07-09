package com.example.neon.deardiary.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.util.CustomTimePickerDialog;
import com.example.neon.deardiary.util.Strings;

import java.util.Calendar;

import static android.app.PendingIntent.getBroadcast;


public class SettingsFragment extends PreferenceFragment implements SettingsContract.View {
    private static final String TAG = "SettingActivity";

    private SettingsContract.Presenter mPresenter;
    private SharedPreferences mPreferences;
    private long mTriggerMills;//设置的提醒时间

    private static final String KEY_DELETE_ALL = "delete_all";
    public static final String KEY_DIARY_COUNT = "diary_count";
    public static final String KEY_REMIND_SET = "remind_set";
    public static final String KEY_REMIND_TIME = "remind_time";
    public static final String KEY_REMIND_ID = "remind_id";
    public static final String KEY_LAST_TIME = "lastTime";

    //发送通知的间隔24小时
    public static final long INTERVAL_DAY = 24 * 60 * 60 * 1000;
    public static final int REMIND_CODE = 0x123;
    public static final String REMIND_ACTION = "com.example.neon.deardiary.Receiver";

    private Preference timeSet;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initPreferences();
    }

    private void initPreferences() {
        //已写日记天数
        Preference diaryCountPre = findPreference(KEY_DIARY_COUNT);
        String diaryCount = mPreferences.getString(KEY_DIARY_COUNT, "0");
        diaryCountPre.setSummary(diaryCount + " 天");

        //删除日记
        Preference deleteAll = findPreference(KEY_DELETE_ALL);
        deleteAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDeleteConfirmDialog();
                return true;
            }
        });

        //设置提醒
        Preference remindSet = findPreference(KEY_REMIND_SET);
        remindSet.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isRemindSet = (boolean) newValue;
                if (isRemindSet) {
                    setRemind();
                }
                return true;
            }
        });

        //提醒时间
        timeSet = findPreference(KEY_REMIND_TIME);
        String time = mPreferences.getString(KEY_REMIND_TIME, "00:00");
        String displayStr = "每天 " + time;
        timeSet.setSummary(displayStr);
        timeSet.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimePicker();
                return false;
            }
        });
    }


    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle("确认")
                .setMessage("确定删除所有日记吗? (该操作不可恢复！)")
                .setNegativeButton("不了", null)
                .setPositiveButton("我想好了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.deleteAll();
                    }
                })
                .create()
                .show();
    }

    private void setRemind() {
        setTriggerMills();
        int oldRemindId = mPreferences.getInt(KEY_REMIND_ID, 0);
        int newRemindId = oldRemindId + 1;//每次设置，id增1，用于与旧的提醒区分
        mPreferences.edit()
                .putInt(KEY_REMIND_ID, newRemindId)
                .apply();

        //启动BroadcastReceiver的Intent
        Intent intent = new Intent();
        intent.setAction(REMIND_ACTION);
        intent.putExtra(KEY_LAST_TIME, mTriggerMills);
        intent.putExtra(KEY_REMIND_ID, newRemindId);

        //用于发送广播的PendingIntent
        PendingIntent alarmIntent = getBroadcast(getActivity(),
                REMIND_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTriggerMills, alarmIntent);
        Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
//        printLog(newRemindId);
    }

    private void setTriggerMills() {
        Calendar trigger = Calendar.getInstance();

        String remindTime = mPreferences.getString(KEY_REMIND_TIME, "09:00");
        String[] time = remindTime.split(":");

        int hourOfDay = Integer.valueOf(time[0]);
        int minute = Integer.valueOf(time[1]);

        trigger.set(Calendar.HOUR_OF_DAY, hourOfDay);
        trigger.set(Calendar.MINUTE, minute);
        trigger.set(Calendar.SECOND, 0);

        //供AlarmManager设置定时任务
        mTriggerMills = trigger.getTimeInMillis();
        //如果设置时间小于当前时间，则加上一个周期(即明天的这个时刻)
        if (mTriggerMills < System.currentTimeMillis()) {
            mTriggerMills += INTERVAL_DAY;
        }
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        CustomTimePickerDialog chooseTimeDialog = new CustomTimePickerDialog(getActivity(),
                new RemindTimeSetListener(),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true);
        chooseTimeDialog.setTitle("设置提醒时间");
        chooseTimeDialog.show();
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDiaryDeleted() {
        mPreferences.edit()
                .putString(KEY_DIARY_COUNT, "0").apply();
        String zero = 0 + " 天";
        findPreference(KEY_DIARY_COUNT).setSummary(zero);
        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
    }


    private void printLog(int newRemindId) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mTriggerMills);
        Log.d(TAG, "onClick:  remindId:" + newRemindId + ",通知时间：" + c.get(Calendar.YEAR) + "年"
                + (c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日"
                + c.get(Calendar.HOUR_OF_DAY) + "时" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒");
    }

    private class RemindTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String remindHour = Strings.formatNumber(hourOfDay);
            String remindMinute = Strings.formatNumber(minute);
            String strRemindTime = remindHour + ":" + remindMinute;
            mPreferences.edit()
                    .putString(KEY_REMIND_TIME, strRemindTime)
                    .apply();
            timeSet.setSummary("每天 " + strRemindTime);
            setRemind();
        }

    }
}
