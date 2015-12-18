package com.cdvdev.atmsearcher.helpers;

import android.content.Context;
import android.view.View;

import com.cdvdev.atmsearcher.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Class for implementing advertising banner
 *
 * @author Dmitriy V. Chernysh (dmitriy.chernysh@gmail.com)
 *         Created on 10.12.15.
 */
public class AdsHelper {

    private static final String TEST_DEVICE_ID = "45985D2F082A018C53FD4C794813997A";

    /**
     * Method for implemented banner to array list
     *
     * @param context Context
     * @return View
     */
    public static View initBanner(final Context context) {
        final View view =  View.inflate(context, R.layout.item_admob, null);
        AdView adView = (AdView) view.findViewById(R.id.ad_banner);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(TEST_DEVICE_ID)
                .build();

        adView.loadAd(adRequest);
        return view;
    }

}
