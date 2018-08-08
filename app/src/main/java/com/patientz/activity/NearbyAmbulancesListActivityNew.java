package com.patientz.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.EmergencyInfoVO;
import com.patientz.adapters.LACAdapter;
import com.patientz.fragments.OfflineAmbulancesListFragment;
import com.patientz.fragments.OnlineAmbulancesListFragment;
import com.patientz.interfaces.LocationSettingsResultListener;
import com.patientz.service.IntentServiceFetchAddress;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class NearbyAmbulancesListActivityNew extends LocationSettingsResultListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, LACAdapter.RecyclerViewOnclick {
    private TabLayout tabLayout;
    private TextView tvLocality;
    private ImageView ivArrow;
    private Tracker mTracker;
    private AddressResultReceiver mResultReceiver;
    private ProgressDialog progressDialog;
    Dialog dialog;
    private PagerAdapter mPagerAdapter;
    protected ProgressDialog progdialog;
    protected static final String TAG = "NearbyAmbulancesListActivityNew";
    protected static final String DIALOG_ERROR = "dialog_error";
    protected static boolean mResolvingError = false;
    protected LocationRequest mLocationRequest;
    protected AlertDialog alertDialog;
    Location mCurrentLocation;
    protected static final int REQUEST_CODE_CHECK_LOCATION_ENABLED = 12;
    protected GoogleApiClient mGoogleApiClient;
    protected static final String STATE_RESOLVING_ERROR = "resolving_error";
    // Request code to use when launching the resolution activity
    protected static final int REQUEST_RESOLVE_ERROR = 1001;
    private SharedPreferences defaultSharedSP;
    private long amulanceId;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_providers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayout llLocality = (LinearLayout) toolbar.findViewById(R.id.ll_locality);
        defaultSharedSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        defaultSharedSP.edit().putBoolean(Constant.FROM_LOC_SEARCH_NEARBY_AMBs,false).commit();
        //parentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        ivArrow = (ImageView) toolbar.findViewById(R.id.iv_arrow);
        tvLocality = (TextView) toolbar.findViewById(R.id.tv_locality);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(1);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mResultReceiver = new AddressResultReceiver(new Handler());
        tvLocality.setSelected(true);
        setLocality("Select Locality");
        llLocality.setOnClickListener(this);
        CallAmbulance application = (CallAmbulance) getApplication();
        mTracker = application.getDefaultTracker();
        //setUpFragments();
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        if (AppUtil.isOnline(getApplicationContext())) {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(NearbyAmbulancesListActivityNew.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            progressDialog = CommonUtils.showProgressDialogWithCustomMsg(NearbyAmbulancesListActivityNew.this, getString(R.string.label_getting_location));
            mGoogleApiClient.connect();
        } else {
            //ProgressDialog mProgressDialog1=CommonUtils.showProgressDialogWithCustomMsg(NearbyAmbulancesListActivityNew.this,"No Internet.Fetching your previous searched data");
            if (!TextUtils.isEmpty(defaultSharedSP.getString("address_nearby_amb", "")) && defaultSharedSP.getString("lat", "0") != "0" && defaultSharedSP.getString("lon", "0") != "0") {
                Double currentLocationLat = Double.valueOf(defaultSharedSP.getString("lat", "0"));
                Double currentLocationLon = Double.valueOf(defaultSharedSP.getString("lon", "0"));
                mCurrentLocation = new Location("");
                mCurrentLocation.setLatitude(currentLocationLat);
                mCurrentLocation.setLongitude(currentLocationLon);
                setLocality(defaultSharedSP.getString("address_nearby_amb", ""));
                setUpFragments();
            }
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setLocality(String locality) {
        tvLocality.setText(locality);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    private void createTab(String tabTitle, int tabIcon, int tab) {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.org_types_custom_tab, null);
        tabOne.setTypeface(null, Typeface.BOLD);
        tabOne.setTextSize(16);
        tabOne.setText(tabTitle);
        tabOne.setSelected(true);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.getTabAt(tab).setCustomView(tabOne);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_org_types, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "KILLING Activity");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_locality:
                Log.d(TAG, "Location Clicked");
                ivArrow.setRotationX(180);
                Intent mIntent = new Intent(NearbyAmbulancesListActivityNew.this, LocationSearchActivity.class);
                mIntent.putExtra("tab_position", mViewPager.getCurrentItem());
                startActivityForResult(mIntent, Constant.REQUEST_CODE_LOCATION_SEARCH_RESULT);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult" + requestCode + " " + resultCode);

        switch (requestCode) {

            case (Constant.REQUEST_CODE_LOCATION_SEARCH_RESULT): {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "REQUEST_CODE_LOCATION_SEARCH_RESULT");
                    mLatLng = data.getParcelableExtra("latlng");
                    String locality = data.getStringExtra("locality");
                    Log.d(TAG, "localityyyy=" + locality);
                    Log.d(TAG, "mLatLng=" + mLatLng);
                    mCurrentLocation = new Location("");
                    mCurrentLocation.setLatitude(mLatLng.latitude);
                    mCurrentLocation.setLongitude(mLatLng.longitude);
                    defaultSharedSP.edit().putString("address_nearby_amb",locality).commit();
                    setLocality(locality);
                    mViewPager.setAdapter(null);
                    mViewPager.setAdapter(mPagerAdapter);
                    tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(mViewPager);
                    createTab("Online", 0, 0);
                    createTab("Offline", 0, 1);
                    mViewPager.setCurrentItem(data.getIntExtra("tab_position", 0));
                }
                break;
            }
            case Constant.REQUEST_CHECK_SETTINGS:
                Log.d(TAG, "REQUEST_CHECK_SETTINGS");
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "ALL REQUIRED CHANGES ARE MADE");

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(TAG, "USER WAS ASKED TO CHANGE SETTING,BUT CHOOSE NOT TO");
                        //finish();

                        break;
                    default:
                        break;
                }
                break;
            case REQUEST_RESOLVE_ERROR:
                mResolvingError = false;
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (!mGoogleApiClient.isConnecting() &&
                                !mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.connect();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
            case REQUEST_CODE_CHECK_LOCATION_ENABLED:
                Log.d(TAG, "REQUEST_CODE_CHECK_LOCATION_ENABLED");
                checkIfLocationPermissionsSatisfied();
                break;
            case (Constant.REQUEST_CODE_AMBULANCE_REQUEST):
                if (resultCode == Activity.RESULT_OK) {
                    {
                        LatLng latLng = data.getParcelableExtra("latlng");
                        String params = "?latitude=" + mLatLng.latitude + "&longitude=" + mLatLng.longitude + "&ambulanceId=" + amulanceId + "&destinationLat=" + latLng.latitude + "&destinationLon=" + latLng.longitude;
                        Log.d(TAG, "Webservice:requestAmbulance:params" + params.toString());
                        progressDialog = CommonUtils.showProgressDialogWithCustomMsg(NearbyAmbulancesListActivityNew.this, getString(R.string.requesting_ambulance));
                        RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                        mRequestQueue.add(ambulanceRequest(params));
                    }
                    break;
                }
        }
    }

    private StringRequest ambulanceRequest(final String params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.requestAmbulance + params;
        Log.d("Webservice:ambulanceRequest:url ", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                HashMap<String, Object> bkDATA = new HashMap<>();
                bkDATA.put("Ambulance Requested", true);
                Log.d("Nearby Ambulance bk data", bkDATA.toString());
                UpshotEvents.createCustomEvent(bkDATA, 16);
                Gson gson = new Gson();
                Type objectType = new TypeToken<EmergencyInfoVO>() {
                }.getType();
                EmergencyInfoVO mEmergencyInfoVO = gson.fromJson(
                        response, objectType);
                finish();
                Intent mIntent = new Intent(NearbyAmbulancesListActivityNew.this, CurrentAmbulanceRequestActivity.class);
                mIntent.putExtra(Constant.KEY_SERIALIZED_EIVO, (Serializable) mEmergencyInfoVO);
                startActivity(mIntent);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String message;
                Log.i("error1 - ", "" + android.os.Build.VERSION.SDK_INT);
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                    message = getString(R.string.offlineMode);
                } else {
                    message = getString(R.string.label_request_failed);
                }
                if (!TextUtils.isEmpty(message)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            NearbyAmbulancesListActivityNew.this);
                    builder.setMessage(message);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
                    builder.create().show();
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        return mRequest;
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        super.onResult(locationSettingsResult);
        Log.d(TAG, "onResult");
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                fetchLatestLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                dismissLocationProgress();
                askToSwitchOnLocation();
                break;
        }
    }

    public void askToSwitchOnLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(NearbyAmbulancesListActivityNew.this);
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
                        if (!TextUtils.isEmpty(defaultSharedSP.getString("address_nearby_amb", "")) && defaultSharedSP.getString("lat", "0") != "0" && defaultSharedSP.getString("lon", "0") != "0") {
                            Double currentLocationLat = Double.valueOf(defaultSharedSP.getString("lat", "0"));
                            Double currentLocationLon = Double.valueOf(defaultSharedSP.getString("lon", "0"));
                            mCurrentLocation = new Location("");
                            mCurrentLocation.setLatitude(currentLocationLat);
                            mCurrentLocation.setLongitude(currentLocationLon);
                            setLocality(defaultSharedSP.getString("address_nearby_amb", ""));
                            setUpFragments();
                        }
                    }
                });

        alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        return mCurrentLocation;
    }

    public void fetchLatestLocation() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                if (getCurrentLocation() == null) {
                    dismissLocationProgress();
                    Log.d(TAG, "getLatestLocation>getCurrentLocation" + getCurrentLocation());
                    progressDialog = CommonUtils.showProgressDialogWithCustomMsg(NearbyAmbulancesListActivityNew.this, getString(R.string.label_getting_location));
                }
                Log.d("startLocationRequest", "C");

            }

            @Override
            protected Void doInBackground(Void... params) {
                if (getCurrentLocation() == null) {

                    for (int i = 0; i < 5000; i++) {

                        if (getCurrentLocation() != null) {

                            break;
                        }
                        Log.d("startLocationRequest", "D");
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
                if (getCurrentLocation() == null) {
                    dismissLocationProgress();
                    populateListWithLastKnownLocation();
                } else {
                    mLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    if (mCurrentLocation != null) {
                        startIntentServiceFetchAddress(mCurrentLocation);
                    }
                }
            }


        }.execute();
    }




    private void populateListWithLastKnownLocation() {
        if (!TextUtils.isEmpty(defaultSharedSP.getString("address_nearby_amb", "")) && defaultSharedSP.getString("lat", "0") != "0" && defaultSharedSP.getString("lon", "0") != "0") {
            Double currentLocationLat = Double.valueOf(defaultSharedSP.getString("lat", "0"));
            Double currentLocationLon = Double.valueOf(defaultSharedSP.getString("lon", "0"));
            mCurrentLocation = new Location("");
            mCurrentLocation.setLatitude(currentLocationLat);
            mCurrentLocation.setLongitude(currentLocationLon);
            setLocality(defaultSharedSP.getString("address_nearby_amb", ""));
            setUpFragments();
        } else {
            AppUtil.showToast(getApplicationContext(), "Couldn't find your location.Search manually and make an Ambulance request");
        }
    }

    private void setUpFragments() {

        //mViewPager.setAdapter(null);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), 2);
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        createTab("Online", 0, 0);
        createTab("Offline", 0, 1);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private void dismissLocationProgress() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkIfLocationPermissionsSatisfied();
    }

    protected void checkIfLocationPermissionsSatisfied() {

        if (mGoogleApiClient != null && mLocationRequest != null) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            final PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(NearbyAmbulancesListActivityNew.this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        dismissLocationProgress();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void getClickedItemPosition(int position) {

    }

    @Override
    public void getAmbulanceId(long ambulanceId) {
        Log.d(TAG, "ambulanceId=" + ambulanceId);
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction(Constant.ANALYTICS_ACTION_AMBULANCE_REQUEST)
                .build());
        amulanceId = ambulanceId;
        Intent mIntent = new Intent(NearbyAmbulancesListActivityNew.this, LocationSearchActivity.class);
        mIntent.putExtra("source", "ambulance_list");
        startActivityForResult(mIntent, Constant.REQUEST_CODE_AMBULANCE_REQUEST);
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    OnlineAmbulancesListFragment onlineAmbulancesFragment = new OnlineAmbulancesListFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("current_tab_position", position);
                    LatLng mLatLng = null;
                    if (mCurrentLocation != null) {
                        mLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    }
                    mBundle.putParcelable("latLng", mLatLng);
                    onlineAmbulancesFragment.setArguments(mBundle);
                    return onlineAmbulancesFragment;
                case 1:
                    OfflineAmbulancesListFragment offlineAmbulancesFragment = new OfflineAmbulancesListFragment();
                    Bundle mBundle1 = new Bundle();
                    LatLng mLatLng1 = null;
                    if (mCurrentLocation != null) {
                        mLatLng1 = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    }
                    mBundle1.putInt("current_tab_position", position);
                    mBundle1.putParcelable("latLng", mLatLng1);
                    offlineAmbulancesFragment.setArguments(mBundle1);
                    return offlineAmbulancesFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try {
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException) {
            }
        }
    }
    protected void startIntentServiceFetchAddress(Location mLastLocation) {
        Intent intent = new Intent(getApplicationContext(), IntentServiceFetchAddress.class);
        intent.putExtra(getString(R.string.package_name) + "." + Constant.RECEIVER, mResultReceiver);
        intent.putExtra(getString(R.string.package_name) + "." + Constant.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            dismissLocationProgress();
            Log.d(TAG, "ON RECEIVE CALLED");
            if (resultData != null) {
                Log.d(TAG, "IntentServiceFetchAddress>>resultData");
                String locality = resultData.getString(getString(R.string.package_name) + "." + Constant.FORMATTED_ADDRESS, "");
                defaultSharedSP.edit().putString("address_nearby_amb",locality).commit();

                setLocality(locality);
                setUpFragments();
            } else {
                Log.d(TAG, "AddressResultReceiver>>result null");
                populateListWithLastKnownLocation();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
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
                connectionResult.startResolutionForResult(NearbyAmbulancesListActivityNew.this, REQUEST_RESOLVE_ERROR);
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

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(NearbyAmbulancesListActivityNew.this.getSupportFragmentManager(), "errorDialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

}
