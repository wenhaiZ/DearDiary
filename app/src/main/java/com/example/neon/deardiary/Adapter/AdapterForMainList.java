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
import com.example.neon.deardiary.DAO.DaoOpsHelper;
import com.example.neon.deardiary.DAO.Diary;
import com.example.neon.deardiary.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * 主界面ListView的适配器
 * Created by Neon on 2016/11/30.
 */

public class AdapterForMainList extends BaseAdapter implements AdapterView.OnItemClickListener {
    private Context context;
    private Calendar calendar;
//    private static final String TAG = "AdapterForMainList";
    private final String[] DAY_OF_WEEK={"","周日","周一","周二","周三","周四","周五","周六"};
    private List<Diary> data;//ListView的数据源

    /**
     * 更改Adapter对应的Calendar，这会更改cursor，即adapter的数据发生变化
     * @param calendar
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.data = new DaoOpsHelper(context).queryAddDefault(this.calendar);
    }


    /**
     * 构造方法
     * @param context
     * @param c
     */
    public AdapterForMainList(Context context, Calendar c) {
        this.calendar = c;
        this.data = new DaoOpsHelper(context).queryAddDefault(this.calendar);
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {

        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //获取视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //获得当前位置的Diary
        Diary diary = this.data.get(position);
        String dayOfWeek = DAY_OF_WEEK[diary.getDayOfWeek()];
        //判断当前记录日记内容是否为空
        if ("".equals(diary.getContent())) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_list_null_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.null_content);

            if (dayOfWeek.equals("周六") || dayOfWeek.equals("周日")) {
                textView.setTextColor(context.getResources().getColor(R.color.
                        red));
            }
            convertView.setTag(R.string.isNull, true);
            return convertView;
        }

        //如果日记内容不为空，但是视图是默认视图或者为空，初始化视图
        ViewHolder holder;
        if (convertView == null || (boolean) convertView.getTag(R.string.isNull)) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.main_list_item, null);
            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.dayOfMonth = (TextView) convertView.findViewById(R.id.day_of_month);
            holder.dayOfWeek = (TextView) convertView.findViewById(R.id.week);
            convertView.setTag(R.string.isNull, false);
            convertView.setTag(holder);
        }
        //设置显示的内容
        holder = (ViewHolder) convertView.getTag();
        holder.dayOfMonth.setText(String.format(Locale.getDefault(),"%d",diary.getDayOfMonth()));
        holder.dayOfWeek.setText(dayOfWeek);
        holder.content.setText(diary.getContent());
        return convertView;
    }


    //监听点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Diary diary = data.get(position);
        Intent intent = new Intent(context, EditActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("diary", diary);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    private class ViewHolder {
        private TextView dayOfMonth;
        private TextView dayOfWeek;
        private TextView content;
    }
}
