package com.example.neon.deardiary.notify

import com.example.neon.deardiary.base.BasePresenter
import com.example.neon.deardiary.base.BaseView
import com.example.neon.deardiary.data.Diary

import java.util.Calendar

interface NotifyContract {
    interface Presenter : BasePresenter {
        fun getDiary(c: Calendar): Diary?

        fun insertDiary(diary: Diary)
    }

    interface View : BaseView<Presenter>
}
