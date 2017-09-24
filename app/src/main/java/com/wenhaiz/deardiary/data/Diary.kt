package com.wenhaiz.deardiary.data

import java.io.Serializable
import java.util.Calendar

class Diary : Serializable {
    var id: Long? = null//数据库id
    var year: Int = 0//年
    var month: Int = 0//月
    var dayOfMonth: Int = 0//日
    var dayOfWeek: Int = 0//星期
    var hour: Int = 0//当天最后一次编辑的小时
    var minute: Int = 0//当天最后一次编辑的分钟
    var title: String? = null//日记标题
    var content: String? = null//日记内容

    constructor(id: Long?, year: Int, month: Int, dayOfMonth: Int, dayOfWeek: Int, hour: Int,
                minute: Int, title: String, content: String) {
        this.id = id
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
        this.dayOfWeek = dayOfWeek
        this.hour = hour
        this.minute = minute
        this.title = title
        this.content = content
    }

    constructor() {
        this.content = ""
        this.title = ""
        this.year = 0
        this.minute = 0
        this.hour = 0
        this.dayOfMonth = 0
        this.dayOfWeek = 0
        this.month = 0
    }

    constructor(c: Calendar) : this() {
        this.year = c.get(Calendar.YEAR)
        this.month = c.get(Calendar.MONTH) + 1
        this.dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
        this.dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
