package com.example.neon.deardiary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.neon.deardiary.Activities.EditActivity;
import com.example.neon.deardiary.DBConstant;
import com.example.neon.deardiary.DBManager;
import com.example.neon.deardiary.R;

import java.util.Calendar;


/**
 * 主界面ListView的适配器
 * Created by Neon on 2016/11/30.
 */

public class AdapterForMainList extends BaseAdapter implements AdapterView.OnItemClickListener {
    private Cursor cursor;
    private Context context;
    private Calendar calendar;
    private static final String TAG = "AdapterForMainList";
    private ListView listView;
    private boolean addFooterView = false;


    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * 更改Adapter对应的Calendar，这会更改cursor，即adapter的数据发生变化
     * @param calendar
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.cursor = DBManager.newInstance(context).queryAddDefault(this.calendar);
    }

    public AdapterForMainList(Context context, Calendar c) {
        this.calendar = c;
        this.cursor = DBManager.newInstance(context).queryAddDefault(this.calendar);
        this.context = context;
    }

    public void setCursor(Cursor cursor) {
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


    //获取视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(listView == null){
            listView = (ListView) parent;
        }
//        View footerView = LayoutInflater.from(context).inflate(R.layout.main_list_footer_item,null);
//        if (addFooterView&&listView.getFooterViewsCount()==0){
//            listView.addFooterView(footerView,null,false);
//        }else if(!addFooterView&&listView.getFooterViewsCount()>0){
//            listView.removeFooterView(footerView);
//        }
        //将游标移动到当前位置
        this.cursor.moveToPosition(position);
        //判断当前记录日记内容是否为空
        if ("".equals(this.cursor.getString(this.cursor.getColumnIndex(DBConstant.CONTENT)))) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_list_null_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.null_content);
            String dayOfWeek = this.cursor.getString(this.cursor.getColumnIndex(DBConstant.WEEKDAY));
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
        holder.dayOfMonth.setText(this.cursor.getString(this.cursor.getColumnIndex(DBConstant.DAY)));
        holder.dayOfWeek.setText(this.cursor.getString(this.cursor.getColumnIndex(DBConstant.WEEKDAY)));
        holder.content.setText(this.cursor.getString(this.cursor.getColumnIndex(DBConstant.CONTENT)));

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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
//        int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
//        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        Log.d(TAG, "notifyDataSetChanged() called");



    }

    public Cursor getCursor() {
        return this.cursor;
    }

    private class ViewHolder {
        private TextView dayOfMonth;
        private TextView dayOfWeek;
        private TextView content;
    }
}
