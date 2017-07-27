package com.example.neon.deardiary.diarylist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.utils.StringUtil

import java.util.ArrayList
import java.util.Locale


internal class DiaryRecyclerAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private var mDiaries: ArrayList<Diary>? = null
    private var mClickListener: OnItemClickListener? = null

    internal interface OnItemClickListener {
        fun onItemClick(view: View)
    }

    init {
        mDiaries = ArrayList<Diary>()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_DIARY) R.layout.item_main_list else R.layout.item_main_list_null
        val itemView = LayoutInflater.from(mContext).inflate(layoutId, parent, false)

        //添加点击效果
        val value = TypedValue()
        mContext.theme.resolveAttribute(R.attr.selectableItemBackground, value, true)
        itemView.setBackgroundResource(value.resourceId)

        itemView.setOnClickListener(this)
        return if (viewType == VIEW_TYPE_DIARY) DiaryViewHolder(itemView) else EmptyViewHolder(itemView)
    }


    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val diary = mDiaries!![position]
        val dayOfWeek = diary.dayOfWeek

        if (holder is DiaryViewHolder) {
            val diaryViewHolder = holder
            diaryViewHolder.mDayOfMonthTV.text = String.format(Locale.getDefault(), "%d", diary.dayOfMonth)
            diaryViewHolder.mDayOfWeekTV.text = DAY_OF_WEEK[dayOfWeek]
            diaryViewHolder.mTitleTV.text = diary.title
            diaryViewHolder.mContentTV.text = diary.content
            val hour = StringUtil.formatNumber(diary.hour)
            val minute = StringUtil.formatNumber(diary.minute)
            val createdTime = "$hour:$minute"
            diaryViewHolder.mEditTimeTV.text = createdTime
        } else if (holder is EmptyViewHolder) {
            val emptyViewHolder = holder
            //周六和周日文本显示红色
            if (dayOfWeek == 1 || dayOfWeek == 7) {
                emptyViewHolder.mNullText.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            } else {
                emptyViewHolder.mNullText.setTextColor(mContext.resources.getColor(R.color.black))
            }
        }
    }

    override fun getItemCount(): Int {
        return mDiaries!!.size
    }

    override fun getItemViewType(position: Int): Int {
        val diary = mDiaries!![position]
        if ("" == diary.content && "" == diary.title) {//日记内容为空
            return VIEW_TYPE_EMPTY
        } else {
            return VIEW_TYPE_DIARY
        }
    }

    fun setData(data: ArrayList<Diary>) {
        mDiaries = data
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Diary {
        return mDiaries!![position]
    }

    override fun onClick(v: View) {
        mClickListener!!.onItemClick(v)
    }

    private inner class DiaryViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mDayOfMonthTV: TextView = itemView.findViewById(R.id.day_of_month) as TextView
        val mDayOfWeekTV: TextView = itemView.findViewById(R.id.week) as TextView
        val mContentTV: TextView = itemView.findViewById(R.id.content) as TextView
        val mTitleTV: TextView = itemView.findViewById(R.id.title) as TextView
        val mEditTimeTV: TextView = itemView.findViewById(R.id.editTime) as TextView

    }

    private inner class EmptyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mNullText: TextView = itemView.findViewById(R.id.null_content) as TextView
    }

    companion object {
        private val DAY_OF_WEEK = arrayOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        private val VIEW_TYPE_DIARY = 0
        private val VIEW_TYPE_EMPTY = 1
    }


}
