package com.cdvdev.atmsearcher.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.helpers.Utils;
import com.cdvdev.atmsearcher.listeners.FragmentListener;

public class AppInfoFragment extends  Fragment implements View.OnClickListener{

    private final static String LINK_CONTACT_GOOGLE = "https://plus.google.com/u/0/communities/116252441884846070882";
    private final static String LINK_CONTACT_LINKEDIN = "https://ua.linkedin.com/in/dmitriychernysh";

    private FragmentListener mFragmentListener;
    private ImageView mImageContactGoggle,
                                    mImageContactLinkedin;

    public static Fragment newInstance(){
         return new AppInfoFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = null;

        try {
            if (context instanceof Activity) {
                activity = (Activity) context;
                mFragmentListener = (FragmentListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be implemented FragmentListener");
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
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        mFragmentListener.onChangeAppBarTitle(R.string.title_about_app);
        mFragmentListener.onSetHomeAsUpEnabled(true);

        mImageContactGoggle = (ImageView) view.findViewById(R.id.ic_contact_google);
        mImageContactGoggle.setOnClickListener(this);
        mImageContactLinkedin = (ImageView) view.findViewById(R.id.ic_contact_linkedin);
        mImageContactLinkedin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    // ---  VIEW ONCLICK CALLBACK

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        String url = "";

        try {
            switch (viewId) {
                case R.id.ic_contact_google:
                    url = LINK_CONTACT_GOOGLE;
                    break;
                case R.id.ic_contact_linkedin:
                    url = LINK_CONTACT_LINKEDIN;
                    break;
            }

            if (!url.equals("")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(Utils.TAG_ERROR_LOG, "Link failed", e);
        }

    }
}
