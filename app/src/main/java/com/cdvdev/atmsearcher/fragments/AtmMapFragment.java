package com.cdvdev.atmsearcher.fragments;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cdvdev.atmsearcher.App;
import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.helpers.DatabaseHelper;
import com.cdvdev.atmsearcher.listeners.FragmentListener;
import com.cdvdev.atmsearcher.models.Atm;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class AtmMapFragment extends Fragment implements OnMapReadyCallback,
                                                                                                           GoogleMap.OnMarkerClickListener,
                                                                                                           GoogleMap.OnCameraChangeListener,
                                                                                                           GoogleMap.OnMyLocationButtonClickListener,
GoogleMap.OnMyLocationChangeListener{

    private static final String KEY_ATM_OBJECT = "atmsearcher.atm";
    private static final int DEFAULT_CAMERA_ZOOM = 15;

    private Atm mSelectedAtm;
    private ArrayList<Atm> mAtmArrayList;
    private FragmentListener mFragmentListener;
    private GoogleMap mGoogleMap;
    private boolean mIsNeedMovedCameraToLocation = true;
    private CameraPosition mSavedCameraPosition;

    public static Fragment newInstance(Atm selectedAtm) {
        Fragment fragment = new AtmMapFragment();
        Bundle bundle = new Bundle();

        if (selectedAtm != null) {
            bundle.putSerializable(KEY_ATM_OBJECT, selectedAtm);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = null;

        try {
            if (context instanceof Activity) {
                activity = (Activity) context;
                mFragmentListener = (FragmentListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be implement FragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAtmArrayList = DatabaseHelper.getInstance(getContext()).getAllAtms();

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(KEY_ATM_OBJECT)) {
                mSelectedAtm = (Atm) getArguments().getSerializable(KEY_ATM_OBJECT);
            }
        }

        setRetainInstance(true);
        setHasOptionsMenu(true);

        //analytics
        App.sTracker.setScreenName("Fragment AtmMap");
        App.sTracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(App.sGACategoryUX)
                        .setAction( mSelectedAtm != null ? "View single ATM on map" : "View all ATMs on map")
                        .build()
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentListener.onChangeAppBarTitle(R.string.title_map_fragment);
        mFragmentListener.onSetHomeAsUpEnabled(true);

        View view = inflater.inflate(R.layout.fragment_atm_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
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

        Atm atm;
        LatLng atmPosition;
        CameraPosition cameraPosition = null;
        Marker marker;

        mGoogleMap = googleMap;

        //show button "my location"
        mGoogleMap.setMyLocationEnabled(true);
        //show zoom controls
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        mGoogleMap.setOnMyLocationChangeListener(this);
        mGoogleMap.setOnCameraChangeListener(this);

        if (mAtmArrayList != null && mAtmArrayList.size() > 0) {
            mIsNeedMovedCameraToLocation = (mSelectedAtm == null);

            for (int i = 0; i < mAtmArrayList.size(); i++) {
               // Log.d(Utils.TAG_DEBUG_LOG, mAtmArrayList.get(i).getName());
                atm = mAtmArrayList.get(i);

               //create marker for atm
                atmPosition = new LatLng(atm.getLocation().getLatitude(), atm.getLocation().getLongitude());
                marker = mGoogleMap.addMarker(
                        new MarkerOptions()
                                .position(atmPosition)
                                .title(atm.getName() + ", " + atm.getBankName())
                                .snippet(atm.getAddress())
                );

                //show info window for selected ATM
                if (mSelectedAtm != null) {
                    if (mSelectedAtm.getName().equals(atm.getName())) {
                        marker.showInfoWindow();
                        cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(
                                        mSelectedAtm.getLocation().getLatitude(),
                                        mSelectedAtm.getLocation().getLongitude()
                                ))
                                .zoom(mSavedCameraPosition != null ? mSavedCameraPosition.zoom : DEFAULT_CAMERA_ZOOM)
                                .build();

                        mIsNeedMovedCameraToLocation = false;
                    }
                }
            }

        }

        if (cameraPosition != null) {
            //move camera
            mGoogleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(cameraPosition.target, 10)
            );
            //animate zoom in camera
            mGoogleMap.animateCamera(
                    CameraUpdateFactory.zoomTo(cameraPosition.zoom),
                    2000,
                    null
            );

            //save current camera position
            mSavedCameraPosition = cameraPosition;
        }

    }

    //--- GOOLGE MAP CALLBACKS

    @Override
    public boolean onMarkerClick(Marker marker) {
        //don`t moved camera to user location
       mIsNeedMovedCameraToLocation = false;
       return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Location location = mGoogleMap.getMyLocation();
        if (location != null) {
            //need to moved camera to user location
            mIsNeedMovedCameraToLocation = true;
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_location_not_desined), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (mSavedCameraPosition != null) {
            //don`t moved camera to user location if user change camera position
            mIsNeedMovedCameraToLocation = false;
            mSavedCameraPosition = cameraPosition;
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (!mIsNeedMovedCameraToLocation) {
            return;
        }

        //when changed location
        if (mGoogleMap != null && location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(mSavedCameraPosition != null ? mSavedCameraPosition.zoom : DEFAULT_CAMERA_ZOOM)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mSavedCameraPosition = cameraPosition;
        }

    }
}
