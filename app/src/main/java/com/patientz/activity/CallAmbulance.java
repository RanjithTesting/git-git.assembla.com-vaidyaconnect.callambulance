package com.patientz.activity;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.brandkinesis.BrandKinesis;
import com.brandkinesis.utils.BKAppStatusUtil;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.patientz.upshot.AuthenticateUpshot;
import com.patientz.upshot.UpshotAuthCallback;
import com.patientz.utils.AppVolley;
import com.patientz.utils.ApplicationLifecycleManager;
import com.patientz.utils.Log;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import io.fabric.sdk.android.Fabric;

@ReportsCrashes(formUri = "", mode = ReportingInteractionMode.DIALOG, mailTo = "sunil@doctrz.com", resToastText = R.string.crash_toast_text, // optional,
        // displayed
        // as
        // soon
        // as
        // the
        // crash
        // occurs,
        // before
        // collecting
        // data
        // which
        // can
        // take
        // a
        // few
        // seconds
        resDialogText = R.string.crash_dialog_text, resDialogIcon = android.R.drawable.ic_dialog_info, // optional.
        // default
        // is
        // a
        // warning
        // sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your
        // application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when
        // defined, adds
        // a user text
        // field input
        // with this
        // text resource
        // as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast
        // message when the user
        // accepts to send a report.
)
public class CallAmbulance extends MultiDexApplication implements BKAppStatusUtil.BKAppStatusListener {

    private static final String TAG = "CallAmbulance";
    private static Context appContext;
    private Tracker mTracker;
    private int appStatus;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    public void onCreate() {
        AppVolley.init(this);
        appContext = getApplicationContext();
        //ACRA.init(this);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sAnalytics = GoogleAnalytics.getInstance(this);
        BKAppStatusUtil.getInstance().register(this, this);
        registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());
    }

    public static Context getAppContext() {
        return appContext;
    }


    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            Log.d(TAG, "mTracker=" + mTracker);
        }
        Log.d(TAG, "mTracker2=" + mTracker);
        return mTracker;
    }
/*

    @Override
    public void onTerminate() {
        UpshotManager.terminate(this);
        super.onTerminate();
    }
*/

    private void initBrandKinesis(Activity activity) {
        new AuthenticateUpshot(activity, new UpshotAuthCallback() {
            @Override
            public void onBKAuthneticationDone(boolean status) {

            }
        });
    }


    @Override
    public void onAppComesForeground(Activity activity) {
        initBrandKinesis(activity);
        BrandKinesis brandKinesis = BrandKinesis.getBKInstance();
        Log.e("App status==", "Foreground::" + brandKinesis);
    }

    @Override
    public void onAppGoesBackground() {
        Log.e("App status==", "Background");
    }

    @Override
    public void onAppRemovedFromRecentsList() {
        Log.e("App status==", "removed from recent list");
    }
}
