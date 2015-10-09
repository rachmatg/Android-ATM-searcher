package com.cdvdev.atmsearcher.helpers;

import android.location.Location;

import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

public class Utils {

    public static final String TAG_DEBUG_LOG = "DEBUG";
    public static final String TAG_ERROR_LOG = "ERROR";

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
