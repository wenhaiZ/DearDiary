package com.example.neon.deardiary.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.neon.deardiary.util.Constant;
import com.example.neon.deardiary.R;
import com.example.neon.deardiary.util.CustomTimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.PendingIntent.getBroadcast;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "SettingActivity";
    //设置项在ListView中的位置
    private static final int REMIND_ME_TO_WRITE = 0;

    //设置项显示文字对应的id
    private int[] mSettingID = {
            R.string.remind_me,
    };
    //设置项图标对应的id
    private int[] mIconId = {
            R.drawable.ic_setting_remind,
    };

    private SharedPreferences mPreferences;
    private boolean mIsRemind;//是否设置了提醒
    private String mRemindTime;//用于显示的提醒时间
    private long mTriggerMills;//设置的提醒时间


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_setting);
        initComponent();
    }


    /**
     * 初始化组件的
     */
    private void initComponent() {
        //右下角按钮，点击退出当前Activity
        Button btnDone = (Button) findViewById(R.id.done_settings);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //显示日记天数
        TextView tvDiaryCount = (TextView) findViewById(R.id.diary_count);
        int count = mPreferences.getInt(Constant.DIARY_COUNT, 0);
        String diaryCountStr = String.format(Locale.getDefault(), "%d 天", count);
        tvDiaryCount.setText(diaryCountStr);

        //为ListView设置适配器和点击监听
        ListView lvSetting = (ListView) findViewById(R.id.setting_list);
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < mSettingID.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", getResources().getString(mSettingID[i]));
            map.put("icon", mIconId[i]);
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.item_setting_list,
                new String[]{"text", "icon"},
                new int[]{R.id.setting_item, R.id.item_icon});
        lvSetting.setAdapter(adapter);
        lvSetting.setOnItemClickListener(this);
    }

    /**
     * 相应LisView点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case REMIND_ME_TO_WRITE:
                //显示对话框,设置提醒
                showRemindSetDialog();
                break;
            default:
        }
    }


    /**
     * 弹出提醒设置对话框，并进行相应的设置
     */
    private void showRemindSetDialog() {
        View dialogView = getRemindDialogView();
        //弹出对话框
        new AlertDialog.Builder(this)
                .setTitle("设置提醒")
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    //点击确定后，设置相应的提醒
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //写入mIsRemind和mRemindTime 信息
                        mPreferences.edit()
                                .putBoolean(Constant.IS_REMIND, mIsRemind)
                                .apply();

                        mPreferences.edit()
                                .putString(Constant.REMIND_TIME, mRemindTime)
                                .apply();

                        //根据是否设置提醒进行相应设置
                        if (mIsRemind) {
                            //启动对应Receiver的Intent
                            Intent intent = new Intent();
                            intent.setAction(Constant.PUSH_ACTION);
                            //携带本次通知的时间
                            intent.putExtra(Constant.LAST_TIME, mTriggerMills);
                            //读取旧的remind_id,如果是第一次设置，默认为0
                            int remindId = mPreferences.getInt(Constant.REMIND_ID, 0);
                            remindId++;//每次设置，id自增1，用于与旧的提醒区分
                            //把新的remind_id放入intent
                            intent.putExtra(Constant.REMIND_ID, remindId);
                            //把新的remind_id写入配置文件
                            mPreferences.edit().putInt(Constant.REMIND_ID, remindId).apply();

                            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                            //用于发送广播的PendingIntent
                            PendingIntent sender = getBroadcast(SettingActivity.this,
                                    Constant.REMIND_CODE,
                                    intent,
                                    PendingIntent.FLAG_ONE_SHOT);
                            //采用精确设置提醒时间，并在通知出现后继续设定下一次提醒
                            am.setExact(AlarmManager.RTC, mTriggerMills, sender);

                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(mTriggerMills);
                            Toast.makeText(SettingActivity.this, "设置成功", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "onClick:  remindId:"
                                    + remindId
                                    + ",通知时间："
                                    + c.get(Calendar.YEAR) + "年"
                                    + (c.get(Calendar.MONTH) + 1) + "月"
                                    + c.get(Calendar.DAY_OF_MONTH) + "日"
                                    + c.get(Calendar.HOUR_OF_DAY) + "时"
                                    + c.get(Calendar.MINUTE) + "分"
                                    + c.get(Calendar.SECOND) + "秒");
                        } else {

                            Toast.makeText(SettingActivity.this, "通知已取消", Toast.LENGTH_SHORT).show();

                        }

                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }


    /**
     * //初始化对话框要显示的视图
     *
     * @return 视图文件对应的View对象
     */
    @NonNull
    private View getRemindDialogView() {
        //对话框要显示的视图
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_remind_time, null);
        //读取 sharedPreference 设置mIsRemind
        mIsRemind = mPreferences.getBoolean(Constant.IS_REMIND, false);
        final LinearLayout llShowRemindTime = (LinearLayout) dialogView.findViewById(R.id.show_remind_time);
        //显示设置的提醒时间的TextView
        final TextView tvRemindTime = (TextView) llShowRemindTime.findViewById(R.id.remind_time);
        //若没有设置提醒，则显示提醒时间的视图不可见
        llShowRemindTime.setVisibility(mIsRemind ? View.VISIBLE : View.INVISIBLE);
        mRemindTime = mPreferences.getString(Constant.REMIND_TIME, "点击设置");
        tvRemindTime.setText(mRemindTime);
        //点击该文本出现对话框，可以选择提醒时间
        tvRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                //创建一个时间选择对话框
                CustomTimePickerDialog chooseTimeDialog = new CustomTimePickerDialog(
                        SettingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Log.d(TAG, "onTimeSet() called with: view = [" + view + "], hourOfDay = [" + hourOfDay + "], minute = [" + minute + "]");
                                //更新要显示的通知时间
                                String strHour = hourOfDay < 10 ? "0" + hourOfDay : hourOfDay + "";
                                String strMinute = minute < 10 ? "0" + minute : minute + "";
                                mRemindTime = strHour + ":" + strMinute;
                                tvRemindTime.setText(mRemindTime);
                                //设置通知时间
                                Calendar trigger = Calendar.getInstance();
                                trigger.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                trigger.set(Calendar.MINUTE, minute);
                                trigger.set(Calendar.SECOND, 0);

                                //供AlarmManager设置定时任务
                                mTriggerMills = trigger.getTimeInMillis();

                                //如果设置时间小于当前时间，则加上一个周期(即明天的这个时刻)
                                if (mTriggerMills < System.currentTimeMillis()) {
                                    mTriggerMills += Constant.INTERVAL;
                                }
                            }
                        },
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        true);

                //设置对话框标题
                chooseTimeDialog.setTitle("设置提醒时间");
                chooseTimeDialog.show();
            }

        });


        //Switch按钮
        Switch btnSwitch = (Switch) dialogView.findViewById(R.id.switch_remind);
        //根据是否设置提醒来初始化switch的选中状态
        btnSwitch.setChecked(mIsRemind);
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //根据选中状态来更改mIsRemind的值和show_remind_time的可见状态
                if (isChecked) {
                    //开启提醒
                    mIsRemind = true;
                    llShowRemindTime.setVisibility(View.VISIBLE);
                } else {
                    //关闭提醒
                    mIsRemind = false;
                    llShowRemindTime.setVisibility(View.INVISIBLE);
                }
            }
        });
        return dialogView;
    }

}
