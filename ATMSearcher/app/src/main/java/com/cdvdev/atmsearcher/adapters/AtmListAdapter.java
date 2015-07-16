package com.cdvdev.atmsearcher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    }

    @Override
    public AtmListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        //inflate view for list item
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_atmlist, viewGroup, false);

        return new AtmListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AtmListAdapter.ViewHolder viewHolder, int i) {
        Atm atm = mAtms.get(i);
        viewHolder.mAtmName.setText(atm.getName());

    }

    @Override
    public int getItemCount() {
        return mAtms.size();
    }

    /**
     * ViewHolder for RecyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mAtmName;

        public ViewHolder(View itemView){
            super(itemView);
            mAtmName = (TextView) itemView.findViewById(R.id.atm_name);
        }
    }
}
