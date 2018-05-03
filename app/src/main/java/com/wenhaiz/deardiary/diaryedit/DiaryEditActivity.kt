package com.wenhaiz.deardiary.diaryedit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wenhaiz.deardiary.R
import com.wenhaiz.deardiary.data.Diary
import com.wenhaiz.deardiary.data.ObjectBoxDataSource
import com.wenhaiz.deardiary.utils.ActivityUtil

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
//        val dataSource = LocalDataSource.getInstance(this)
        val dataSource = ObjectBoxDataSource(this)
        //create presenter
        DiaryEditPresenter(dataSource, diaryEditFragment)
    }

    companion object {
        const val DATA_DIARY = "diary"
    }

}
