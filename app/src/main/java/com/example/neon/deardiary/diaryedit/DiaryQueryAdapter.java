package com.example.neon.deardiary.diaryedit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.Diary;

import java.util.ArrayList;

/**
 * 搜索页面ListView的适配器
 * Created by Neon on 2016/12/1.
 */

public class DiaryQueryAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Diary> mDiaryList;

    public DiaryQueryAdapter(Context context) {
        this.mContext = context;
        this.mDiaryList = new ArrayList<>();
    }

    public void setData(ArrayList<Diary> data) {
        mDiaryList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.mDiaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDiaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_search_list, null);
            holder = new ViewHolder();
            holder.mContentTV = (TextView) convertView.findViewById(R.id.result_content);
            holder.mDateTV = (TextView) convertView.findViewById(R.id.result_date);
            holder.mTitleTV = (TextView) convertView.findViewById(R.id.result_title);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        Diary diary = mDiaryList.get(position);
        holder.mContentTV.setText(diary.getContent());
        holder.mTitleTV.setText(diary.getTitle());
        String year = diary.getYear() + "";
        String month = diary.getMonth() + "";
        String dayOfMonth = diary.getDayOfMonth() + "";
        //设置分钟
        String minute = diary.getMinute() < 10 ? "0" + diary.getMinute() : diary.getMinute() + "";
        String hour = diary.getHour() < 10 ? "0" + diary.getHour() : diary.getHour() + "";
        holder.mDateTV.setText(
                year + "年" + month + "月" + dayOfMonth + "日 "
                        + hour + "时" + minute + "分");

        return convertView;
    }

    private class ViewHolder {
        private TextView mDateTV;
        private TextView mTitleTV;
        private TextView mContentTV;

    }


}
