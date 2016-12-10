package com.example.neon.deardiary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.neon.deardiary.Activities.EditActivity;
import com.example.neon.deardiary.Diary;
import com.example.neon.deardiary.R;

import java.util.List;

/**
 * 搜索页面ListView的适配器
 * Created by Neon on 2016/12/1.
 */

public class AdapterForSearch extends BaseAdapter implements AdapterView.OnItemClickListener {
    private Context context;
    private List<Diary> diaries;

    public AdapterForSearch(Context context, List<Diary> diaries) {
        this.context = context;
        this.diaries = diaries;
    }

    @Override
    public int getCount() {
        return this.diaries.size();
    }

    @Override
    public Object getItem(int position) {
        return diaries.get(position);
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
        Diary diary = diaries.get(position);
        holder.content.setText(diary.getContent());
        String year = diary.getYear()+"";
        String month = diary.getMonth()+"";
        String dayOfMonth = diary.getDayOfMonth()+"";
        holder.date.setText(year + "年" + month + "月" + dayOfMonth + "日");
        return convertView;
    }


    //监听点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Diary diary = diaries.get(position);
        Intent intent = new Intent(context, EditActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("diary", diary);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    private class ViewHolder {
        private TextView content;
        private TextView date;

    }


}
