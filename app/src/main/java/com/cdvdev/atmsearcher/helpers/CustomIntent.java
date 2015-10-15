package com.cdvdev.atmsearcher.helpers;

import android.content.Intent;
import android.net.Uri;

import java.io.Serializable;

public class CustomIntent extends Intent implements Serializable {

    public CustomIntent(String action) {
        super(action);
    }

    public CustomIntent(String action, Uri uri) {
        super(action, uri);
    }

}
