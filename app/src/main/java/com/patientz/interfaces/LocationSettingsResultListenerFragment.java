package com.patientz.interfaces;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;

/**
 * Created by sunil on 23/5/17.
 */

public class LocationSettingsResultListenerFragment implements ResultCallback<LocationSettingsResult> {

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }
}
