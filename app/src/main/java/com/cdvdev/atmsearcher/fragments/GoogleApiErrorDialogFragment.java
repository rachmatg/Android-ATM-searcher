package com.cdvdev.atmsearcher.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.GoogleApiAvailability;

public class GoogleApiErrorDialogFragment extends DialogFragment{

    public final static String KEY_DIALOG_ERROR_CODE = "atmsearcher.dialog_error";

    public static GoogleApiErrorDialogFragment newInstance(int errorCode){
        GoogleApiErrorDialogFragment dialog = new GoogleApiErrorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_DIALOG_ERROR_CODE, errorCode);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int errorCode = getArguments().getInt(KEY_DIALOG_ERROR_CODE);

        return GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), errorCode, 111);
    }

}
