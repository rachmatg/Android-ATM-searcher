package com.cdvdev.atmsearcher.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.adapters.AtmListAdapter;
import com.cdvdev.atmsearcher.helpers.DebugHelper;
import com.cdvdev.atmsearcher.loaders.NetworkLoaderManager;

/**
 * Fragment for creating list of ATMS
 */
public class AtmListFragment extends Fragment{

    private final int LOADER_ID = 1;
    private Loader<String> mNetworkLoader;

    public static Fragment newInstance(){
        return new AtmListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized loader
        mNetworkLoader = getLoaderManager().initLoader(
                LOADER_ID,
                null,
                new NetworkLoaderManager(getActivity())
        );
        mNetworkLoader.forceLoad();


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

        //starting load from network
        if (mNetworkLoader.isStarted()) {
            Log.d("DEBUG", "network loader is started!");
        }

        return view;
    }

}
