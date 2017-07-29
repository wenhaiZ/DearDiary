package com.example.neon.deardiary.widget

import android.app.DatePickerDialog
import android.content.Context

/**
 * 解决主界面选择时间后重复执行 onDateSet() 方法
 * Created by Neon on 2016/12/5.
 */

internal class CustomDatePickerDialog(context: Context, callBack: DatePickerDialog.OnDateSetListener, year: Int, monthOfYear: Int, dayOfMonth: Int) : DatePickerDialog(context, callBack, year, monthOfYear, dayOfMonth) {

    override fun onStop() {
        //super.onStop();
    }
}
