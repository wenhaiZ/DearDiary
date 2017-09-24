package com.example.neon.deardiary.utils

import android.util.Log

object LogUtil {
    var debug = true

    fun d(tag: String, msg: String) {
        if (debug) {
            Log.d(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
    }

}