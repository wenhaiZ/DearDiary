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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;


public class DiaryListFragment extends Fragment implements DiaryListContract.View {

    //    private static final String TAG = "DiaryListFragment";
    public static final int REQUEST_EDIT_DIARY = 0x1;
    @BindView(R.id.diaryList)
    RecyclerView mDiaryList;
    @BindView(R.id.bottom_year)
    TextView mTvYear;
    @BindView(R.id.bottom_month)
    TextView mTvMonth;

    private DiaryListContract.Presenter mPresenter;
    private Calendar mCalendar;
    private DiaryRecyclerAdapter mAdapter;
    private int mScrollPos;
    private int mScrollTop;
    private boolean isFirstStart;
    private Unbinder mUnbinder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DiaryRecyclerAdapter(getActivity());
        mCalendar = Calendar.getInstance();
        isFirstStart = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diary_list, container, false);
        mUnbinder = ButterKnife.bind(this, root);
        initView();
        return root;
    }

    private void initView() {
        mDiaryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mDiaryList.setAdapter(mAdapter);
        mDiaryList.setVerticalScrollBarEnabled(false);
        mDiaryList.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setOnItemClickListener(new DiaryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int itemPosition = mDiaryList.getChildAdapterPosition(view);
                Diary diary = mAdapter.getItem(itemPosition);
                editDiary(diary);
            }
        });
        setBottomDate();
    }

    public void setBottomDate() {
        int curYear = mCalendar.get(Calendar.YEAR);
        int curMonth = mCalendar.get(Calendar.MONTH) + 1;
        mTvYear.setText(String.format(Locale.getDefault(), "%d", curYear));
        mTvMonth.setText(String.format(Locale.getDefault(), "%d", curMonth));
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
    public void setPresenter(DiaryListContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void showDiary(ArrayList<Diary> diaries) {
        mAdapter.setData(diaries);
    }


    @Override
    public void onPause() {
        super.onPause();
        saveListScrollPosition();
    }

    //记录滑动的位置
    private void saveListScrollPosition() {
//        mScrollPos = mDiaryList.getFirstVisiblePosition();//获取当前可见第一项在listView中的位置
//        View v1 = mDiaryList.getChildAt(0);//获取当前屏幕可见第一项对应的View
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
//            mDiaryList.setSelection(mDiaryList.getCount() - 1);
            isFirstStart = false;
        } else {
            //恢复到指定位置
//            mDiaryList.setSelectionFromTop(mScrollPos, mScrollTop);
        }
    }

    @OnClick({R.id.write_today, R.id.search, R.id.settings, R.id.chooseDate})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_today:
                editTodayDiary();
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

    private void editTodayDiary() {
        Calendar today = Calendar.getInstance();
        Diary diary = mPresenter.getDiary(today);
        if (diary == null) {
            diary = new Diary(today);
            mPresenter.insertDiary(diary);
        }
        editDiary(diary);
    }

    @Override
    public void editDiary(Diary diary) {
        Bundle data = new Bundle();
        data.putSerializable(DiaryEditActivity.DATA_DIARY, diary);
        Intent intent = new Intent(getActivity(), DiaryEditActivity.class);
        intent.putExtras(data);
        startActivityForResult(intent, REQUEST_EDIT_DIARY);
    }

    private void showDatePickDialog() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Calendar now = Calendar.getInstance();
                if (mCalendar.before(now)) {
                    mPresenter.loadDiaries(mCalendar);
                    mDiaryList.scrollToPosition(dayOfMonth - 1);
                } else {//如果所选为当前月份或超前，跳到当前月份
                    mCalendar = now;
                    mPresenter.loadDiaries(mCalendar);
                    Toast.makeText(getActivity(), "只能选到今天", Toast.LENGTH_SHORT).show();
                }
                setBottomDate();
            }
        };

        CustomDatePickerDialog datePicker = new CustomDatePickerDialog(getActivity(), listener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
