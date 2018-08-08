package com.patientz.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.EmergencyStatusVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.EmergencyUtil;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ActivityEmergencyTrackAndRevoke extends BaseActivity implements View.OnClickListener {
    int revokeCode;
    long patientId;
    private static final String TAG = "ActivityEmergencyTrackAndRevoke";
    private ImageView ivLocationStatus, ivNotifiedEmri, ivNotifiedContacts, ivNotifiedAmbulance, ivShare, ivNearbyHospitals;
    private PatientUserVO mPatientUserVO;
    private EmergencyStatusVO emergencyStatusVO;
    private Button btCallEMRI, btRedial, btTrack;
    private String emergencyNumber;
    private SharedPreferences defaultSp;
    private ProgressDialog mProgressDialog;
    private DatabaseHandler mDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "**************** onCreate *********************");
        setContentView(R.layout.activity_emergency_track_and_revoke);
        mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());

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
        btRedial = (Button) findViewById(R.id.bt_redial);
        ImageView ivMap = (ImageView) findViewById(R.id.iv_map);
        TextView tvRevoke = (TextView) findViewById(R.id.tv_revoke);
        btTrack = (Button) findViewById(R.id.bt_track);
        btTrack.setOnClickListener(this);
        btCallEMRI = (Button) findViewById(R.id.bt_call_108);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        ivShare.setOnClickListener(this);
        btCallEMRI.setOnClickListener(this);
        btRedial.setOnClickListener(this);
        tvRevoke.setOnClickListener(this);

        ivLocationStatus = (ImageView) findViewById(R.id.iv_location_status);
        ivLocationStatus.setOnClickListener(this);

        ivNotifiedEmri = (ImageView) findViewById(R.id.iv_notified_emri);
        ivNotifiedEmri.setOnClickListener(this);

        ivNotifiedContacts = (ImageView) findViewById(R.id.iv_notified_contacts);
        ivNotifiedContacts.setOnClickListener(this);

        ivNotifiedAmbulance = (ImageView) findViewById(R.id.iv_notified_ambulance);
        ivNotifiedAmbulance.setOnClickListener(this);

        ivNearbyHospitals = (ImageView) findViewById(R.id.iv_nearby_hospital);
        ivNearbyHospitals.setOnClickListener(this);
        defaultSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent mIntent = getIntent();
        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, getApplicationContext().MODE_PRIVATE);
        emergencyNumber = mSharedPreferences.getString("emergencyNumber", "108");
        patientId = mIntent.getLongExtra("patientId", 0);
        setRedialButtonVisibility();
        TextView mTextView = (TextView) findViewById(R.id.tv_emergency_token);
        String emergencyTokenPart1 = mTextView.getText().toString();
        SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (defaultSp.getString("emergency_token", "").equalsIgnoreCase("")) {
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(emergencyTokenPart1 + (defaultSp.getString("emergency_token", "")));
        }
        TextView tvDisplayPersonInEmergency = (TextView) findViewById(R.id.tv_display_person_in_emergency_part1);
        SharedPreferences sp = getApplicationContext().getSharedPreferences(
                Constant.EMERGENCY_SHARED_PREFERENCE,
                getApplicationContext().MODE_PRIVATE);
        boolean isMapSnapshotAvailable = sp.getBoolean(
                "EmergencyLocationImage", false);
        if (isMapSnapshotAvailable) {
            String STORAGE_PATH = getResources().getString(
                    R.string.profileImagePath);
            File cnxDir = new File(Environment.getExternalStorageDirectory(),
                    STORAGE_PATH);
            Bitmap bitmap = BitmapFactory.decodeFile(cnxDir
                    + "/EmergencyLocation.png");
            ivMap.setImageBitmap(bitmap);
        }

        emergencyStatusVO = mDatabaseHandler.getUserDetailsForActivityEmergencyTrackAndRevokeScreenFromEmergencyTable(patientId);
        if(emergencyStatusVO.getEmergencyStatus()!=null) {
            if (emergencyStatusVO.getEmergencyStatus().equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                tvRevoke.setText(getString(R.string.label_finish));
            }
        }
            Log.d(TAG, "PATIENT ID=" + patientId);
            mPatientUserVO = mDatabaseHandler.getUserDetailsForActivityEmergencyTrackAndRevokeScreen(patientId);
                if (emergencyStatusVO != null) {
                    Log.d(TAG, "EMERGENCY STATUS NOT NULL");
                    setEmergencyStatus(emergencyStatusVO);
            }
            tvDisplayPersonInEmergency.setText(mPatientUserVO.getFirstName());
    }
    private void setPageTitle(Toolbar toolbar) {
        SharedPreferences mSharedPreferences= PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPreferences.getBoolean(Constant.IS_TEST_EMERGENCY,false)) {
            toolbar.setTitle(R.string.test_emergency_tracker);
        }else
        {
            toolbar.setTitle(R.string.emergency_tracker);
        }
    }

    private void setRedialButtonVisibility() {

        SharedPreferences sp = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, getApplicationContext().MODE_PRIVATE);
        String emergencyNumber = sp.getString("emergencyNumber", getApplicationContext().getString(R.string.ambulanceNo));
        if (emergencyNumber.equalsIgnoreCase("108") || emergencyNumber.equalsIgnoreCase("")) {
            btRedial.setVisibility(View.GONE);
        } else {
            btRedial.setVisibility(View.VISIBLE);
        }
    }

    private void setEmergencyStatus(EmergencyStatusVO emergencyStatusVO) {
        if(emergencyStatusVO.getEmergencyStatus()!=null) {
            if (emergencyStatusVO.getEmergencyStatus().equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                ivLocationStatus.setImageResource(R.drawable.emergency_green_tick);
                ivNotifiedEmri.setImageResource(R.drawable.emergency_green_tick);
                ivNotifiedContacts.setImageResource(R.drawable.emergency_green_tick);
                ivNotifiedAmbulance.setImageResource(R.drawable.emergency_green_tick);
            }else
            {
                if (emergencyStatusVO.isHasGotEmergencyLocation()) {
                    ivLocationStatus.setImageResource(R.drawable.emergency_green_tick);
                }
                if (emergencyStatusVO.isHasEmriNotified()) {
                    ivNotifiedEmri.setImageResource(R.drawable.emergency_green_tick);
                }
                if (emergencyStatusVO.isHasNotifiedContacts()) {
                    ivNotifiedContacts.setImageResource(R.drawable.emergency_green_tick);
                }
                if (emergencyStatusVO.isHasCalledAmbulance()) {
                    ivNotifiedAmbulance.setImageResource(R.drawable.emergency_green_tick);
                }
            }
        }else {
            if (emergencyStatusVO.isHasGotEmergencyLocation()) {
                ivLocationStatus.setImageResource(R.drawable.emergency_green_tick);
            }
            if (emergencyStatusVO.isHasEmriNotified()) {
                ivNotifiedEmri.setImageResource(R.drawable.emergency_green_tick);
            }
            if (emergencyStatusVO.isHasNotifiedContacts()) {
                ivNotifiedContacts.setImageResource(R.drawable.emergency_green_tick);
            }
            if (emergencyStatusVO.isHasCalledAmbulance()) {
                ivNotifiedAmbulance.setImageResource(R.drawable.emergency_green_tick);
            }
        }
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
    public void onResume() {
        super.onResume();
    }


    public void revokeEmergency() {
        CharSequence[] values = new String[3];
        values = getApplicationContext().getResources().getStringArray(R.array.revoke_reasons);
        new AlertDialog.Builder(this)
                .setTitle(getApplicationContext().getString(R.string.msg_dialog_onclick_revoke_button))
                .setSingleChoiceItems(values, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                                revokeCode = whichButton + 1;
                                initiateRevoke();
                            }
                        }).create().show();
    }

    private void initiateRevoke() {
        PatientUserVO loggedPatientVO = AppUtil
                .getLoggedPatientVO(getApplicationContext());

        String sms = EmergencyUtil.prepareSMSForServer(
                getApplicationContext(), patientId,
                loggedPatientVO.getUserProfileId(), "rvk", null, revokeCode, getApplicationContext(), emergencyNumber);
        if (AppUtil.isOnline(getApplicationContext())) {
            RequestQueue mRequestQueue = AppVolley.getRequestQueue();
            String emergencyApiKey = mDatabaseHandler.getEmergencyApiKeyOfPatient(patientId);
            Log.d(TAG, "EMERGENCY API KEY=" + emergencyApiKey);
            Log.d(TAG, "SMS=" + sms);
            mProgressDialog=CommonUtils.showProgressDialogWithCustomMsg(ActivityEmergencyTrackAndRevoke.this,getString(R.string.label_revoking_emergency));
            mRequestQueue.add(createEmergencyWebServiceCallRequest(sms, emergencyApiKey));
        } else {
            revokeUsingServerSms(sms);
            deleteEmergencyFromTable();
            finish();
        }
    }




    private StringRequest createEmergencyWebServiceCallRequest(final String smsServerWebService, final String emergencyApiKey) {

        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.callEmergency;
        Log.e("url----> ", szServerUrl);

        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "revoke response : " + response);
                ResponseVO responseVo = null;
                try {
                    if (response != null) {
                        Log.d(TAG, "Got webservice response");
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<ResponseVO>() {
                        }.getType();

                        Log.d(TAG, "Parsing Response VO");
                        responseVo = gson.fromJson(
                                response, objectType);

                        if (responseVo != null) {
                            Log.d(TAG, "ResponseVO not null");
                            responseHandler(responseVo, smsServerWebService);
                        } else {
                            responseHandler(responseVo, smsServerWebService);
                        }
                    } else {
                        responseHandler(responseVo, smsServerWebService);
                    }
                } catch (JsonParseException e) {
                    responseHandler(responseVo, smsServerWebService);
                }
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                cancelProgressDialog();
                revokeUsingServerSms(smsServerWebService);
                deleteEmergencyFromTable();
                AppUtil.showToast(getApplicationContext(), getString(R.string.revokeSuceesfulMSg));
                finish();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("smsVO", smsServerWebService);
                params.put("emergencyAPIKey", emergencyApiKey);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO, String smsServerWebService) {
        Log.d(TAG, "Inside responseHandler");

        if (mResponseVO != null) {

            int code = (int) mResponseVO.getCode();
            Log.d(TAG, "RESPONSE CODE=" + code);
            switch (code) {
                case Constant.RESPONSE_SUCCESS:
                    AppUtil.showToast(getApplicationContext(), getString(R.string.revokeSuceesfulMSg));
                    revokeUsingServerSms(smsServerWebService);
                    deleteEmergencyFromTable();
                    finish();
                    break;
                default:
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_revoke_failed));
                    revokeUsingServerSms(smsServerWebService);
                    deleteEmergencyFromTable();
                    finish();
                    break;
            }

        } else {
            revokeUsingServerSms(smsServerWebService);
            deleteEmergencyFromTable();
            finish();

        }
        cancelProgressDialog();
    }

    private void cancelProgressDialog() {
        if(mProgressDialog!=null)
        {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelProgressDialog();

    }

    private void deleteEmergencyFromTable() {
        //PatientUserVO mPatientUserVO = AppUtil.getPatientVO(patientId, getApplicationContext());
        mDatabaseHandler.updateUserTable(patientId, "", false);
        mDatabaseHandler.removeEmergency(patientId);
    }

    public void revokeUsingServerSms(String smsVO) {
        String prefixSMS = getString(R.string.smsCountryKeyWord);
        String serverNumber = getString(R.string.serverSMSnumber);
        String message = prefixSMS + " " + smsVO;
        if (message.length() <= 160) {

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(serverNumber, null, message, null, null);
            Log.d(TAG, "sending SMS to Server number: " + serverNumber + "  sms length:" + message.length());
        } else {
            Log.d(TAG, "sending SMS to Server number: " + serverNumber
                    + "failed due to sms length:" + message.length());
        }
    }

    @Override
    public void onClick(View v) {
        HashMap<String, Object> upshotData = new HashMap<>();

        switch (v.getId()) {
            case R.id.bt_track:
                upshotData.put(Constant.UpshotEvents.EMERGENCY_TRACK_EMERGENCY_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_TRACK_EMERGENCY_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                String emergencyToken = mPatientUserVO.getEmergencyToken();
                if (!TextUtils.isEmpty(emergencyToken)) {
                    String tokenURL="";
                    if(emergencyStatusVO.getEmergencyStatus()!=null) {
                        if (emergencyStatusVO.getEmergencyStatus().equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                            tokenURL = WebServiceUrls.test_emergency_url;
                        }else
                        {
                            tokenURL = WebServiceUrls.serverEmergencyUrl + emergencyToken;

                        }
                    }else {
                        tokenURL = WebServiceUrls.serverEmergencyUrl + emergencyToken;
                    }
                    Log.d(TAG,"track clicked");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(tokenURL));
                    if (i.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                        startActivity(i);
                    }
                } else {
                    AppUtil.showDialog(this,
                            getString(R.string.msg_revoke_screen_tracking_fail));
                }
                break;
            case R.id.bt_redial:
                upshotData.put(Constant.UpshotEvents.EMERGENCY_REDIAL_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_REDIAL_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                AppUtil.setDialerScreenDialogFlag(getApplicationContext(), false);
                SharedPreferences sp = getApplicationContext()
                        .getSharedPreferences(Constant.COMMON_SP_FILE, getApplicationContext().MODE_PRIVATE);
                String emergencyNumber = sp.getString("emergencyNumber", getApplicationContext().getString(R.string.ambulanceNo));
                AppUtil.call(getApplicationContext(), emergencyNumber);

                break;
            case R.id.bt_call_108:
                upshotData.put(Constant.UpshotEvents.EMERGENCY_108_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_108_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                AppUtil.setDialerScreenDialogFlag(getApplicationContext(), false);
                AppUtil.call(getApplicationContext(), "108");
                break;
            case R.id.iv_share:
                upshotData.put(Constant.UpshotEvents.EMERGENCY_SHARE_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_SHARE_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                String emergencyToken1 = mPatientUserVO.getEmergencyToken();
                if (!TextUtils.isEmpty(emergencyToken1)) {
                    String tokenURL="";
                    if(emergencyStatusVO.getEmergencyStatus()!=null) {
                        if (emergencyStatusVO.getEmergencyStatus().equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                            tokenURL = WebServiceUrls.test_emergency_url;
                        }else
                        {
                            tokenURL = WebServiceUrls.serverEmergencyUrl + emergencyToken1;

                        }
                    }else {
                        tokenURL = WebServiceUrls.serverEmergencyUrl + emergencyToken1;
                    }
                    AppUtil.share(getApplicationContext(), tokenURL);
                } else {
                    AppUtil.showDialog(this,
                            getString(R.string.tracker_url_not_found));
                }
                break;
            case R.id.iv_nearby_hospital:
                upshotData.put(Constant.UpshotEvents.EMERGENCY_NEARBY_HOSPITALS_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_NEARBY_HOSPITALS_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                Intent intentOrgType = new Intent(this, NearbyOrgsActivity.class);
                startActivity(intentOrgType);
                break;

            case R.id.tv_revoke:
                upshotData.put(Constant.UpshotEvents.EMERGENCY_REVOKE_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_REVOKE_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                Log.d(TAG,"REVOKE CLICKED");
                if(emergencyStatusVO.getEmergencyStatus()!=null) {
                    if (emergencyStatusVO.getEmergencyStatus().equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
                       finish();
                    }else
                    {
                        if (AppUtil.requestPermissions(ActivityEmergencyTrackAndRevoke.this, android.Manifest.permission.READ_SMS, 1)) {
                            Log.d(TAG,"READ PERMISSION ALREADY GRANTED");
                            revokeEmergency();
                        }
                    }
                }else {
                    if (AppUtil.requestPermissions(ActivityEmergencyTrackAndRevoke.this, android.Manifest.permission.READ_SMS, 1)) {
                        Log.d(TAG,"READ PERMISSION ALREADY GRANTED");
                        revokeEmergency();
                    }
                }
                break;


            case R.id.iv_location_status:
                if (!emergencyStatusVO.isHasGotEmergencyLocation()) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_track_page_location_status_failure));
                }
                break;
            case R.id.iv_notified_emri:
                if (!emergencyStatusVO.isHasEmriNotified()) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_track_page_emri_status_failure));
                }
                break;
            case R.id.iv_notified_contacts:
                if (!emergencyStatusVO.isHasNotifiedContacts()) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_track_page_contacts_status_failure));
                }
                break;
            case R.id.iv_notified_ambulance:
                if (!emergencyStatusVO.isHasCalledAmbulance()) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_track_page_ambulance_status_failure));
                }
                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtil.setDialerScreenDialogFlag(getApplicationContext(), true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ONPAUSE");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    revokeEmergency();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(ActivityEmergencyTrackAndRevoke.this,
                            android.Manifest.permission.READ_SMS)) {
                        launchSettingPermissionScreen();
                    }
                }
                break;
            }
        }

    }
    private void launchSettingPermissionScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.permission_grant_msg);
        builder.setTitle("");
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //   AppUtil.showDialog(this,getString(R.string.permission_grant_msg));
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
