package com.cdvdev.atmsearcher.helpers;

import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParseHelper {

    private static final String KEY_ATMS_ARRAY = "atms";
    private static final String KEY_BANK_NAME = "bank_name";
    private static final String KEY_ATM_NAME = "name";
    private static final String KEY_ATM_COUNTRY = "country";
    private static final String KEY_ATM_CITY_NAME = "city";
    private static final String KEY_ATM_ADDRESS = "address";
    private static final String KEY_ATM_WORKTIME = "worktime";
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
                atm.setBankName(atmJsonObject.has(KEY_BANK_NAME) ? atmJsonObject.getString(KEY_BANK_NAME) : "");
                atm.setName(atmJsonObject.has(KEY_ATM_NAME) ? atmJsonObject.getString(KEY_ATM_NAME) : "");
                atm.setCountry(atmJsonObject.has(KEY_ATM_COUNTRY) ? atmJsonObject.getString(KEY_ATM_COUNTRY) : "");
                atm.setCity(atmJsonObject.has(KEY_ATM_CITY_NAME) ? atmJsonObject.getString(KEY_ATM_CITY_NAME) : "");
                atm.setAddress(atmJsonObject.has(KEY_ATM_ADDRESS) ? atmJsonObject.getString(KEY_ATM_ADDRESS) : "");
                atm.setWorktime(atmJsonObject.has(KEY_ATM_WORKTIME) ? atmJsonObject.getString(KEY_ATM_WORKTIME) : "");
                latitude = atmJsonObject.has(KEY_ATM_LATITUDE) ? atmJsonObject.getDouble(KEY_ATM_LATITUDE) : 0;
                longitude = atmJsonObject.has(KEY_ATM_LONGITUDE) ? atmJsonObject.getDouble(KEY_ATM_LONGITUDE) : 0;
                atm.setLocation(new LocationPoint(latitude,longitude));
                atms.add(atm);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return atms;
    }

}
