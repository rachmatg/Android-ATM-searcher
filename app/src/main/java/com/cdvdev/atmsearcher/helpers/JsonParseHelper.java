package com.cdvdev.atmsearcher.helpers;

import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParseHelper {

    private static final String KEY_RESPONSE_CODE = "respcode";
    private static final String KEY_ATMS_ARRAY = "results";
    private static final String KEY_ATM_NAME = "name";
    private static final String KEY_ATM_COUNTRY = "country";
    private static final String KEY_ATM_CITY_ID = "city_id";
    private static final String KEY_ATM_CITY_NAME = "city_name";
    private static final String KEY_ATM_ADDRESS = "address";
    private static final String KEY_ATM_WORKTIME = "worktime";
    private static final String KEY_ATM_LOCATION = "location";
    private static final String KEY_ATM_LATITUDE = "latitude";
    private static final String KEY_ATM_LONGITUDE = "longitude";


    /**
     * Method for getting array of atms
     *
     * @param jsonObject JsonObject
     * @return ArrayList
     */
    public static ArrayList<Atm> getAtmsList(JSONObject jsonObject) {
        ArrayList<Atm> atms = new ArrayList<>();
        Atm atm;
        JSONObject atmJsonObject;

        try {
            JSONArray array = jsonObject.getJSONArray(KEY_ATMS_ARRAY);
            double latitude, longitude;

            for (int i = 0, j = array.length(); i < j; i++) {

                atmJsonObject = array.getJSONObject(i);
                atm = new Atm();
                atm.setName(atmJsonObject.getString(KEY_ATM_NAME));
                atm.setCountry(atmJsonObject.getString(KEY_ATM_COUNTRY));
                atm.setCityId(atmJsonObject.getInt(KEY_ATM_CITY_ID));
                atm.setCity(atmJsonObject.getString(KEY_ATM_CITY_NAME));
                atm.setAddress(atmJsonObject.getString(KEY_ATM_ADDRESS));
                atm.setWorktime(atmJsonObject.getString(KEY_ATM_WORKTIME));
                latitude = atmJsonObject.getJSONObject(KEY_ATM_LOCATION).getDouble(KEY_ATM_LATITUDE);
                longitude = atmJsonObject.getJSONObject(KEY_ATM_LOCATION).getDouble(KEY_ATM_LONGITUDE);
                atm.setLocation(new LocationPoint(latitude,longitude));
                atms.add(atm);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return atms;
    }

    /**
     * Method for getting response code from server response
     *
     * @param jsonObject JsonObject
     * @return Integer
     */
    /*
    public static int getRespCode(JSONObject jsonObject) {
        int code = -1;

        try {
            code = jsonObject.getInt(KEY_RESPONSE_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return code;
    }
    */
}
