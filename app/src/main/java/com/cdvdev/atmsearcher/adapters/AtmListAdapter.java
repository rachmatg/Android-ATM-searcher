package com.cdvdev.atmsearcher.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.models.Atm;

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
            atmAddress.setText(atm.getAddress() + ", " + atm.getCity());
        }

        TextView atmDistance = (TextView) convertView.findViewById(R.id.atm_distance);
        if (atmDistance != null) {
            double distance = new BigDecimal(atm.getDistance()).setScale(3, RoundingMode.UP).doubleValue();
            atmDistance.setText( (distance > 0 ? Double.toString(atm.getDistance()) : "--.--") + " " + mContext.getResources().getString(R.string.label_kilometers));
        }

        FrameLayout showPlace = (FrameLayout) convertView.findViewById(R.id.ic_place_container);
        if (showPlace != null) {
            showPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double lat = atm.getLocation().getLatitude();
                    double lon = atm.getLocation().getLongitude();

                    Toast.makeText(mContext, "Lat " + lat + " Lon " + lon, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + atm.getName() + ")"));
                    mContext.startActivity(intent);
                }
            });
        }


        return convertView;
    }
}
