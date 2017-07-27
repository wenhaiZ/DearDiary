package com.example.neon.deardiary.utils

import android.app.Fragment
import android.app.FragmentManager


object ActivityUtil {
    fun addFragmentToActivity(fragmentManager: FragmentManager, fragment: Fragment, id: Int) {
        fragmentManager.beginTransaction().add(id, fragment).commit()
    }
}
