package com.example.neon.deardiary.diaryedit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.example.neon.deardiary.R
import com.example.neon.deardiary.data.Diary
import com.example.neon.deardiary.data.source.local.originaldb.DiaryLocalDataSource
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
        val data = Bundle()
        data.putSerializable(DATA_DIARY, diary)
        diaryEditFragment.arguments = data
        //create dataSource
        val dataSource = DiaryLocalDataSource.getInstance(this)
        //create presenter
        DiaryEditPresenter(dataSource, diaryEditFragment)
    }

    companion object {
        val DATA_DIARY = "diary"
    }

}
