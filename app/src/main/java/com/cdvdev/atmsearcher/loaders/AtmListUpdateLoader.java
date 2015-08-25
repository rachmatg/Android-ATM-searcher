package com.cdvdev.atmsearcher.loaders;

import android.content.Context;
import android.util.Log;

import com.cdvdev.atmsearcher.models.Atm;
import java.util.ArrayList;

/**
 * Loader for updating ATM list
 */
public class AtmListUpdateLoader  extends android.support.v4.content.AsyncTaskLoader<ArrayList<Atm>> {

    public AtmListUpdateLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Atm> loadInBackground() {
        Log.d("DEBUG", "loadInBackground");
        //TODO: get data from network

        //TODO: update DB

        //for debug
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return new ArrayList<Atm>();
    }
}
