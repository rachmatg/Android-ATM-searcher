package com.cdvdev.atmsearcher.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.fragments.AtmListFragment;
import com.cdvdev.atmsearcher.fragments.NetworkOffFragment;
import com.cdvdev.atmsearcher.helpers.FragmentsHelper;
import com.cdvdev.atmsearcher.helpers.NetworkHelper;
import com.cdvdev.atmsearcher.listeners.OnBackPressedListener;
import com.cdvdev.atmsearcher.listeners.OnSearchViewListener;
import com.cdvdev.atmsearcher.receivers.UpdateDataReceiver;
import com.cdvdev.atmsearcher.services.UpdateDataService;



public class MainActivity extends AppCompatActivity implements OnSearchViewListener{

    private static final String KEY_DATA_UPDATE = "com.cdvdev.atmsearcher.dataupdated";
    private FragmentManager mFm;
    private boolean mDataUpdated = false;
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

        if (savedInstanceState != null) {
            mDataUpdated = savedInstanceState.getBoolean(KEY_DATA_UPDATE);
        }

        mFm = getSupportFragmentManager();
        Fragment newFragment = null;

        if (NetworkHelper.isDeviceOnline(getApplicationContext())) {
             newFragment =  AtmListFragment.newInstance();
            //if data not updated yet
            if (!mDataUpdated) {
                //start service for download data from network and update into DB
                createUpdateService();
            }
        } else {
             newFragment = NetworkOffFragment.newInstance();
        }

        FragmentsHelper.createFragment(mFm, newFragment, false);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_DATA_UPDATE, mDataUpdated);
        super.onSaveInstanceState(outState);
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

    /**
     * Method for creating service for download data from network and update into DB
     */
    private void createUpdateService(){

        //creating receiver for service
        final UpdateDataReceiver receiver = new UpdateDataReceiver(new Handler());
        receiver.setReceiverCallback(new UpdateDataReceiver.ReceiverCallback(){
            @Override
            public void onReceiverResult(int resultCode, Bundle data) {
                if (resultCode == NetworkHelper.SUCCESS_RESP_CODE) {
                    //update data in DB
                    Toast.makeText(MainActivity.this, getApplication().getResources().getString(R.string.message_update_success) + " (" + resultCode + ")", Toast.LENGTH_SHORT).show();
                    updateAtmsList();
                    mDataUpdated = true;
                } else {
                    Toast.makeText(MainActivity.this, getApplication().getResources().getString(R.string.message_update_failed) + " (" + resultCode + ")", Toast.LENGTH_SHORT).show();
                }
                //TODO: stop update indicator in toolbar
                //....
            }
        });

        //create service
        Intent intent = new Intent(this, UpdateDataService.class);
        intent.putExtra(UpdateDataService.KEY_RECEIVER_NAME, receiver);
        startService(intent);
    }

    /**
     * Method for updating ATM`s list when new data received
     */
    private void updateAtmsList(){
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main_container);

        if (fragment instanceof AtmListFragment) {
            ((AtmListFragment) fragment).updateList();
        }
    }

    @Override
    public void onOpenSearchView() {
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCloseSearchView() {
       mActionBar.setDisplayHomeAsUpEnabled(false);
    }
}
