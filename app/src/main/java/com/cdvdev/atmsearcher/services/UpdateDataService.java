package com.cdvdev.atmsearcher.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cdvdev.atmsearcher.helpers.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service for getting data from server and inserted it to DB
 */
public class UpdateDataService extends IntentService {

    public static final String KEY_RECEIVER_NAME = "receiver";

    public UpdateDataService() {
        super("update-data-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("DEBUG", "UpdateDataService.onHandleIntent()");

        final ResultReceiver resultReceiver = intent.getParcelableExtra(KEY_RECEIVER_NAME);

        //create request with Volley
        JsonObjectRequest request = new JsonObjectRequest(
                NetworkHelper.ATMS_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        int respCode = -1;

                        try{
                            respCode = jsonObject.getInt(NetworkHelper.KEY_RESP_CODE);
                        } catch (JSONException e) {
                            Log.e("ERROR", e.getMessage());
                        }

                        //if response success , parse Json and update DB
                        if (respCode == NetworkHelper.SUCCESS_RESP_CODE) {
                            //TODO: parse JSON and update DB
                            //...
                        }

                        /*
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_RESP_CODE, result);
                        */
                        resultReceiver.send(respCode, null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        VolleyLog.e("ERROR: ", volleyError.getMessage());
                        resultReceiver.send(NetworkHelper.FAILED_RESP_CODE, null);
                    }
                });


        //add request to que
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }

}
