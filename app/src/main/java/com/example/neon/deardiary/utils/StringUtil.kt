package com.example.neon.deardiary.utils


object StringUtil {
    fun formatNumber(number: Int): String {
        return if (number < 10) "0$number" else "$number"
    }
}
