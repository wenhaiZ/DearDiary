package com.example.neon.deardiary.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.neon.deardiary.Adapter.AdapterForSearch;
import com.example.neon.deardiary.DAO.DaoOpsHelper;
import com.example.neon.deardiary.DAO.Diary;
import com.example.neon.deardiary.R;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText search;
    private static final String TAG = "SearchActivity";
    private ListView resultList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        ImageView close = (ImageView) findViewById(R.id.search_close_btn);
        resultList = (ListView) findViewById(R.id.search_result_list);
        resultList.setDividerHeight(0);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        search = (EditText) findViewById(R.id.searchKey);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString();
                List<Diary> diaries;
                if (!"".equals(key)) {
                    resultList.setVisibility(View.VISIBLE);
                    diaries = new DaoOpsHelper(SearchActivity.this).queryByContent("%"+key+"%");
                    AdapterForSearch adapterForSearch = new AdapterForSearch(SearchActivity.this, diaries);
                    resultList.setAdapter(adapterForSearch);
                    resultList.setOnItemClickListener(adapterForSearch);
                } else {
                    //若输入为空,则隐藏resultList
                    resultList.setVisibility(View.INVISIBLE);
                }

            }
        });

    }
}
