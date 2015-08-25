package com.cdvdev.atmsearcher.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.adapters.AtmListAdapter;
import com.cdvdev.atmsearcher.helpers.DatabaseHelper;
import com.cdvdev.atmsearcher.helpers.Utils;
import com.cdvdev.atmsearcher.listeners.OnBackPressedListener;
import com.cdvdev.atmsearcher.listeners.OnSearchViewListener;
import com.cdvdev.atmsearcher.loaders.AtmListUpdateLoader;
import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Fragment for creating list of ATMS
 */
public class AtmListFragment extends SwipeRefreshBaseFragment implements OnBackPressedListener,
                                                                                                                                       LoaderManager.LoaderCallbacks {

    private final static int KEY_UPDATE_LIST_LOADER = 0;
    private ArrayList<Atm> mAtmArrayList;
    private ArrayAdapter<Atm> mAdapter;
    private SearchView mSearchView;
    private String mSearchQueryString = "";
    private OnSearchViewListener mCallbacks;
    private Loader mUpdateListLoader;

    public static Fragment newInstance() {
        return new AtmListFragment();
    }

    @Override
    int getLayoutResId() {
        return R.layout.fragment_atmlist;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAtmArrayList = getAtmArrayList();

        mAdapter = new AtmListAdapter(getActivity(), mAtmArrayList);
        setListAdapter(mAdapter);

        //save fragment object
        setRetainInstance(true);
        //create actionbar menu
        setHasOptionsMenu(true);
        //init loader
        mUpdateListLoader = getLoaderManager().initLoader(KEY_UPDATE_LIST_LOADER, null, this);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallbacks = (OnSearchViewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be implement OnSearchViewListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("DEBUG", "On refresh called from SwipeRefresh");

                //TODO: Start refresh operation
                mUpdateListLoader.forceLoad();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallbacks = null;
    }

    private ArrayList<Atm> getAtmArrayList() {
        ArrayList<Atm> atms;

        //TODO: need to define current location
        //current location
        LocationPoint currentLocation = new LocationPoint(48.462468, 35.036538);
        //get ATMs list from DB
        if (mSearchQueryString.equals("")) {
            atms = DatabaseHelper.getInstance(getActivity()).getAllAtms();
        } else {
            atms = DatabaseHelper.getInstance(getActivity()).getSearchAtm(mSearchQueryString);
        }

        Log.d("DEBUG", "atms size = " + atms.size());

        //calculate and set distance for each ATM
        atms = Utils.addDistanceToAtms(atms, currentLocation);
        //sorted ArrayList by distance
        Collections.sort(atms, new Utils.LocationComparator());

        return atms;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_atms_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            //set hint to EditText
            mSearchView.setQueryHint(getActivity().getResources().getString(R.string.action_search_hint));

            mSearchView.setOnQueryTextListener(
                    new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            //if search text is not empty
                            if (!query.equals("")) {
                                mSearchView.clearFocus();
                                //Toast.makeText(getActivity(), "Query: " + query, Toast.LENGTH_SHORT).show();
                                //query atms
                                mSearchQueryString = query;
                                updateList();
                            }
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            mSearchQueryString = newText;
                            updateList();
                            return false;
                        }
                    }
            );

            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //query all ATM`s list
                    mSearchQueryString = "";
                    mCallbacks.onCloseSearchView();
                    updateList();
                    return false;
                }
            });

            //when search icon clicked
            mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onOpenSearchView();
                }
            });

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onBackPressed() {
        if (!mSearchView.isIconified()) {
            closeSearchView();
            return true;  //not called super.onBackPressed in Activity
        }

        return false; //called super.onBackPressed in Activity
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            //back to home
            case android.R.id.home:
                closeSearchView();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Method for closing SearchView
     */
    private void closeSearchView() {
        //close SearchView
        mSearchView.setQuery("", true);
        mSearchView.setIconified(true);
        //hide home action button
        mCallbacks.onCloseSearchView();
    }

    /**
     * Method for updating ATM`s list
     */
    public void updateList() {
        mAtmArrayList.clear();
        mAtmArrayList.addAll(getAtmArrayList());
        mAdapter.notifyDataSetChanged();
    }

   // ------------------------------- LOADER CALLBACKS

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case KEY_UPDATE_LIST_LOADER:
                return new AtmListUpdateLoader(getActivity().getBaseContext());
            default:
                return null;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d("DEBUG", "Load reset!");
        stopRefreshing();
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Log.d("DEBUG", "Load finished!");
        stopRefreshing();
        //TODO: update array list

        //TODO: adapter.notifyDataSetChanges()
    }

}