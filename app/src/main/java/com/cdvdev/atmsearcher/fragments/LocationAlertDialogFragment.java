package com.cdvdev.atmsearcher.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.cdvdev.atmsearcher.App;
import com.cdvdev.atmsearcher.R;
import com.google.android.gms.analytics.HitBuilders;

public class LocationAlertDialogFragment extends DialogFragment {

    private static final String KEY_TITLE = "atmsearcher.alertdialog.title";
    private static final String KEY_MESSAGE = "amtsearcher.alertdialog.message";

    public static LocationAlertDialogFragment newInstance(int title, int message) {
        LocationAlertDialogFragment fragment = new LocationAlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE, title);
        bundle.putInt(KEY_MESSAGE, message);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //analytics
        App.sTracker.setScreenName("Location Alert dialog");
        App.sTracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(App.sGACategoryUX)
                        .setAction("View location alert dialog")
                        .build()
        );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(KEY_TITLE);
        int message = getArguments().getInt(KEY_MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (title != 0) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setPositiveButton(
                R.string.button_settings,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //go to location settings
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }
        );
        builder.setNegativeButton(
                android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                }
        );

        return builder.create();
    }
}
