package com.cdvdev.atmsearcher.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class for working with network
 */
public class NetworkHelper {

    public static final String PARSE_API_VERSION = "1";
    public static final String PARSE_APPLICATION_ID = "shwbK8hzXFEK3CDQO5hJ2Sy1pz6asmo7ZqRqVJKA";
    public static final String PARSE_API_KEY = "mlmog7WlQUB2KbNbWMRXb918rruarYDs6Q0nmfMY";
    public static final String ATMS_URL = "https://api.parse.com/" + PARSE_API_VERSION + "/classes/atms_list";
    public static final int WAITING_ANSWER_TIMEOUT = 10000;


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

    /**
     * Method which creating headers for HTTP request to Parse.com
     * @return HashMap
     */
    public static HashMap<String, String> getRequestHeaders(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Parse-Application-Id", PARSE_APPLICATION_ID);
        headers.put("X-Parse-REST-API-Key", PARSE_API_KEY);
        return headers;
    }

}
