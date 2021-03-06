package com.wenhaiz.deardiary.widget

import android.app.TimePickerDialog
import android.content.Context

/**
 * 解决 onTimeSet() 重复执行
 */

internal class CustomTimePickerDialog(context: Context, listener: TimePickerDialog.OnTimeSetListener, hourOfDay: Int, minute: Int, is24HourView: Boolean) : TimePickerDialog(context, listener, hourOfDay, minute, is24HourView) {

    override fun onStop() {
        //super.onStop();
    }
}
