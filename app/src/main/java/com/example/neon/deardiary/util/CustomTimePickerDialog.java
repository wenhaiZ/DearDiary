package com.example.neon.deardiary.util;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * 解决onTimeSet()重复执行
 * Created by Neon on 2016/12/5.
 */

public class CustomTimePickerDialog extends TimePickerDialog {
    public CustomTimePickerDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
    }

    @Override
    protected void onStop() {
//        super.onStop();
    }
}
