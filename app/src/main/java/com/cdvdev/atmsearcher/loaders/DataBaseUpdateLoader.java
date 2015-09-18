package com.cdvdev.atmsearcher.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.cdvdev.atmsearcher.helpers.DatabaseHelper;
import com.cdvdev.atmsearcher.models.Atm;

import java.util.ArrayList;


public class DataBaseUpdateLoader extends AsyncTaskLoader {

    private static DataBaseUpdateLoader sLoader;
    private Context mContext;
    private ArrayList<Atm> mAtms;

    private DataBaseUpdateLoader(Context context, ArrayList<Atm> atms) {
        super(context);
        mContext = context;
        mAtms = atms;
    }

    public static DataBaseUpdateLoader getInstance(Context context, ArrayList<Atm> atms){
         if(sLoader == null) {
             sLoader = new DataBaseUpdateLoader(context, atms);
         }

        return sLoader;
    }

    @Override
    public Object loadInBackground() {

        DatabaseHelper db = DatabaseHelper.getInstance(mContext);
        try {
            db.insertOrUpdateAtms(mAtms);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

        return null;
    }
}
