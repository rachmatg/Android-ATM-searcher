package com.cdvdev.atmsearcher.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cdvdev.atmsearcher.helpers.DatabaseHelper;
import com.cdvdev.atmsearcher.helpers.JsonParseHelper;
import com.cdvdev.atmsearcher.helpers.NetworkHelper;
import com.cdvdev.atmsearcher.models.Atm;
import org.json.JSONObject;

import java.util.ArrayList;

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

                        int respCode = JsonParseHelper.getRespCode(jsonObject);

                        //if response success , parse Json and update DB
                        if (respCode == NetworkHelper.SUCCESS_RESP_CODE) {
                            //get atms
                            ArrayList<Atm> atms = JsonParseHelper.getAtmsList(jsonObject);

                            //save into DB
                            try {
                                DatabaseHelper db = DatabaseHelper.getInstance(UpdateDataService.this);
                                db.insertOrUpdateAtms(atms);
                            } catch (Exception e) {
                                resultReceiver.send(NetworkHelper.FAILED_RESP_CODE, null);
                            }

                        }

                        Log.d("DEBUG", "start send resp code");
                        resultReceiver.send(respCode, null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                      //  VolleyLog.e("ERROR: ", volleyError.toString() + " : " + volleyError.getMessage());
                        resultReceiver.send(NetworkHelper.FAILED_RESP_CODE, null);
                        Log.e("ERROR", "onErrorResponse: " + volleyError.toString());
                    }
                });


        //add request to que
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }

}
