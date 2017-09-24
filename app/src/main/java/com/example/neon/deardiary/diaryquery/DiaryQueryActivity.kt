package com.example.neon.deardiary.diaryquery

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.LocalDataSource
import com.example.neon.deardiary.utils.ActivityUtil

class DiaryQueryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_query)
        var diaryQueryFragment: DiaryQueryFragment? = fragmentManager.findFragmentById(R.id.query_container) as? DiaryQueryFragment
        if (diaryQueryFragment == null) {
            diaryQueryFragment = DiaryQueryFragment()
            ActivityUtil.addFragmentToActivity(fragmentManager, diaryQueryFragment, R.id.query_container)
        }
        val dataSource = LocalDataSource.getInstance(this)
        //create presenter
        DiaryQueryPresenter(diaryQueryFragment, dataSource)

    }
}
