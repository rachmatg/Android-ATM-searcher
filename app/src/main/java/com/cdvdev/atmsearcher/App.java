package com.cdvdev.atmsearcher;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class App extends Application {
    //period for sending data to google analytics
    private final static int GA_DISPATCH_PERIOD = 180; //in seconds
    private final static String GA_TRACKER_ID = "UA-69567539-1";
    public final static String sGALabelErrors = "Error";
    public final static String sGACategoryUX = "UX";

    public static GoogleAnalytics sAnalytics;
    public static Tracker sTracker;


    @Override
    public void onCreate() {
        super.onCreate();

        sAnalytics = GoogleAnalytics.getInstance(this);
        sAnalytics.setLocalDispatchPeriod(GA_DISPATCH_PERIOD);
        //true - don`t send statistics while debug app
        sAnalytics.setDryRun(BuildConfig.DEBUG);

        sTracker = sAnalytics.newTracker(GA_TRACKER_ID);
        sTracker.enableExceptionReporting(true);
        sTracker.enableAutoActivityTracking(true);

    }
}
