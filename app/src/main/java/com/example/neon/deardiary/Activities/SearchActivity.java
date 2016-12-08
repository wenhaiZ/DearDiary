package com.example.neon.deardiary.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.neon.deardiary.Adapter.AdapterForSearch;
import com.example.neon.deardiary.DBConstant;
import com.example.neon.deardiary.DBManager;
import com.example.neon.deardiary.R;

public class SearchActivity extends AppCompatActivity {
    private EditText search;
    private static final String TAG = "SearchActivity";
    private ListView resultList;
    private SQLiteDatabase database;

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
                Log.d(TAG, "afterTextChanged() called with: s = [" + s + "]");
                database = DBManager.newInstance(SearchActivity.this).getReadableDatabase();
                String key = s.toString();
                Cursor cursor;
                if (!"".equals(key)) {
                    resultList.setVisibility(View.VISIBLE);
                    cursor = database.rawQuery("select * from " + DBConstant.TABLE_NAME + " where " + DBConstant.CONTENT + " like '%" + key + "%'", null);
                    AdapterForSearch adapterForSearch = new AdapterForSearch(SearchActivity.this, cursor);
                    resultList.setAdapter(adapterForSearch);
                    resultList.setOnItemClickListener(adapterForSearch);
                    database.close();

                } else {
                    //若输入为空,则隐藏resultList
                    resultList.setVisibility(View.INVISIBLE);
                }

            }
        });

    }
}
