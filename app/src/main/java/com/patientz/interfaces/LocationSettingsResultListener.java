package com.patientz.interfaces;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;
import com.patientz.activity.BaseActivity;

/**
 * Created by sunil on 23/5/17.
 */

public class LocationSettingsResultListener extends BaseActivity implements ResultCallback<LocationSettingsResult> {

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }
}
