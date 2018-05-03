package com.wenhaiz.deardiary

import android.app.Application
import com.wenhaiz.deardiary.data.MyObjectBox
import io.objectbox.BoxStore

class MyApp : Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
    }

    fun getMyBoxStore(): BoxStore {
        return boxStore
    }
}