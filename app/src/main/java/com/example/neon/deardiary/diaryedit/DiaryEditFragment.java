package com.example.neon.deardiary.diaryedit;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.util.Constant;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


public class DiaryEditFragment extends Fragment implements DiaryEditContract.View, View.OnClickListener {

    private Diary mDiary;
    private EditText mDiaryContentET;
    private EditText mDiaryTitleET;
    private DiaryEditContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiary = (Diary) getArguments().getSerializable(DiaryEditActivity.DATA_DIARY);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diary_edit, container, false);
        mDiaryTitleET = (EditText) root.findViewById(R.id.diary_title);
        mDiaryContentET = (EditText) root.findViewById(R.id.diary_content);
        initView();
        return root;
    }

    private void initView() {
        mDiaryTitleET.setText(mDiary.getTitle());
        mDiaryContentET.setText(mDiary.getContent());
        if ("".equals(mDiary.getTitle())) {
            mDiaryTitleET.requestFocus();
        } else {
            mDiaryContentET.requestFocus();
            mDiaryContentET.setSelection(mDiaryContentET.getText().length());
        }

        Button btnAppendTime = (Button) getActivity().findViewById(R.id.appendTime);
        Button btnDone = (Button) getActivity().findViewById(R.id.done_write);
        btnDone.setOnClickListener(this);
        btnAppendTime.setOnClickListener(this);

        TextView tvTitleTime = (TextView) getActivity().findViewById(R.id.titleTime);
        String date = mDiary.getYear() + "年"
                + mDiary.getMonth() + "月"
                + mDiary.getDayOfMonth() + "日";
        tvTitleTime.setText(date);
    }

    @Override
    public void setPresenter(DiaryEditContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appendTime:
                appendTimeInContent();
                break;
            case R.id.done_write:
                updateDiaryIfNeeded();
                break;
            default:
        }
    }

    private void appendTimeInContent() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        mDiaryContentET.append(hour + "时" + minute + "分");
    }

    private void updateDiaryIfNeeded() {
        String newContent = mDiaryContentET.getText().toString();
        String newTitle = mDiaryTitleET.getText().toString();
        //如果标题或内容变化进行更新数据
        if (!newContent.equals(mDiary.getContent())
                || !newTitle.equals(mDiary.getTitle())) {
            mDiary.setContent(newContent);
            mDiary.setTitle(newTitle);

            Calendar c = Calendar.getInstance();
            int updateHour = c.get(Calendar.HOUR_OF_DAY);
            int updateMinute = c.get(Calendar.MINUTE);
            mDiary.setHour(updateHour);
            mDiary.setMinute(updateMinute);

            mPresenter.saveDiary(mDiary);
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onDiaryUpdated() {
        updateValidDiaryCount();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    //更新有效日记数量并写入sp
    private void updateValidDiaryCount() {
        int diaryCount = mPresenter.getValidDiaryCount();
        getActivity()
                .getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE)
                .edit()
                .putInt(Constant.DIARY_COUNT, diaryCount)
                .apply();
    }

    @Override
    public void onDiaryUpdateFailed() {
        Toast.makeText(getActivity(), "日记更新失败,请重试", Toast.LENGTH_SHORT).show();
    }

}
