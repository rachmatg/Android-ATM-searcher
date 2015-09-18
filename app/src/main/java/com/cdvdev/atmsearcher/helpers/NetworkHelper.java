package com.cdvdev.atmsearcher.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Class for working with network
 */
public class NetworkHelper {

    //public static final String ATMS_URL = "https://www.creditdnepr.com/bsclient/v1/cgi/bsi.dll?T=cdb_api.getAtms";
    public static final String ATMS_URL = "https://api.myjson.com/bins/34zse";
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
