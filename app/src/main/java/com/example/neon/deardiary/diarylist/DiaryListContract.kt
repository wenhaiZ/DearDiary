package com.example.neon.deardiary.diarylist

import com.example.neon.deardiary.base.BaseView
import com.example.neon.deardiary.data.Diary
import java.util.ArrayList
import java.util.Calendar


internal interface DiaryListContract {
    interface View : BaseView<Presenter> {
        fun showDiary(diaries: ArrayList<Diary>)

        fun editDiary(diary: Diary)

    }

    interface Presenter  {
        fun loadDiaries(c: Calendar)

        fun getDiary(c: Calendar): Diary?

        fun insertDiary(diary: Diary)
    }
}
