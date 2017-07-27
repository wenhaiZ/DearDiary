package com.example.neon.deardiary.settings

import com.example.neon.deardiary.base.BasePresenter
import com.example.neon.deardiary.base.BaseView


interface SettingsContract {
    interface View : BaseView<Presenter> {
        fun onDiaryDeleted()
    }

    interface Presenter : BasePresenter {
        fun deleteAll()
    }
}
