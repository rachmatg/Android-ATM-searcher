package com.cdvdev.atmsearcher.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Receiver for UpdateDataService result
 */
public class UpdateDataReceiver extends ResultReceiver {

    public interface ReceiverCallback {
        void onReceiverResult(int resultCode, Bundle data);
    }

    private ReceiverCallback mReceiverCallback;


    public UpdateDataReceiver(Handler handler){
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiverCallback != null) {
            mReceiverCallback.onReceiverResult(resultCode, resultData);
        }
    }

    public void setReceiverCallback(ReceiverCallback receiverCallback){
        mReceiverCallback = receiverCallback;
    }


}
