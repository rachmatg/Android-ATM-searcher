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
import com.cdvdev.atmsearcher.helpers.DebugHelper;

/**
 * Fragment for creating list of ATMS
 */
public class AtmListFragment extends Fragment{

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

        //setup recycler view adapter
        AtmListAdapter adapter = new AtmListAdapter(getActivity().getBaseContext(), DebugHelper.getDemoAtmList());
        atmList.setAdapter(adapter);

        //setup items position
        atmList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

}
