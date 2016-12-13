package com.example.neon.deardiary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.neon.deardiary.adapter.SearchAdapter;
import com.example.neon.deardiary.dao.DaoOpsHelper;
import com.example.neon.deardiary.dao.Diary;
import com.example.neon.deardiary.R;

import java.util.List;


public class SearchActivity extends AppCompatActivity {
//    private static final String TAG = "SearchActivity";

    private ListView mResultLV;//显示搜索结果的 ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initComponent();

    }


    private void initComponent() {
        ImageView ivClose = (ImageView) findViewById(R.id.search_close_btn);
        mResultLV = (ListView) findViewById(R.id.search_result_list);
        mResultLV.setDividerHeight(0);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        EditText etSearch = (EditText) findViewById(R.id.searchKey);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString();
                List<Diary> diaryList;
                if (!"".equals(key)) {
                    mResultLV.setVisibility(View.VISIBLE);
                    diaryList = new DaoOpsHelper(SearchActivity.this).queryByContent("%" + key + "%");
                    SearchAdapter searchAdapter = new SearchAdapter(SearchActivity.this, diaryList);
                    mResultLV.setAdapter(searchAdapter);
                    mResultLV.setOnItemClickListener(searchAdapter);
                } else {
                    //若输入为空,则隐藏mResultLV
                    mResultLV.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }
}
