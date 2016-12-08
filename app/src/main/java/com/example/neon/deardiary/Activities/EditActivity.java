package com.example.neon.deardiary.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neon.deardiary.Constant;
import com.example.neon.deardiary.DBManager;
import com.example.neon.deardiary.DiaryBean;
import com.example.neon.deardiary.MySQLHelper;
import com.example.neon.deardiary.R;

import java.util.Calendar;
import java.util.Locale;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    //    private static final String TAG = "EditActivity";
    private EditText diaryContent;//编辑日记内容的输入框
    private MySQLHelper helper;
    private DiaryBean diaryBean;//每一个EditActivity对应一个日记实体
    private Calendar calendar;//当前编辑页面对应的日期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        initComponent();
    }


    //初始化组件
    private void initComponent() {
        //从Intent获取 Calendar 对象,
        initCalendarFromIntent();
        helper = DBManager.newInstance(this);
        diaryBean = helper.getDiaryBean(calendar);
        Button appendTime = (Button) findViewById(R.id.appendTime);
        Button done = (Button) findViewById(R.id.done_write);
        diaryContent = (EditText) findViewById(R.id.diary_content);
        diaryContent.setText(diaryBean.getContent());
        //设置顶部标题时间
        TextView year = (TextView) findViewById(R.id.year_in_edit);
        TextView month = (TextView) findViewById(R.id.month_in_edit);
        TextView day = (TextView) findViewById(R.id.day_in_edit);
        year.setText(String.format(Locale.getDefault(), "%d", calendar.get(Calendar.YEAR)));
        month.setText(String.format(Locale.getDefault(), "%d", (calendar.get(Calendar.MONTH) + 1)));
        day.setText(String.format(Locale.getDefault(), "%d", calendar.get(Calendar.DAY_OF_MONTH)));

        //将光标移到末尾
        diaryContent.requestFocus();
        diaryContent.setSelection(diaryContent.getText().length());
        done.setOnClickListener(this);
        appendTime.setOnClickListener(this);
    }


    /**
     * 从 intent 初始化 calendar 对象
     * 因为从页面点击日记记录启动编辑页面时，会发送Calendar信息
     * 所以calendar能成功初始化
     * <p>
     * 若出现错误，则提示信息并关闭 activity
     */
    private void initCalendarFromIntent() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            calendar = (Calendar) b.get("calendar");
        } else {
            new AlertDialog.Builder(this).setMessage("未知错误,请重试。").setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            }).create().show();
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
                diaryBean.setContent(newContent);
                if (helper.updateDiaryContent(diaryBean)) {
                    //写入当前日记数量
                    int diaryCount = helper.getValidRecord().getCount();
                    getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE).edit().putInt(Constant.DIARY_COUNT, diaryCount).apply();
                    finish();
                } else {
                    Toast.makeText(this, "内存不足，保存失败。", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
