package com.example.neon.deardiary.dao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * 用于获取 DaoSession 对象
 * Created by Neon on 2016/12/10.
 */

public class DaoManager {
    private static DaoSession sDaoSession;

    public static DaoSession getDaoSession(Context context) {

        if (sDaoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "diary.db", null);
            Database db = helper.getReadableDb();
            DaoMaster daoMaster = new DaoMaster(db);
            sDaoSession = daoMaster.newSession();
        }
        return sDaoSession;
    }
}
