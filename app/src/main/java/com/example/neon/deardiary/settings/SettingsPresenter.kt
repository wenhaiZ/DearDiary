package com.example.neon.deardiary.settings

import com.example.neon.deardiary.data.DataSource

internal class SettingsPresenter(private val mDataSource: DataSource?, private val mView: SettingsContract.View) : SettingsContract.Presenter {

    init {
        mView.setPresenter(this)
    }

    override fun deleteAll() {
        mDataSource !!.deleteAll()
        mView.onDiaryDeleted()
    }
}
