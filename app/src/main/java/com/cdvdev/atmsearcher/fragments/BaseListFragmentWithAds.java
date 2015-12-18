package com.cdvdev.atmsearcher.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.helpers.AdsHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

/**
 * Class for implement advertise to header of FragmentList
 *
 * @author Dmitriy V. Chernysh (dmitriy.chernysh@gmail.com)
 *         Created on 17.12.15.
 */
public abstract class BaseListFragmentWithAds extends BaseListFragment {

    private View mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create advertise banner
        mAdView = AdsHelper.initBanner(getContext());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final ListView listView = getListView();

        if (listView.getHeaderViewsCount() == 0) {
            if (mAdView != null) {
                //hide advertise view
                final AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        1 //height
                );
                mAdView.setLayoutParams(params);

                AdView banner = (AdView) mAdView.findViewById(R.id.ad_banner);
                if (banner != null) {
                    //add banner to header of  list
                    listView.addHeaderView(mAdView);
                    banner.setAdListener(
                            new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // show advertise view
                                    params.height = AbsListView.LayoutParams.WRAP_CONTENT;
                                    mAdView.setLayoutParams(params);
                                }
                            }
                    );

                }
            }
        }

        super.onViewCreated(view, savedInstanceState);
    }
}

