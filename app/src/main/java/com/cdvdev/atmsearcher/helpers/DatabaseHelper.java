package com.cdvdev.atmsearcher.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import java.util.ArrayList;

/**
 * Helper class for working with SQLite DB
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    final static String DB_NAME = "atm.searcher";
    final static int DB_VERSION = 1;
    final String TABLE_ATMS = "atms";

    final String COLUMN_ATM_ID = "_id";
    final String COLUMN_BANK_NAME = "bank_name";
    final String COLUMN_ATM_NAME = "name";
    final String COLUMN_ATM_COUNTRY = "country";
    final String COLUMN_ATM_CITY_ID = "city_id";
    final String COLUMN_ATM_CITY = "city";
    final String COLUMN_ATM_ADDRESS = "address";
    final String COLUMN_ATM_WORKTIME = "worktime";
    final String COLUMN_ATM_LATITUDE = "latitude";
    final String COLUMN_ATM_LONGITUDE = "longitude";

    private static DatabaseHelper sDatabaseHelper;

    public static synchronized DatabaseHelper getInstance(Context context) {
         if (sDatabaseHelper == null) {
             sDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
         }

        return sDatabaseHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_ATMS + " (" +
                        COLUMN_ATM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_BANK_NAME + " TEXT, " +
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
    public  void updateAtms(ArrayList<Atm> atms) {

        try {
            Log.d(Utils.TAG_DEBUG_LOG, "start insert/update");

            SQLiteDatabase db = getWritableDatabase();
            Atm atm;
            ContentValues cv;
            String whereAtmsIn = "";

            for (int i = 0, j = atms.size(); i < j; i++) {

                atm = atms.get(i);
                cv = new ContentValues();
                int updateRows;

                //skip empty names and kiosks
                if (atm.getName().equals("") || atm.getName().contains("K10")) {
                    continue;
                }

                cv.put(COLUMN_BANK_NAME, atm.getBankName());
                cv.put(COLUMN_ATM_NAME, atm.getName());
                cv.put(COLUMN_ATM_COUNTRY, atm.getCountry());
                cv.put(COLUMN_ATM_CITY_ID, atm.getCityId());
                cv.put(COLUMN_ATM_CITY, atm.getCity());
                cv.put(COLUMN_ATM_ADDRESS, atm.getAddress());
                cv.put(COLUMN_ATM_WORKTIME, atm.getWorktime());
                cv.put(COLUMN_ATM_LATITUDE, atm.getLocation().getLatitude());
                cv.put(COLUMN_ATM_LONGITUDE, atm.getLocation().getLongitude());

                updateRows = db.update(
                        TABLE_ATMS,
                        cv,
                        COLUMN_ATM_NAME + "=?",
                        new String[]{atm.getName()}
                );
                //if no ATM in DB, insert it
                if (updateRows == 0) {
                    db.insert(
                            TABLE_ATMS,
                            null,
                            cv
                    );
                }

                if (whereAtmsIn.equals("")) {
                    whereAtmsIn = "'" + atm.getName() + "'";
                } else {
                    whereAtmsIn = whereAtmsIn + ", '" + atm.getName() + "'";
                }
            }

            //if no ATMs in answer, remove them from DB
            if (!whereAtmsIn.equals("")) {
                db.execSQL(
                        String.format("DELETE FROM " + TABLE_ATMS + " WHERE " + COLUMN_ATM_NAME + " NOT IN (%s)", whereAtmsIn)
                );
            }

            db.close();

            Log.d(Utils.TAG_DEBUG_LOG, "end insert/update");

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }


    /**
     * Method for getting all ATMs
     *
     * @return
     */
    public ArrayList<Atm> getAllAtms() {
        return getAtmsArray("");
    }

    /**
     * Methos for getting searchable ATM
     *
     * @param searchString - query string
     * @return ArrayList<Atm>
     */
    public ArrayList<Atm> getSearchAtm(String searchString){
        return getAtmsArray(searchString);
    }

    /**
     * Helper method for creating ATM list array
     *
     * @param searchString - query string
     * @return ArrayList<Atm>
     */
    private ArrayList<Atm> getAtmsArray(String searchString) {
        ArrayList<Atm> atms = new ArrayList<>();
        Atm atm;
        String address;

        searchString = String.valueOf(searchString).toLowerCase();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = getAtmsCursor(searchString);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    address = getStringFieldValue(cursor, COLUMN_ATM_ADDRESS).toLowerCase();
                    if (address.contains(searchString)) {
                        atm = new Atm();
                        atm.setBankName(getStringFieldValue(cursor, COLUMN_BANK_NAME));
                        atm.setName(getStringFieldValue(cursor, COLUMN_ATM_NAME));
                        atm.setCountry(getStringFieldValue(cursor, COLUMN_ATM_COUNTRY));
                        atm.setCityId(getIntFieldValue(cursor, COLUMN_ATM_CITY_ID));
                        atm.setCity(getStringFieldValue(cursor, COLUMN_ATM_CITY));
                        atm.setAddress(getStringFieldValue(cursor, COLUMN_ATM_ADDRESS));
                        atm.setWorktime(getStringFieldValue(cursor, COLUMN_ATM_WORKTIME));
                        atm.setLocation(new LocationPoint(
                                getDoubleFieldValue(cursor, COLUMN_ATM_LATITUDE),
                                getDoubleFieldValue(cursor, COLUMN_ATM_LONGITUDE)
                        ));
                        atms.add(atm);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();

        return atms;
    }

    /**
     * Method for getting ATMs
     *
     * @return Cursor
     */
    private Cursor getAtmsCursor(String searchString) {

        //LOWER, UPPER DON`T WORKING WITH CIRILLIC AND "LIKE"
        return getReadableDatabase().query(
                TABLE_ATMS,
                null,
                COLUMN_ATM_LATITUDE + " > 0 AND " +
                        COLUMN_ATM_LONGITUDE + " > 0",
                        //(!searchString.equals("") ? " AND lower(" + COLUMN_ATM_ADDRESS + ") LIKE ?" : ""), //where
                null,//(!searchString.equals("") ? new String[]{"%" + String.valueOf(searchString) + "%"} : null), //where args (DON`T WORKING WITH CASE-INSENSITIVE)
                null, //group by
                null, //having
                COLUMN_ATM_NAME + " ASC" //order by
        );
    }

    /**
     * Helper method for getting field value
     *
     * @param cursor     Cursor
     * @param columnName String
     * @return String
     */
    private String getStringFieldValue(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    /**
     * Helper method for getting field value
     *
     * @param cursor     Cursor
     * @param columnName String
     * @return Integer
     */
    private int getIntFieldValue(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    /**
     * Helper method for getting field value
     *
     * @param cursor     Cursor
     * @param columnName String
     * @return Double
     */
    private double getDoubleFieldValue(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }
}
