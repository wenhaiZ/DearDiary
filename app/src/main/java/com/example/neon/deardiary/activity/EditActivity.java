package com.example.neon.deardiary.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.neon.deardiary.util.Constant;
import com.example.neon.deardiary.dao.DaoOpsHelper;
import com.example.neon.deardiary.dao.Diary;
import com.example.neon.deardiary.R;

import java.util.Calendar;

import static com.example.neon.deardiary.R.id.appendTime;
import static com.example.neon.deardiary.R.id.titleTime;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EditActivity";

    private EditText mDiaryContentET;//编辑日记内容的输入框
    private EditText mDiaryTitleET;

    private DaoOpsHelper mHelper;
    private Diary mDiary;//每一个EditActivity对应一个日记实体

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initComponent();
    }


    //初始化组件
    private void initComponent() {
        //从Intent获取 mDiary 对象,
        initDiaryFromIntent();
        mHelper = new DaoOpsHelper(this);
        mDiaryTitleET = (EditText) findViewById(R.id.diary_title);
        mDiaryTitleET.setText(mDiary.getTitle());

        mDiaryContentET = (EditText) findViewById(R.id.diary_content);
        mDiaryContentET.setText(mDiary.getContent());
        //将光标移到末尾
        mDiaryContentET.requestFocus();
        mDiaryContentET.setSelection(mDiaryContentET.getText().length());

        Button btnAppendTime = (Button) findViewById(appendTime);
        Button btnDone = (Button) findViewById(R.id.done_write);
        btnDone.setOnClickListener(this);
        btnAppendTime.setOnClickListener(this);

        //设置底部标题时间
        TextView tvTitleTime = (TextView) findViewById(titleTime);
        tvTitleTime.setText(mDiary.getYear()
                + "年"
                + mDiary.getMonth()
                + "月"
                + mDiary.getDayOfMonth()
                + "日");
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
            mDiary = (Diary) b.get(Constant.DIARY);
            assert mDiary != null;
            Log.d(TAG, "initDiaryFromIntent: "
                    + mDiary.getYear()
                    + ","
                    + mDiary.getMonth()
                    + ","
                    + mDiary.getDayOfMonth());
        } else {
            new AlertDialog
                    .Builder(this)
                    .setMessage("未知错误,请重试。")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
        if (mDiary == null) {
            Log.e(TAG, "initDiaryFromIntent: diary == null");
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case appendTime:
                //附加的是当前时间，重新获得一个Calendar对象
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                mDiaryContentET.append(hour + "时" + minute + "分");//附加时间
                break;
            case R.id.done_write:
                //更新日记内容
                String newContent = mDiaryContentET.getText().toString();
                String title = mDiaryTitleET.getText().toString();
                //如果标题或内容变化进行更新数据
                if (!newContent.equals(
                        mDiary.getContent()) || !title.equals(mDiary.getTitle())
                        ) {
                    mDiary.setContent(newContent);
                    mDiary.setTitle(title);
                    //设定时间
                    Calendar c = Calendar.getInstance();
                    int wHour = c.get(Calendar.HOUR_OF_DAY);
                    int wMinute = c.get(Calendar.MINUTE);
                    mDiary.setHour(wHour);
                    mDiary.setMinute(wMinute);
                    mHelper.updateDiary(mDiary);
                    //更新有效日记数量并写入
                    int diaryCount = mHelper.getValidDiaryCount();
                    getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE)
                            .edit()
                            .putInt(Constant.DIARY_COUNT, diaryCount)
                            .apply();
                }
                finish();
                break;

            default:
        }
    }

}
