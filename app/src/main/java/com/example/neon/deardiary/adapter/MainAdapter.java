package com.example.neon.deardiary.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.neon.deardiary.activity.EditActivity;
import com.example.neon.deardiary.dao.DaoOpsHelper;
import com.example.neon.deardiary.dao.Diary;
import com.example.neon.deardiary.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * 主界面ListView的适配器
 * Created by Neon on 2016/11/30.
 */

public class MainAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private final String[] DAY_OF_WEEK = {
            "", "Sun", "Mon",
            "Tue", "Wed", "Thu",
            "Fri", "Sat"};

    private Context mContext;
    private Calendar mCalendar;
    private List<Diary> mDataList;//ListView的数据源


    /**
     * 构造方法
     */
    public MainAdapter(Context context, Calendar c) {
        this.mCalendar = c;
        this.mDataList = new DaoOpsHelper(context).queryAddDefault(this.mCalendar);
        this.mContext = context;
    }


    /**
     * 更改Adapter对应的Calendar，这会更改cursor，即adapter的数据发生变化
     */
    public void setCalendar(Calendar calendar) {
        this.mCalendar = calendar;
        this.mDataList = new DaoOpsHelper(mContext).queryAddDefault(this.mCalendar);
    }


    @Override
    public int getCount() {
        return this.mDataList.size();
    }

    @Override
    public Object getItem(int position) {

        return this.mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //获取视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获得当前位置的Diary
        Diary diary = this.mDataList.get(position);
        int dayOfWeek = diary.getDayOfWeek();
        //判断当前记录日记内容和标题是否都为空
        if ("".equals(diary.getContent()) && "".equals(diary.getTitle())) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_main_list_null, null);
            TextView textView = (TextView) convertView.findViewById(R.id.null_content);
            if (dayOfWeek == 1 || dayOfWeek == 7) {
                //noinspection deprecation
                textView.setTextColor(mContext.getResources().getColor(R.color.
                        red));
            }
            convertView.setTag(R.string.isNull, true);
            return convertView;
        }

        //如果日记内容不为空，但是视图是默认视图或者为空，初始化视图
        ViewHolder holder;
        if (convertView == null || (boolean) convertView.getTag(R.string.isNull)) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_main_list, null);
            holder = new ViewHolder();
            holder.mContentTV = (TextView) convertView.findViewById(R.id.content);
            holder.mDayOfMonthTV = (TextView) convertView.findViewById(R.id.day_of_month);
            holder.mDayOfWeekTV = (TextView) convertView.findViewById(R.id.week);
            holder.mEditTimeTV = (TextView) convertView.findViewById(R.id.editTime);
            holder.mTitleTV = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(R.string.isNull, false);
            convertView.setTag(holder);
        }
        //设置显示的内容
        holder = (ViewHolder) convertView.getTag();
        holder.mDayOfMonthTV.setText(String.format(Locale.getDefault(), "%d", diary.getDayOfMonth()));
        holder.mDayOfWeekTV.setText(DAY_OF_WEEK[dayOfWeek]);
        holder.mTitleTV.setText(diary.getTitle());
        holder.mContentTV.setText(diary.getContent());
        //分钟小于0，前面加个0
        String minute = diary.getMinute() < 10 ? "0" + diary.getMinute() : diary.getMinute() + "";
        String hour = diary.getHour() < 10 ? "0" + diary.getHour() : diary.getHour() + "";
        holder.mEditTimeTV.setText(hour + ":" + minute);
        return convertView;
    }

    //监听点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Diary diary = this.mDataList.get(position);
        Intent intent = new Intent(mContext, EditActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("diary", diary);
        intent.putExtras(b);
        mContext.startActivity(intent);
    }


    private class ViewHolder {
        private TextView mDayOfMonthTV;
        private TextView mDayOfWeekTV;
        private TextView mContentTV;
        private TextView mTitleTV;
        private TextView mEditTimeTV;
    }
}
