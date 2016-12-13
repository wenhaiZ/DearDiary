package com.example.neon.deardiary.util;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * 解决主界面选择时间后重复执行onDateSet()方法
 * Created by Neon on 2016/12/5.
 */

public class CustomDatePickerDialog extends DatePickerDialog {
    public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {
//        super.onStop();
    }
}
