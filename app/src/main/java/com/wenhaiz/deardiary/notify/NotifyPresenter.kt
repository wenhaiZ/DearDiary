package com.wenhaiz.deardiary.notify

import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.data.DataSource
import com.wenhaiz.deardiary.notify.NotifyContract

import java.util.Calendar

internal class NotifyPresenter(private val mDataSource: DataSource?, view: NotifyContract.View) : NotifyContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun getDiary(c: Calendar): Diary? {
        var foundDiary: Diary? = null
        mDataSource?.queryByDay(c, object : DataSource.LoadDiaryCallBack {
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
        mDataSource?.insertDiary(diary)
    }
}
