package com.cdvdev.atmsearcher.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cdvdev.atmsearcher.R;

public class FragmentsHelper {

    private static Fragment sCurrentVisibleFragment;

    /**
     * Method for creating new fragment
     * @param fm FragmentManager
     */
    public static void createFragment(FragmentManager fm, Fragment newFragment, boolean addToBackStack) {

        if (fm == null) {
            return;
        }

        Fragment oldFragment = fm.findFragmentById(R.id.main_container);

        if (oldFragment != null) {
            //don`t create fragment second time
            if (oldFragment.getClass().toString().equals(newFragment.getClass().toString())) {
                return;
            }
        }

        if (sCurrentVisibleFragment != null) {
            newFragment = sCurrentVisibleFragment;
            //reset saved visible fragment
            FragmentsHelper.resetSavedVisibleFragment();
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container, newFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        ft.commit();

    }

    /**
     * Method for saving visible fragment when activity was stopped
     * @param fm- FragmentManager
     */
    public static void saveVisibleFragment(FragmentManager fm) {
        if (fm == null) {
             return;
        }

        Fragment fragment = fm.findFragmentById(R.id.main_container);

        if (fragment != null) {
            sCurrentVisibleFragment = fragment;
        } else {
            sCurrentVisibleFragment = null;
        }

    }

    /**
     * Method for reset visible fragment when activity was destroyed
     */
    public static void resetSavedVisibleFragment(){
        sCurrentVisibleFragment = null;
    }

}
