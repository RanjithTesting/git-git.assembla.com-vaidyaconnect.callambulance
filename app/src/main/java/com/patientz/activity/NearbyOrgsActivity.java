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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.os.ResultReceiver;
import android.support.v4.view.PagerAdapter;
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
import com.patientz.fragments.NearbyOrgsFragment;
import com.patientz.interfaces.LocationSettingsResultListener;
import com.patientz.service.IntentServiceFetchAddress;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.HashMap;


public class NearbyOrgsActivity extends LocationSettingsResultListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "NearbyOrgsActivity";
    private TabLayout tabLayout;
    private TextView tvLocality;
    private ImageView ivArrow;
    private AddressResultReceiver mResultReceiver;
    private LinearLayout parentLayout;
    private ProgressDialog progressDialog;

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
    private long amulanceId;
    /**
     * The {@link ViewPager} that will host the section contents.
     */


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_types);
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
        parentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        ivArrow = (ImageView) toolbar.findViewById(R.id.iv_arrow);
        tvLocality = (TextView) toolbar.findViewById(R.id.tv_locality);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mResultReceiver = new AddressResultReceiver(new Handler());
        tvLocality.setSelected(true);
        Log.d(TAG, "Current location details=" + mLatLng);
        setLocality("");

        llLocality.setOnClickListener(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
        createTab("Hospitals", R.drawable.org_hospital, 0);
        createTab("Blood Banks", R.drawable.org_blood_group, 3);
        createTab("Pharmacy", R.drawable.org_pharmacy, 4);
        createTab("Diagnostic", R.drawable.org_diagnostics, 5);
        createTab("Doctors", R.drawable.tab_doctor, 2);
        createTab("AED", R.drawable.aed, 1);
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        createLocationRequest();
        progressDialog=CommonUtils.showProgressDialogWithCustomMsg(NearbyOrgsActivity.this,"Getting your current location");
        mGoogleApiClient = new GoogleApiClient.Builder(NearbyOrgsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setLocality(String locality) {
        if (mLatLng == null) {
            tvLocality.setText("Select Locality");
        } else {
            tvLocality.setText(locality);
        }
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
        tabOne.setText(tabTitle);
        tabOne.setSelected(true);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, tabIcon, 0, 0);
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
                Intent mIntent = new Intent(NearbyOrgsActivity.this, LocationSearchActivity.class);
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
                    setLocality(locality);
                    mViewPager.setAdapter(null);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(mViewPager);
                    createTab("Hospitals", R.drawable.org_hospital, 0);
                    createTab("Blood Banks", R.drawable.org_blood_group, 3);
                    createTab("Pharmacy", R.drawable.org_pharmacy, 4);
                    createTab("Diagnostic", R.drawable.org_diagnostics, 5);
                    createTab("Doctors", R.drawable.tab_doctor, 2);
                    createTab("AED", R.drawable.aed, 1);
                    mViewPager.setCurrentItem(data.getIntExtra("tab_position", 0));
                }
                break;
            }
            case Constant.REQUEST_CHECK_SETTINGS:
                Log.d(TAG, "REQUEST_CHECK_SETTINGS");
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "ALL REQUIRED CHANGES ARE MADE");
                       /* progdialog = CommonUtils.showProgressDialog(this);
                        startLocationUpdates();*/
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
                        break;
                    default:
                        break;
                }
                break;
                case REQUEST_CODE_CHECK_LOCATION_ENABLED:
                    Log.d(TAG, "REQUEST_CODE_CHECK_LOCATION_ENABLED");
                    checkIfLocationPermissionsSatisfied();
                    break;
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkIfLocationPermissionsSatisfied();
    }

    @Override
    public void onConnectionSuspended(int i) {
        dismissLocationProgress();

    }

    protected void checkIfLocationPermissionsSatisfied() {

        if (mGoogleApiClient != null && mLocationRequest != null) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            final PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(NearbyOrgsActivity.this);
        }
    }
    private void dismissLocationProgress() {
        if(progressDialog!=null)
        {
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "OnConnectionFailed");
        dismissLocationProgress();
        if (mResolvingError) {
            Log.d(TAG, "ALREADY RESOLVING ERROR");

            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(NearbyOrgsActivity.this, REQUEST_RESOLVE_ERROR);
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
        NearbyOrgsActivity.ErrorDialogFragment dialogFragment = new NearbyOrgsActivity.ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(NearbyOrgsActivity.this.getSupportFragmentManager(), "errorDialog");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("onPageSelected","position="+position);
        HashMap<String, Object> upshotData = new HashMap<>();
        switch (position)
        {
            case 0:
                upshotData.put(Constant.UpshotEvents.NEARBY_HOSPITALS_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NEARBY_HOSPITALS_CLICKED);
                break;
            case 1:
                upshotData.put(Constant.UpshotEvents.NEARBY_AED_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NEARBY_AED_CLICKED);
                break;
            case 2:
                upshotData.put(Constant.UpshotEvents.NEARBY_DOCTORS_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NEARBY_DOCTORS_CLICKED);
                break;
            case 3:
                upshotData.put(Constant.UpshotEvents.NEARBY_BLOOD_BANKS_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NEARBY_BLOOD_BANKS_CLICKED);
                break;
            case 4:
                upshotData.put(Constant.UpshotEvents.NEARBY_PHARMACY_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NEARBY_PHARMACY_CLICKED);
                break;
            case 5:
                upshotData.put(Constant.UpshotEvents.NEARBY_DIAGNOSTICS_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NEARBY_DIAGNOSTICS_CLICKED);
                break;
        }
        Log.d(TAG,"upshot data="+upshotData.entrySet());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
    public void onLocationChanged(Location location) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d(TAG, "TAB POSITION=" + position);
            Log.d(TAG, "Current selected location=" + mLatLng);
            Bundle mBundle = new Bundle();
            mBundle.putInt("current_tab_position", position);
            mBundle.putParcelable("latLon", mLatLng);
            return NearbyOrgsFragment.newInstance(mBundle);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
                case 4:
                    return "SECTION 5";
                case 5:
                    return "SECTION 6";
                case 6:
                    return "SECTION 7";
            }
            return null;
        }
        @Override
        public void finishUpdate(ViewGroup container) {
            try{
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException){
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
            if(progressDialog!=null)
            {
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }
            Log.d(TAG, "ON RECEIVE CALLED");
            if (resultData != null) {
                Log.d(TAG,"IntentServiceFetchAddress>>resultData");
                String locality = resultData.getString(getString(R.string.package_name) + "." + Constant.FORMATTED_ADDRESS, "");
                setLocality(locality);
                setUpFragments();
            }else
            {
                Log.d(TAG,"AddressResultReceiver>>result null");
                populateListWithLastKnownLocation();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissLocationProgress();
    }
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        super.onResult(locationSettingsResult);
        Log.d(TAG, "onResult");
        dismissLocationProgress();
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
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    public void fetchLatestLocation()
    {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                if (getCurrentLocation() == null) {
                    Log.d(TAG,"getLatestLocation>getCurrentLocation"+getCurrentLocation());
                    progressDialog=CommonUtils.showProgressDialogWithCustomMsg(NearbyOrgsActivity.this,getString(R.string.label_getting_location));
                }
                Log.d("startLocationRequest","C");

            }

            @Override
            protected Void doInBackground(Void... params) {


                if (getCurrentLocation() == null) {

                    for (int i=0;i<5000;i++ ) {

                        if (getCurrentLocation() != null) {

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
                if(getCurrentLocation()==null)
                {
                    if(progressDialog!=null)
                    {
                        if(progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                        }
                    }
                    populateListWithLastKnownLocation();
                }else{
                    mLatLng=new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
                    if(mCurrentLocation!=null)
                    {
                        startIntentServiceFetchAddress(mCurrentLocation);
                    }
                }
            }


        }.execute();
    }
    private void populateListWithLastKnownLocation() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(NearbyOrgsActivity.this);
        if(!TextUtils.isEmpty(mSharedPreferences.getString("address","")) && mSharedPreferences.getString("lat", "0")!="0" && mSharedPreferences.getString("lon", "0")!="0" )
        {
            Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
            Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
            mCurrentLocation=new Location("");
            mCurrentLocation.setLatitude(currentLocationLat);
            mCurrentLocation.setLongitude(currentLocationLon);
            setLocality(mSharedPreferences.getString("address",""));
            setUpFragments();
        }else
        {
            AppUtil.showToast(getApplicationContext(),"Couldn't find your location.Search manually and make an Ambulance request");
        }

    }

    private void setUpFragments() {
        mViewPager.setAdapter(null);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        createTab("Hospitals", R.drawable.org_hospital, 0);
        createTab("Blood Banks", R.drawable.org_blood_group, 3);
        createTab("Pharmacy", R.drawable.org_pharmacy, 4);
        createTab("Diagnostic", R.drawable.org_diagnostics, 5);
        createTab("Doctors", R.drawable.tab_doctor, 2);
        createTab("AED", R.drawable.aed, 1);
    }

    public Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        return mCurrentLocation;
    }
    public void askToSwitchOnLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(NearbyOrgsActivity.this);
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
                        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(NearbyOrgsActivity.this);
                        if(!TextUtils.isEmpty(mSharedPreferences.getString("address","")) && mSharedPreferences.getString("lat", "0")!="0" && mSharedPreferences.getString("lon", "0")!="0" )
                        {
                            Log.d(TAG,"LAST KNOWN LOCATION EXISTS");
                            Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
                            Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
                            mCurrentLocation=new Location("");
                            mCurrentLocation.setLatitude(currentLocationLat);
                            mCurrentLocation.setLongitude(currentLocationLon);
                            mLatLng=new LatLng(currentLocationLat,currentLocationLon);
                            setLocality(mSharedPreferences.getString("address",""));
                            setUpFragments();
                        }
                    }
                });

        alertDialog = builder.create();
        if(!alertDialog.isShowing())
        {
            alertDialog.show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient!=null)
        {
            if(mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
            }
        }
    }
}
