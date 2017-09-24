package com.wenhaiz.deardiary.notify

import com.wenhaiz.deardiary.base.BaseView
import com.wenhaiz.deardiary.data.Diary
import java.util.Calendar

internal interface NotifyContract {
    interface Presenter {
        fun getDiary(c: Calendar): Diary?

        fun insertDiary(diary: Diary)
    }

    interface View : BaseView<Presenter>
}
