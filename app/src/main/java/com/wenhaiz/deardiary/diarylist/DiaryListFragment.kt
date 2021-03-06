package com.wenhaiz.deardiary.diarylist

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.diaryedit.DiaryEditActivity
import com.wenhaiz.deardiary.diaryquery.DiaryQueryActivity
import com.wenhaiz.deardiary.settings.SettingsActivity
import com.wenhaiz.deardiary.widget.CustomDatePickerDialog
import java.util.Calendar
import java.util.Locale


internal class DiaryListFragment : Fragment(), DiaryListContract.View {
    @BindView(R.id.diaryList)
    lateinit var mDiaryList: RecyclerView
    @BindView(R.id.bottom_year)
    lateinit var mTvYear: TextView
    @BindView(R.id.bottom_month)
    lateinit var mTvMonth: TextView

    private lateinit var mPresenter: DiaryListContract.Presenter
    private lateinit var mCalendar: Calendar
    private lateinit var mAdapter: DiaryListAdapter
    private var isFirstStart: Boolean = false
    private lateinit var mUnBinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = DiaryListAdapter(activity)
        mCalendar = Calendar.getInstance()
        isFirstStart = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_diary_list, container, false)
        mUnBinder = ButterKnife.bind(this, rootView)
        initView()
        return rootView
    }

    private fun initView() {
        mDiaryList.layoutManager = LinearLayoutManager(activity)
        mDiaryList.adapter = mAdapter
        mDiaryList.isVerticalScrollBarEnabled = false
        mDiaryList.itemAnimator = DefaultItemAnimator()
        mAdapter.setOnItemClickListener(object : DiaryListAdapter.OnItemClickListener {
            override fun onItemClick(view: View) {
                val itemPosition = mDiaryList.getChildAdapterPosition(view)
                val diary = mAdapter.getItem(itemPosition)
                editDiary(diary)
            }
        })
        setBottomDate()
    }

    private fun setBottomDate() {
        val curYear = mCalendar.get(Calendar.YEAR)
        val curMonth = mCalendar.get(Calendar.MONTH) + 1
        mTvYear.text = String.format(Locale.getDefault(), "%d", curYear)
        mTvMonth.text = String.format(Locale.getDefault(), "%d", curMonth)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_DIARY) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(activity, getString(R.string.diary_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun setPresenter(presenter: DiaryListContract.Presenter) {
        mPresenter = presenter
    }


    override fun showDiary(diaries: ArrayList<Diary>) {
        mAdapter.setData(diaries)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.loadDiaries(mCalendar)
    }

    @OnClick(R.id.settings)
    fun goToSettings() {
        val startSetting = Intent(activity, SettingsActivity::class.java)
        startActivity(startSetting)
    }

    @OnClick(R.id.search)
    fun goToSearch() {
        val startSearch = Intent(activity, DiaryQueryActivity::class.java)
        startActivity(startSearch)
    }

    @OnClick(R.id.write_today)
    fun editTodayDiary() {
        val today = Calendar.getInstance()
        var diary: Diary? = mPresenter.getDiary(today)
        if (diary == null) {
            diary = Diary(today)
            mPresenter.insertDiary(diary)
        }
        editDiary(diary)
    }

    override fun editDiary(diary: Diary) {
        val data = Bundle()
        data.putSerializable(DiaryEditActivity.DATA_DIARY, diary)
        val intent = Intent(activity, DiaryEditActivity::class.java)
        intent.putExtras(data)
        startActivityForResult(intent, REQUEST_EDIT_DIARY)
    }

    @OnClick(R.id.chooseDate)
    fun showDatePickDialog() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, monthOfYear)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val now = Calendar.getInstance()

            if (mCalendar.before(now)) {
                mPresenter.loadDiaries(mCalendar)
                mDiaryList.scrollToPosition(dayOfMonth - 1)
            } else {//如果所选为当前月份或超前，跳到当前月份
                mCalendar = now
                mPresenter.loadDiaries(mCalendar)
                Toast.makeText(activity, getString(R.string.choose_date_reach_limit), Toast.LENGTH_SHORT).show()
            }
            setBottomDate()
        }

        val datePicker = CustomDatePickerDialog(activity, listener,
                mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    companion object {
        const val REQUEST_EDIT_DIARY = 0x1
    }
}
