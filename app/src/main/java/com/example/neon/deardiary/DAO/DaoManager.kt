//package com.example.neon.deardiary.dao
//
//import android.content.Context
//
//import org.greenrobot.greendao.database.Database
//
///**
// * 用于获取 DaoSession 对象
// * Created by Neon on 2016/12/10.
// */
//
//object DaoManager {
//    private var sDaoSession: DaoSession? = null
//
//    fun getDaoSession(context: Context): DaoSession? {
//        if (sDaoSession == null) {
//            synchronized(DaoManager::class.java) {
//                if (sDaoSession == null) {
//                    val helper = DaoMaster.DevOpenHelper(context, "diary.db", null)
//                    val db = helper.readableDb
//                    val daoMaster = DaoMaster(db)
//                    sDaoSession = daoMaster.newSession()
//                }
//            }
//        }
//        return sDaoSession
//    }
//}
