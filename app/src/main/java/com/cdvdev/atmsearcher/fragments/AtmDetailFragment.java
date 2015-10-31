package com.cdvdev.atmsearcher.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.listeners.FabListener;
import com.cdvdev.atmsearcher.listeners.FragmentListener;
import com.cdvdev.atmsearcher.models.Atm;

/**
 * Fragment class for ATM detail view
 */
public class AtmDetailFragment extends Fragment implements FabListener {

    private static final String KEY_BUNDLE_ATM = "atmsearcher.atm";
    private FragmentListener mFragmentListener;
    private Atm mAtm;

    public static Fragment newInstance(Atm atm) {
        Fragment fragment = new AtmDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_BUNDLE_ATM, atm);
        fragment.setArguments(bundle);
        return fragment;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_atm_detail, container, false);


        if (getArguments() != null) {
            mAtm = (Atm) getArguments().getSerializable(KEY_BUNDLE_ATM);

            if (mAtm != null) {
                TextView bankName = (TextView) view.findViewById(R.id.bank_name);
                bankName.setText(mAtm.getBankName());

                TextView atmAddress = (TextView) view.findViewById(R.id.atm_address);
                atmAddress.setText(mAtm.getAddress() + ", " + mAtm.getCity());

                TextView workTime = (TextView) view.findViewById(R.id.atm_worktime);
                workTime.setText(mAtm.getWorktime());

            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentListener.onChangeAppBarTitle(R.string.title_detail_fragment);
        mFragmentListener.onSetHomeAsUpEnabled(true);

        //setup fab
        mFragmentListener.onShowFab(R.drawable.ic_place_white_24dp);
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

    //--- FAB CALLBACK

    @Override
    public void onFabClick() {
        mFragmentListener.onViewAtmOnMap(mAtm);
    }
}
