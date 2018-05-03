package com.wenhaiz.deardiary.diarylist

import com.wenhaiz.deardiary.data.DataSource
import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.utils.LogUtil
import java.util.ArrayList
import java.util.Calendar


internal class DiaryListPresenter(dataSource: DataSource, private val mView: DiaryListContract.View) : DiaryListContract.Presenter {
    private val mDataSource: DataSource = dataSource

    init {
        mView.setPresenter(this)
    }

    override fun loadDiaries(c: Calendar) {
        mDataSource.queryAndAddDefault(c, object : DataSource.LoadDiariesCallBack {
            override fun onDiaryLoaded(diaryList: ArrayList<Diary>) {
                mView.showDiary(diaryList)
            }

            override fun onDataNotAvailable() {
                LogUtil.e(TAG,"onDataNotAvailable: 数据读取失败")
            }
        })

    }

    override fun getDiary(c: Calendar): Diary? {
        var foundDiary: Diary? = null
        mDataSource.queryByDay(c, object : DataSource.LoadDiaryCallBack {
            override fun onDiaryGot(diary: Diary) {
                foundDiary = diary
            }

            override fun onDataNotAvailable() {
                foundDiary = null
            }
        })
        return foundDiary

    }

    override fun insertDiary(diary: Diary) {
        mDataSource.insertDiary(diary)
    }

    companion object {
        private const val TAG = "DiaryListPresenter"
    }
}
