package com.example.neon.deardiary.diaryedit

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.settings.SettingsFragment

import java.util.Calendar

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder


internal class DiaryEditFragment : Fragment(), DiaryEditContract.View {

    @BindView(R.id.diary_content)
    internal lateinit var mEtDiaryContent: EditText
    @BindView(R.id.diary_title)
    internal lateinit var mEtDiaryTitle: EditText
    @BindView(R.id.titleTime)
    internal lateinit var mTvTitleTime: TextView

    private lateinit var mDiary: Diary
    private lateinit var mPresenter: DiaryEditContract.Presenter
    private lateinit var mUnBinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDiary = arguments.getSerializable(DiaryEditActivity.DATA_DIARY) as Diary
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_diary_edit, container, false)
        mUnBinder = ButterKnife.bind(this, root)
        initView()
        return root
    }

    private fun initView() {
        mEtDiaryTitle.setText(mDiary.title)
        mEtDiaryContent.setText(mDiary.content)
        if ("" == mDiary.title) {
            mEtDiaryTitle.requestFocus()
        } else {
            mEtDiaryContent.requestFocus()
            mEtDiaryContent.setSelection(mEtDiaryContent.text.length)
        }
        val date = "${mDiary.year}${getString(R.string.main_year)}${mDiary.month}${getString(R.string.main_month)}${mDiary.dayOfMonth}${getString(R.string.day_of_month)}"
        mTvTitleTime.text = date
    }

    override fun setPresenter(presenter: DiaryEditContract.Presenter) {
        mPresenter = presenter
    }


    @OnClick(R.id.appendTime, R.id.done_write)
    fun onButtonClick(v: View) {
        when (v.id) {
            R.id.appendTime -> appendTimeInContent()
            R.id.done_write -> updateDiaryIfNeeded()
            else -> {
            }
        }
    }

    private fun appendTimeInContent() {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        mEtDiaryContent.append("$hour${getString(R.string.hour)}$minute${getString(R.string.minute)}")
    }

    private fun updateDiaryIfNeeded() {
        val newContent = mEtDiaryContent.text.toString()
        val newTitle = mEtDiaryTitle.text.toString()
        //如果标题或内容变化进行更新数据
        if (newContent != mDiary.content || newTitle != mDiary.title) {
            mDiary.content = newContent
            mDiary.title = newTitle
            val c = Calendar.getInstance()
            val updateHour = c.get(Calendar.HOUR_OF_DAY)
            val updateMinute = c.get(Calendar.MINUTE)
            mDiary.hour = updateHour
            mDiary.minute = updateMinute
            mPresenter.saveDiary(mDiary)
        } else {
            activity.finish()
        }
    }

    override fun onDiaryUpdated() {
        updateValidDiaryCount()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }


    private fun updateValidDiaryCount() {
        val diaryCount = mPresenter.validDiaryCount()
        PreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putString(SettingsFragment.KEY_DIARY_COUNT, diaryCount.toString() + "")
                .apply()
    }

    override fun onDiaryUpdateFailed() {
        Toast.makeText(activity, getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }
}
