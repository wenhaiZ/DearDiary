package com.wenhaiz.deardiary.base

interface BaseView<in T> {
    fun setPresenter(presenter: T)
}
