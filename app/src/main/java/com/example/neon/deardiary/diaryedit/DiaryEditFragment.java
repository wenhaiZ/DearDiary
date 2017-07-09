package com.example.neon.deardiary.diaryedit;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.neon.deardiary.settings.SettingsFragment;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class DiaryEditFragment extends Fragment implements DiaryEditContract.View {

    @BindView(R.id.diary_content)
    EditText mEtDiaryContent;
    @BindView(R.id.diary_title)
    EditText mEtDiaryTitle;
    @BindView(R.id.titleTime)
    TextView mTvTitleTime;

    private Diary mDiary;
    private DiaryEditContract.Presenter mPresenter;
    private Unbinder mUnbinder;

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
        mUnbinder = ButterKnife.bind(this, root);
        initView();
        return root;
    }

    private void initView() {
        mEtDiaryTitle.setText(mDiary.getTitle());
        mEtDiaryContent.setText(mDiary.getContent());
        if ("".equals(mDiary.getTitle())) {
            mEtDiaryTitle.requestFocus();
        } else {
            mEtDiaryContent.requestFocus();
            mEtDiaryContent.setSelection(mEtDiaryContent.getText().length());
        }
        String date = mDiary.getYear() + "年"
                + mDiary.getMonth() + "月"
                + mDiary.getDayOfMonth() + "日";
        mTvTitleTime.setText(date);
    }

    @Override
    public void setPresenter(DiaryEditContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @OnClick({R.id.appendTime, R.id.done_write})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.appendTime:
                appendTimeInContent();
                break;
            case R.id.done_write:
                updateDiaryIfNeeded();
                break;
            default:
                break;
        }
    }

    private void appendTimeInContent() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        mEtDiaryContent.append(hour + "时" + minute + "分");
    }

    private void updateDiaryIfNeeded() {
        String newContent = mEtDiaryContent.getText().toString();
        String newTitle = mEtDiaryTitle.getText().toString();
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


    private void updateValidDiaryCount() {
        int diaryCount = mPresenter.getValidDiaryCount();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putString(SettingsFragment.KEY_DIARY_COUNT, diaryCount + "")
                .apply();
    }

    @Override
    public void onDiaryUpdateFailed() {
        Toast.makeText(getActivity(), "日记更新失败,请重试", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
