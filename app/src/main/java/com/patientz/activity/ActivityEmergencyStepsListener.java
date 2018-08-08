package com.patientz.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.patientz.VO.PublicProviderVO;
import com.patientz.service.ServiceEmergencyStepsBroadCaster;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.HashMap;

public class ActivityEmergencyStepsListener extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ActivityEmergencyStepsListener";
    BroadcastReceiver mReceiver = null;
    private long patientId;
    private String emergencyStatus;
    private LocalBroadcastManager mLocalBroadcastManager;
    private boolean serviceAlreadyCalled;
    private AnimationDrawable animationDrawable;
    private TelephonyManager mTM;
    private boolean callTrackScreen;
    private EndCallListener callListener;
    private String ambulanceProvider;
    private String ambulanceProviderName = "108";
    private String ambulanceProviderNo = "108";
    private SharedPreferences defaultSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_initialization);
        Log.d(TAG, "************** onCreate **************");
        final TextView tvCallingAmbulanceProvider = (TextView) findViewById(R.id.tv_calling_ambulance_provider);
        Intent mIntent = getIntent();
        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        ambulanceProviderNo = mIntent.getStringExtra("ambulance_provider_pno");
        Log.d(TAG, "APPNAME=" + mIntent.getStringExtra("ambulance_provider_name"));
        Log.d(TAG, "APPNUMBER=" + ambulanceProviderNo);
        if (!TextUtils.isEmpty(ambulanceProviderNo)) {
            ambulanceProviderNo = mIntent.getStringExtra("ambulance_provider_pno");
            ambulanceProviderName = mIntent.getStringExtra("ambulance_provider_name");
            mEditor.putString("emergencyNumber", ambulanceProviderNo);
            mEditor.commit();
        } else {
            final PublicProviderVO mPublicProviderVO = AppUtil.getPublicEmergencyProvider(getApplicationContext());
            ambulanceProviderNo = (mPublicProviderVO != null ? mPublicProviderVO.getEmergencyPhoneNo() : "108");
        }
        if (savedInstanceState != null) {
            serviceAlreadyCalled = savedInstanceState.getBoolean("service_already_called");
        }
        TextView mTextView = (TextView) findViewById(R.id.tv_emergency_token);
        String emergencyTokenPart1 = mTextView.getText().toString();
        defaultSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (defaultSp.getString("emergency_token", "").equalsIgnoreCase("")) {
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(emergencyTokenPart1 + (defaultSp.getString("emergency_token", "")));
        }
        final TextView tvBroadcastValue = (TextView) findViewById(R.id.tv_broadcast_value);
        final LinearLayout llGettingLocation = (LinearLayout) findViewById(R.id.rl_getting_loc);
        final LinearLayout llAlertingEmri = (LinearLayout) findViewById(R.id.rl_alerting_emri);
        final LinearLayout llNotifyingContacts = (LinearLayout) findViewById(R.id.rl_notifying_contacts);
        final LinearLayout rlCallingAmbulanceProvider = (LinearLayout) findViewById(R.id.rl_calling_108);

        final TextView tvAlertingEmri = (TextView) findViewById(R.id.tv_alerting_emri);
        final TextView tvNotifyingContacts = (TextView) findViewById(R.id.tv_notifying_contacts);
        final TextView tvSkipCall = (TextView) findViewById(R.id.tv_skip_call);
        final TextView tvCounterValueHolder = (TextView) findViewById(R.id.tv_counter_value_holder);
        final ImageView mProgressBar = (ImageView) findViewById(R.id.progress_bar);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
        animationDrawable.start();
        tvSkipCall.setOnClickListener(this);
        // Register receiver to broadcast and set filters and receive broadcasted data

        mLocalBroadcastManager = LocalBroadcastManager
                .getInstance(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_LOCATION_BROADCAST);
        filter.addAction(Constant.ACTION_EMRI_ALERT);
        filter.addAction(Constant.ACTION_NOTIFY_CONTACTS);
        filter.addAction(Constant.ACTION_CALL_EMERGENCY_NUMBER);
        filter.addAction(Constant.ACTION_TEST_EMERGENCY_COUNTER);

        final String finalAmbulanceProviderName = ambulanceProviderName;
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equalsIgnoreCase(Constant.ACTION_LOCATION_BROADCAST)) {
                    Log.d(TAG, "BROAD CAST RECEIVED ACTION_LOCATION_BROADCAST");
                    llGettingLocation.setVisibility(View.VISIBLE);
                    tvBroadcastValue.setText(intent.getStringExtra("status"));


                }
                if (action.equalsIgnoreCase(Constant.ACTION_EMRI_ALERT)) {
                    tvCounterValueHolder.setText("");
                    llAlertingEmri.setVisibility(View.VISIBLE);
                    Log.d(TAG, "BROAD CAST RECEIVED ACTION_EMRI_ALERT");
                    if (intent.getIntExtra("statusCode", 0) == (Constant.STATUS_CODE_PROCESSING)) {
                        Log.d(TAG, "STATUS_CODE_PROCESSING");
                        Log.d(TAG, "COUNTER VALUE=" + intent.getStringExtra("progressMsg"));
                        tvCounterValueHolder.setText(intent.getStringExtra("progressMsg"));
                        tvAlertingEmri.setText(intent.getStringExtra("status"));
                    } else if (intent.getIntExtra("statusCode", 0) == (Constant.STATUS_CODE_COMPLETED)) {
                        Log.d(TAG, "STATUS_CODE_COMPLETED");
                        tvAlertingEmri.setText(intent.getStringExtra("status"));
                    } else {
                        Log.d(TAG, "STATUS_CODE_FAILED");
                        tvAlertingEmri.setText(intent.getStringExtra("status"));
                    }
                }
                if (action.equalsIgnoreCase(Constant.ACTION_NOTIFY_CONTACTS)) {
                    tvCounterValueHolder.setText("");
                    Log.d(TAG, "BROAD CAST RECEIVED ACTION_NOTIFY_CONTACTS");
                    llNotifyingContacts.setVisibility(View.VISIBLE);

                    if (intent.getIntExtra("statusCode", 0) == (Constant.STATUS_CODE_PROCESSING)) {
                        Log.d(TAG, "STATUS_CODE_PROCESSING");
                        Log.d(TAG, "COUNTER VALUE=" + intent.getStringExtra("progressMsg"));
                        tvCounterValueHolder.setText(intent.getStringExtra("progressMsg"));
                        tvNotifyingContacts.setText(intent.getStringExtra("status"));

                    } else if (intent.getIntExtra("statusCode", 0) == (Constant.STATUS_CODE_COMPLETED)) {
                        Log.d(TAG, "STATUS_CODE_COMPLETED");
                        tvNotifyingContacts.setText(intent.getStringExtra("status"));
                    } else {
                        Log.d(TAG, "STATUS_CODE_FAILED");
                        tvNotifyingContacts.setText(intent.getStringExtra("status"));
                    }
                }
                if (action.equalsIgnoreCase(Constant.ACTION_CALL_EMERGENCY_NUMBER)) {
                    tvCounterValueHolder.setText("");
                    rlCallingAmbulanceProvider.setVisibility(View.VISIBLE);
                    tvCallingAmbulanceProvider.setText((!TextUtils.isEmpty(finalAmbulanceProviderName) ? ("Calling " + finalAmbulanceProviderName) : ("Calling " + (finalAmbulanceProviderName == null ? "EMRI" : finalAmbulanceProviderName))));
                    emergencyStatus = intent.getStringExtra(Constant.EMERGENCY_STATUS_TEST);
                    patientId = intent.getLongExtra("patientId", 0);

                    Log.d(TAG, "BROAD CAST RECEIVED ACTION_CALL_EMERGENCY_NUMBER");

                    if (intent.getIntExtra("statusCode", 0) == (Constant.STATUS_CODE_PROCESSING)) {
                        Log.d(TAG, "STATUS_CODE_PROCESSING");
                        Log.d(TAG, "COUNTER VALUE=" + intent.getStringExtra("progressMsg"));
                        tvCounterValueHolder.setText(intent.getStringExtra("progressMsg"));
                        if (emergencyStatus.equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                            tvSkipCall.setVisibility(View.GONE);
                        } else {
                            tvSkipCall.setText(getString(R.string.label_skip_call));
                            tvSkipCall.setVisibility(View.VISIBLE);
                        }
                    } else if (intent.getIntExtra("statusCode", 0) == (Constant.STATUS_CODE_COMPLETED)) {
                        callListener = new EndCallListener();
                        mTM = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
                        if (!emergencyStatus.equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                            Log.d(TAG, "STATUS_CODE_COMPLETED");
                            tvSkipCall.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "PATIENT ID=" + patientId);
                        } else {
                            tvSkipCall.setVisibility(View.GONE);
                        }
                    }
                }
                if (action.equalsIgnoreCase(Constant.ACTION_TEST_EMERGENCY_COUNTER)) {
                    if (intent.getIntExtra("statusCode", 0) == (Constant.STATUS_CODE_PROCESSING)) {
                        Log.d(TAG, "STATUS_CODE_PROCESSING");
                        Log.d(TAG, "COUNTER VALUE=" + intent.getStringExtra("progressMsg"));
                        tvCounterValueHolder.setText(intent.getStringExtra("progressMsg"));
                        tvSkipCall.setVisibility(View.GONE);
                    } else if (intent.getIntExtra("statusCode", 0) == (Constant.STATUS_CODE_COMPLETED)) {
                        if (patientId != 0) {
                            Intent mIntent = new Intent(ActivityEmergencyStepsListener.this, ActivityEmergencyTrackAndRevoke.class);
                            mIntent.putExtra("patientId", patientId);
                            mIntent.putExtra("ambulance_provider_name", ambulanceProviderName);
                            mIntent.putExtra("ambulance_provider_pno", ambulanceProviderNo);
                            startActivity(mIntent);
                            finish();
                        } else {
                            finish();
                        }

                    }
                }

            }
        };

        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
        if (!serviceAlreadyCalled) {
            startEmergencyService();
        }


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
    }

    private void setPageTitle(Toolbar toolbar) {
        SharedPreferences mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPreferences.getBoolean(Constant.IS_TEST_EMERGENCY, false)) {
            toolbar.setTitle(R.string.test);
        } else {
            toolbar.setTitle(R.string.label_initiating_emergency);
        }
    }

    private void startEmergencyService() {
        Intent intent = new Intent(this, ServiceEmergencyStepsBroadCaster.class);
        intent.putExtra("ambulance_provider_name", ambulanceProviderName);
        intent.putExtra("ambulance_provider_pno", ambulanceProviderNo);
        startService(intent);

    }

    private class EndCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (TelephonyManager.CALL_STATE_RINGING == state) {
                Log.d(TAG, "CALL_STATE_RINGING");
                callTrackScreen = true;
            }
            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                Log.d(TAG, "CALL_STATE_OFFHOOK");
                callTrackScreen = true;
            }
            if (TelephonyManager.CALL_STATE_IDLE == state) {
                Log.d(TAG, "CALL_STATE_IDLE");
                if (!emergencyStatus.equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                    if (callTrackScreen) {
                        if (patientId != 0) // close activity if stranger
                        {
                            createUpshotEvent();
                            Intent mIntent = new Intent(ActivityEmergencyStepsListener.this, ActivityEmergencyTrackAndRevoke.class);
                            mIntent.putExtra("patientId", patientId);
                            mIntent.putExtra("ambulance_provider_name", ambulanceProviderName);
                            mIntent.putExtra("ambulance_provider_pno", ambulanceProviderNo);
                            startActivity(mIntent);
                            Log.d(TAG, "ActivityEmergencyTrackAndRevoke called");
                        }
                        finish();
                        Log.d(TAG, "ActivityEmergencyStepsListener finished");
                    }
                } else {
                    if (callTrackScreen) {
                        createUpshotEvent();
                        Intent mIntent = new Intent(ActivityEmergencyStepsListener.this, ActivityEmergencyTrackAndRevoke.class);
                        mIntent.putExtra("patientId", patientId);
                        startActivity(mIntent);
                        finish();
                    }
                }
            }
        }
    }

    private void createUpshotEvent() {
        HashMap<String, Object> upshotData = new HashMap<>();
        upshotData.put(Constant.UpshotEvents.EMERGENCY_CALL_HANDLED, true);
        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_CALL_HANDLED);
        Log.d(TAG, "upshot data=" + upshotData.entrySet());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip_call:
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.EMERGENCY_SKIP_CALL_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_SKIP_CALL_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                Log.d(TAG, "Killing Activity");
                if (mTM != null)
                    mTM.listen(callListener, PhoneStateListener.LISTEN_NONE);
                sendBroadcast(
                        Constant.ACTION_STOP_CALL_AMBULANCE_COUNTER,
                        "", Constant.STATUS_CODE_SUCCESS);

                if (!emergencyStatus.equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                    if (patientId != 0) { // If user is a stranger
                        Intent mIntent = new Intent(ActivityEmergencyStepsListener.this, ActivityEmergencyTrackAndRevoke.class);
                        mIntent.putExtra("patientId", patientId);
                        startActivity(mIntent);
                        finish();
                    } else {
                        sendBroadcast(
                                Constant.ACTION_STOP_SERVICE,
                                "", Constant.STATUS_CODE_SUCCESS);
                        finish();
                    }
                } else {
                    if (patientId == 0) { // If user is  a stranger
                        sendBroadcast(
                                Constant.ACTION_STOP_SERVICE,
                                "", Constant.STATUS_CODE_SUCCESS);
                        finish();
                    } else {
                        Intent mIntent = new Intent(ActivityEmergencyStepsListener.this, ActivityEmergencyTrackAndRevoke.class);
                        mIntent.putExtra("patientId", patientId);
                        startActivity(mIntent);
                        finish();
                    }
                }
                break;
        }

    }

    public void sendBroadcast(String key, String value, int statusCode) {
        Intent mIntent = new Intent(key);
        mIntent.putExtra("value", value);
        mIntent.putExtra("statusCode", statusCode);
        mLocalBroadcastManager.sendBroadcast(mIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "************** onRestoreInstanceState **************");
        serviceAlreadyCalled = savedInstanceState.getBoolean("service_already_called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "************** onSaveInstanceState **************");
        outState.putBoolean("service_already_called", true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "************* onDestroy **************");
        Log.d(TAG, "mReceiver" + mReceiver);
        if (mReceiver != null) {
            mLocalBroadcastManager.unregisterReceiver(mReceiver);
        }
        if (mTM != null)
            mTM.listen(callListener, PhoneStateListener.LISTEN_NONE);
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_cancel, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "********* onStop ***********");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "********* onRestart ***********");

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
}
