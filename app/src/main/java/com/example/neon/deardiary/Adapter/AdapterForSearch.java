package com.example.neon.deardiary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.neon.deardiary.Activities.EditActivity;
import com.example.neon.deardiary.DBConstant;
import com.example.neon.deardiary.R;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * 搜索页面ListView的适配器
 * Created by Neon on 2016/12/1.
 */

public class AdapterForSearch extends BaseAdapter implements AdapterView.OnItemClickListener {
    private Context context;
    private Cursor cursor;

    public AdapterForSearch(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return this.cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.search_result_item, null);
            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.result_content);
            holder.date = (TextView) convertView.findViewById(R.id.result_date);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        this.cursor.moveToPosition(position);
        holder.content.setText(this.cursor.getString(this.cursor.getColumnIndex(DBConstant.CONTENT)));
        String year = this.cursor.getString(this.cursor.getColumnIndex(DBConstant.YEAR));
        String month = this.cursor.getString(this.cursor.getColumnIndex(DBConstant.MONTH));
        String dayOfMonth = this.cursor.getString(this.cursor.getColumnIndex(DBConstant.DAY));
        holder.date.setText(year + "年" + month + "月" + dayOfMonth + "日");
        return convertView;
    }


    //监听点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemClick() called with: position = [" + position + "], id = [" + id + "]");
        Calendar calendar = getCalendarByPosition(position);
        Intent intent = new Intent(context, EditActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("calendar", calendar);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    @NonNull
    private Calendar getCalendarByPosition(int position) {
        this.cursor.moveToPosition(position);
        int year = this.cursor.getInt(this.cursor.getColumnIndex(DBConstant.YEAR));
        int month = this.cursor.getInt(this.cursor.getColumnIndex(DBConstant.MONTH));
        int dayOfMonth = this.cursor.getInt(this.cursor.getColumnIndex(DBConstant.DAY));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return calendar;
    }

    private class ViewHolder {
        private TextView content;
        private TextView date;

    }


}
