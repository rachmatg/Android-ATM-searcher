package com.cdvdev.atmsearcher.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
public class AtmListFragment extends ListFragment{

    private ArrayList<Atm> mAtmArrayList;
    private ArrayAdapter<Atm> mAdapter;

    public static Fragment newInstance(){
        return new AtmListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAtmArrayList = getAtmArrayList();

        mAdapter = new AtmListAdapter(getActivity(), mAtmArrayList);
        setListAdapter(mAdapter);

        //save fragment object
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_atmlist, container, false);
    }

    private ArrayList<Atm> getAtmArrayList(){
        ArrayList<Atm> atms;

        //TODO: need to define current location
        //current location
        LocationPoint currentLocation = new LocationPoint(48.462468, 35.036538);
        //get ATMs list from DB
        atms = new DatabaseHelper(getActivity()).getAllAtms();
        //calculate and set distance for each ATM
        atms = Utils.addDistanceToAtms(atms, currentLocation);
        //sorted ArrayList by distance
        Collections.sort(atms, new Utils.LocationComparator());

        return atms;
    }

    /**
     * Method for updating ATM`s list
     */
    public void updateList(){
        mAtmArrayList.clear();
        mAtmArrayList.addAll(getAtmArrayList());
        mAdapter.notifyDataSetChanged();
    }

}
