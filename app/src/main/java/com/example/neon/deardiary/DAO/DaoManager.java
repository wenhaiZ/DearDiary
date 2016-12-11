package com.example.neon.deardiary.DAO;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * 用于获取 DaoSession 对象
 * Created by Neon on 2016/12/10.
 */

public class DaoManager {
    private static DaoSession daoSession;

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "diary.db", null);
            Database db = helper.getReadableDb();
            DaoMaster daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
