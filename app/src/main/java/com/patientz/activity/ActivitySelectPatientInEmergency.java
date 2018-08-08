package com.patientz.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.patientz.VO.EmergencyStatusVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.adapters.AdapterSelectPatientInEmergency;
import com.patientz.databases.DatabaseHandler;
import com.patientz.service.IntentServiceFetchAddress;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.DividerItemDecoration;
import com.patientz.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;

import static com.patientz.activity.R.id.tv_timer_count;

public class ActivitySelectPatientInEmergency extends LocationActivity {

    private static final String TAG = "ActivitySelectPatientInEmergency";
    long millisUntilFinishedd = 10000;
    AddressResultReceiver mResultReceiver;
    private CountDownTimer countDownTimer;
    private TextView tvTimerCount;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PatientUserVO> usersList, uList;
    private ProgressDialog mProgressDialog;
    private SharedPreferences defaultSp;
    private Intent mIntent;
    private DatabaseHandler mDatabaseHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_patient_in_emergency);
        defaultSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mIntent = getIntent();
        tvTimerCount = (TextView) findViewById(tv_timer_count);
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
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
        try {
            usersList = mDatabaseHandler.getAllUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "No of users=" + usersList.size());

        /* Adding stranger in case of emergency */
        PatientUserVO strangerPatientUserVO = new PatientUserVO();
        strangerPatientUserVO.setPatientId(0);
        strangerPatientUserVO.setFirstName("Stranger");
        strangerPatientUserVO.setLastName("");
        strangerPatientUserVO.setRelationship("none");
        strangerPatientUserVO.setRole("none");
        strangerPatientUserVO.setPicId(0);
        usersList.add(0, strangerPatientUserVO);

        mAdapter = new AdapterSelectPatientInEmergency(getApplicationContext(), usersList);
        mRecyclerView.setAdapter(mAdapter);
        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    private void setPageTitle(Toolbar toolbar) {
        if (defaultSp.getBoolean(Constant.IS_TEST_EMERGENCY, false)) {
            toolbar.setTitle(R.string.test);
        } else {
            toolbar.setTitle(R.string.title_activity_activity_select_patient_in_emergency);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_cancel, menu);
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        millisUntilFinishedd = savedInstanceState.getLong("counterTimeRemaining");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("counterTimeRemaining", millisUntilFinishedd);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        super.onResult(locationSettingsResult);
        Log.d(TAG, "onResult");
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                getLatestLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d(TAG, "SETTINGS_CHANGE_UNAVAILABLE");
                askToSwitchOnLocation();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((AdapterSelectPatientInEmergency) mAdapter).setOnItemClickListener(new AdapterSelectPatientInEmergency.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                long patientId = usersList.get(position).getPatientId();
                Log.i(TAG, " Clicked on Item " + position);
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                mDatabaseHandler.deleteAllEmergencyNotInvokedUsersFromEmergencyTable();
                defaultSp.edit().remove("emergency_token").commit();
                SharedPreferences sp = getApplicationContext().getSharedPreferences(Constant.COMMON_SP_FILE, getApplicationContext().MODE_PRIVATE);
                sp.edit().remove("emergencyNumber").commit();
                String emergencyToken = usersList.get(position).getEmergencyToken();
                defaultSp.edit().putString("emergency_token", emergencyToken).commit();

                if (!usersList.get(position).isUnderEmergency()) {
                    if (mIntent.getBooleanExtra(Constant.IS_TEST_EMERGENCY, false)) {
                        callSelectEmergencyTypeScreen(patientId, Constant.EMERGENCY_STATUS_TEST);
                    } else {
                        Log.d(TAG, "$raising emergency$");
                        callSelectEmergencyTypeScreen(patientId, Constant.EMERGENCY_STATUS_INVOKE);
                    }
                } else {
                    if (mIntent.getBooleanExtra(Constant.IS_TEST_EMERGENCY, false)) {
                        AppUtil.showToast(getApplicationContext(), getString(R.string.user_in_emergency));
                    } else {
                        Intent mIntent = new Intent(ActivitySelectPatientInEmergency.this, ActivityEmergencyTrackAndRevoke.class);
                        mIntent.putExtra("patientId", patientId);
                        startActivity(mIntent);
                        finish();
                    }
                }
            }
        });
    }

    private void callSelectEmergencyTypeScreen(long patientId, String emergencyStatus) {
        HashMap<String, Object> upshotData = new HashMap<>();
        upshotData.put(Constant.UpshotEvents.EMERGENCY_PATIENT_SELECTED, true);
        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_ADD_MEMBER_CLICKED);
        Log.d(TAG, "upshot data=" + upshotData.entrySet());
        EmergencyStatusVO mEmergencyStatusVO = new EmergencyStatusVO();
        mEmergencyStatusVO.setPatientId(patientId);
        mEmergencyStatusVO.setEmergencyStatus(emergencyStatus);
        mDatabaseHandler.updateEmergencyTable(mEmergencyStatusVO);
        Intent mIntent = new Intent(ActivitySelectPatientInEmergency.this, ActivityEmergencyType.class);
        startActivity(mIntent);
        finish();
    }

    private void defaultInitiatingEmergencyForSelf() {

        countDownTimer = new CountDownTimer(millisUntilFinishedd, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinishedd = millisUntilFinished;
                Log.d(TAG, "Starting emergency call in  "
                        + millisUntilFinished);
                Log.d(TAG, "Starting emergency call in  "
                        + (millisUntilFinished) / 1000 + " s");
                tvTimerCount.setText(getString(R.string.select_patient_in_emergency_timer_text) + " " + ((millisUntilFinished) / 1000) + "s");
            }

            @Override
            public void onFinish() {
                PatientUserVO mPatientUserVO = AppUtil.getLoggedPatientVO(getApplicationContext());
                mDatabaseHandler.deleteAllEmergencyNotInvokedUsersFromEmergencyTable();
                String emergencyToken = mPatientUserVO.getEmergencyToken();
                defaultSp.edit().putString("emergency_token", emergencyToken).commit();
                if (!mIntent.getBooleanExtra(Constant.IS_TEST_EMERGENCY, false)) {
                    callSelectEmergencyTypeScreen(mPatientUserVO.getPatientId(), Constant.EMERGENCY_STATUS_INVOKE);
                } else {
                    callSelectEmergencyTypeScreen(mPatientUserVO.getPatientId(), Constant.EMERGENCY_STATUS_TEST);
                }
            }

        };
        countDownTimer.start();
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
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHECK_LOCATION_ENABLED:
                Log.d(TAG, "REQUEST_CODE_CHECK_LOCATION_ENABLED");
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                } else {
                    checkIfLocationPermissionsSatisfied();
                }
                break;
        }
    }

    public void onGettingCurrentLocation(Location currentLocation) {
        Log.d(TAG, "UpdatedLocation=" + currentLocation);
        if (currentLocation == null) {
            AppUtil.showToast(getApplicationContext(), getString(R.string.switching_to_last_known_loc));
            String lat = defaultSp.getString("lat", null);
            String lon = defaultSp.getString("lon", null);
            if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)) {
                Location mLocation = new Location("");
                mLocation.setLatitude(Double.parseDouble(lat));
                mLocation.setLongitude(Double.parseDouble(lon));
                initiateEmergency(mLocation);
            } else {
                initiateEmergency(currentLocation);
            }
        } else {
            initiateEmergency(currentLocation);
        }
        super.onGettingCurrentLocation(currentLocation);
    }

    private void initiateEmergency(Location mLocation) {
        if (mLocation != null) {
            startIntentServiceFetchAddress(mLocation);
        }
        PatientUserVO mPatientUserVO = mDatabaseHandler.getLoggedInUserDetails();
        if (mPatientUserVO.isUnderEmergency()) {
            tvTimerCount.setVisibility(View.GONE);
        } else {
            tvTimerCount.setVisibility(View.VISIBLE);
            defaultInitiatingEmergencyForSelf();
        }
    }

    protected void startIntentServiceFetchAddress(Location mLastLocation) {
        Intent intent = new Intent(getApplicationContext(), IntentServiceFetchAddress.class);
        intent.putExtra(getResources().getString(R.string.package_name) + "." + Constant.RECEIVER, mResultReceiver);
        intent.putExtra(getString(R.string.package_name) + "." + Constant.LOCATION_DATA_EXTRA, mLastLocation);
        getApplicationContext().startService(intent);
    }

    public void askToSwitchOnLocation() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySelectPatientInEmergency.this);
        builder.setMessage("It seems your location service is turned off or not set to High Accuracy mode in your device settings.Setting these up will help us to locate you precisely and display better results")
                .setCancelable(false)
                .setNegativeButton(getString(R.string.label_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startLocationUpdates();
                        getLatestLocation();
                    }
                })
                .setPositiveButton(getString(R.string.switch_on), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_CHECK_LOCATION_ENABLED);
                    }
                });
        alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

        }
    }

}

