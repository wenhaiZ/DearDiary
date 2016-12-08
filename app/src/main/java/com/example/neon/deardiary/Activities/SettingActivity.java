package com.example.neon.deardiary.Activities;

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

import com.example.neon.deardiary.Constant;
import com.example.neon.deardiary.R;
import com.example.neon.deardiary.TimePickerDialogForBug;

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
    //    private static final int TEXT_SIZE = 1;
    private static final int SET_PASSWORD = 1;


    private int[] settingsID = {R.string.remind_me,
            R.string.set_password};
    private int[] iconId = {R.drawable.remind_icon,
            R.drawable.lock_icon};

    private SharedPreferences preferences;

    private boolean isRemind;
    private String remindTime;
    private long triggerMills;//设置提醒的时间


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(Constant.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        setContentView(R.layout.settings);
        initComponent();


    }


    /**
     * 初始化组件的
     */
    private void initComponent() {
        //右下角按钮，点击退出当前Activity
        Button done = (Button) findViewById(R.id.done_settings);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //显示日记天数
        TextView diaryCount = (TextView) findViewById(R.id.diary_count);
        //获得当前日记数
        int count = preferences.getInt(Constant.DIARY_COUNT, 0);
        String diaryCountStr = String.format(Locale.getDefault(), "%d 天", count);
        diaryCount.setText(diaryCountStr);

        //为ListView设置适配器和点击监听
        ListView settingList = (ListView) findViewById(R.id.setting_list);
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < settingsID.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", getResources().getString(settingsID[i]));
            map.put("icon", iconId[i]);
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.setting_item,
                new String[]{"text", "icon"},
                new int[]{R.id.setting_item, R.id.item_icon});
        settingList.setAdapter(adapter);
        settingList.setOnItemClickListener(this);
    }


    /**
     * 相应LisView点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case REMIND_ME_TO_WRITE:
                //显示对话框,设置提醒
                showRemindSetDialog();
                break;
//            case TEXT_SIZE:
//                Toast.makeText(this, "该功能正在开发中...", Toast.LENGTH_SHORT).show();
//                break;
            case SET_PASSWORD:
                Toast.makeText(this, "该功能正在开发中...", Toast.LENGTH_SHORT).show();
                break;
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
                        //写入isRemind和remindTime 信息
                        preferences.edit().putBoolean(Constant.IS_REMIND, isRemind).apply();
                        preferences.edit().putString(Constant.REMIND_TIME, remindTime).apply();
                        if (isRemind) {
                            //启动对应Receiver的Intent
                            Intent intent = new Intent();
                            intent.setAction("com.example.neon.deardiary.Reciver");
                            //携带本次通知的时间
                            intent.putExtra(Constant.LAST_TIME, triggerMills);
                            //读取旧的remind_id,如果是第一次设置，默认为0
                            int remind_id = preferences.getInt(Constant.REMIND_ID, 0);
                            remind_id++;//每次设置，id自增1，用于与旧的提醒区分
                            //把新的remind_id放入intent
                            intent.putExtra(Constant.REMIND_ID, remind_id);
                            //把新的remind_id写入配置文件
                            preferences.edit().putInt(Constant.REMIND_ID, remind_id).apply();

                            //用于发送广播的PendingIntent
                            //启动一个服务，服务发送一个通知
                            PendingIntent sender = getBroadcast(SettingActivity.this, Constant.REMIND_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
                            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                            //采用精确设置提醒时间，并在通知出现后继续设定下一次提醒
                            am.setExact(AlarmManager.RTC, triggerMills, sender);
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(triggerMills);
                            Toast.makeText(SettingActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onClick:  remindId:" + remind_id + ",通知时间：" + c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月"
                                    + c.get(Calendar.DAY_OF_MONTH) + "日" + c.get(Calendar.HOUR_OF_DAY) + "时" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒");
                        } else {
                            Toast.makeText(SettingActivity.this, "通知已取消", Toast.LENGTH_SHORT).show();

                        }

                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }


    /**
     * //初始化对话框要显示的视图
     *
     * @return 视图文件对应的View对象
     */
    @NonNull
    private View getRemindDialogView() {


        View dialogView = LayoutInflater.from(this).inflate(R.layout.remind_set_dialog, null);
        //读取 sharedPreference 设置isRemind
        isRemind = preferences.getBoolean(Constant.IS_REMIND, false);
        final LinearLayout show_remind_time = (LinearLayout) dialogView.findViewById(R.id.show_remind_time);
        //显示设置的提醒时间的TextView
        final TextView tv_remindTime = (TextView) show_remind_time.findViewById(R.id.remind_time);
        //若没有设置提醒，则显示提醒时间的视图不可见
        show_remind_time.setVisibility(isRemind ? View.VISIBLE : View.INVISIBLE);
        remindTime = preferences.getString(Constant.REMIND_TIME, "点击设置");
        tv_remindTime.setText(remindTime);
        //点击该文本出现对话框，可以选择提醒时间
        tv_remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                //创建一个时间选择对话框
                TimePickerDialogForBug chooseTimeDialog = new TimePickerDialogForBug(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d(TAG, "onTimeSet() called with: view = [" + view + "], hourOfDay = [" + hourOfDay + "], minute = [" + minute + "]");
                        //更新要显示的通知时间
                        String strHour = hourOfDay < 10 ? "0" + hourOfDay : hourOfDay + "";
                        String strMinute = minute < 10 ? "0" + minute : minute + "";
                        remindTime = strHour + ":" + strMinute;
                        tv_remindTime.setText(remindTime);
                        //设置通知时间
                        Calendar trigger = Calendar.getInstance();
                        Log.d(TAG, "onTimeSet: trigger " + trigger.getTimeInMillis());
                        trigger.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        trigger.set(Calendar.MINUTE, minute);
                        trigger.set(Calendar.SECOND, 0);
                        //供AlarmManager设置定时任务
                        triggerMills = trigger.getTimeInMillis();
                        //如果设置时间小于当前时间，则加上一个周期(即明天的这个时刻)
                        if (triggerMills < System.currentTimeMillis()) {
                            triggerMills += Constant.INTERVAL;
                        }

                        Log.d(TAG, "onTimeSet: triggerMills " + triggerMills);
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                //设置丢画框标题
                chooseTimeDialog.setTitle("设置提醒时间");
                chooseTimeDialog.show();
            }

        });


        //Switch按钮
        Switch switchBtn = (Switch) dialogView.findViewById(R.id.switch_remind);
        switchBtn.setChecked(isRemind);//根据是否设置提醒来初始化switch的选中状态
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //根据选中状态来更改isRemind的值和show_remind_time的可见状态
                if (isChecked) {
                    //开启提醒
                    isRemind = true;
                    show_remind_time.setVisibility(View.VISIBLE);
                } else {
                    //关闭提醒
                    isRemind = false;
                    show_remind_time.setVisibility(View.INVISIBLE);
                }
            }
        });
        return dialogView;
    }

}
