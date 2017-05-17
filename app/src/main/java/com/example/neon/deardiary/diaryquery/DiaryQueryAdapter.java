package com.example.neon.deardiary.diaryquery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.Diary;
import com.example.neon.deardiary.util.Strings;

import java.util.ArrayList;

class DiaryQueryAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Diary> mDiaryList;

    DiaryQueryAdapter(Context context) {
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
        Diary diary = mDiaryList.get(position);
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

        String year = diary.getYear() + "";
        String month = diary.getMonth() + "";
        String dayOfMonth = diary.getDayOfMonth() + "";
        String hour = Strings.formatNumber(diary.getHour());
        String minute = Strings.formatNumber(diary.getMinute());
        String time = year + "年" + month + "月" + dayOfMonth + "日 "
                + hour + "时" + minute + "分";
        holder.mTitleTV.setText(diary.getTitle());
        holder.mContentTV.setText(diary.getContent());
        holder.mDateTV.setText(time);

        return convertView;
    }

    private class ViewHolder {
        private TextView mDateTV;
        private TextView mTitleTV;
        private TextView mContentTV;
    }


}
