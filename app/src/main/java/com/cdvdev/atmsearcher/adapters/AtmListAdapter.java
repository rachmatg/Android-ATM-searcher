package com.cdvdev.atmsearcher.adapters;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cdvdev.atmsearcher.App;
import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.fragments.AtmMapFragment;
import com.cdvdev.atmsearcher.models.Atm;
import com.google.android.gms.analytics.HitBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Adapter for Atm list
 */
public class AtmListAdapter extends ArrayAdapter<Atm> {

    private Context mContext;

    public AtmListAdapter(Context context, ArrayList<Atm> atms) {
        super(context, 0, atms);
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.item_atmlist, null);
        }

        final Atm atm = getItem(position);

        TextView bankName = (TextView) convertView.findViewById(R.id.bank_name);
        if (bankName != null) {
            bankName.setText(atm.getBankName());
        }

        TextView atmAddress = (TextView) convertView.findViewById(R.id.atm_address);
        if (atmAddress != null) {
            atmAddress.setText(atm.getAddress());
        }

        TextView atmCity = (TextView) convertView.findViewById(R.id.atm_city);
        if (atmCity != null) {
            atmCity.setText(atm.getCity());
        }

        TextView atmDistance = (TextView) convertView.findViewById(R.id.atm_distance);
        if (atmDistance != null) {
            double distance = new BigDecimal(atm.getDistance()).setScale(3, RoundingMode.UP).doubleValue();
            if (distance > 0) {
                String distanceText =  Double.toString(atm.getDistance());
                atmDistance.setText(distanceText);
                convertView.findViewById(R.id.layout_distance).setVisibility(View.VISIBLE);
            }
        }


        FrameLayout showPlace = (FrameLayout) convertView.findViewById(R.id.ic_place_container);
        if (showPlace != null) {
            showPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm =((FragmentActivity)mContext).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(
                            R.id.main_container,
                            AtmMapFragment.newInstance(atm)
                    )
                            .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null);
                    ft.commit();

                    //analytics
                    App.sTracker.setScreenName("Fragment AtmsList");
                    App.sTracker.send(
                            new HitBuilders.EventBuilder()
                                    .setCategory(App.sGACategoryUX)
                                    .setAction("Click list item icon: " + atm.getBankName())
                                    .build()
                    );
                }
            });
        }


        return convertView;
    }
}
