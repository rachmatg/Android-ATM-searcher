package com.cdvdev.atmsearcher.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

        final Atm atm = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.item_atmlist, null);
            viewHolder.bankName = (TextView) convertView.findViewById(R.id.bank_name);
            viewHolder.atmAddress = (TextView) convertView.findViewById(R.id.atm_address);
            viewHolder.atmCity =  (TextView) convertView.findViewById(R.id.atm_city);
            viewHolder.atmDistance = (TextView) convertView.findViewById(R.id.atm_distance);
            viewHolder.layoutShowPlace = (FrameLayout) convertView.findViewById(R.id.ic_place_container);
            viewHolder.layoutDistance = (LinearLayout) convertView.findViewById(R.id.layout_distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (viewHolder.bankName != null) {
            viewHolder.bankName.setText(atm.getBankName());
        }

        if (viewHolder.atmAddress != null) {
            viewHolder.atmAddress.setText(atm.getAddress());
        }

        if (viewHolder.atmCity != null) {
            viewHolder.atmCity.setText(atm.getCity());
        }

        if (viewHolder.atmDistance != null) {
            double distance = new BigDecimal(atm.getDistance()).setScale(3, RoundingMode.UP).doubleValue();
            if (distance > 0) {
                String distanceText =  Double.toString(atm.getDistance());
                viewHolder.atmDistance.setText(distanceText);
                viewHolder.layoutDistance.setVisibility(View.VISIBLE);
            }
        }


        if (viewHolder.layoutShowPlace != null) {
            viewHolder.layoutShowPlace.setOnClickListener(new View.OnClickListener() {
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

    private static class ViewHolder {
        TextView bankName, atmAddress, atmCity, atmDistance;
        FrameLayout layoutShowPlace;
        LinearLayout layoutDistance;
    }
}
