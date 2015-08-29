package com.cdvdev.atmsearcher.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.fragments.AtmListFragment;
import com.cdvdev.atmsearcher.fragments.NetworkOffFragment;
import com.cdvdev.atmsearcher.helpers.FragmentsHelper;
import com.cdvdev.atmsearcher.helpers.NetworkHelper;
import com.cdvdev.atmsearcher.listeners.OnBackPressedListener;
import com.cdvdev.atmsearcher.listeners.OnSearchViewListener;

public class MainActivity extends AppCompatActivity implements
                                        OnSearchViewListener{

    private FragmentManager mFm;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        mActionBar = getSupportActionBar();

        mFm = getSupportFragmentManager();
        Fragment newFragment = null;

        if (NetworkHelper.isDeviceOnline(getApplicationContext())) {
             newFragment =  AtmListFragment.newInstance();
        } else {
             newFragment = NetworkOffFragment.newInstance();
        }

        FragmentsHelper.createFragment(mFm, newFragment, false);

    }

    @Override
    public void onBackPressed() {
        OnBackPressedListener listener = null;
        boolean backInFragment = false;

        //inplement back pressed listener in fragment
        for (Fragment fragment : mFm.getFragments()) {
            if (fragment instanceof OnBackPressedListener) {
                listener = (OnBackPressedListener) fragment;
                break;
            }
        }

        if (listener != null) {
            backInFragment = listener.onBackPressed();
        }

        if (!backInFragment) {
            super.onBackPressed();
        }
    }

    //---- SEARCHVIEW CALLBACKS

    @Override
    public void onOpenSearchView() {
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCloseSearchView() {
       mActionBar.setDisplayHomeAsUpEnabled(false);
    }

}
