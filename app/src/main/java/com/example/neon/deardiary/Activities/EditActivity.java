package com.example.neon.deardiary.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.neon.deardiary.Constant;
import com.example.neon.deardiary.DAO.DaoOpsHelper;
import com.example.neon.deardiary.DAO.Diary;
import com.example.neon.deardiary.R;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EditActivity";
    private EditText diaryContent;//编辑日记内容的输入框
    private EditText diaryTitle;
    private DaoOpsHelper helper;
    private Diary diary;//每一个EditActivity对应一个日记实体

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        initComponent();
    }


    //初始化组件
    private void initComponent() {
        //从Intent获取 Diary 对象,
        initDiaryFromIntent();
        helper = new DaoOpsHelper(this);
        Button appendTime = (Button) findViewById(R.id.appendTime);
        Button done = (Button) findViewById(R.id.done_write);
        diaryTitle = (EditText) findViewById(R.id.diary_title);
        diaryTitle.setText(diary.getTitle());
        diaryContent = (EditText) findViewById(R.id.diary_content);
        diaryContent.setText(diary.getContent());
        //设置底部标题时间
        TextView titleTime = (TextView) findViewById(R.id.titleTime);
        titleTime.setText(diary.getYear() + "年" + diary.getMonth() + "月" + diary.getDayOfMonth() + "日");
        //将光标移到末尾
        diaryContent.requestFocus();
        diaryContent.setSelection(diaryContent.getText().length());
        done.setOnClickListener(this);
        appendTime.setOnClickListener(this);
    }


    /**
     * 从 intent 初始化 diary 对象
     * 因为从页面点击日记记录启动编辑页面时，传入一个Diary
     * 所以Diary能成功初始化
     * <p>
     * 若出现错误，则提示信息并关闭 activity
     */
    private void initDiaryFromIntent() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            diary = (Diary) b.get("diary");
            Log.d(TAG, "initDiaryFromIntent: " + diary.getYear() + "," + diary.getMonth() + "," + diary.getDayOfMonth());
        } else {
            new AlertDialog.Builder(this).setMessage("未知错误,请重试。").setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create().show();
        }
        if (diary == null) {
            Log.e(TAG, "initDiaryFromIntent: diary == null");
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.appendTime:
                //附加的是当前时间，重新获得一个Calendar对象
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                diaryContent.append(hour + "时" + minute + "分");//附加时间
                break;
            case R.id.done_write:
                //更新日记内容
                String newContent = diaryContent.getText().toString();
                String title = diaryTitle.getText().toString();
                //如果标题或内容变化进行更新数据
                if (!newContent.equals(diary.getContent()) || !title.equals(diary.getTitle())) {
                    diary.setContent(newContent);
                    diary.setTitle(title);
                    //设定时间
                    Calendar c = Calendar.getInstance();
                    int wHour = c.get(Calendar.HOUR_OF_DAY);
                    int wMinute = c.get(Calendar.MINUTE);
                    diary.setHour(wHour);
                    diary.setMinute(wMinute);
                    helper.updateDiary(diary);
                    //更新有效日记数量并写入
                    int diaryCount = helper.getValidDiaryCount();
                    getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE)
                            .edit().putInt(Constant.DIARY_COUNT, diaryCount)
                            .apply();
                }
                finish();
                break;
        }
    }

}
