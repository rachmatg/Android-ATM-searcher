package com.cdvdev.atmsearcher.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.adapters.AtmListAdapter;
import com.cdvdev.atmsearcher.helpers.DatabaseHelper;
import com.cdvdev.atmsearcher.helpers.Utils;
import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Fragment for creating list of ATMS
 */
public class AtmListFragment extends Fragment{

    private ArrayList<Atm> mAtmArrayList;

    public static Fragment newInstance(){
        return new AtmListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_atmlist, container, false);

        RecyclerView atmList = (RecyclerView) view.findViewById(R.id.atm_list);

        //current location
        LocationPoint currentLocation = new LocationPoint(48.462468, 35.036538);
        //get ATMs list from DB
        mAtmArrayList = new DatabaseHelper(getActivity()).getAllAtms();
        //calculate and set distance for each ATM
        mAtmArrayList = Utils.addDistanceToAtms(mAtmArrayList, currentLocation);
        //sorted ArrayList by distance
        Collections.sort(mAtmArrayList, new Utils.LocationComparator());


        //setup recycler view adapter
        AtmListAdapter adapter = new AtmListAdapter(
                getActivity(),
                mAtmArrayList
        );
        atmList.setAdapter(adapter);

        //setup items position
        atmList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

}
