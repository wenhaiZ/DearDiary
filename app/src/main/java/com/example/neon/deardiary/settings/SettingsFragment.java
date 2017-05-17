package com.example.neon.deardiary.settings;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.util.Constant;
import com.example.neon.deardiary.util.CustomTimePickerDialog;
import com.example.neon.deardiary.util.Strings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.PendingIntent.getBroadcast;


public class SettingsFragment extends Fragment implements SettingsContract.View, AdapterView.OnItemClickListener {
    private static final String TAG = "SettingActivity";
    private static final String SETTING_TEXT = "text";
    private static final String SETTING_ICON = "icon";
    //设置项在ListView中的位置
    private static final int REMIND_ME_TO_WRITE = 0;
    private static final int DELETE_ALL = 1;
    //设置项显示文字对应的id
    private static final int[] mSettingTextID = {
            R.string.remind_me, R.string.delete_all
    };
    //设置项图标对应的id
    private static final int[] mSettingIconId = {
            R.drawable.ic_settings_remind,
            R.drawable.ic_settings_delete
    };

    private SettingsContract.Presenter mPresenter;
    private SharedPreferences mPreferences;
    private boolean mIsRemindSet;//是否设置了提醒
    private String mStrRemindTime;//用于显示的提醒时间
    private long mTriggerMills;//设置的提醒时间

    private Button btnBack;
    private TextView tvDiaryCount;
    private ListView lvSettings;
    private TextView tvDialogRemindTime;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getActivity().getSharedPreferences(Constant.SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void onDiaryDeleted() {
        mPreferences.edit().putInt(Constant.DIARY_COUNT, 0).apply();
        String zero = 0 + " 天";
        tvDiaryCount.setText(zero);
        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        btnBack = (Button) root.findViewById(R.id.done_settings);
        tvDiaryCount = (TextView) root.findViewById(R.id.diary_count);
        lvSettings = (ListView) root.findViewById(R.id.setting_list);
        initView();
        return root;
    }

    private void initView() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        int diaryCount = mPreferences.getInt(Constant.DIARY_COUNT, 0);
        String diaryCountStr = String.format(Locale.getDefault(), "%d 天", diaryCount);
        tvDiaryCount.setText(diaryCountStr);
        initSettingListView();
    }

    private void initSettingListView() {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < mSettingTextID.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put(SETTING_TEXT, getResources().getString(mSettingTextID[i]));
            map.put(SETTING_ICON, mSettingIconId[i]);
            data.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
                R.layout.item_setting_list,
                new String[]{SETTING_TEXT, SETTING_ICON},
                new int[]{R.id.setting_item, R.id.item_icon});
        lvSettings.setAdapter(adapter);
        lvSettings.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case DELETE_ALL:
                showDeleteConfirmDialog();
                break;
            case REMIND_ME_TO_WRITE:
                showRemindSetDialog();
                break;
            default:
        }
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

    private void showRemindSetDialog() {
        View dialogView = getRemindSetDialogView();
        DialogInterface.OnClickListener positiveListener = new MyPositiveListener();
        new AlertDialog.Builder(getActivity())
                .setTitle("设置提醒")
                .setView(dialogView)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    @NonNull
    private View getRemindSetDialogView() {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_set_remind_time, null);

        //读取 sharedPreference 设置mIsRemindSet
        mIsRemindSet = mPreferences.getBoolean(Constant.IS_REMIND, false);
        final LinearLayout llShowRemindTime = (LinearLayout) dialogView.findViewById(R.id.show_remind_time);
        llShowRemindTime.setVisibility(mIsRemindSet ? View.VISIBLE : View.INVISIBLE);

        //显示提醒的时间，未设置时显示“点击设置”
        mStrRemindTime = mPreferences.getString(Constant.REMIND_TIME, "点击设置");
        tvDialogRemindTime = (TextView) llShowRemindTime.findViewById(R.id.remind_time);
        tvDialogRemindTime.setText(mStrRemindTime);
        //点击该文本出现对话框，可以选择提醒时间
        tvDialogRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }

        });

        Switch btnSwitch = (Switch) dialogView.findViewById(R.id.switch_remind);
        btnSwitch.setChecked(mIsRemindSet);
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsRemindSet = true;
                    llShowRemindTime.setVisibility(View.VISIBLE);
                } else {
                    mIsRemindSet = false;
                    llShowRemindTime.setVisibility(View.INVISIBLE);
                }
            }
        });

        return dialogView;
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


    private class MyPositiveListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            saveRemindSettingInfo();
            if (mIsRemindSet) {
                setRemind();
            } else {
                Toast.makeText(getActivity(), "通知已取消", Toast.LENGTH_SHORT).show();
            }
        }

        private void saveRemindSettingInfo() {
            mPreferences.edit()
                    .putBoolean(Constant.IS_REMIND, mIsRemindSet)
                    .putString(Constant.REMIND_TIME, mStrRemindTime)
                    .apply();
        }

        private void setRemind() {
            int oldRemindId = mPreferences.getInt(Constant.REMIND_ID, 0);
            int newRemindId = oldRemindId + 1;//每次设置，id增1，用于与旧的提醒区分
            mPreferences.edit().putInt(Constant.REMIND_ID, newRemindId).apply();

            //启动BroadcastReceiver的Intent
            Intent intent = new Intent();
            intent.setAction(Constant.REMIND_ACTION);
            intent.putExtra(Constant.LAST_TIME, mTriggerMills);
            intent.putExtra(Constant.REMIND_ID, newRemindId);

            //用于发送广播的PendingIntent
            PendingIntent remindBroadcast = getBroadcast(getActivity(),
                    Constant.REMIND_CODE,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC, mTriggerMills, remindBroadcast);

            printLog(newRemindId);
        }

        private void printLog(int newRemindId) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(mTriggerMills);
            Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onClick:  remindId:" + newRemindId + ",通知时间：" + c.get(Calendar.YEAR) + "年"
                    + (c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日"
                    + c.get(Calendar.HOUR_OF_DAY) + "时" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒");
        }
    }


    private class RemindTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String remindHour = Strings.formatNumber(hourOfDay);
            String remindMinute = Strings.formatNumber(minute);
            mStrRemindTime = remindHour + ":" + remindMinute;
            tvDialogRemindTime.setText(mStrRemindTime);
            setTriggerMills(hourOfDay, minute);
        }

        private void setTriggerMills(int hourOfDay, int minute) {
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
    }
}
