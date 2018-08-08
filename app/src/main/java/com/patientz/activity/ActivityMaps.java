package com.patientz.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.patientz.VO.EmergencyStatusVO;
import com.patientz.VO.PublicProviderVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ActivityMaps extends BaseActivity implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final String TAG = "ActivityMaps";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private boolean connected;
    private TextView tvTimerCount;
    private CountDownTimer countDownTimer;
    long millisUntilFinishedd = 10000;
    private Handler handler;
    private DelayForScreenShot mDelayForScreenShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tvTimerCount = (TextView) findViewById(R.id.tv_timer_count);

        Log.d(TAG, "********** onCreate ***********");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setPageTitle(toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (!connected) {
            buildGoogleApiClient();
        }
    }
    private void setPageTitle(Toolbar toolbar) {
        SharedPreferences mSharedPreferences=PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPreferences.getBoolean(Constant.IS_TEST_EMERGENCY,false)) {
            toolbar.setTitle(R.string.test);
        }else
        {
            toolbar.setTitle(R.string.title_activity_activity_maps);
        }
    }
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "************* onStart ******************");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "********** onResume ***********");
        startCounter();

    }

    private void startCounter() {
        countDownTimer = new CountDownTimer(millisUntilFinishedd, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinishedd = millisUntilFinished;
                Log.d(TAG, "Starting emergency call in  "
                        + millisUntilFinished);
                Log.d(TAG, "Starting emergency call in  "
                        + (millisUntilFinished) / 1000 + " s");
                tvTimerCount.setText(" " + ((millisUntilFinished) / 1000) + "s");
            }

            @Override
            public void onFinish() {
                AppUtil.showToast(getApplicationContext(), getString(R.string.toast_activity_maps_taking_time));
                startActivityAmbulanceList();
            }

        };
        countDownTimer.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "********** onRestart ***********");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_kill:
                Log.d(TAG, "Killing Activity");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "************** onPause **************");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "************** onRestoreInstanceState **************");
        connected = savedInstanceState.getBoolean("connected");
        millisUntilFinishedd = savedInstanceState.getLong("counterTimeRemaining");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "************** onSaveInstanceState **************");
        outState.putBoolean("connected", true);
        outState.putLong("counterTimeRemaining", millisUntilFinishedd);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "************** onStop **************");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (handler != null) {
            handler.removeCallbacks(mDelayForScreenShot);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "************** onDestroy **************");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGoogleApiClient.connect();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mGoogleApiClient.connect();
            }
        });
  
    }

    private void takeScreenShotOfMap(GoogleMap mMap) {
        if (mMap != null) {
            Log.d(TAG, "TAKING SCREEN SHOT");
            mMap.snapshot(this);
            startActivityAmbulanceList();
        } else {
            startActivityAmbulanceList();
        }
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        String STORAGE_PATH = getResources().getString(
                R.string.profileImagePath);
        File cnxDir = new File(Environment
                .getExternalStorageDirectory(),
                STORAGE_PATH);
        if (!cnxDir.exists()) {
            cnxDir.mkdirs();
        }
        FileOutputStream out;
        SharedPreferences sp = getSharedPreferences(
                Constant.EMERGENCY_SHARED_PREFERENCE,
                MODE_PRIVATE);
        try {
            String imageName = "EmergencyLocation.png";
            out = new FileOutputStream(cnxDir
                    .getAbsolutePath()
                    + "/"
                    + imageName);
            bitmap.compress(Bitmap.CompressFormat.PNG,
                    90, out);
            sp.edit()
                    .putBoolean(
                            "EmergencyLocationImage",
                            true).commit();
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());
            sp.edit()
                    .putBoolean(
                            "EmergencyLocationImage",
                            false).commit();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "********** onConnecte ************");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onMapReady : location Permissions not granted");
            startActivityAmbulanceList();
        } else {
            Location mLocation =null;
            SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String lat=defaultSp.getString("lat",null);
            String lon=defaultSp.getString("lon",null);
            if(!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon))
            {
                mLocation=new Location("");
                mLocation.setLatitude(Double.parseDouble(lat));
                mLocation.setLongitude(Double.parseDouble(lon));
            }
            Log.d(TAG, "onMapReady : mLocation=" + mLocation);
            if (mLocation != null) {
                Log.d(TAG, "location not null");
                final LatLng ll = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, 15);
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ll);
                markerOptions.title("Position");
                mMap.addMarker(markerOptions);
                if (AppUtil.isOnline(getApplicationContext())) {
                    mMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            Log.d(TAG, "ON FINISH");
                            // delay by 2 seconds to load clear location image for screenshot
                            handler = new Handler();
                            mDelayForScreenShot = new DelayForScreenShot();
                            handler.postDelayed(mDelayForScreenShot, 2000);
                        }

                        @Override
                        public void onCancel() {
                            Log.d(TAG, "ON CANCEL");
                        }
                    });

                } else {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_activity_maps_no_internet));
                    startActivityAmbulanceList();
                }
            } else {
                AppUtil.showToast(getApplicationContext(), getString(R.string.toast_activity_maps_loc_not_found));
                startActivityAmbulanceList();
            }

        }
    }

    private void startActivityAmbulanceList() {
        if(countDownTimer!=null)
        {
            countDownTimer.cancel();
        }
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
        EmergencyStatusVO user = mDatabaseHandler.getUserNotInEmergency();
        SharedPreferences mSharedPreferences= PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        int emergencyType=mSharedPreferences.getInt("selectedEmergencyType",0);
        Log.d(TAG,"EMERGENCY TYPE="+emergencyType);
        if (user.getPatientId() != 0 && emergencyType!=Constant.EMERGENCTY_TYPE_POLICE && emergencyType!=Constant.EMERGENCTY_TYPE_FIRE) {
            Intent intent = new Intent(ActivityMaps.this,
                    com.patientz.activity.OrgBranchesActivity.class);
            startActivity(intent);
        } else {
            callActivityEmergencyStepsBroadCaster();
        }
        finish();
    }
    public void callActivityEmergencyStepsBroadCaster()
    {
        final PublicProviderVO mPublicProviderVO=AppUtil.getPublicEmergencyProvider(getApplicationContext());
        Intent intent = new Intent(ActivityMaps.this,
                ActivityEmergencyStepsListener.class);
            intent.putExtra("ambulance_provider_name", mPublicProviderVO.getDisplayName());
            intent.putExtra("ambulance_provider_pno", mPublicProviderVO.getEmergencyPhoneNo());
        startActivity(intent);
    }
    public class DelayForScreenShot implements Runnable {

        @Override
        public void run() {
            takeScreenShotOfMap(mMap);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        AppUtil.showToast(getApplicationContext(), getString(R.string.toast_activity_maps_loc_not_found));
        startActivityAmbulanceList();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        AppUtil.showToast(getApplicationContext(), getString(R.string.toast_activity_maps_loc_not_found));
        startActivityAmbulanceList();
    }

}
