package com.wenhaiz.deardiary.diaryquery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.diaryedit.DiaryEditActivity
import com.wenhaiz.deardiary.utils.StringUtil
import java.util.*

internal class QueryResultAdapter(private val mContext: Context) : RecyclerView.Adapter<QueryResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.item_query_result_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diary = mDiaryList[position]
        val year = diary.year.toString()
        val month = diary.month.toString()
        val dayOfMonth = diary.dayOfMonth.toString()
        val hour = StringUtil.formatNumber(diary.hour)
        val minute = StringUtil.formatNumber(diary.minute)
        val time = "$year 年$month 月$dayOfMonth 日 $hour 时$minute 分"
        holder.apply {
            mTitleTV.text = diary.title
            mContentTV.text = diary.content
            mDateTV.text = time
            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(DiaryEditActivity.DATA_DIARY, diary)
                val intent = Intent(mContext, DiaryEditActivity::class.java)
                intent.putExtras(bundle)
                mContext.startActivity(intent)
            }
        }
    }

    private var mDiaryList: ArrayList<Diary> = ArrayList()


    override fun getItemCount(): Int = mDiaryList.size

    fun setData(data: ArrayList<Diary>) {
        mDiaryList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mDateTV: TextView = itemView.findViewById(R.id.result_date)
        var mTitleTV: TextView = itemView.findViewById(R.id.result_title)
        var mContentTV: TextView = itemView.findViewById(R.id.result_content)
    }
}
