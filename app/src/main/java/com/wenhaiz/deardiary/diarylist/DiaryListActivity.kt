package com.wenhaiz.deardiary.diarylist

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.data.objectbox.ObjectBoxDataSource
import com.wenhaiz.deardiary.utils.ActivityUtil

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

//        val dataSource = SQLDataSource.getInstance(this)
        val dataSource = ObjectBoxDataSource(this)
        //create presenter
        DiaryListPresenter(dataSource, diaryListFragment)
    }

}
