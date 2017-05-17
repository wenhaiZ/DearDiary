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


public class DiaryQueryFragment extends Fragment implements DiaryQueryContract.View {

    private static final String TAG = "DiaryQueryFragment";
    private DiaryQueryContract.Presenter mPresenter;
    private ListView mResultLV;
    private DiaryQueryAdapter mAdapter;
    private EditText mEtQuery;
    private ImageView mIvClose;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diary_query, container, false);
        mIvClose = (ImageView) root.findViewById(R.id.search_close_btn);
        mResultLV = (ListView) root.findViewById(R.id.search_result_list);
        mResultLV.setDividerHeight(0);
        mEtQuery = (EditText) root.findViewById(R.id.searchKey);
        setListeners();
        return root;
    }

    private void setListeners() {
        mResultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Diary diary = (Diary) mAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(DiaryEditActivity.DATA_DIARY, diary);
                Intent intent = new Intent(getActivity(), DiaryEditActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void beginQuery(String keyword) {
        if (!"".equals(keyword)) {
            mResultLV.setVisibility(View.VISIBLE);
            if (mAdapter == null) {
                mAdapter = new DiaryQueryAdapter(getActivity());
                mResultLV.setAdapter(mAdapter);
            }
            mPresenter.diaryQueryByKeyword(keyword);
        } else {
            mResultLV.setVisibility(View.INVISIBLE);
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
}
