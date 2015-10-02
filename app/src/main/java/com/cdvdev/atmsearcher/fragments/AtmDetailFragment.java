package com.cdvdev.atmsearcher.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.listeners.FragmentListener;
import com.cdvdev.atmsearcher.models.Atm;

/**
 * Fragment class for ATM detail view
 */
public class AtmDetailFragment extends Fragment {

    private static final String KEY_BUNDLE_ATM = "atmsearcher.atm";
    private FragmentListener mFragmentListener;

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

        Button buttonAtmOnMap = (Button) view.findViewById(R.id.button_atm_onmap);

        if (getArguments() != null) {
            final Atm atm = (Atm) getArguments().getSerializable(KEY_BUNDLE_ATM);

            if (atm != null) {
                TextView bankName = (TextView) view.findViewById(R.id.bank_name);
                bankName.setText(atm.getBankName());

                TextView atmAddress = (TextView) view.findViewById(R.id.atm_address);
                atmAddress.setText(atm.getAddress() + ", " + atm.getCity());

                TextView workTime = (TextView) view.findViewById(R.id.atm_worktime);
                workTime.setText(atm.getWorktime());

                buttonAtmOnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFragmentListener.onViewAtmOnMap(atm);
                    }
                });
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentListener.onChangeAppBarTitle(R.string.title_detail_fragment);
        mFragmentListener.onSetHomeAsUpEnabled(true);
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
}
