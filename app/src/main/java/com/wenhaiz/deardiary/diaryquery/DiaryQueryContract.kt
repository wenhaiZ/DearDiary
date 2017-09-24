package com.wenhaiz.deardiary.diaryquery

import com.wenhaiz.deardiary.base.BaseView
import com.wenhaiz.deardiary.data.Diary
import java.util.ArrayList


internal interface DiaryQueryContract {
    interface View : BaseView<Presenter> {
        fun querySuccessfully(diaries: ArrayList<Diary>)
        fun queryFailed()

    }

    interface Presenter {
        fun diaryQueryByKeyword(keyword: String)
    }
}
