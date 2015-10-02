package com.cdvdev.atmsearcher.activities;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.fragments.AtmDetailFragment;
import com.cdvdev.atmsearcher.fragments.AtmListFragment;
import com.cdvdev.atmsearcher.fragments.LocationAlertDialogFragment;
import com.cdvdev.atmsearcher.fragments.AtmMapFragment;
import com.cdvdev.atmsearcher.fragments.NetworkOffFragment;
import com.cdvdev.atmsearcher.helpers.FragmentsHelper;
import com.cdvdev.atmsearcher.helpers.NetworkHelper;
import com.cdvdev.atmsearcher.listeners.FragmentListener;
import com.cdvdev.atmsearcher.listeners.OnBackPressedListener;
import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;

public class MainActivity extends AppCompatActivity implements
                                        GoogleApiClient.ConnectionCallbacks,
                                        GoogleApiClient.OnConnectionFailedListener,
                                        ResultCallback<LocationSettingsResult>,
                                        LocationListener,
                                        FragmentListener{

    private static final int UPDATE_LOCATION_INTERVAL = 10000; //milliseconds
    private static final int UPDATE_LOCATION_INTERVAL_FASTEST = UPDATE_LOCATION_INTERVAL / 2;
    private static final String KEY_SHOW_LOCATION_SETTINGS_REQUEST = "atmsearcher.show_location_settings_request";

    private FragmentManager mFm;
    private ActionBar mActionBar;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private boolean mShowLocationSettingsRequest = true;


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

        //update values from Bundle
        if (savedInstanceState != null) {
             mShowLocationSettingsRequest = savedInstanceState.getBoolean(KEY_SHOW_LOCATION_SETTINGS_REQUEST);
        }

        if (NetworkHelper.isDeviceOnline(getApplicationContext())) {
             mGoogleApiClient = initGoogleApiClient();
             mLocationRequest = initLocationRequest();
             newFragment = AtmListFragment.newInstance();
        } else {
             newFragment = NetworkOffFragment.newInstance();
        }

        FragmentsHelper.createFragment(mFm, newFragment, false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if ( mGoogleApiClient != null ) {
             mGoogleApiClient.connect();
             checkLocationSettings();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdate();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdate();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_SHOW_LOCATION_SETTINGS_REQUEST, mShowLocationSettingsRequest);
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
     * Helper method for initialize location client
     * @return GoogleApiClient
     */
    private GoogleApiClient initGoogleApiClient() {

        return new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Helper method for initialize location request
     * @return LocationRequest
     */
    private LocationRequest initLocationRequest(){
        return new LocationRequest()
                .setInterval(UPDATE_LOCATION_INTERVAL)
                .setFastestInterval(UPDATE_LOCATION_INTERVAL_FASTEST)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //PRIORITY_BALANCED_POWER_ACCURACY  - Wifi and Network
        //PRIORITY_HIGH_ACCURACY - WiFi , GPS and Network
    }

    /**
     * Helper method for initialize location settings request
     * @return LocationSettingsRequest
     */
    private LocationSettingsRequest initLocationSettingsRequest(){
        return new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .build();
    }

    /**
     * Method for checking location settings (Show dialog message in onResult)
     */
    private void checkLocationSettings(){
        if (mShowLocationSettingsRequest) {
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(
                    mGoogleApiClient,
                    initLocationSettingsRequest()
            );
            result.setResultCallback(this);
        }
    }

    /**
     * Helper method for starting location updates
     */
    private void startLocationUpdate(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        );
    }

    /**
     * Helper method for stop location updates
     */
    private void stopLocationUpdate(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        );
    }

    /**
     * Helper method for updating ATM`s list
     * @param point -LocationPoint
     */
    private void updateIU(LocationPoint point) {
        for (Fragment fragment : mFm.getFragments()) {
            if (fragment instanceof AtmListFragment) {
                ((AtmListFragment) fragment).onUpdateLocation(point);
                break;
            }
        }
    }

    //----- GOOGLE API CONNECTION CALLBACKS

    @Override
    public void onConnected(Bundle bundle) {
       // LocationPoint point = null;

        Toast.makeText(this, "GoogleApiClient connected!", Toast.LENGTH_SHORT).show();
        if (mCurrentLocation == null) {
            //get last location
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
               // Toast.makeText(this, "Current location: lat " + mCurrentLocation.getLatitude() + ", lon " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                LocationPoint point = new LocationPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                updateIU(point);
            }
        }

        startLocationUpdate();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "GoogleApiClient suspended!", Toast.LENGTH_SHORT).show();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "GoogleApiClient connection failed! Code " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }


    //---- CHECK LOCATION SETTINGS RESULT CALLBACK

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();

        if (states.isNetworkLocationPresent() && !states.isNetworkLocationUsable()) {
            LocationAlertDialogFragment.newInstance(0, R.string.message_need_on_location)
                    .show(mFm, "location.alert");

        } else if (states.isGpsPresent() && !states.isGpsUsable()) {
            LocationAlertDialogFragment.newInstance(0, R.string.message_need_on_gps)
                    .show(mFm, "location.alert");
        }

        mShowLocationSettingsRequest = false;
    }

    //---- LOCATION CALLBACK

    @Override
    public void onLocationChanged(Location location) {
        Log.d("DEBUG", "location = " + location.toString());
        mCurrentLocation = location;
        LocationPoint point = new LocationPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        updateIU(point);
    }

    //--- FRAGMENTS CALLBACKS

    @Override
    public void onAtmListItemSelected(Atm atm) {
        Fragment newFragment = AtmDetailFragment.newInstance(atm);
        FragmentTransaction ft = mFm.beginTransaction();
        ft.replace(R.id.main_container, newFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onChangeAppBarTitle(int res) {
        mActionBar.setTitle(res > 0 ? res : R.string.app_name);
    }

    @Override
    public void onSetHomeAsUpEnabled(boolean isEnabled) {
        mActionBar.setDisplayHomeAsUpEnabled(isEnabled);
    }

    @Override
    public void onViewAtmOnMap(Atm atm) {
        FragmentTransaction ft = mFm.beginTransaction();
        ft.replace(R.id.main_container, AtmMapFragment.newInstance(atm))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null);
        ft.commit();
    }

}
