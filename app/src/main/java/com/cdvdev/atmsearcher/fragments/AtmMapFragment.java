package com.cdvdev.atmsearcher.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.listeners.FragmentListener;
import com.cdvdev.atmsearcher.models.Atm;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class AtmMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String KEY_ATM_OBJECT = "atmsearcher.atm";
    private Atm mAtm;
    private FragmentListener mFragmentListener;

    public static Fragment newInstance(Atm atm) {
        Fragment fragment = new AtmMapFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ATM_OBJECT, atm);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(ArrayList<Atm> atmArrayList) {
        Fragment fragment = new AtmMapFragment();
        //Bundle bundle = new Bundle();
       // bundle.putSerializable(KEY_ATM_OBJECT, atm);
        //fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentListener = (FragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be implement FragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAtm = (Atm) getArguments().getSerializable(KEY_ATM_OBJECT);
        }
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_atm_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentListener.onChangeAppBarTitle(R.string.title_map_fragment);
        mFragmentListener.onSetHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (mAtm != null) {
            LatLng atmLocation = new LatLng(mAtm.getLocation().getLatitude(), mAtm.getLocation().getLongitude());

            googleMap.setMyLocationEnabled(true);
            Marker marker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(atmLocation)
                            .title(mAtm.getName() + ", " + mAtm.getBankName())
                            .snippet(mAtm.getAddress())

            );
            //show marker always
            marker.showInfoWindow();

            //move camera
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atmLocation, 10));
            //animate zoom in camera
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 3000, null);
        }
    }
}
