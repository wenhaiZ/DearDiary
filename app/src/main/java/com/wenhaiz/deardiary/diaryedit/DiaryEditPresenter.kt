package com.wenhaiz.deardiary.diaryedit

import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.data.DataSource

internal class DiaryEditPresenter(private val mDataSource: DataSource?, private val mView: DiaryEditContract.View) : DiaryEditContract.Presenter {

    init {
        mView.setPresenter(this)
    }

    override fun validDiaryCount(): Int = mDataSource !!.validDairyCount()

    override fun saveDiary(diary: Diary) {
        mDataSource !!.updateDiary(diary, object : DataSource.UpdateDiaryCallback {
            override fun onDiaryUpdated() {
                mView.onDiaryUpdated()
            }

            override fun onDiaryUpdateFailed() {
                mView.onDiaryUpdateFailed()
            }
        })

    }
}
