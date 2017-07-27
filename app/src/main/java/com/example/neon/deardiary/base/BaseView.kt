package com.example.neon.deardiary.base

interface BaseView<in T> {
    fun setPresenter(presenter: T)
}
