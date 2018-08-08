package com.patientz.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

public class MarketingCampaignActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int AUTO_HIDE_DELAY_MILLIS = 4000;
    private static final String TAG = "MarketingCampaignActivity";
    /**
     * the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    ImageView ivAppLogo;
    Animation zoomin;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_campaign);
        ivAppLogo = (ImageView) findViewById(R.id.iv_app_logo);
        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        ivAppLogo.startAnimation(zoomin);
        handleDynamicLink();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launchNextActivity();
            }
        }, AUTO_HIDE_DELAY_MILLIS);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void handleDynamicLink() {
        // Build GoogleApiClient with AppInvite API for receiving deep links
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();

        // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
        // would automatically launch the deep link if one is found.
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Log.d(TAG, "deep link found");
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    Log.d("handleDynamicLink", "incoming intent : " + intent);
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    Log.d("handleDynamicLink", "Deep Link : " + deepLink);
                                    SharedPreferences sharedPreferences = getSharedPreferences(
                                            Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
                                    sharedPreferences.edit().putString(Constant.INCOMING_DYNAMIC_LINK, deepLink).commit();

                                    AppUtil.sendCampaignDetails(getApplicationContext(), Constant.EVENT_LAUNCH);
                                } else {
                                    Log.d(TAG, "No deep link found");
                                }
                            }
                        });
    }
    public void launchNextActivity() {
        if (AppUtil.checkLoginDetails(MarketingCampaignActivity.this)) {
            Intent mainIntent = new Intent(MarketingCampaignActivity.this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        } else {
            AppUtil.callLoginScreen(getApplicationContext());
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}


