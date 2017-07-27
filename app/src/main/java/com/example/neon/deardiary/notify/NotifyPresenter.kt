package com.example.neon.deardiary.notify

import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.data.source.DataSource

import java.util.Calendar


internal class NotifyPresenter(private val mDataSource: DataSource?, view: NotifyContract.View) : NotifyContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun start() {

    }

    override fun getDiary(c: Calendar): Diary? {
        var foundDiary:Diary? = null
        mDataSource!!.queryByDay(c, object : DataSource.GetDiaryCallBack {
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
        mDataSource!!.insertDiary(diary)

    }
}
