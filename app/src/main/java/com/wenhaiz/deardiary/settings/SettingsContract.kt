package com.wenhaiz.deardiary.settings

import com.wenhaiz.deardiary.base.BaseView


internal interface SettingsContract {
    interface View : BaseView<Presenter> {
        fun onDiaryDeleted()
    }

    interface Presenter {
        fun deleteAll()
    }
}
