package com.wenhaiz.deardiary.diarylist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.utils.StringUtil
import java.util.ArrayList
import java.util.Locale

internal class DiaryListAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private var mDiaries: ArrayList<Diary> = ArrayList()
    private var mClickListener: OnItemClickListener? = null

    internal interface OnItemClickListener {
        fun onItemClick(view: View)
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
        val diary = mDiaries[position]
        val dayOfWeek = diary.dayOfWeek
        when (holder) {
            is DiaryViewHolder -> {
                holder.apply {
                    mDayOfMonthTV.text = String.format(Locale.getDefault(), "%d", diary.dayOfMonth)
                    mDayOfWeekTV.text = DAY_OF_WEEK[dayOfWeek]
                    mTitleTV.text = diary.title
                    mContentTV.text = diary.content
                    val hour = StringUtil.formatNumber(diary.hour)
                    val minute = StringUtil.formatNumber(diary.minute)
                    val createdTime = "$hour:$minute"
                    mEditTimeTV.text = createdTime
                }

            }
            is EmptyViewHolder -> {
                holder.apply {
                    //周六和周日文本显示红色
                    if (dayOfWeek == 1 || dayOfWeek == 7) {
                        mNullText.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
                    } else {
                        mNullText.setTextColor(mContext.resources.getColor(R.color.black))
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mDiaries.size
    }

    override fun getItemViewType(position: Int): Int {
        val diary = mDiaries[position]
        return if (diary.content.isBlank() && diary.title.isBlank()) {//日记内容为空
            VIEW_TYPE_EMPTY
        } else {
            VIEW_TYPE_DIARY
        }
    }

    fun setData(data: ArrayList<Diary>) {
        mDiaries = data
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Diary {
        return mDiaries[position]
    }

    override fun onClick(v: View) {
        mClickListener?.onItemClick(v)
    }

    private inner class DiaryViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mDayOfMonthTV: TextView = itemView.findViewById(R.id.day_of_month)
        val mDayOfWeekTV: TextView = itemView.findViewById(R.id.week)
        val mContentTV: TextView = itemView.findViewById(R.id.content)
        val mTitleTV: TextView = itemView.findViewById(R.id.title)
        val mEditTimeTV: TextView = itemView.findViewById(R.id.editTime)
    }

    private inner class EmptyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mNullText: TextView = itemView.findViewById(R.id.null_content)
    }

    companion object {
        private val DAY_OF_WEEK = arrayOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        private val VIEW_TYPE_DIARY = 0
        private val VIEW_TYPE_EMPTY = 1
    }
}
