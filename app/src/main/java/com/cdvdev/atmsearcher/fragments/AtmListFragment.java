package com.cdvdev.atmsearcher.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.adapters.AtmListAdapter;
import com.cdvdev.atmsearcher.helpers.DatabaseHelper;
import com.cdvdev.atmsearcher.helpers.Utils;
import com.cdvdev.atmsearcher.listeners.OnBackPressedListener;
import com.cdvdev.atmsearcher.listeners.OnSearchViewListener;
import com.cdvdev.atmsearcher.models.Atm;
import com.cdvdev.atmsearcher.models.LocationPoint;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Fragment for creating list of ATMS
 */
public class AtmListFragment extends ListFragment implements OnBackPressedListener{

    private ArrayList<Atm> mAtmArrayList;
    private ArrayAdapter<Atm> mAdapter;
    private SearchView mSearchView;
    private String mSearchQueryString = "";
    private OnSearchViewListener mCallbacks;

    public static Fragment newInstance(){
        return new AtmListFragment();
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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallbacks = (OnSearchViewListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be implement OnSearchViewListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_atmlist, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallbacks = null;
    }

    private ArrayList<Atm> getAtmArrayList(){
        ArrayList<Atm> atms;

        //TODO: need to define current location
        //current location
        LocationPoint currentLocation = new LocationPoint(48.462468, 35.036538);
        //get ATMs list from DB
        if (mSearchQueryString.equals("")) {
            atms = new DatabaseHelper(getActivity()).getAllAtms();
        } else {
            atms = new DatabaseHelper(getActivity()).getSearchAtm(mSearchQueryString);
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
    private void closeSearchView(){
        //close SearchView
        mSearchView.setQuery("", true);
        mSearchView.setIconified(true);
        //hide home action button
        mCallbacks.onCloseSearchView();
    }

    /**
     * Method for updating ATM`s list
     */
    public void updateList(){
        mAtmArrayList.clear();
        mAtmArrayList.addAll( getAtmArrayList() );
        mAdapter.notifyDataSetChanged();
    }

}
