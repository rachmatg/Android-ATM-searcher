package com.cdvdev.atmsearcher.listeners;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

public interface GoogleApiListener extends GoogleApiClient.ConnectionCallbacks,
                                           GoogleApiClient.OnConnectionFailedListener{
    @Override
    void onConnected(Bundle bundle);

    @Override
    void onConnectionSuspended(int i);

    @Override
    void onConnectionFailed(ConnectionResult connectionResult);
}
