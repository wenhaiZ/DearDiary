package com.example.neon.deardiary.diaryquery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.diaryedit.DiaryEditActivity
import com.example.neon.deardiary.utils.StringUtil
import java.util.ArrayList

internal class DiaryQueryAdapter(private val mContext: Context) : RecyclerView.Adapter<DiaryQueryAdapter.ViewHolder>() {
    private var mDiaryList: ArrayList<Diary> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.item_search_list, null)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val diary = mDiaryList[position]
        val year = diary.year.toString()
        val month = diary.month.toString()
        val dayOfMonth = diary.dayOfMonth.toString()
        val hour = StringUtil.formatNumber(diary.hour)
        val minute = StringUtil.formatNumber(diary.minute)
        val time = "$year 年$month 月$dayOfMonth 日 $hour 时$minute 分"
        holder !!.mTitleTV.text = diary.title
        holder.mContentTV.text = diary.content
        holder.mDateTV.text = time
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(DiaryEditActivity.DATA_DIARY, diary)
            val intent = Intent(mContext, DiaryEditActivity::class.java)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = mDiaryList.size

    fun setData(data: ArrayList<Diary>) {
        mDiaryList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mDateTV: TextView = itemView.findViewById(R.id.result_date) as TextView
        var mTitleTV: TextView = itemView.findViewById(R.id.result_title) as TextView
        var mContentTV: TextView = itemView.findViewById(R.id.result_content) as TextView
    }
}
