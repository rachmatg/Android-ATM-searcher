package com.cdvdev.atmsearcher.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Service for getting data from server and inserted it to DB
 */
public class UpdateDataService extends IntentService {

    public static final String KEY_RECEIVER_NAME = "receiver";
    public static final String KEY_VALUE = "ResultValue";

    public UpdateDataService() {
        super("update-data-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("DEBUG", "UpdateDataService.onHandleIntent()");

        ResultReceiver resultReceiver = intent.getParcelableExtra(KEY_RECEIVER_NAME);

        Bundle bundle = new Bundle();
        bundle.putString(KEY_VALUE, "This is result from service");

        resultReceiver.send(Activity.RESULT_OK, bundle);

    }

}
