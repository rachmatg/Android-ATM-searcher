package com.cdvdev.atmsearcher.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.models.Atm;

import java.util.ArrayList;

/**
 * Adapter for Atm list
 */
public class AtmListAdapter extends RecyclerView.Adapter<AtmListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Atm> mAtms;

    public AtmListAdapter(Context context, ArrayList<Atm> atms){
       mContext = context;
       mAtms = atms;

       //TODO: list need to be sorted by distance from current location
        //....
        //..
    }

    @Override
    public AtmListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        //inflate view for list item
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_atmlist, viewGroup, false);

        return new AtmListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AtmListAdapter.ViewHolder viewHolder, int i) {
        final Atm atm = mAtms.get(i);
        viewHolder.mAtmName.setText(atm.getName());
        viewHolder.mAtmAddress.setText(atm.getAddress() + ", " + atm.getCity());
        viewHolder.mAtmDistance.setText("0 км");

        viewHolder.mShowPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double lat = atm.getLatitude();
                double lon = atm.getLongitude();

                Toast.makeText(mContext, "Lat " + lat + " Lon " + lon, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + atm.getName() + ")"));
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mAtms.size();
    }

    /**
     * ViewHolder for RecyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mAtmName, mAtmAddress, mAtmDistance;
        public FrameLayout mShowPlace;

        public ViewHolder(View itemView){
            super(itemView);
            mAtmName = (TextView) itemView.findViewById(R.id.atm_name);
            mAtmAddress = (TextView) itemView.findViewById(R.id.atm_address);
            mAtmDistance = (TextView) itemView.findViewById(R.id.atm_distance);
            mShowPlace = (FrameLayout) itemView.findViewById(R.id.ic_place_container);
        }
    }
}
