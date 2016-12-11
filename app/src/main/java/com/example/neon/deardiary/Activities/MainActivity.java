package com.example.neon.deardiary.Activities;

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

import com.example.neon.deardiary.Adapter.AdapterForMainList;
import com.example.neon.deardiary.DAO.DaoOpsHelper;
import com.example.neon.deardiary.DatePickerDialogForBug;
import com.example.neon.deardiary.DAO.Diary;
import com.example.neon.deardiary.R;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    private static final String TAG = "MainActivity";

    private TextView year, month;//底部显示年月
    private ListView diaryList;//当月日记列表
    private AdapterForMainList adapter;//ListView的适配器
    private Calendar today;
    private DaoOpsHelper daoHelper;
    private int scrollPos, scrollTop;//记录滑动位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        today = Calendar.getInstance();//获得今天日期
        daoHelper = new DaoOpsHelper(this);
        initView();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        Button mEdit = (Button) findViewById(R.id.write);//主界面“撰”按钮
        Button mSearch = (Button) findViewById(R.id.search);//主界面“搜”按钮
        Button mSetting = (Button) findViewById(R.id.settings);//主界面“设”按钮
        mEdit.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mSearch.setOnClickListener(this);

        //长按“设”按钮可清空所有数据
        mSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("清空所有数据?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                daoHelper.deleteAll();
                                adapter.setCalendar(today);
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("否", null).create().show();

                return true;
            }
        });

        //主界面左下角的显示时间的LinearLayout，点击可选择时间
        LinearLayout chooseDate = (LinearLayout) findViewById(R.id.chooseDate);
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialogForBug datePick = new DatePickerDialogForBug(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //根据所选日期更新ListView和主页面时间
                        today.set(Calendar.YEAR, year);
                        today.set(Calendar.MONTH, monthOfYear);
                        today.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Calendar calender = Calendar.getInstance();
                        if (today.before(calender)) {
                            setMainDate();
                            adapter.setCalendar(today);
                            adapter.notifyDataSetChanged();
                            diaryList.setSelection(dayOfMonth - 1);
                        } else {//如果当前所选当前月份超前，跳到当前月份
                            today = calender;
                            setMainDate();
                            adapter.setCalendar(today);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "只能选到今天", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                datePick.show();
            }
        });

        //设置显示的日期
        year = (TextView) findViewById(R.id.year_in_main);
        month = (TextView) findViewById(R.id.month_in_main);
        setMainDate();

        //初始化ListView
        inflateListView();

    }


    /**
     * 填充ListView数据项
     */
    private void inflateListView() {
        diaryList = (ListView) findViewById(R.id.diaryList);
        diaryList.setDividerHeight(0);
        //从数据库中查询当前月的日记记录
        adapter = new AdapterForMainList(this, today);
        diaryList.setAdapter(adapter);
        diaryList.setOnItemClickListener(adapter);
        diaryList.setSelection(today.get(Calendar.DAY_OF_MONTH)-1);
//        diaryList.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
//
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                atBottom = false;
//                atTop = false;
////                Log.d(TAG, "onScroll() called with: firstVisibleItem = [" + firstVisibleItem + "], visibleItemCount = [" + visibleItemCount + "], totalItemCount = [" + totalItemCount + "]");
//
//                //判断ListView是否滑动到底部
//                if (firstVisibleItem + visibleItemCount == totalItemCount) {
//                    View lastItemView = diaryList.getChildAt(diaryList.getChildCount() - 1);
//                    if ((diaryList.getBottom()) == lastItemView.getBottom()) {
//                        atBottom = true;
//                    }
//                }
//
//                //判断ListView是否滑动到顶部
//                if (firstVisibleItem == 0 && visibleItemCount > 0) {
//                    View firstItemView = diaryList.getChildAt(0);
//                    if (diaryList.getTop() == firstItemView.getTop()) {
//                        atTop = true;
//
//                    }
//                }
////                System.out.println("isAtBottom?" + atBottom);
////                System.out.println("isAtTop?" + atTop);
//
//            }
//        });

//        diaryList.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                detector.onTouchEvent(event);
//                return false;
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        //重设adapter的Calendar,并刷新数据
        adapter.setCalendar(today);
        adapter.notifyDataSetChanged();

        //恢复到指定位置
        diaryList.setSelectionFromTop(scrollPos, scrollTop);


    }

    @Override
    protected void onPause() {
        super.onPause();
        //记录滑动的位置
        scrollPos = diaryList.getFirstVisiblePosition();//获取当前可见第一项在listView中的位置
        View v1 = diaryList.getChildAt(0);//获取获取当前屏幕可见第一项对应的View
        scrollTop = (v1 == null) ? 0 : v1.getTop();//获取当前View对应的距List顶部的高度

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.write:
                Intent intent = new Intent(this, EditActivity.class);
                Bundle b = new Bundle();
                //无论何时按下"撰",都会打开今天日记的编辑页面
                Diary diary = daoHelper.queryByDay(Calendar.getInstance());
                b.putSerializable("diary", diary);
                intent.putExtras(b);
                startActivity(intent);
                break;
            case R.id.search:
                Intent intent2 = new Intent(this, SearchActivity.class);
                startActivity(intent2);
                break;
            case R.id.settings:
                Intent intent1 = new Intent(this, SettingActivity.class);
                startActivity(intent1);
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 由Calendar对象设置主界面时间
     */
    public void setMainDate() {
        int curYear = today.get(Calendar.YEAR);
        int curMonth = today.get(Calendar.MONTH) + 1;
        year.setText(String.format(Locale.getDefault(), "%d", curYear));
        month.setText(String.format(Locale.getDefault(), "%d", curMonth));

    }


}
