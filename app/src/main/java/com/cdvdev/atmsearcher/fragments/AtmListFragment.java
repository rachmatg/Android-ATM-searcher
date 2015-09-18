package com.cdvdev.atmsearcher.fragments;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.adapters.AtmListAdapter;
import com.cdvdev.atmsearcher.helpers.DatabaseHelper;
import com.cdvdev.atmsearcher.helpers.JsonParseHelper;
import com.cdvdev.atmsearcher.helpers.NetworkHelper;
import com.cdvdev.atmsearcher.helpers.Utils;
import com.cdvdev.atmsearcher.listeners.OnBackPressedListener;
import com.cdvdev.atmsearcher.listeners.OnLocationListener;
import com.cdvdev.atmsearcher.listeners.OnSearchViewListener;
import com.cdvdev.atmsearcher.loaders.DataBaseUpdateLoader;
import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Fragment for creating list of ATMS
 */
public class AtmListFragment
        extends SwipeRefreshBaseFragment
        implements OnBackPressedListener,
                   OnLocationListener,
                   LoaderManager.LoaderCallbacks,
                   Response.Listener<JSONObject>,
                   Response.ErrorListener{

    private final static int UPDATE_DB_LOADER = 1;

    private Context mContext;
    private ArrayList<Atm> mAtmArrayList;
    private ArrayList<Atm> mTempAtmArrayList;
    private ArrayAdapter<Atm> mAdapter;
    private SearchView mSearchView;
    private String mSearchQueryString = "";
    private OnSearchViewListener mSearchViewCallbacks;
    private LocationPoint mCurrentLocation = null;

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

        mContext = getActivity().getBaseContext();
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
                startUpdateAtmList();
            }
        });

        //start first update
        getSwipeRefreshLayout().post(new Runnable() {
            @Override
            public void run() {
                startUpdateAtmList();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mSearchViewCallbacks =  (OnSearchViewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be implement OnSearchViewListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSearchViewCallbacks = null;
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

        Log.d("DEBUG", "atms size = " + atms.size());

        //if location is defined
        if (mCurrentLocation != null) {
            //calculate and set distance for each ATM
            atms = Utils.addDistanceToAtms(atms, mCurrentLocation);
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
                                //Toast.makeText(getActivity(), "Query: " + query, Toast.LENGTH_SHORT).show();
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
                    //query all ATM`s list
                    mSearchQueryString = "";
                    mSearchViewCallbacks.onCloseSearchView();
                    updateListView();
                    return false;
                }
            });

            //when search icon clicked
            mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSearchViewCallbacks.onOpenSearchView();
                }
            });

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


    /**
     * Method for closing SearchView
     */
    private void closeSearchView() {
        //close SearchView
        mSearchView.setQuery("", true);
        mSearchView.setIconified(true);
        //hide home action button
        mSearchViewCallbacks.onCloseSearchView();
    }

    /**
     * Method for updating ATM`s list
     */
    private void updateListView() {
        mAtmArrayList.clear();
        mAtmArrayList.addAll(getAtmArrayList());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Method for start updating Atm List
     */
    public void startUpdateAtmList(){
        Log.d("DEBUG", "AtmListFragment.startUpdateAtmList");
        if (!isRefreshing()) {
            startRefresh();
        }

        //create URL request with Volley
        JsonObjectRequest request = new JsonObjectRequest(
                NetworkHelper.ATMS_URL,
                null,
                this,
                this
        );
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }

    /**
     *  Method for stop updating Atm List
     */
    public void stopUpdateAtmList(){
        updateListView();
        if (isRefreshing()) {
            stopRefreshing();
        }
    }

    /**
     *  Method for cancel updating Atm List
     */
    public void cancelUpdateAtmList(){
        if (isRefreshing()) {
            stopRefreshing();
        }
    }

    //------------ LOADER CALLBACKS

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case UPDATE_DB_LOADER:
                return DataBaseUpdateLoader.getInstance(mContext, mTempAtmArrayList);
            default:
                return null;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
          switch (loader.getId()) {
              case UPDATE_DB_LOADER:
                  stopUpdateAtmList();
                  Toast.makeText(mContext, getResources().getString(R.string.message_update_reset), Toast.LENGTH_SHORT).show();
                  break;
          }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case UPDATE_DB_LOADER:
                stopUpdateAtmList();
                Toast.makeText(mContext, getResources().getString(R.string.message_update_success), Toast.LENGTH_SHORT).show();
                getLoaderManager().destroyLoader(UPDATE_DB_LOADER);
                break;
        }
    }

    //--------- VOLLEY CALLBACKS

    @Override
    public void onResponse(JSONObject jsonObject) {
         int respCode = JsonParseHelper.getRespCode(jsonObject);

        if (respCode == NetworkHelper.SUCCESS_RESP_CODE) {
            mTempAtmArrayList = JsonParseHelper.getAtmsList(jsonObject);
            //create loader
           Loader loader = getLoaderManager().initLoader(UPDATE_DB_LOADER, null, this);
           loader.forceLoad();
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(mContext, getResources().getString(R.string.message_update_failed), Toast.LENGTH_SHORT).show();
        Log.e("ERROR", volleyError.toString());
        cancelUpdateAtmList();
    }

    //------- LOCATION CUSTOM CALLBACKS

    @Override
    public void onUpdateLocation(LocationPoint locationPoint) {
        if (locationPoint == null) {
            Log.d("DEBUG", "Current location IS NULL");
            mCurrentLocation = null;
            return;
        }

        Log.d("DEBUG", "Current location: lat " + locationPoint.getLatitude() + ", lon " + locationPoint.getLongitude());
        mCurrentLocation = locationPoint;
        updateListView();
    }
}