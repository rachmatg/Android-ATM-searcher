package com.cdvdev.atmsearcher.fragments;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cdvdev.atmsearcher.App;
import com.cdvdev.atmsearcher.R;
import com.cdvdev.atmsearcher.helpers.CustomIntent;
import com.cdvdev.atmsearcher.listeners.FragmentListener;
import com.google.android.gms.analytics.HitBuilders;

public class ErrorFragment extends Fragment {

    private static final String KEY_ERROR_TEXT = "atmsearcher.errorfragment.error_text";
    private static final String KEY_ERROR_ACTION = "atmsearcher.errorfragment.error_action";
    private static final String KEY_ACTION_BUTTON_TEXT = "atmsearcher.errorfragment.action_button_text";
    private FragmentListener mFragmentListener;

    public static Fragment newInstance(String errorText, CustomIntent actionIntent, String buttonText){
        Fragment fragment = new ErrorFragment();
        Bundle bundle = new Bundle();

        if (errorText != null && !errorText.equals("")) {
            bundle.putString(KEY_ERROR_TEXT, errorText);
        }

        if (actionIntent != null) {
           bundle.putSerializable(KEY_ERROR_ACTION, actionIntent);
        }

        if (buttonText != null && !buttonText.equals("")){
            bundle.putString(KEY_ACTION_BUTTON_TEXT, buttonText);
        }

        fragment.setArguments(bundle);
        return fragment;
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
        //analytics
        App.sTracker.setScreenName("Fragment Error");
        App.sTracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(App.sGACategoryUX)
                        .setLabel(App.sGALabelErrors)
                        .setAction((String) getArguments().get(KEY_ERROR_TEXT))
                        .build()
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, container, false);

        TextView errorTextView = (TextView) view.findViewById(R.id.error_text);
        Button actionButton = (Button) view.findViewById(R.id.error_action_button);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(KEY_ERROR_TEXT)) {
                errorTextView.setText(
                        bundle.getString(KEY_ERROR_TEXT)
                );
            } else {
                errorTextView.setText(getResources().getString(R.string.message_error_undefined));
            }

            if (bundle.containsKey(KEY_ERROR_ACTION) && bundle.containsKey(KEY_ERROR_TEXT)) {
               final CustomIntent intent = (CustomIntent) bundle.getSerializable(KEY_ERROR_ACTION);
               String buttonText = bundle.getString(KEY_ACTION_BUTTON_TEXT);

               actionButton.setOnClickListener(new View.OnClickListener(){
                   @Override
                   public void onClick(View view) {
                       startActivity(intent);
                   }
               });
               actionButton.setText(buttonText);
               actionButton.setVisibility(View.VISIBLE);

            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentListener.onHideFab();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }
}
