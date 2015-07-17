package com.cdvdev.atmsearcher.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

public class NetworkLoaderManager implements LoaderManager.LoaderCallbacks<String> {

    private Context mContext;

    public NetworkLoaderManager(Context context){
        mContext = context;
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Loader<String> loader = null;

        if (id == 1) {
            loader = new NetworkLoaderTask(mContext);
            Log.d("DEBUG", "onCreateLoader(): loader " + loader);
        }

        return loader;
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        Log.d("DEBUG", "onLoaderReset()");
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d("DEBUG", "onLoadFinished(): " + data);
    }
}
