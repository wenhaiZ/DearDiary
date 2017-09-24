package com.example.neon.deardiary.diarylist

import com.example.neon.deardiary.data.DataSource
import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.data.LocalDataSource
import com.example.neon.deardiary.utils.LogUtil
import java.util.ArrayList
import java.util.Calendar


internal class DiaryListPresenter(dataSource: LocalDataSource, private val mView: DiaryListContract.View) : DiaryListContract.Presenter {
    private val mDataSource: DataSource

    init {
        mDataSource = dataSource
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
        private val TAG = "DiaryListPresenter"
    }
}
