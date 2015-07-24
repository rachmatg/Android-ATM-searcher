package com.cdvdev.atmsearcher.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdvdev.atmsearcher.R;

public class NetworkOffFragment extends Fragment {

    public static Fragment newInstance(){
        return new NetworkOffFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nework_off, container, false);
    }
}
