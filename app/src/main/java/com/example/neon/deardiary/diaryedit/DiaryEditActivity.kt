package com.example.neon.deardiary.diaryedit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.data.LocalDataSource
import com.example.neon.deardiary.utils.ActivityUtil

class DiaryEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_edit)

        var diaryEditFragment: DiaryEditFragment? = fragmentManager.findFragmentById(R.id.edit_container) as? DiaryEditFragment
        if (diaryEditFragment == null) {
            diaryEditFragment = DiaryEditFragment()
            ActivityUtil.addFragmentToActivity(fragmentManager, diaryEditFragment, R.id.edit_container)
        }
        val diary = intent.getSerializableExtra(DATA_DIARY) as Diary

        diaryEditFragment.arguments = Bundle().apply {
            putSerializable(DATA_DIARY, diary)
        }
        //create dataSource
        val dataSource = LocalDataSource.getInstance(this)
        //create presenter
        DiaryEditPresenter(dataSource, diaryEditFragment)
    }

    companion object {
        val DATA_DIARY = "diary"
    }

}
