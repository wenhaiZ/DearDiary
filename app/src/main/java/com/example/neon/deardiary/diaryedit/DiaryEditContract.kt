package com.example.neon.deardiary.diaryedit

import com.example.neon.deardiary.base.BasePresenter
import com.example.neon.deardiary.base.BaseView
import com.example.neon.deardiary.data.Diary


interface DiaryEditContract {
    interface View : BaseView<Presenter> {
        fun onDiaryUpdated()

        fun onDiaryUpdateFailed()
    }

    interface Presenter : BasePresenter {
        fun saveDiary(diary: Diary)

        val validDiaryCount: Int

    }


}
