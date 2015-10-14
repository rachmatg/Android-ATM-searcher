package com.cdvdev.atmsearcher.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.listeners.FragmentListener;

public class NetworkOffFragment extends Fragment {

    private FragmentListener mFragmentListener;

    public static Fragment newInstance(){
        return new NetworkOffFragment();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network_off, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentListener.onShowFab(R.drawable.ic_refresh_white_24dp);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFragmentListener.onHideFab();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragmentListener = null;
    }
}
