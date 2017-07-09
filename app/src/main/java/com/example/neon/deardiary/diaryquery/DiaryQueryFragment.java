package com.example.neon.deardiary.diaryquery;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.diaryedit.DiaryEditActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;


public class DiaryQueryFragment extends Fragment implements DiaryQueryContract.View {

    private static final String TAG = "DiaryQueryFragment";

    @BindView(R.id.search_result_list)
    ListView mLvResult;
    @BindView(R.id.searchKey)
    EditText mEtQuery;

    private DiaryQueryContract.Presenter mPresenter;
    private DiaryQueryAdapter mAdapter;
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diary_query, container, false);
        mUnbinder = ButterKnife.bind(this, root);
        mLvResult.setDividerHeight(0);
        mEtQuery.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                beginQuery(keyword);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        return root;
    }

    @OnItemClick(R.id.search_result_list)
    public void onItemClick(int position){
        Diary diary = (Diary) mAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DiaryEditActivity.DATA_DIARY, diary);
        Intent intent = new Intent(getActivity(), DiaryEditActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @OnClick(R.id.search_close_btn)
    public void close(){
        getActivity().finish();
    }

    private void beginQuery(String keyword) {
        if (!"".equals(keyword)) {
            mLvResult.setVisibility(View.VISIBLE);
            if (mAdapter == null) {
                mAdapter = new DiaryQueryAdapter(getActivity());
                mLvResult.setAdapter(mAdapter);
            }
            mPresenter.diaryQueryByKeyword(keyword);
        } else {
            mLvResult.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setPresenter(DiaryQueryContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        beginQuery(mEtQuery.getText().toString());
    }

    @Override
    public void querySuccessfully(ArrayList<Diary> diaries) {
        mAdapter.setData(diaries);
    }

    @Override
    public void queryFailed() {
        Log.e(TAG, "queryFailed: 查询失败");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
