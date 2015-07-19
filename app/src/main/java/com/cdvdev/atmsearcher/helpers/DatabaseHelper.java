package com.cdvdev.atmsearcher.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cdvdev.atmsearcher.models.Atm;

import java.util.ArrayList;

/**
 * Helper class for working with SQLite DB
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    final static String DB_NAME = "atm.searcher";
    final static int DB_VERSION = 1;
    final String TABLE_ATMS = "atms";

    final String COLUMN_ATM_ID = "_id";
    final String COLUMN_ATM_NAME = "name";
    final String COLUMN_ATM_COUNTRY = "country";
    final String COLUMN_ATM_CITY_ID = "city_id";
    final String COLUMN_ATM_CITY = "city";
    final String COLUMN_ATM_ADDRESS = "address";
    final String COLUMN_ATM_WORKTIME = "worktime";
    final String COLUMN_ATM_LATITUDE = "latitude";
    final String COLUMN_ATM_LONGITUDE = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DEBUG", "DatabaseHelper.onCreate()");

        db.execSQL(
                "CREATE TABLE " + TABLE_ATMS + " (" +
                        COLUMN_ATM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_ATM_NAME + " TEXT, " +
                        COLUMN_ATM_COUNTRY + " TEXT, " +
                        COLUMN_ATM_CITY_ID + " INTEGER," +
                        COLUMN_ATM_CITY + " TEXT, " +
                        COLUMN_ATM_ADDRESS + " TEXT, " +
                        COLUMN_ATM_WORKTIME + " TEXT, " +
                        COLUMN_ATM_LATITUDE + " REAL, " +
                        COLUMN_ATM_LONGITUDE + " REAL" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Method for insert or update ATMs in DB
     */
    public void insertOrUpdateAtms(ArrayList<Atm> atms) {

        SQLiteDatabase db = getWritableDatabase();
        Atm atm;
        ContentValues cv;

        for (int i = 0, j = atms.size(); i < j; i++) {

            atm = atms.get(i);
            cv = new ContentValues();

            //skip empty names and kiosks
           if (atm.getName().equals("") || atm.getName().contains("K10")) {
                continue;
            }

            cv.put(COLUMN_ATM_NAME, atm.getName());
            cv.put(COLUMN_ATM_COUNTRY, atm.getCountry());
            cv.put(COLUMN_ATM_CITY_ID, atm.getCityId());
            cv.put(COLUMN_ATM_CITY, atm.getCity());
            cv.put(COLUMN_ATM_ADDRESS, atm.getAddress());
            cv.put(COLUMN_ATM_WORKTIME, atm.getWorktime());
            cv.put(COLUMN_ATM_LATITUDE, atm.getLatitude());
            cv.put(COLUMN_ATM_LONGITUDE, atm.getLongitude());

            if (isAtmRecordExist(atm)) {
                db.update(
                        TABLE_ATMS,
                        cv,
                        COLUMN_ATM_NAME + "=?",
                        new String[]{atm.getName()}
                );
            } else {
                db.insert(
                        TABLE_ATMS,
                        null,
                        cv
                );
            }

        }

        db.close();

    }

    /**
     * Method for checking existence of record of ATM
     *
     * @param atm Atm
     * @return Boolean
     */
    private boolean isAtmRecordExist(Atm atm) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ATMS,
                new String[]{COLUMN_ATM_NAME},
                COLUMN_ATM_NAME + "=?",
                new String[]{atm.getName()},
                null,
                null,
                null
        );

        int records = cursor.getCount();
        cursor.close();

    //    Log.d("DEBUG", records > 0 ? atm.getName() + " is exist" : atm.getName() + " is not exist");

        return records > 0;
    }
}
