package com.cdvdev.atmsearcher.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NetworkLoaderTask extends AsyncTaskLoader<String> {

    public NetworkLoaderTask(Context context){
        super(context);
    }

    @Override
    public String loadInBackground() {
        Log.d("DEBUG", "start loadInBackground()");

        //for debugging
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Log.d("DEBUG", e.getMessage());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a", Locale.getDefault());

        //TODO: send request to network

        //TODO: save data to DB

      return sdf.format(new Date());
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d("DEBUG", hashCode() + " onStartLoading()");
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.d("DEBUG", hashCode() + " onStopLoading()");
    }

    @Override
    protected void onReset() {
        super.onReset();
        Log.d("DEBUG", hashCode() + " onReset()");
    }

}
