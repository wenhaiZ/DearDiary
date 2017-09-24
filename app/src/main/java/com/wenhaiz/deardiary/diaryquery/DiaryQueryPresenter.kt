package com.wenhaiz.deardiary.diaryquery

import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.data.DataSource
import com.wenhaiz.deardiary.diaryquery.DiaryQueryContract

import java.util.ArrayList

internal class DiaryQueryPresenter(
        private val mView: DiaryQueryContract.View,
        private val mDataSource: DataSource?
) : DiaryQueryContract.Presenter {

    init {
        mView.setPresenter(this)
    }

    override fun diaryQueryByKeyword(keyword: String) {
        mDataSource?.queryByKeyword(keyword, object : DataSource.LoadDiariesCallBack {
            override fun onDiaryLoaded(diaryList: ArrayList<Diary>) {
                mView.querySuccessfully(diaryList)
            }

            override fun onDataNotAvailable() {
                mView.queryFailed()
            }
        })
    }
}
