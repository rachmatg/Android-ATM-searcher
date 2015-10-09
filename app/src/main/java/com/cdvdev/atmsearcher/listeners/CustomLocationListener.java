package com.cdvdev.atmsearcher.listeners;

import android.location.Location;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsResult;

public interface CustomLocationListener extends LocationListener,
                                                ResultCallback<LocationSettingsResult> {

    @Override
    void onLocationChanged(Location location);

    @Override
    void onResult(LocationSettingsResult locationSettingsResult);
}
