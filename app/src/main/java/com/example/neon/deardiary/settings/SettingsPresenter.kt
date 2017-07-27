package com.example.neon.deardiary.settings

import com.example.neon.deardiary.data.source.DataSource

internal class SettingsPresenter(private val mDataSource: DataSource?, private val mView: SettingsContract.View) : SettingsContract.Presenter {

    init {
        mView.setPresenter(this)
    }

    override fun start() {

    }

    override fun deleteAll() {
        mDataSource!!.deleteAll()
        mView.onDiaryDeleted()
    }
}
