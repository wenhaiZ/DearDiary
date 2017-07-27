package com.example.neon.deardiary.diarylist

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity

import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource
import com.example.neon.deardiary.utils.ActivityUtil

class DiaryListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        var diaryListFragment: DiaryListFragment? = fragmentManager.findFragmentById(R.id.list_container) as? DiaryListFragment
        if (diaryListFragment == null) {
            diaryListFragment = DiaryListFragment()
            ActivityUtil.addFragmentToActivity(fragmentManager, diaryListFragment, R.id.list_container)
        }

        val dataSource = DiaryLocalDataSource.getInstance(this)
        //create presenter
        DiaryListPresenter(dataSource!!, diaryListFragment)
    }

}
