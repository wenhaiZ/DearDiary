package com.example.neon.deardiary.notify

import com.example.neon.deardiary.base.BaseView
import com.example.neon.deardiary.data.Diary
import java.util.Calendar

internal interface NotifyContract {
    interface Presenter {
        fun getDiary(c: Calendar): Diary?

        fun insertDiary(diary: Diary)
    }

    interface View : BaseView<Presenter>
}
