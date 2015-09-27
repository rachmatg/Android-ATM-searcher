package com.cdvdev.atmsearcher.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cdvdev.atmsearcher.R;

public class FragmentsHelper {

    /**
     * Method for creating new fragment
     * @param fm FragmentManager
     */
    public static void createFragment(FragmentManager fm, Fragment newFragment, boolean addToBackStack) {

        Fragment fragment = fm.findFragmentById(R.id.main_container);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.main_container, newFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            if (addToBackStack) {
                ft.addToBackStack(null);
            }

            ft.commit();
        }

    }

}
