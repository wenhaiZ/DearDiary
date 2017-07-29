package com.example.neon.deardiary.diaryquery

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnItemClick
import butterknife.Unbinder
import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.diaryedit.DiaryEditActivity
import java.util.ArrayList


internal class DiaryQueryFragment : Fragment(), DiaryQueryContract.View {

    @BindView(R.id.search_result_list)
    internal lateinit var mLvResult: ListView
    @BindView(R.id.searchKey)
    internal lateinit var mEtQuery: EditText

    private var mPresenter: DiaryQueryContract.Presenter? = null
    private var mAdapter: DiaryQueryAdapter? = null
    private var mUnBinder: Unbinder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_diary_query, container, false)
        mUnBinder = ButterKnife.bind(this, root)
        mLvResult.dividerHeight = 0
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

    @OnItemClick(R.id.search_result_list)
    fun onItemClick(position: Int) {
        val diary = mAdapter!!.getItem(position) as Diary
        val bundle = Bundle()
        bundle.putSerializable(DiaryEditActivity.DATA_DIARY, diary)
        val intent = Intent(activity, DiaryEditActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }

    @OnClick(R.id.search_close_btn)
    fun close() {
        activity.finish()
    }

    private fun beginQuery(keyword: String) {
        if ("" != keyword) {
            mLvResult.visibility = View.VISIBLE
            if (mAdapter == null) {
                mAdapter = DiaryQueryAdapter(activity)
                mLvResult.adapter = mAdapter
            }
            mPresenter!!.diaryQueryByKeyword(keyword)
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
        mAdapter!!.setData(diaries)
    }

    override fun queryFailed() {
        Log.e(TAG, "queryFailed: 查询失败")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder!!.unbind()
    }

    companion object {

        private val TAG = "DiaryQueryFragment"
    }
}
