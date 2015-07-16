package com.cdvdev.atmsearcher.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class for working with network
 */
public class NetworkHelper {

    /**
     * Method for checking network connection
     * @param context - application context
     * @return true - device online
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }



}
