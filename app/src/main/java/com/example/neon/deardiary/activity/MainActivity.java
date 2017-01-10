package com.example.neon.deardiary.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.adapter.MainAdapter;
import com.example.neon.deardiary.dao.DaoOpsHelper;
import com.example.neon.deardiary.dao.Diary;
import com.example.neon.deardiary.util.CustomDatePickerDialog;

import java.util.Calendar;
import java.util.Locale;

import static com.example.neon.deardiary.R.id.chooseDate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mYearTV, mMonthTV;//底部显示年月
    private ListView mDiaryLV;//当月日记列表

    private Calendar mToday;//代表当前日期
    private DaoOpsHelper mDaoHelper;
    private boolean isFirstStart;//标识是否第一次启动
    private MainAdapter mMainAdapter;//ListView的适配器
    private int mScrollPos, mScrollTop;//记录滑动位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToday = Calendar.getInstance();//获得今天日期
        mDaoHelper = new DaoOpsHelper(this);
        isFirstStart = true;
        initView();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        Button btnEdit = (Button) findViewById(R.id.write);//主界面“撰”按钮
        Button btnSearch = (Button) findViewById(R.id.search);//主界面“搜”按钮
        Button btnSetting = (Button) findViewById(R.id.settings);//主界面“设”按钮

        btnEdit.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSetting.setOnClickListener(this);

        //长按“设”按钮可清空所有数据
        btnSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("清空所有数据?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDaoHelper.deleteAll();
                                mMainAdapter.setCalendar(mToday);
                                mMainAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("否", null)
                        .create().show();

                return true;
            }
        });

        //主界面左下角的显示时间的LinearLayout，点击可选择时间
        LinearLayout llChooseDate = (LinearLayout) findViewById(chooseDate);
        llChooseDate.setOnClickListener(this);

        //设置显示的日期
        mYearTV = (TextView) findViewById(R.id.year_in_main);
        mMonthTV = (TextView) findViewById(R.id.month_in_main);
        setMainDate();
        //初始化ListView
        inflateListView();

    }


    /**
     * 填充ListView数据项
     */
    private void inflateListView() {
        mDiaryLV = (ListView) findViewById(R.id.diaryList);
        mDiaryLV.setDividerHeight(0);
        //从数据库中查询当前月的日记记录
        mMainAdapter = new MainAdapter(this, mToday);
        mDiaryLV.setAdapter(mMainAdapter);
        mDiaryLV.setOnItemClickListener(mMainAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configList();
    }


    //设置ListView显示位置
    private void configList() {
        if (isFirstStart) {
            mDiaryLV.setSelection(mToday.get(Calendar.DAY_OF_MONTH) - 1);
            mDiaryLV.setSelection(mDiaryLV.getCount() - 1);
            isFirstStart = false;
        } else {
            //重设mMainAdapter的Calendar,并刷新数据
            mMainAdapter.setCalendar(mToday);
            mMainAdapter.notifyDataSetChanged();
            //恢复到指定位置
            mDiaryLV.setSelectionFromTop(mScrollPos, mScrollTop);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //记录滑动的位置
        mScrollPos = mDiaryLV.getFirstVisiblePosition();//获取当前可见第一项在listView中的位置
        View v1 = mDiaryLV.getChildAt(0);//获取获取当前屏幕可见第一项对应的View
        mScrollTop = (v1 == null) ? 0 : v1.getTop();//获取当前View对应的距List顶部的高度

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.write:
                Intent intent1 = new Intent(this, EditActivity.class);
                Bundle b = new Bundle();
                //无论何时按下"撰",都会打开今天日记的编辑页面
                Calendar t = Calendar.getInstance();
                Diary diary = mDaoHelper.queryByDay(t);
                //这是个小概率时间，即用户在Activity中过零点后点击，此时数据库总并没有第二天的数据
                if (diary == null) {
                    diary = new Diary(t);
                    mDaoHelper.insertDairy(diary);
                }
                b.putSerializable("diary", diary);
                intent1.putExtras(b);
                startActivity(intent1);
                break;
            case R.id.search:
                Intent intent2 = new Intent(this, SearchActivity.class);
                startActivity(intent2);
                break;
            case R.id.settings:
                Intent intent3 = new Intent(this, SettingActivity.class);
                startActivity(intent3);
                break;
            case chooseDate:
                showDatePickDialog();
                break;
            default:
        }

    }


    //显示选择时间对话框
    private void showDatePickDialog() {
        final CustomDatePickerDialog datePick = new CustomDatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //根据所选日期更新ListView和主页面时间
                mToday.set(Calendar.YEAR, year);
                mToday.set(Calendar.MONTH, monthOfYear);
                mToday.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Calendar calender = Calendar.getInstance();
                if (mToday.before(calender)) {
                    setMainDate();
                    mMainAdapter.setCalendar(mToday);
                    mMainAdapter.notifyDataSetChanged();
                    mDiaryLV.setSelection(dayOfMonth - 1);
                } else {//如果当前所选当前月份超前，跳到当前月份
                    mToday = calender;
                    setMainDate();
                    mMainAdapter.setCalendar(mToday);
                    mMainAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "只能选到今天", Toast.LENGTH_SHORT).show();
                }
            }
        }, mToday.get(Calendar.YEAR), mToday.get(Calendar.MONTH), mToday.get(Calendar.DAY_OF_MONTH));
        datePick.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 由Calendar对象设置主界面时间
     */
    public void setMainDate() {
        int curYear = mToday.get(Calendar.YEAR);
        int curMonth = mToday.get(Calendar.MONTH) + 1;
        mYearTV.setText(String.format(Locale.getDefault(), "%d", curYear));
        mMonthTV.setText(String.format(Locale.getDefault(), "%d", curMonth));

    }


}
