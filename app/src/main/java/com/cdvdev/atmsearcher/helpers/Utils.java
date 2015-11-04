package com.cdvdev.atmsearcher.helpers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Utils {

    public static final String TAG_DEBUG_LOG = "DEBUG";
    public static final String TAG_ERROR_LOG = "ERROR";
    private static final String KEY_LAST_UPDATE_ATMS = "atmsearcher.lastupdate";
    private static final int UPDATE_PERIOD = 60 * 60 * 1000; //milliseconds

    /**
     * Method for set distance for each Atm
     * @param atms Atm
     */
    public static ArrayList<Atm> addDistanceToAtms(ArrayList<Atm> atms, LocationPoint currentLocation) {

        for (int i = 0, j = atms.size(); i < j; i++) {
            Atm atm = atms.get(i);
            atm.setDistance(Utils.calcDistance(currentLocation, atm.getLocation()));
        }

        return atms;
    }

    /**
     * Method for calculate distance between two location points
     * @param startLocation Current location point
     * @param endLocation ATM location point
     * @return double distance in kilometers
     */
    private static double calcDistance(LocationPoint startLocation, LocationPoint endLocation) {

        float[] results = {0};
        Location.distanceBetween(
                startLocation.getLatitude(),
                startLocation.getLongitude(),
                endLocation.getLatitude(),
                endLocation.getLongitude(),
                results
        );

        return Utils.roundDecimalValue( (double) results[0] / 1000, 3);
    }


    /**
     * Method for round decimal value
     * @param value double value
     * @param digits discharge
     * @return double value
     */
    public static double roundDecimalValue(double value, int digits){
        return new BigDecimal(""+value).setScale(digits, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * Method for saving last time of ATMs list update
     * @param activity Activity
     */
    public static void saveLastUpdateTime(Activity activity){
        SharedPreferences prefs = activity.getPreferences(0);
        SharedPreferences.Editor editor = prefs.edit();
        Date dateTime = new Date();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        editor.putString(KEY_LAST_UPDATE_ATMS, dateFormat.format(dateTime));
        editor.apply();
    }

    /**
     * Helper method for checking that ATMs last update be more then certain time period
     *
     * @param activity Activity
     * @return true - need to update
     */
    public static boolean isNeedToUpdate(Activity activity){
        Boolean b = true;

        SharedPreferences prefs = activity.getPreferences(0);
        String saveTime = prefs.getString(KEY_LAST_UPDATE_ATMS, null);

        if (saveTime != null) {
            try {
                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                Date lastDateTime = dateFormat.parse(saveTime);
                Date currentDateTime = dateFormat.parse(dateFormat.format(new Date()));

                if (currentDateTime.getTime() - lastDateTime.getTime() < UPDATE_PERIOD) {
                    b = false;
                }
                //if ATMs list is empty in database (e.g. removed database)
                if (DatabaseHelper.getInstance(activity).getAllAtms().size() == 0){
                    b =true;
                }

            } catch (ParseException e) {
                Log.e(Utils.TAG_ERROR_LOG, e.getMessage());
            }
        }

        return b;
    }

    /**
     * Helper class for sort ATMs list by distance (from nearest to farther)
     */
    public static class LocationComparator implements Comparator<Atm>{

        public int compare(Atm lhs, Atm rhs) {
            double start = lhs.getDistance(),
                    end = rhs.getDistance();
            return start > end ? 1 : start == end ? 0 : -1;
        }
    }


}
