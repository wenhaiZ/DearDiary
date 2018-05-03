package com.wenhaiz.deardiary.diaryquery

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.utils.LogUtil
import java.util.ArrayList

internal class DiaryQueryFragment : Fragment(), DiaryQueryContract.View {
    @BindView(R.id.search_result_list)
    lateinit var mLvResult: RecyclerView
    @BindView(R.id.searchKey)
    lateinit var mEtQuery: EditText

    private lateinit var mPresenter: DiaryQueryContract.Presenter
    private lateinit var mResultAdapter: QueryResultAdapter
    private lateinit var mUnBinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_diary_query, container, false)
        mUnBinder = ButterKnife.bind(this, root)
        mResultAdapter = QueryResultAdapter(activity)
        mLvResult.adapter = mResultAdapter
        mLvResult.layoutManager = LinearLayoutManager(activity)
        mEtQuery.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                val keyword = s.toString()
                beginQuery(keyword)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        return root
    }

    @OnClick(R.id.search_close_btn)
    fun close() {
        activity.finish()
    }

    private fun beginQuery(keyword: String) {
        if ("" != keyword) {
            mLvResult.visibility = View.VISIBLE
            mPresenter.diaryQueryByKeyword(keyword)
        } else {
            mLvResult.visibility = View.INVISIBLE
        }
    }

    override fun setPresenter(presenter: DiaryQueryContract.Presenter) {
        mPresenter = presenter
    }

    override fun onResume() {
        super.onResume()
        beginQuery(mEtQuery.text.toString())
    }

    override fun querySuccessfully(diaries: ArrayList<Diary>) {
        mResultAdapter.setData(diaries)
    }

    override fun queryFailed() {
        LogUtil.e(TAG, "queryFailed: 查询失败")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    companion object {
        private const val TAG = "DiaryQueryFragment"
    }
}
