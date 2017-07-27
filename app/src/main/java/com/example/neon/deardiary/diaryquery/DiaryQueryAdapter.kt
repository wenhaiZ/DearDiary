package com.example.neon.deardiary.diaryquery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.utils.StringUtil

import java.util.ArrayList

internal class DiaryQueryAdapter(private val mContext: Context) : BaseAdapter() {
    private var mDiaryList: ArrayList<Diary>? = null

    init {
        this.mDiaryList = ArrayList<Diary>()
    }

    fun setData(data: ArrayList<Diary>) {
        mDiaryList = data
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return this.mDiaryList!!.size
    }

    override fun getItem(position: Int): Any {
        return mDiaryList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
        var convertView = itemView
        var holder: ViewHolder
        val diary = mDiaryList!![position]
        if (convertView == null) {
            val inflater = LayoutInflater.from(mContext)
            convertView = inflater.inflate(R.layout.item_search_list, null)
            holder = ViewHolder()
            holder.mContentTV = convertView!!.findViewById(R.id.result_content) as TextView
            holder.mDateTV = convertView.findViewById(R.id.result_date) as TextView
            holder.mTitleTV = convertView.findViewById(R.id.result_title) as TextView
            convertView.tag = holder
        }
        holder = convertView.tag as ViewHolder

        val year = diary.year.toString()
        val month = diary.month.toString()
        val dayOfMonth = diary.dayOfMonth.toString()
        val hour = StringUtil.formatNumber(diary.hour)
        val minute = StringUtil.formatNumber(diary.minute)
        val time = "${year}年${month}月${dayOfMonth}日 ${hour}时${minute}分"
        holder.mTitleTV.text = diary.title
        holder.mContentTV.text = diary.content
        holder.mDateTV.text = time

        return convertView
    }

    private inner class ViewHolder {
        lateinit var mDateTV: TextView
        lateinit var mTitleTV: TextView
        lateinit var mContentTV: TextView
    }


}
