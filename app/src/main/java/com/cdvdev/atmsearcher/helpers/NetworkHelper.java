package com.cdvdev.atmsearcher.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class for working with network
 */
public class NetworkHelper {

    public static final String ATMS_URL = "https://www.creditdnepr.com/bsclient/v1/cgi/bsi.dll?T=cdb_api.getAtms";
    public static final String KEY_RESP_CODE = "respcode";
    public static final int SUCCESS_RESP_CODE = 0;
    public static final int FAILED_RESP_CODE = 1;

    /**
     * Method for checking network connection
     *
     * @param context - application context
     * @return true - device online
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }
}
