package com.patientz.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by sunil on 22/12/16.
 */

public class GpsLocationReceiver extends BroadcastReceiver implements LocationListener
{

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
