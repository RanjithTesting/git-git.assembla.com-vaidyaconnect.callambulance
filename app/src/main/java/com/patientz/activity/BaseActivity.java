package com.patientz.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.brandkinesis.BrandKinesis;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.patientz.upshot.Tag;
import com.patientz.upshot.UpshotAuthCallback;
import com.patientz.upshot.UpshotEvents;
import com.patientz.upshot.UpshotManager;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;

public class BaseActivity extends AppCompatActivity implements UpshotAuthCallback {
    private String eventID = "";
    protected Tracker tracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","onCreate");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        CallAmbulance application = (CallAmbulance) getApplication();
        tracker = application.getDefaultTracker();
        if (AppUtil.checkLoginDetails(this)) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("onResume","onResume");
        tracker.setScreenName(this.getClass().getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        Log.d("SCREEN NAME=",getClass().getSimpleName());
        eventID = UpshotEvents.createPageEvent(Tag.getTagForScreen(getClass().getSimpleName()));
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();//AuthenticateUpshot.getBKInstance();
        if (bkInstance != null) {
            UpshotManager.showActivity(getApplicationContext(), Tag.getTagForScreen(getClass().getSimpleName()));
        } else {
            Log.d("bkInstance", "Tag : " + Tag.getTagForScreen(getClass().getSimpleName()) + ", bkInstance : " + bkInstance);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UpshotEvents.closePageEvent(eventID);
    }

    @Override
    public void onBKAuthneticationDone(boolean status) {
        Log.d("BaseActivity", "onBKAuthnetication status " + status);
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else if (!ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this,
                            Manifest.permission.READ_PHONE_STATE)) {
                        showDialogToAskExternalStatePermissionForNeverAllow();
                    } else {
                        askForPermissions();
                    }
                }
                break;
        }
    }
    private void askForPermissions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setMessage(getString(R.string.phone_state_permission_mandatory_msg))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.grant)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(BaseActivity.this,new String[]{android.Manifest.permission.READ_PHONE_STATE},1);
                            }
                        }
                )
                .setNegativeButton(getString(R.string.deny)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                finish();
                            }
                        }
                )
                .create();
        builder.create().show();
    }

    private void showDialogToAskExternalStatePermissionForNeverAllow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setMessage(getString(R.string.phone_state_mandatory_msg))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.grant)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                                launchSettingPermissionScreen();
                                finish();
                            }
                        }
                )
                .setNegativeButton(getString(R.string.deny)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                finish();
                            }
                        }
                )
                .create();
        builder.create().show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 10000);
    }
    private void launchSettingPermissionScreen() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}
