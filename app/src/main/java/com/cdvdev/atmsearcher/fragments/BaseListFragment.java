package com.cdvdev.atmsearcher.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * Class for base ListFragment
 *
 * @author Dmitriy V. Chernysh (dmitriy.chernysh@gmail.com)
 *         Created on 17.12.15.
 */
public abstract class BaseListFragment extends ListFragment {

    protected abstract ArrayAdapter createAdapter();
    protected ArrayAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = createAdapter();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(mAdapter);
    }

}
