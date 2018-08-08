package com.patientz.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.patientz.interfaces.LocationSettingsResultListener;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Log;

public class LocationActivity2 extends LocationSettingsResultListener {
    protected static final String TAG = "LocationActivity2";
    protected LocationRequest mLocationRequest;
    protected AlertDialog alertDialog;
    protected static final int REQUEST_CODE_CHECK_LOCATION_ENABLED = 12;
    protected GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private Task<LocationSettingsResponse> task;
    private SettingsClient client;
    LocationSettingsRequest mLocationSettingsRequest;
    private Location mlocation;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest.Builder builder;
    private ProgressDialog mProgressDialog;
    private CountDownTimer wCounter;
    protected boolean basicLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            basicLocationRequest = savedInstanceState.getBoolean("basicLocationRequest");
            Log.d("basicLocationRequest", basicLocationRequest + "");
        }
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        createLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("onLocationResult>>location=", location + "");
                    mlocation = location;
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopLocationUpdates();
    }

    private void dismissAlertDialog(AlertDialog alertDialog) {
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void dismissProgressDialog(ProgressDialog mProgressDialog) {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        if (basicLocationRequest) {
            mLocationRequest.setNumUpdates(1);
        }
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public class OnSuccessListenerLocationSettingsResponse implements OnSuccessListener<LocationSettingsResponse> {

        @Override
        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            // All location settings are satisfied
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (!basicLocationRequest)
                mProgressDialog = CommonUtils.showProgressDialogWithCustomMsg(LocationActivity2.this, getString(R.string.label_getting_location));
            Task<LocationAvailability> mLocationAvailability = mFusedLocationClient.getLocationAvailability();
            mLocationAvailability.addOnSuccessListener(LocationActivity2.this, new OnSuccessListener<LocationAvailability>() {
                @Override
                public void onSuccess(LocationAvailability locationAvailability) {
                    Log.d("isLocationAvailable", locationAvailability.isLocationAvailable() + "");
                    if (locationAvailability.isLocationAvailable()) {
                        if (ActivityCompat.checkSelfPermission(LocationActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(LocationActivity2.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(final Location location) {
                                        dismissProgressDialog(mProgressDialog);
                                        if (location != null) {
                                            Log.d("LAST_KNOWN_LOC", location + "");
                                            if (ActivityCompat.checkSelfPermission(LocationActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            OnReceivedLocation(location);
                                        }
                                    }
                                });
                    } else {
                        wCounter = new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                if (mlocation != null) {
                                    Log.d("LAST_KNOWN_LOC", mlocation + "");
                                    dismissProgressDialog(mProgressDialog);
                                    wCounter.cancel();
                                    OnReceivedLocation(mlocation);
                                }
                            }

                            public void onFinish() {
                                Log.d(TAG, "wCounter onFinish");
                                dismissProgressDialog(mProgressDialog);
                                wCounter.cancel();
                                OnReceivedLocation(null);
                            }
                        };
                        wCounter.start();
                    }
                }
            });
        }
    }


    public class OnFailureListenerLocationSettingsResponse implements OnFailureListener {
        @Override
        public void onFailure(@NonNull Exception e) {
            if (e instanceof ResolvableApiException) {
                askToSwitchOnLocation();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        client = LocationServices.getSettingsClient(this);
        task = client.checkLocationSettings(mLocationSettingsRequest);
        task.addOnSuccessListener(this, new OnSuccessListenerLocationSettingsResponse());
        if (!basicLocationRequest)
            task.addOnFailureListener(this, new OnFailureListenerLocationSettingsResponse());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void askToSwitchOnLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity2.this);
        builder.setMessage("It seems your location service is turned off or not set to High Accuracy mode in your device settings.Setting these up will help us to locate you precisely and display better results")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.switch_on), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_CHECK_LOCATION_ENABLED);
                    }
                })
                .setNegativeButton(getString(R.string.label_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        rejectedToSwitchOnLocation();
                    }
                });

        alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    void rejectedToSwitchOnLocation() {
        Log.d(TAG, "rejectedToSwitchOnLocation");
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissAlertDialog(alertDialog);
        dismissProgressDialog(mProgressDialog);
        cancelCounter(wCounter);
        stopLocationUpdates();
    }

    private void cancelCounter(CountDownTimer wCounter) {
        if (wCounter != null) {
            wCounter.cancel();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    public void onGettingCurrentLocation(Location currentLocation) {
    }

    void OnReceivedLocation(Location location) {
        Log.d(TAG, "location=" + location + "");
    }
}