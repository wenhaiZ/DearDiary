package com.wenhaiz.deardiary.diaryedit

import com.wenhaiz.deardiary.base.BaseView
import com.wenhaiz.deardiary.data.Diary


internal interface DiaryEditContract {
    interface View : BaseView<Presenter> {
        fun onDiaryUpdated()

        fun onDiaryUpdateFailed()
    }

    interface Presenter {
        fun saveDiary(diary: Diary)

        fun validDiaryCount(): Int
    }


}
