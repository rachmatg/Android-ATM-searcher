package com.cdvdev.atmsearcher.activities;

import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.fragments.AtmDetailFragment;
import com.cdvdev.atmsearcher.fragments.AtmListFragment;
import com.cdvdev.atmsearcher.fragments.ErrorFragment;
import com.cdvdev.atmsearcher.fragments.LocationAlertDialogFragment;
import com.cdvdev.atmsearcher.fragments.AtmMapFragment;
import com.cdvdev.atmsearcher.helpers.CustomIntent;
import com.cdvdev.atmsearcher.helpers.FragmentsHelper;
import com.cdvdev.atmsearcher.helpers.GoogleApiErrorsHelper;
import com.cdvdev.atmsearcher.helpers.JsonParseHelper;
import com.cdvdev.atmsearcher.helpers.NetworkHelper;
import com.cdvdev.atmsearcher.helpers.Utils;
import com.cdvdev.atmsearcher.listeners.CustomLocationListener;
import com.cdvdev.atmsearcher.listeners.FabListener;
import com.cdvdev.atmsearcher.listeners.FragmentListener;
import com.cdvdev.atmsearcher.listeners.GoogleApiListener;
import com.cdvdev.atmsearcher.listeners.BackPressedListener;
import com.cdvdev.atmsearcher.listeners.ProgressListener;
import com.cdvdev.atmsearcher.listeners.VolleyListener;
import com.cdvdev.atmsearcher.loaders.DataBaseUpdateLoader;
import com.cdvdev.atmsearcher.models.Atm;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
                                        GoogleApiListener,
                                        CustomLocationListener,
                                        FragmentListener,
                                        VolleyListener,
                                        LoaderManager.LoaderCallbacks,
                                        FloatingActionButton.OnClickListener{

    private static final int UPDATE_LOCATION_INTERVAL = 10000; //milliseconds
    private static final int UPDATE_LOCATION_INTERVAL_FASTEST = UPDATE_LOCATION_INTERVAL / 2;
    private static final String KEY_SHOW_LOCATION_SETTINGS_REQUEST = "atmsearcher.show_location_settings_request";
    private static final String REQUESTS_VOLLEY_TAG = "volley.requests";
    private final static int UPDATE_DB_LOADER_ID = 1;

    private FragmentManager mFm;
    private ActionBar mActionBar;
    private FloatingActionButton mFab;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private boolean mShowLocationSettingsRequest = true;
    private ArrayList<Atm> mTempAtmArrayList;
    private RequestQueue mRequestQueue;
    private boolean isUpdating = false;
    private Handler mHandler = new Handler();
    private boolean mResolvingGoogleApiError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        mActionBar = getSupportActionBar();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (mFab != null) {
            mFab.setOnClickListener(this);
        }

        mFm = getSupportFragmentManager();
        Fragment newFragment;

        //update values from Bundle
        if (savedInstanceState != null) {
             mShowLocationSettingsRequest = savedInstanceState.getBoolean(KEY_SHOW_LOCATION_SETTINGS_REQUEST);
        }

        if (NetworkHelper.isDeviceOnline(getApplicationContext())) {
            mGoogleApiClient = initGoogleApiClient();
            mLocationRequest = initLocationRequest();
        } else {
            newFragment = ErrorFragment.newInstance(
                    getResources().getString(R.string.message_network_off),
                    new CustomIntent(Settings.ACTION_WIRELESS_SETTINGS),
                    getResources().getString(R.string.button_settings)
            );
            FragmentsHelper.createFragment(mFm, newFragment, false);
        }

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
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(REQUESTS_VOLLEY_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_SHOW_LOCATION_SETTINGS_REQUEST, mShowLocationSettingsRequest);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        BackPressedListener listener = null;
        boolean backInFragment = false;

        //implement back pressed listener in fragment
        for (Fragment fragment : mFm.getFragments()) {
            if (fragment instanceof BackPressedListener) {
                listener = (BackPressedListener) fragment;
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
    private void startLocationUpdate() {
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
     * @param location -Location
     */
    private void updateUI(Location location) {
       if (mFm.getFragments() == null) {
            return;
        }
        Fragment fragment = mFm.findFragmentById(R.id.main_container);
        if (fragment != null) {
            if (fragment instanceof AtmListFragment) {
                ((AtmListFragment) fragment).updateFragmentUI(location);
            }
        }
    }

    /**
     * Method for sending Network request for updating ATMs list
     */
    private void doUpdateAtms() {

        if (isUpdating) {
            Toast.makeText(this, getResources().getString(R.string.message_update_waiting), Toast.LENGTH_SHORT).show();
            return;
        }

        isUpdating = true;
        //create URL request with Volley
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                NetworkHelper.ATMS_URL,
                null,
                this,
                this
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return NetworkHelper.getRequestHeaders();
            }
        };
        request.setTag(REQUESTS_VOLLEY_TAG);
        request.setRetryPolicy(new DefaultRetryPolicy(
                NetworkHelper.WAITING_ANSWER_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue = Volley.newRequestQueue(getBaseContext());
        mRequestQueue.add(request);
    }

    /**
     * Helper method for hiding progress bar in some fragments
     * @param isHide - true - hide progress
     */
    private void hideProgress(boolean isHide) {
        isUpdating = false;
        Fragment fragment = mFm.findFragmentById(R.id.main_container);
        if (fragment != null) {
            if (fragment instanceof ProgressListener) {
                ((ProgressListener) fragment).onShowHideProgressBar(!isHide);
            }
        }
    }

    //----- GOOGLE API CONNECTION CALLBACKS

    @Override
    public void onConnected(Bundle bundle) {
     //   Toast.makeText(this, "GoogleApiClient connected!", Toast.LENGTH_SHORT).show();
        if (mCurrentLocation == null) {
            //get last location
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                updateUI(mCurrentLocation);
            }
        }

        Fragment newFragment = AtmListFragment.newInstance();
        FragmentsHelper.createFragment(mFm, newFragment, false);
        startLocationUpdate();
        //start first update only if last update be more than certain time
        if (Utils.isNeedToUpdate(this)) {
            doUpdateAtms();
        }
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

        int errorCode = connectionResult.getErrorCode();

        if (!mResolvingGoogleApiError) {
            if (connectionResult.hasResolution()) {
                try {
                    mResolvingGoogleApiError = true;
                    connectionResult.startResolutionForResult(this, 111);
                } catch (IntentSender.SendIntentException e) {
                    mGoogleApiClient.connect();
                }
            } else {
                GoogleApiErrorsHelper errorsHelper = new GoogleApiErrorsHelper(this, errorCode);
                Fragment newFragment = ErrorFragment.newInstance(
                        errorsHelper.getErrorMessage(),
                        errorsHelper.getIntent(),
                        errorsHelper.getButtonText()
                );
                FragmentsHelper.createFragment(mFm, newFragment, false);
                mResolvingGoogleApiError = true;
            }
        }
    }

    //----- LOCATION CUSTOM CALLBACKS

    @Override
    public void onLocationChanged(Location location) {
        //method called every time, even when location don`t changed. WTF??
        mCurrentLocation = location;
        updateUI(mCurrentLocation);
    }

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

    //--------- VOLLEY CALLBACKS
    @Override
    public void onResponse(JSONObject jsonObject) {
        mTempAtmArrayList = JsonParseHelper.getAtmsList(jsonObject);
        if (mTempAtmArrayList.size() > 0 ) {
            //create loader
            Loader loader = getSupportLoaderManager().initLoader(UPDATE_DB_LOADER_ID, null, this);
            loader.forceLoad();
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(this, getResources().getString(R.string.message_update_failed), Toast.LENGTH_SHORT).show();
        Log.e("ERROR", volleyError.toString());
        updateUI(mCurrentLocation);
        hideProgress(true);
    }

    //--- LOADER CALLBACKS

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case UPDATE_DB_LOADER_ID:
                return DataBaseUpdateLoader.getInstance(this, mTempAtmArrayList);
            default:
                return null;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
      // Log.d(Utils.TAG_DEBUG_LOG, getClass().getSimpleName() + ".onLoaderReset: ОБНОВЛЕНИЕ ПРЕРВАНО");
       switch (loader.getId()) {
           case UPDATE_DB_LOADER_ID:
               updateUI(mCurrentLocation);
               hideProgress(true);
               Toast.makeText(this, getResources().getString(R.string.message_update_reset), Toast.LENGTH_SHORT).show();
               break;
       }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
      // Log.d(Utils.TAG_DEBUG_LOG, getClass().getSimpleName() + ".onLoadFinished: ОБНОВЛЕНИЕ ЗАВЕРШЕНО");
       switch (loader.getId()) {
           case UPDATE_DB_LOADER_ID:
               updateUI(mCurrentLocation);
               hideProgress(true);
               Toast.makeText(this, getResources().getString(R.string.message_update_success), Toast.LENGTH_SHORT).show();
               getSupportLoaderManager().destroyLoader(UPDATE_DB_LOADER_ID);
               //save datetime of success update
               Utils.saveLastUpdateTime(this);
               break;
       }
    }

    //--- FAB CALLBACK

    @Override
    public void onClick(View view) {
        if (mFab == null) {
            return;
        }

        Fragment fragment = mFm.findFragmentById(R.id.main_container);
        if (fragment != null) {
            if (fragment instanceof FabListener) {
                ((FabListener) fragment).onFabClick();
            }
        }
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

    @Override
    public boolean onGetUpdateProgress() {
        return isUpdating;
    }

    @Override
    public void onRefreshData() {
        doUpdateAtms();
    }

    @Override
    public void onShowFab(final int srcResId) {
        if (mFab == null) {
            return;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float fabHeight = mFab.getHeight();
                float fabMargin = mFab.getResources().getDimension(R.dimen.fab_margin);

                //if FAB been showed earlier
                if (mFab.getTranslationY() == 0) {
                    return;
                }

                mFab.setTranslationY(fabHeight + fabMargin); //from

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mFab.setImageDrawable(getApplicationContext().getResources().getDrawable(srcResId, getApplicationContext().getTheme()));
                } else {
                    mFab.setImageDrawable(getApplicationContext().getResources().getDrawable(srcResId));
                }

                //mFab.show();
                mFab
                        .animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(2)) /*to*/
                        .start();

            }
        }, 200);

    }

    @Override
    public void onHideFab() {

         if (mFab == null) {
             return;
         }

        mHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        float fabHeight = mFab.getHeight();
                        float fabMargin = mFab.getResources().getDimension(R.dimen.fab_margin);

                        if (mFab.getTranslationY() == fabHeight + fabMargin) {
                            return;
                        }
                        mFab.setTranslationY(0); //from
                        mFab
                                .animate()
                                .translationY(fabHeight + fabMargin) /*to*/
                                .setInterpolator(new DecelerateInterpolator(2))
                                .start();
                    }
                }, 100
        );
    }

    /**
     * Callback method for activity result from ErrorFragment
     */
    @Override
    public void onRepeatConnect() {

        if (NetworkHelper.isDeviceOnline(getApplicationContext())) {
            mGoogleApiClient = initGoogleApiClient();
            mLocationRequest = initLocationRequest();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
                checkLocationSettings();
            }
        }

    }

}
