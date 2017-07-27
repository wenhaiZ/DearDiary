package com.example.neon.deardiary.diaryquery


import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.data.source.DataSource

import java.util.ArrayList

internal class DiaryQueryPresenter(private val mView: DiaryQueryContract.View, private val mDataSource: DataSource?) : DiaryQueryContract.Presenter {

    init {
        mView.setPresenter(this)
    }

    override fun start() {

    }

    override fun diaryQueryByKeyword(keyword: String) {
        mDataSource!!.queryByKeyword(keyword, object : DataSource.LoadDiaryCallBack {
            override fun onDiaryLoaded(diaryList: ArrayList<Diary>) {
                mView.querySuccessfully(diaryList)
            }

            override fun onDataNotAvailable() {
                mView.queryFailed()
            }
        })

    }
}
