package com.example.neon.deardiary.settings

import com.example.neon.deardiary.base.BaseView


internal interface SettingsContract {
    interface View : BaseView<Presenter> {
        fun onDiaryDeleted()
    }

    interface Presenter {
        fun deleteAll()
    }
}
