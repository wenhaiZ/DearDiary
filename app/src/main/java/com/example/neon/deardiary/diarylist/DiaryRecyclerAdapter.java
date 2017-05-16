package com.example.neon.deardiary.diarylist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.neon.deardiary.R;
import com.example.neon.deardiary.data.Diary;

import java.util.ArrayList;
import java.util.Locale;


class DiaryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private final String[] DAY_OF_WEEK = {"", "Sun", "Mon",
            "Tue", "Wed", "Thu",
            "Fri", "Sat"};
    private static final int VIEW_TYPE_DIARY = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private ArrayList<Diary> mDiaries;
    private Context mContext;
    private OnItemClickListener mClickListener;

    DiaryRecyclerAdapter(Context context) {
        mContext = context;
        mDiaries = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = viewType == VIEW_TYPE_DIARY ? R.layout.item_main_list : R.layout.item_main_list_null;
        View itemView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        TypedValue value = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground,value,true);
        itemView.setBackgroundResource(value.resourceId);
        itemView.setOnClickListener(this);
        return viewType == VIEW_TYPE_DIARY ? new DiaryViewHolder(itemView) : new EmptyViewHolder(itemView);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //获得当前位置的Diary
        Diary diary = mDiaries.get(position);
        int dayOfWeek = diary.getDayOfWeek();

        if (holder instanceof DiaryViewHolder) {
            DiaryViewHolder diaryViewHolder = (DiaryViewHolder) holder;
            diaryViewHolder.mDayOfMonthTV.setText(String.format(Locale.getDefault(), "%d", diary.getDayOfMonth()));
            diaryViewHolder.mDayOfWeekTV.setText(DAY_OF_WEEK[dayOfWeek]);
            diaryViewHolder.mTitleTV.setText(diary.getTitle());
            diaryViewHolder.mContentTV.setText(diary.getContent());
            //分钟小于0，前面加个0
            String minute = diary.getMinute() < 10 ? "0" + diary.getMinute() : diary.getMinute() + "";
            String hour = diary.getHour() < 10 ? "0" + diary.getHour() : diary.getHour() + "";
            diaryViewHolder.mEditTimeTV.setText(hour + ":" + minute);

        } else if (holder instanceof EmptyViewHolder) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            //周六和周日文本显示红色
            if (dayOfWeek == 1 || dayOfWeek == 7) {
                emptyViewHolder.mNullText.setTextColor(mContext.getResources().getColor(R.color.
                        colorPrimary));
            }

        }

    }


    @Override
    public int getItemCount() {
        return mDiaries.size();
    }

    @Override
    public int getItemViewType(int position) {
        Diary diary = mDiaries.get(position);
        if ("".equals(diary.getContent()) && "".equals(diary.getTitle())) {//日记内容为空
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_DIARY;
        }
    }

    public void setData(ArrayList<Diary> data) {
        mDiaries = data;
        notifyDataSetChanged();
    }

    Diary getItem(int position) {
        return mDiaries.get(position);
    }

    @Override
    public void onClick(View v) {
        mClickListener.onItemClick(v);
    }

    private class DiaryViewHolder extends RecyclerView.ViewHolder {
        private TextView mDayOfMonthTV;
        private TextView mDayOfWeekTV;
        private TextView mContentTV;
        private TextView mTitleTV;
        private TextView mEditTimeTV;

        DiaryViewHolder(View itemView) {
            super(itemView);
            mContentTV = (TextView) itemView.findViewById(R.id.content);
            mDayOfMonthTV = (TextView) itemView.findViewById(R.id.day_of_month);
            mDayOfWeekTV = (TextView) itemView.findViewById(R.id.week);
            mEditTimeTV = (TextView) itemView.findViewById(R.id.editTime);
            mTitleTV = (TextView) itemView.findViewById(R.id.title);
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private TextView mNullText;

        EmptyViewHolder(View itemView) {
            super(itemView);
            mNullText = (TextView) itemView.findViewById(R.id.null_content);
        }
    }


    interface OnItemClickListener {
        void onItemClick(View view);
    }
}
