package com.patientz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.patientz.interfaces.LocationSettingsResultListener;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Log;

public class LocationActivity extends LocationSettingsResultListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    protected ProgressDialog progdialog;
    protected static final String TAG = "LocationActivity";
    protected static final String DIALOG_ERROR = "dialog_error";
    protected static boolean mResolvingError = false;
    protected LocationRequest mLocationRequest;
    protected AlertDialog alertDialog;
    Location mCurrentLocation;
    protected static final int REQUEST_CODE_CHECK_LOCATION_ENABLED =12 ;
    protected GoogleApiClient mGoogleApiClient;
    protected static final String STATE_RESOLVING_ERROR = "resolving_error";
    // Request code to use when launching the resolution activity
    protected static final int REQUEST_RESOLVE_ERROR = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(LocationActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alertDialog!=null)
        {
            if(alertDialog.isShowing())
            {
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        }
        Log.d(TAG,"mGoogleApiClient.isConnected()="+mGoogleApiClient.isConnected());

        stopLocationUpdates();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient.isConnected()==false)
        {
            mGoogleApiClient.connect();
        }else
        {
            checkIfLocationPermissionsSatisfied();
        }
    }

    protected void checkIfLocationPermissionsSatisfied() {
        Log.d(TAG, "mGoogleApiClient=" + mGoogleApiClient);
        Log.d(TAG, "mLocationRequest=" + mLocationRequest);
        Log.d(TAG, "isconnected=" + mGoogleApiClient.isConnected());


        if (mGoogleApiClient != null && mLocationRequest != null) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            final PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(LocationActivity.this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"onConnected");
        checkIfLocationPermissionsSatisfied();
    }

    public void askToSwitchOnLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
        builder.setMessage("It seems your location service is turned off or not set to High Accuracy mode in your device settings.Setting these up will help us to locate you precisely and display better results")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.switch_on), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        dialog.cancel();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_CHECK_LOCATION_ENABLED);
                    }
                })
                .setNegativeButton(getString(R.string.label_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        dialog.cancel();
                        permissionDialogCancelled();

                    }
                });

        alertDialog = builder.create();
        if(!alertDialog.isShowing())
        {
            alertDialog.show();
        }
    }

    protected void permissionDialogCancelled() {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }


    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(LocationActivity.this.getSupportFragmentManager(), "errorDialog");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"onLocationChanged="+location);
        if(location!=null)
        {
            mCurrentLocation = location;
            Log.d("location:", String.valueOf(mCurrentLocation));
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            mResolvingError = false;
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "OnConnectionFailed");
        if (mResolvingError) {
            Log.d(TAG, "ALREADY RESOLVING ERROR");

            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(LocationActivity.this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                Log.d(TAG, "There was an error with the resolution intent");
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog(progdialog);
    }

    public void dismissProgressDialog(ProgressDialog pDialog)
    {

        if(pDialog!=null)
        {
            if(pDialog.isShowing())
            {
                pDialog.dismiss();
                pDialog.cancel();
            }
        }
    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient!=null ) {
            if(mGoogleApiClient.isConnected())
            {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, (LocationListener) this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG,"startLocationUpdates");

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    public Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG,"mGoogleApiClient.isConnected() ="+mGoogleApiClient.isConnected());
        return mCurrentLocation;
    }
    public void getLatestLocation()
    {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                if (getCurrentLocation() == null) {
                    Log.d(TAG,"getLatestLocation>getCurrentLocation"+getCurrentLocation());
                   progdialog = CommonUtils.showProgressDialogWithCustomMsg(LocationActivity.this,getString(R.string.label_getting_location));
                }

                Log.d("startLocationRequest","C");

            }

            @Override
            protected Void doInBackground(Void... params) {


                if (getCurrentLocation() == null) {

                    for (int i=0;i<300;i++ ) {

                        if (getCurrentLocation() != null) {
                            if(progdialog!=null)
                            {
                                if(progdialog.isShowing())
                                {
                                    progdialog.dismiss();
                                }
                            }
                            //CommonUtils.dismissprogdialog(progdialog);
                            break;
                        }
                        Log.d("startLocationRequest","D");
                    }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                //CommonUtils.dismissprogdialog(progdialog);
                onGettingCurrentLocation(getCurrentLocation());

                if(progdialog!=null)
                {
                    if(progdialog.isShowing())
                    {
                        progdialog.dismiss();
                    }
                }
                Log.d("startLocationRequest","F");
            }


        }.execute();
    }

    public void onGettingCurrentLocation(Location currentLocation) {
    }

}