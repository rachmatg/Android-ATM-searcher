package com.cdvdev.atmsearcher.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cdvdev.atmsearcher.App;
import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.adapters.AtmListAdapter;
import com.cdvdev.atmsearcher.helpers.DatabaseHelper;
import com.cdvdev.atmsearcher.helpers.Utils;
import com.cdvdev.atmsearcher.listeners.FabListener;
import com.cdvdev.atmsearcher.listeners.FragmentListener;
import com.cdvdev.atmsearcher.listeners.BackPressedListener;
import com.cdvdev.atmsearcher.listeners.ProgressListener;
import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;
import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Fragment for creating list of ATMS
 */
public class AtmListFragment
        extends SwipeRefreshBaseFragment
        implements BackPressedListener, ProgressListener, FabListener {

    private final static String  KEY_IS_SEARCHVIEW_OPEN = "atmsearcher.issearchviewopen";
    private final static String KEY_SEARCHVIEW_QUERY = "atmsearcher.searchviewquery";
    private final static int FAB_ICON = R.drawable.ic_map_white_24dp;

    private ArrayList<Atm> mAtmArrayList;
    private ArrayAdapter<Atm> mAdapter;
    private SearchView mSearchView;
    private String mSearchQueryString = "";
    private String mSaveSearchQueryString = "";
    private boolean mIsSearchViewOpen = false;
    private FragmentListener mFragmentListener;
    private LocationPoint mCurrentLocationPoint = null;

    public static Fragment newInstance() {
        return new AtmListFragment();
    }

    @Override
    int getLayoutResId() {
        return R.layout.fragment_atmlist;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mFragmentListener = (FragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be implement FragmentListener");
        }
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

        //set refresh listener
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFragmentListener.onRefreshData();
            }
        });

        //analytics
        App.sTracker.setScreenName("Fragment AtmsList");
        App.sTracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(App.sGACategoryUX)
                        .setAction("View ATMs list")
                        .build()
        );
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState !=null) {
             if (savedInstanceState.containsKey(KEY_IS_SEARCHVIEW_OPEN)) {
                 mIsSearchViewOpen = savedInstanceState.getBoolean(KEY_IS_SEARCHVIEW_OPEN);
             }
            if (savedInstanceState.containsKey(KEY_SEARCHVIEW_QUERY)) {
                mSaveSearchQueryString = savedInstanceState.getString(KEY_SEARCHVIEW_QUERY);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentListener.onChangeAppBarTitle(R.string.app_name);

        if (mSearchView != null && !mSaveSearchQueryString.equals("")) {
            mSearchView.setIconified(false);
            mSearchView.setQuery(mSaveSearchQueryString, true);
        } else if (!mIsSearchViewOpen) {
            mFragmentListener.onSetHomeAsUpEnabled(false);
        }

        //show FAB when list is not empty
        showFab(mAtmArrayList.size() > 0);

        onShowHideProgressBar(mFragmentListener.onGetUpdateProgress());
    }

    @Override
    public void onPause() {
        super.onPause();
        showFab(false);
    }

    @Override
    public void onDestroyView() {
        mSaveSearchQueryString = mSearchView.getQuery().toString();
        closeSearchView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragmentListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_SEARCHVIEW_OPEN, mIsSearchViewOpen);
        outState.putString(KEY_SEARCHVIEW_QUERY, mSaveSearchQueryString);
        super.onSaveInstanceState(outState);
    }

    /**
     * Method for creating ordered ATM list by distance
     * @return ArrayList<Atm>
     */
    private ArrayList<Atm> getAtmArrayList() {
        ArrayList<Atm> atms;

        //get ATMs list from DB
        if (mSearchQueryString.equals("")) {
            atms = DatabaseHelper.getInstance(getActivity()).getAllAtms();
        } else {
            atms = DatabaseHelper.getInstance(getActivity()).getSearchAtm(mSearchQueryString);
        }

        //if location is defined
        if (mCurrentLocationPoint != null) {
            //calculate and set distance for each ATM
            atms = Utils.addDistanceToAtms(atms, mCurrentLocationPoint);
            //sorted ArrayList by distance
            Collections.sort(atms, new Utils.LocationComparator());
        }

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
                                //query atms
                                mSearchQueryString = query;
                                updateListView();
                            }
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            mSearchQueryString = newText;
                            updateListView();
                            return false;
                        }
                    }
            );

            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Log.d(Utils.TAG_DEBUG_LOG, getClass().getSimpleName() + ".onClose search view");
                    //query all ATM`s list
                    mSearchQueryString = "";
                    mFragmentListener.onSetHomeAsUpEnabled(false);
                    mIsSearchViewOpen = false;
                    return false;
                }
            });

            //when search icon clicked
            mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragmentListener.onSetHomeAsUpEnabled(true);
                    mIsSearchViewOpen = true;
                }
            });

            //if search view was opened, restore state
            if (mIsSearchViewOpen) {
                mSearchView.setIconified(false);
                mSearchView.setQuery(mSaveSearchQueryString, true);
            }

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onBackPressed() {
        if (!mSearchView.isIconified()) {
            closeSearchView();
            return true;
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Atm atm = ((AtmListAdapter) getListAdapter()).getItem(position);
        mFragmentListener.onAtmListItemSelected(atm);

        //analytics
        App.sTracker.setScreenName("Fragment AtmsList");
        App.sTracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(App.sGACategoryUX)
                        .setAction("Click list item: " + atm.getBankName())
                        .build()
        );
    }

    /**
     * Method for closing SearchView
     */
    private void closeSearchView() {
        //close SearchView
        mSearchView.setQuery("", true);
        mSearchView.setIconified(true);
        //hide home action button
        mFragmentListener.onSetHomeAsUpEnabled(false);
    }

    /**
     * Method for updating ATM`s list
     */
    private void updateListView() {
        mAtmArrayList.clear();
        mAtmArrayList.addAll(getAtmArrayList());
        mAdapter.notifyDataSetChanged();
        //show FAB if list is not empty
        showFab(mAtmArrayList.size() > 0);
    }

    /**
     * Helper method for hide or show FAB for AtmList
     * @param isShow - true - show FAB
     */
    private void showFab(boolean isShow){
         if (isShow) {
             mFragmentListener.onShowFab(FAB_ICON);
         } else {
             mFragmentListener.onHideFab();
         }
    }

    /**
     * Method for call update fragment  UI from Activity
     * @param location Location
     */
    public void updateFragmentUI(Location location) {
        mCurrentLocationPoint = (location != null) ? new LocationPoint(location.getLatitude(), location.getLongitude()) : null;
        updateListView();
    }

    //--- PROGRESS CALLBACK
    @Override
    public void onShowHideProgressBar(final boolean isShow) {
        final SwipeRefreshLayout swipeRefreshLayout = getSwipeRefreshLayout();
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        if (isShow) {
                            if (!isRefreshing()) {
                                startRefresh();
                            }
                        } else {
                            if (isRefreshing()) {
                                stopRefreshing();
                            }
                        }
                    }
                }
        );
        Log.d(Utils.TAG_DEBUG_LOG, getClass().getSimpleName() + ".onShowHideProgressBar() : " + (isShow ? "show" : "hide"));
    }

    //--- FAB CALLBACK

    @Override
    public void onFabClick() {
          mFragmentListener.onViewAtmOnMap(null);
    }
}