package com.example.neon.deardiary;

import android.content.Context;

/**
 * 单例模式，确保只有一个MySQLHelper
 * Created by Neon on 2016/11/30.
 */

public class DBManager {
    public static MySQLHelper helper;

    public static MySQLHelper newInstance(Context context) {
        if (helper == null) {
            helper = new MySQLHelper(context);
        }
        return helper;
    }
}
