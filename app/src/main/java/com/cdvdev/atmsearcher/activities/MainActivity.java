package com.cdvdev.atmsearcher.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.fragments.AtmListFragment;
import com.cdvdev.atmsearcher.fragments.NetworkOffFragment;
import com.cdvdev.atmsearcher.helpers.FragmentsHelper;
import com.cdvdev.atmsearcher.helpers.NetworkHelper;
import com.cdvdev.atmsearcher.receivers.UpdateDataReceiver;
import com.cdvdev.atmsearcher.services.UpdateDataService;

public class MainActivity extends AppCompatActivity {

    private FragmentManager mFm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mFm = getSupportFragmentManager();
        Fragment newFragment = null;

        if (NetworkHelper.isDeviceOnline(getApplicationContext())) {
             newFragment =  AtmListFragment.newInstance();
        } else {
             newFragment = NetworkOffFragment.newInstance();
        }

        FragmentsHelper.createFragment(mFm, newFragment, false);

        //start service for download data from network and update into DB
        createUpdateService();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    Toast.makeText(MainActivity.this, getApplication().getResources().getString(R.string.message_update_success) + " (" + resultCode + ")", Toast.LENGTH_SHORT).show();
                    updateAtmsList();
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
        AtmListFragment atmListFragment = (AtmListFragment) fm.findFragmentById(R.id.main_container);
        if (atmListFragment != null) {
            atmListFragment.updateList();
        }
    }

}
