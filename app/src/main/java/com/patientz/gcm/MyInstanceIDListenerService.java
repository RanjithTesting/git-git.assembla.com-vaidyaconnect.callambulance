package com.patientz.gcm;


import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.patientz.utils.AppUtil;


/**
 * Called if InstanceID token is updated. This may occur if the security of
 * the previous token had been compromised. This call is initiated by the
 * InstanceID provider.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    public MyInstanceIDListenerService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        SharedPreferences prefs = AppUtil.getGcmPreferences(getApplicationContext());
        prefs.edit().clear().commit();
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);
    }
}
