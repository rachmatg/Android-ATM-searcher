package com.cdvdev.atmsearcher.listeners;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface VolleyListener extends Response.Listener<JSONObject>,
                                        Response.ErrorListener{
    @Override
    void onResponse(JSONObject jsonObject);

    @Override
    void onErrorResponse(VolleyError volleyError);
}
