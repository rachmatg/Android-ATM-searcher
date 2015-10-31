package com.cdvdev.atmsearcher.helpers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import com.cdvdev.atmsearcher.R;

public class GoogleApiErrorsHelper {

    private Context mContext;
    private int mErrorCode;
    private Resources mResources;

    public GoogleApiErrorsHelper(Context context, int errorCode) {
          mContext = context;
          mErrorCode = errorCode;
          mResources = mContext.getResources();
    }

    public String getErrorMessage() {
        String message = "";

        switch (mErrorCode) {
            case 1:
                return mResources.getString(R.string.message_error_google_services_missing);
            case 2:
                return mResources.getString(R.string.message_error_google_services_update_required);
            case 3:
                return mResources.getString(R.string.message_error_google_services_disabled);
            default:
                return mResources.getString(R.string.message_error_google_services_default);
        }
    }

    public CustomIntent getIntent() {
        switch (mErrorCode) {
            case 1:
            case 2:
                    return new CustomIntent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"));
            default:
                return null;
        }
    }

    public String getButtonText() {
          switch (mErrorCode) {
              case 1:
                  return mResources.getString(R.string.button_text_install);
              case 2:
                  return mResources.getString(R.string.button_text_update);
              default:
                  return null;
          }
    }

}
