package com.example.neon.deardiary.diaryedit

import com.example.neon.deardiary.base.BaseView
import com.example.neon.deardiary.data.Diary


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
