package com.example.neon.deardiary.diarylist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.diaryedit.DiaryEditActivity;
import com.example.neon.deardiary.diaryquery.DiaryQueryActivity;
import com.example.neon.deardiary.settings.SettingsActivity;
import com.example.neon.deardiary.util.CustomDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class DiaryListFragment extends Fragment implements DiaryListContract.View, View.OnClickListener {

    private static final String TAG = "DiaryListFragment";
    public static final int REQUEST_EDIT_DIARY = 0x1;
    private DiaryListContract.Presenter mPresenter;
    private Calendar mCalendar;
    private RecyclerView mListView;
    private DiaryRecyclerAdapter mListAdapter;
    private TextView mYearTV;//底部显示年月
    private TextView mMonthTV;
    private int mScrollPos;
    private int mScrollTop;
    private boolean isFirstStart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new DiaryRecyclerAdapter(getActivity());
        mCalendar = Calendar.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diary_list, container, false);
        mListView = (RecyclerView) root.findViewById(R.id.diaryList);
        initView();
        return root;
    }

    private void initView() {
        isFirstStart = true;
        mListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mListView.setAdapter(mListAdapter);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListAdapter.setOnItemClickListener(new DiaryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int adapterPos = mListView.getChildAdapterPosition(view);
                Diary diary = mListAdapter.getItem(adapterPos);
                editDiary(diary);
            }
        });

        Button btnEdit = (Button) getActivity().findViewById(R.id.write_today);//主界面“撰”按钮
        Button btnSearch = (Button) getActivity().findViewById(R.id.search);//主界面“搜”按钮
        Button btnSetting = (Button) getActivity().findViewById(R.id.settings);//主界面“设”按钮
        LinearLayout llChooseDate = (LinearLayout) getActivity().findViewById(R.id.chooseDate);

        btnEdit.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        llChooseDate.setOnClickListener(this);

        //设置显示的日期
        mYearTV = (TextView) getActivity().findViewById(R.id.bottom_year);
        mMonthTV = (TextView) getActivity().findViewById(R.id.bottom_month);
        setBottomDate();
    }

    @Override
    public void editDiary(Diary diary) {
        Bundle data = new Bundle();
        data.putSerializable(DiaryEditActivity.DATA_DIARY, diary);
        Intent intent = new Intent(getActivity(), DiaryEditActivity.class);
        intent.putExtras(data);
        startActivityForResult(intent, REQUEST_EDIT_DIARY);
    }

    public void setBottomDate() {
        int curYear = mCalendar.get(Calendar.YEAR);
        int curMonth = mCalendar.get(Calendar.MONTH) + 1;
        mYearTV.setText(String.format(Locale.getDefault(), "%d", curYear));
        mMonthTV.setText(String.format(Locale.getDefault(), "%d", curMonth));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_DIARY) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "日记已保存", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        saveListScrollPosition();
    }

    //记录滑动的位置
    private void saveListScrollPosition() {
//        mScrollPos = mListView.getFirstVisiblePosition();//获取当前可见第一项在listView中的位置
//        View v1 = mListView.getChildAt(0);//获取当前屏幕可见第一项对应的View
//        mScrollTop = (v1 == null) ? 0 : v1.getTop();//获取当前View顶部距ListView顶部的高度
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadDiaries(mCalendar);
        configList();
    }

    //设置ListView显示位置
    private void configList() {
        if (isFirstStart) {
//            mListView.setSelection(mListView.getCount() - 1);
            isFirstStart = false;
        } else {
            //恢复到指定位置
//            mListView.setSelectionFromTop(mScrollPos, mScrollTop);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_today:
                //无论何时按下"撰",都会打开今天日记的编辑页面
                Calendar today = Calendar.getInstance();
                Diary diary = mPresenter.getDiary(today);
                if (diary == null) {
                    diary = new Diary(today);
                    mPresenter.insertDiary(diary);
                }
                editDiary(diary);
                break;
            case R.id.search:
                Intent startQuery = new Intent(getActivity(), DiaryQueryActivity.class);
                startActivity(startQuery);
                break;
            case R.id.settings:
                Intent startSetting = new Intent(getActivity(), SettingsActivity.class);
                startActivity(startSetting);
                break;
            case R.id.chooseDate:
                showDatePickDialog();
                break;
            default:
        }
    }

    private void showDatePickDialog() {
        final CustomDatePickerDialog datePicker = new CustomDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //根据所选日期更新ListView和主页面时间
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Calendar now = Calendar.getInstance();
                if (mCalendar.before(now)) {
                    mPresenter.loadDiaries(mCalendar);
//                    mListView.setSelection(dayOfMonth - 1);
                } else {//如果所选为当前月份或超前，跳到当前月份
                    mCalendar = now;
                    mPresenter.loadDiaries(mCalendar);
                    Toast.makeText(getActivity(), "只能选到今天", Toast.LENGTH_SHORT).show();
                }
                setBottomDate();
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }


    @Override
    public void setPresenter(DiaryListContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void showDiary(ArrayList<Diary> diaries) {
        mListAdapter.setData(diaries);
    }
}
