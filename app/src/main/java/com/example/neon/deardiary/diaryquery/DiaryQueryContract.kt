package com.example.neon.deardiary.diaryquery

import com.example.neon.deardiary.base.BasePresenter
import com.example.neon.deardiary.base.BaseView
import com.example.neon.deardiary.data.Diary

import java.util.ArrayList


internal interface DiaryQueryContract {
    interface View : BaseView<Presenter> {
        fun querySuccessfully(diaries: ArrayList<Diary>)
        fun queryFailed()

    }

    interface Presenter : BasePresenter {
        fun diaryQueryByKeyword(keyword: String)
    }
}
