package com.example.neon.deardiary.util;

import android.app.Fragment;
import android.app.FragmentManager;

/**
 * Created by Neon on 2017/5/15.
 */

public class ActivityUtils {
    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int id) {
        fragmentManager.beginTransaction().add(id, fragment).commit();
    }
}
