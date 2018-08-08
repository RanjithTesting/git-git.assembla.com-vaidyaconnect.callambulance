package com.patientz.service;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.EMTResponseVO;
import com.patientz.VO.EmergencyContactsVO;
import com.patientz.VO.EmergencyStatusVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.AsyncAlertingEmri;
import com.patientz.utils.Constant;
import com.patientz.utils.EmergencyUtil;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.ksoap2.serialization.SoapPrimitive;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServiceEmergencyStepsBroadCaster extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "ServiceEmergencyStepsBroadCasterGetter";
    private GoogleApiClient mGoogleApiClient;
    private LocalBroadcastManager mLocalBroadcastManager;
    private PatientUserVO loggedInPatientUserVO;
    private RequestQueue mRequestQueue;
    private Location mLastLocation;
    private EmergencyStatusVO userNotInEmergency;
    private BroadcastReceiver serviceBroadCastReceiver;
    private DatabaseHandler mDatabaseHandler;
    private String emergencyProviderNumber;
    private CountDownTimer wCounter, emergencyCallCounter, emriCounter;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    final static int EMG_UPDATE = 777;
    private AsyncAlertingEmri alertingEmriAsync;
    private boolean emriWebserviceCallTimerFinish, emriWebserviceCallOnPostExecuteCalled;


    static int mCurUpdate = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EMG_UPDATE: {
                    Log.d(TAG, "EMG_UPDATE");
                    mCurUpdate++;
                    if (mCurUpdate == 300) {// 5*60 invoking location update webservice at every 5 min
                        sendLocationUpdate();
                        mCurUpdate = 0;
                        mHandler.removeMessages(EMG_UPDATE);
                        Message msgg = mHandler.obtainMessage(EMG_UPDATE);
                        mHandler.sendMessageDelayed(msgg, 1000);
                    } else {
                        Log.d(TAG, "!EMG_UPDATE");
                        Message msgNew = mHandler.obtainMessage(EMG_UPDATE);
                        mHandler.sendMessageDelayed(msgNew, 1000);
                    }
                }
                break;

                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "******************* ONCREATE ******************* \n");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "******************* onStartCommand ******************* \n");
        if (intent != null) {
            Log.d(TAG, "******************* intent available ******************* \n");
            mRequestQueue = AppVolley.getRequestQueue();
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
            mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.ACTION_STOP_CALL_AMBULANCE_COUNTER);
            serviceBroadCastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equalsIgnoreCase(Constant.ACTION_STOP_CALL_AMBULANCE_COUNTER)) {
                        Log.d(TAG, "ON SKIP CLICKED >> CANCELLING COUNTER");
                        if (emergencyCallCounter != null) {
                            emergencyCallCounter.cancel();
                        }
                    }
                    if (action.equalsIgnoreCase(Constant.ACTION_STOP_SERVICE)) {
                        stopSelf();
                        Log.d(TAG, "serviceBroadCastReceiver>>service stopped");
                    }
                }
            };
            mLocalBroadcastManager.registerReceiver(serviceBroadCastReceiver, filter);
            loggedInPatientUserVO = AppUtil.getLoggedPatientVO(getApplicationContext());

            emergencyProviderNumber = intent.getStringExtra("ambulance_provider_pno");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
        return START_NOT_STICKY; // tells the system not to bother to restart the service, even when it has sufficient memory
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "******************* onConnected ******************* \n");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String lat = defaultSp.getString("lat", null);
            String lon = defaultSp.getString("lon", null);
            if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)) {
                mLastLocation = new Location("");
                mLastLocation.setLatitude(Double.parseDouble(lat));
                mLastLocation.setLongitude(Double.parseDouble(lon));
            }
        } else {
            //TODO show toast saying location permission not granted
        }
        userNotInEmergency = mDatabaseHandler.getUserNotInEmergency();
        if (!userNotInEmergency.getEmergencyStatus().equalsIgnoreCase(Constant.EMERGENCY_STATUS_TEST)) {
            Log.d(TAG, "******************* STARTING REAL EMERGENCY ******************* \n");
            broadCastLocationStatus();
        } else {
            wCounter = new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {
                    sendBroadcast(Constant.ACTION_TEST_EMERGENCY_COUNTER, (millisUntilFinished) / 1000 + " s", Constant.STATUS_CODE_PROCESSING, "");
                }

                public void onFinish() {
                    Log.d(TAG, "broadCastStrangerRegistrationStatus onFinish");
                    wCounter.cancel();
                    sendBroadcast(
                            Constant.ACTION_TEST_EMERGENCY_COUNTER,
                            "", Constant.STATUS_CODE_COMPLETED, "");
                }
            };
            wCounter.start();
            Log.d(TAG, "******************* STARTING TEST EMERGENCY ******************* \n");
            Log.d(TAG, "test emergency steps");
            sendBroadcast(
                    Constant.ACTION_LOCATION_BROADCAST,
                    "", Constant.STATUS_CODE_COMPLETED, "Location Found");
            sendBroadcast(
                    Constant.ACTION_NOTIFY_CONTACTS,
                    "", Constant.STATUS_CODE_COMPLETED, "Notified Contacts");
            sendBroadcast(
                    Constant.ACTION_EMRI_ALERT,
                    "", Constant.STATUS_CODE_COMPLETED, "Ambulance Provider Notified");
            sendBroadcast(
                    Constant.ACTION_CALL_EMERGENCY_NUMBER,
                    "", Constant.STATUS_CODE_COMPLETED, "Call Initiated");
        }
    }

    private void broadCastAllSteps() {
        makeWebServiceCall();
    }

    private void makeWebServiceCall() {
        if (userNotInEmergency.getPatientId() != 0) {
            Log.d(TAG, "**** INVOKING EMERGENCY FOR REGISTERED USER ****");
            broadCastCallWebserviceAndNotifyContacts();
        } else {
            Log.d(TAG, "**** INVOKING EMERGENCY FOR STRANGER ****");
            broadCastStrangerRegistrationStatus();
        }
    }

    private void broadCastStrangerRegistrationStatus() {
        //if (AppUtil.isOnline(getApplicationContext())) {
        wCounter = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                sendBroadcast(Constant.ACTION_NOTIFY_CONTACTS, (millisUntilFinished) / 1000 + " s", Constant.STATUS_CODE_PROCESSING, "Notifying Server");
            }

            public void onFinish() {
                Log.d(TAG, "broadCastStrangerRegistrationStatus onFinish");
                wCounter.cancel();
            }
        };
        wCounter.start();
        mRequestQueue.add(createStrangerEmergencyWebServiceCallRequest());
    }

    private StringRequest createStrangerEmergencyWebServiceCallRequest() {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.WEBSERVICE_INVOKE_EMERGENCY_FOR_STRANGER;

        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stopCounter();
                Log.d(TAG, "createStrangerEmergencyWebServiceCallRequest onResponse>> =" + response);
                EMTResponseVO responseVo = null;
                try {
                    if (response != null) {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<EMTResponseVO>() {
                        }.getType();

                        responseVo = gson.fromJson(
                                response, objectType);

                        if (responseVo != null) {
                            responseHandlerStranger(responseVo);
                        } else {
                            responseHandlerStranger(responseVo);
                        }
                    } else {
                        responseHandlerStranger(responseVo);
                    }
                } catch (JsonParseException e) {
                    responseHandlerStranger(responseVo);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "*******************createStrangerEmergencyWebServiceCallRequest onErrorResponse ******************* \n");
                stopCounter();
                proceedToEmriWebserviceCallStranger();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                SharedPreferences defaultSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                int reportedIssue = defaultSharedPreferences.getInt("selectedEmergencyType", 7);// infected body area
                params.put(Constant.STATUS, userNotInEmergency.getEmergencyStatus());
                params.put(Constant.PATIENT_ID, "0");
                params.put(Constant.REPORTED_ISSUE, String.valueOf(reportedIssue));
                params.put(Constant.INITIATED_LAT, String.valueOf(mLastLocation.getLatitude()));
                params.put(Constant.INITIATED_LON, String.valueOf(mLastLocation.getLongitude()));
                Log.d(TAG, params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    private void responseHandlerStranger(EMTResponseVO mResponseVO) {
        if (mResponseVO != null) {
            Log.d(TAG, "createStrangerEmergencyWebServiceCallRequest response code=" + mResponseVO.getCode());
            int code = (int) mResponseVO.getCode();
            switch (code) {
                case Constant.RESPONSE_SUCCESS:
                    sendBroadcast(
                            Constant.ACTION_NOTIFY_CONTACTS,
                            "", Constant.STATUS_CODE_COMPLETED, "Notified Server");
                    broadCastEmriAlert(mResponseVO.getEmergencyInfo().getTokenizedUrl());
                    break;
                default:
                    proceedToEmriWebserviceCallStranger();
                    break;
            }

        } else {
            proceedToEmriWebserviceCallStranger();
        }
    }

    private void proceedToEmriWebserviceCallStranger() {
        invokeEmergencyThroughServerSms();
        wCounter = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                sendBroadcast(Constant.ACTION_NOTIFY_CONTACTS, (millisUntilFinished) / 1000 + " s", Constant.STATUS_CODE_PROCESSING, "Notifying Server");
            }

            public void onFinish() {
                stopCounter();
                unregisterSmsReceivers();
                AppUtil.showToast(getApplicationContext(), getString(R.string.offline_emergency_failed));
                sendBroadcast(
                        Constant.ACTION_NOTIFY_CONTACTS,
                        "", Constant.STATUS_CODE_COMPLETED, "Notifying Server failed");
                broadCastCallEmergencyNumberStatus(userNotInEmergency);
            }
        };
        wCounter.start();
    }


    private void broadCastEmriAlert(String emergencyToken) {
        Log.d(TAG, "******************* broadCastEmriAlert ******************* \n");
        String counter_msg = "";
        if (userNotInEmergency.getPatientId() != 0) {
            counter_msg = "Notifying Ambulance Provider";
        } else {
            counter_msg = "Notifying EMRI";
        }
        final String finalCounter_msg = counter_msg;
        wCounter = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                sendBroadcast(Constant.ACTION_EMRI_ALERT, (millisUntilFinished) / 1000 + " s", Constant.STATUS_CODE_PROCESSING, finalCounter_msg);
            }

            public void onFinish() {
                alertingEmriAsync.cancel(true);
                emriWebserviceCallTimerFinish = true;
                if (!emriWebserviceCallOnPostExecuteCalled) {
                    stopCounter();
                    if (userNotInEmergency.getPatientId() != 0) {
                        mDatabaseHandler.updateCallToEmriStatus(userNotInEmergency.getPatientId(), false);
                    }
                    sendBroadcast(
                            Constant.ACTION_EMRI_ALERT,
                            "", Constant.STATUS_CODE_COMPLETED, "Failed Notifying Ambulance Provider");
                    broadCastCallEmergencyNumberStatus(userNotInEmergency);
                }
            }
        };
        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        alertingEmriAsync = new AsyncAlertingEmri(
                getApplicationContext(), mLastLocation, emergencyToken, mSharedPreferences.getString("address", ""), userNotInEmergency.getPatientId()) {
            @Override
            protected void onPostExecute(SoapPrimitive result) {
                super.onPostExecute(result);
                emriWebserviceCallOnPostExecuteCalled = true;
                if (!emriWebserviceCallTimerFinish) {
                    Log.d(TAG, "EMRI_WEBSERVICE_RESPONSE=" + result);
                    stopCounter();
                    if (result != null) {
                        try {
                            int resultCode = Integer.parseInt(result.toString());
                            switch (resultCode) {
                                case Constant.RESPONSE_SUCCESS_EMRI:
                                    Log.d(TAG, "RESPONSE_SUCCESS_EMRI");
                                    mSharedPreferences.edit().putInt(Constant.RESPONSE_EMRI_EMERGENCY, 1)
                                            .commit(); // Tell webservice that EMRI is already notified with location so dont bother to call it again
                                    mDatabaseHandler.updateCallToEmriStatus(userNotInEmergency.getPatientId(), true);
                                    if (userNotInEmergency.getPatientId() != 0) {
                                        sendBroadcast(
                                                Constant.ACTION_EMRI_ALERT,
                                                "", Constant.STATUS_CODE_COMPLETED, "Ambulance Provider Notified");
                                    } else {
                                        sendBroadcast(
                                                Constant.ACTION_EMRI_ALERT,
                                                "", Constant.STATUS_CODE_COMPLETED, "Ambulance Provider Notified");
                                    }
                                    Log.d(TAG, "6");

                                    broadCastCallEmergencyNumberStatus(userNotInEmergency);
                                    break;
                                default:
                                    if (userNotInEmergency.getPatientId() != 0) {
                                        sendBroadcast(
                                                Constant.ACTION_EMRI_ALERT,
                                                "", Constant.STATUS_CODE_FAILED, "Failed Notifying Ambulance Provider");
                                    } else {
                                        sendBroadcast(
                                                Constant.ACTION_EMRI_ALERT,
                                                "", Constant.STATUS_CODE_FAILED, "Failed Notifying Ambulance Provider");
                                    }
                                    Log.d(TAG, "7");
                                    broadCastCallEmergencyNumberStatus(userNotInEmergency);
                                    break;
                            }
                        } catch (Exception exception) {
                            if (userNotInEmergency.getPatientId() != 0) {
                                sendBroadcast(
                                        Constant.ACTION_EMRI_ALERT,
                                        "", Constant.STATUS_CODE_FAILED, "Failed Notifying Ambulance Provider");
                            } else {
                                sendBroadcast(
                                        Constant.ACTION_EMRI_ALERT,
                                        "", Constant.STATUS_CODE_FAILED, "Failed Notifying Ambulance Provider");
                            }
                            Log.d(TAG, "8");

                            broadCastCallEmergencyNumberStatus(userNotInEmergency);
                        }
                    } else {
                        if (userNotInEmergency.getPatientId() != 0) {
                            sendBroadcast(
                                    Constant.ACTION_EMRI_ALERT,
                                    "", Constant.STATUS_CODE_FAILED, "Failed Notifying Ambulance Provider");
                        } else {
                            sendBroadcast(
                                    Constant.ACTION_EMRI_ALERT,
                                    "", Constant.STATUS_CODE_FAILED, "Failed Notifying Ambulance Provider");
                        }
                        Log.d(TAG, "8");

                        broadCastCallEmergencyNumberStatus(userNotInEmergency);
                    }
                }
            }
        };
        alertingEmriAsync.execute();
        wCounter.start();
    }

    private void broadCastCallEmergencyNumberStatus(final EmergencyStatusVO emergencyStatusVO) {
        Log.d(TAG, "******************* broadCastCallEmergencyNumberStatus ******************* \n");
        startLocationUpdate();
        emergencyCallCounter = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendBroadcast(
                        Constant.ACTION_CALL_EMERGENCY_NUMBER,
                        (millisUntilFinished) / 1000 + " s", Constant.STATUS_CODE_PROCESSING, "");
            }

            @Override
            public void onFinish() {
                emergencyCallCounter.cancel();
                AppUtil.setDialerScreenDialogFlag(getApplicationContext(), false);
                callEmergency(emergencyStatusVO);
            }
        };
        emergencyCallCounter.start();
    }

    private void startLocationUpdate() {
        mHandler.removeMessages(EMG_UPDATE);
        Message msg = mHandler.obtainMessage(EMG_UPDATE);
        mHandler.sendMessageDelayed(msg, 1000);
    }


    private void callEmergency(final EmergencyStatusVO emergencyStatusVO) {
        Log.d(TAG, "******************** callEmergency ******************** \n");
        sendBroadcast(
                Constant.ACTION_CALL_EMERGENCY_NUMBER,
                "", Constant.STATUS_CODE_COMPLETED, "Initiating Call");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.msg_no_call_permission));
                    return;
                }
                final Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callIntent.setData(Uri.parse("tel:" + emergencyProviderNumber));
                startActivity(callIntent);
                mDatabaseHandler.updateCallToAmbulanceProviderStatus(userNotInEmergency.getPatientId(), true);
                if (emergencyStatusVO.getPatientId() == 0) {
                    stopSelf();
                }
            }
        }).start();
    }

    private void broadCastCallWebserviceAndNotifyContacts() {
        Log.d(TAG, "******************* broadCastCallWebserviceAndNotifyContacts ******************* \n");
        wCounter = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                sendBroadcast(Constant.ACTION_NOTIFY_CONTACTS, (millisUntilFinished) / 1000 + " s", Constant.STATUS_CODE_PROCESSING, "Notifying Contacts");
            }

            public void onFinish() {
                stopCounter();
            }
        };
        String emergencyApiKey = mDatabaseHandler.getEmergencyApiKeyOfPatient(userNotInEmergency.getPatientId());
        String smsServerWebService = EmergencyUtil.prepareWebVOForServer(getApplicationContext(),
                userNotInEmergency.getPatientId(),
                loggedInPatientUserVO.getUserProfileId(), userNotInEmergency.getEmergencyStatus(),
                mLastLocation, 0, getApplicationContext(), emergencyProviderNumber);
        Log.d(TAG, "broadCastCallWebserviceAndNotifyContacts>>params=" + smsServerWebService + "\n");
        mRequestQueue.add(createEmergencyWebServiceCallRequest(smsServerWebService, emergencyApiKey));
        wCounter.start();
    }


    private void setNotifiedContactStatus() {
        mDatabaseHandler.updateNotifiedContactStatus(userNotInEmergency.getPatientId(), true);
    }

    private StringRequest createEmergencyWebServiceCallRequest(final String smsServerWebService, final String emergencyApiKey) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.callEmergency;
        Log.d(TAG, "broadCastCallWebserviceAndNotifyContacts>>callEmergency" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stopCounter();
                Log.d(TAG, "createEmergencyWebServiceCallRequest RESPONSE >>" + response + "\n");
                ResponseVO responseVo = null;
                try {
                    if (response != null) {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<ResponseVO>() {
                        }.getType();
                        responseVo = gson.fromJson(
                                response, objectType);
                        responseHandler(responseVo);
                    } else {
                        responseHandler(responseVo);
                    }
                } catch (JsonParseException e) {
                    responseHandler(responseVo);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                stopCounter();
                invokeOfflineEmergency();
            }

        })


        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("webVO", smsServerWebService);
                params.put("emergencyAPIKey", emergencyApiKey);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, 0,
                0));
        mRequest.setTag("TAG_WEBSERVICE_CALL_EMERGENCY");
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {
        if (mResponseVO != null) {
            int code = (int) mResponseVO.getCode();
            switch (code) {
                case Constant.EMERGENCY_INVOKED_SUCCESSFULLY:
                    sendBroadcast(
                            Constant.ACTION_NOTIFY_CONTACTS,
                            "", Constant.STATUS_CODE_COMPLETED, "Notified Contacts");
                    mDatabaseHandler.updateUserTable(userNotInEmergency.getPatientId(), mResponseVO.getResponse(), true);
                    mDatabaseHandler.updateEmergencyTableWithWebserviceCallStatus(userNotInEmergency.getPatientId(), Constant.EMERGENCY_WEBSERVICE_SUCCESS);
                    setNotifiedContactStatus();
                    String emergencyToken = mDatabaseHandler.getEmergencyTokenOfUser(userNotInEmergency.getPatientId());
                    broadCastEmriAlert(emergencyToken);
                    break;
                default:
                    invokeOfflineEmergency();
                    break;
            }

        } else {
            invokeOfflineEmergency();
        }
    }

    private void invokeOfflineEmergency() {
        wCounter = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                sendBroadcast(Constant.ACTION_NOTIFY_CONTACTS, (millisUntilFinished) / 1000 + " s", Constant.STATUS_CODE_PROCESSING, "Notifying Contacts");
            }

            public void onFinish() {
                Log.d(TAG, "invokeOfflineEmergency>>onFinish" + "\n");
                stopCounter();
                unregisterSmsReceivers();
                AppUtil.showToast(getApplicationContext(), getString(R.string.offline_emergency_failed));
                sendBroadcast(
                        Constant.ACTION_NOTIFY_CONTACTS,
                        "", Constant.STATUS_CODE_COMPLETED, "Notifying contacts failed");
                setNotifiedContactStatus(false);
                mDatabaseHandler.updateUserTable(userNotInEmergency.getPatientId(), "", false);
                mDatabaseHandler.updateEmergencyTableWithWebserviceCallStatus(userNotInEmergency.getPatientId(), Constant.EMERGENCY_WEBSERVICE_FAILED);
                String emergencyToken = mDatabaseHandler.getEmergencyTokenOfUser(userNotInEmergency.getPatientId());
                broadCastEmriAlert(emergencyToken);
            }
        };
        wCounter.start();
        invokeEmergencyThroughServerSms();
        Log.d(TAG, "createEmergencyWebServiceCallRequest onErrorResponse" + "\n");
    }

    private void sendSMSToContacts() {
        //EmergencyStatusVO mStatusVO = getEmergencyStatus(patientId);
        Log.d(TAG, "******************* sendSMSToContacts ******************* \n");
        if (userNotInEmergency != null) {
            if (checkSmsToContact(userNotInEmergency.getSmsToContactsStatus())) {

                ArrayList<EmergencyContactsVO> mEmergencyContactsVOs = getAllEmergencyContacts(userNotInEmergency
                        .getPatientId());
                sendSMSTOEmergencyContacts(prepareSmsForContacts(userNotInEmergency.getPatientId(), userNotInEmergency.getLaunchLocation()), mEmergencyContactsVOs);

            }
        }
    }

    private String prepareSmsForContacts(long patientId, Location location) {
        PatientUserVO mPatientUserVO;
        String sms = getString(R.string.emergencySmsToContacts);
        mPatientUserVO = AppUtil.getPatientVO(patientId, getApplicationContext());
        if (TextUtils.isEmpty(mPatientUserVO.getEmergencyToken())) {

            if (mPatientUserVO.getFirstName() != null) {
                sms = EmergencyUtil.prepareInvokeSMSForContactsWithoutToken(mPatientUserVO.getFirstName(), location, getApplicationContext());
            }
        } else {
            String url = WebServiceUrls.serverEmergencyUrl;
            sms = EmergencyUtil.prepareInvokeSMSForContactWithToken(mPatientUserVO.getFirstName(), url + mPatientUserVO.getEmergencyToken(), getApplicationContext());
        }

        return sms;
    }

    public void sendSMSTOEmergencyContacts(String msg,
                                           ArrayList<EmergencyContactsVO> mContactsVOs) {
        if (mContactsVOs != null) {
            for (EmergencyContactsVO emergencyContactsVO : mContactsVOs) {
                if (!TextUtils.isEmpty(emergencyContactsVO.getPhoneNumber())) {
                    sendSMS(emergencyContactsVO.getPhoneNumber(), msg);
                }
            }
            AppUtil.showToast(getApplicationContext(), "sms sent to contacts");
        }
    }

    public void sendSMS(String number, String message) {
        String SENT = "SMS_SENT1";
        String DELIVERED = "SMS_DELIVERED1";

        PendingIntent sentPI = PendingIntent.getBroadcast(
                getApplicationContext(), 0,
                new Intent(SENT).putExtra("number", number),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(
                getApplicationContext(), 0,
                new Intent(DELIVERED).putExtra("time", new Date().toString()),
                PendingIntent.FLAG_UPDATE_CURRENT);

        //if (message.length() <= 160) {

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(number, null, message, sentPI, deliveredPI);

       /* } else {

        }*/
    }

    public ArrayList<EmergencyContactsVO> getAllEmergencyContacts(long patientId) {
        try {
            return mDatabaseHandler.getAllContacts(patientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void invokeEmergencyThroughServerSms() {
        Log.d(TAG, "invokeEmergencyThroughServerSms");
        smsSentReceiver = new SmsSentReceiver();
        smsDeliveredReceiver = new SmsDeliveredReceiver();
        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT_SERVER"));
        registerReceiver(smsDeliveredReceiver, new IntentFilter("SMS_DELIVERED_SERVER"));

        String smsServerMobile = EmergencyUtil.prepareSMSForServer(getApplicationContext(),
                userNotInEmergency.getPatientId(),
                loggedInPatientUserVO.getUserProfileId(), userNotInEmergency.getEmergencyStatus(),
                mLastLocation, 0, getApplicationContext(), emergencyProviderNumber);
        Log.d(TAG, "JSON FOR SMS COUNTRY=" + smsServerMobile);
        invokeServerSMS(smsServerMobile);
        Log.d(TAG, "UPDATING SMS TO SERVER STATUS=STATUS_SMS_SERVER_SENT");
    }


    public void invokeServerSMS(String smsVO) {
        String prefixSMS = getString(R.string.smsCountryKeyWord);
        String serverNumber = getString(R.string.serverSMSnumber);
        AppUtil.showToast(getApplicationContext(), getString(R.string.invoking_emergency_offline_msg));
        sendServerSMS(serverNumber, prefixSMS + " " + smsVO);
    }


    public void sendServerSMS(String numberServer, String message) {
        Log.d(TAG, "sendServerSMS");

        String SENT_SERVER = "SMS_SENT_SERVER";
        String DELIVERED_SERVER = "SMS_DELIVERED_SERVER";

        PendingIntent sentPIServer = PendingIntent.getBroadcast(
                getApplicationContext(), 0,
                new Intent(SENT_SERVER).putExtra("number", numberServer),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deliveredPIServer = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                new Intent(DELIVERED_SERVER).putExtra("time",
                        new Date().toString()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        //if (message.length() <= 160) {
        SmsManager sms = SmsManager.getDefault();
        Log.d(TAG, "sending SMS to Server number: " + numberServer + "/n sms length:" + message.length());

        // if(message.length()>160) {
        ArrayList<String> messageParts = sms.divideMessage(message);
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(messageParts.size());
        sentIntents.add(sentPIServer);
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>(messageParts.size());
        deliveryIntents.add(deliveredPIServer);
        sms.sendMultipartTextMessage(numberServer, null, messageParts, sentIntents,
                deliveryIntents);
    }

    private void setNotifiedContactStatus(boolean b) {
        mDatabaseHandler.updateNotifiedContactStatus(userNotInEmergency.getPatientId(), b);
    }

    public class SmsSentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "SmsSentReceiver-->onReceive");
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.d(TAG, "RESULT_ERROR_GENERIC_FAILURE");
                    AppUtil.showToastShort(getApplicationContext(), getString(R.string.offline_emergency_failure_no_sms_bal));
                    proceedToEmriWebserviceCall();
                    break;
                default:
                    Log.d(TAG, "RESULT_ERROR_NULL_PDU");
                    AppUtil.showToastShort(getApplicationContext(), getString(R.string.offline_emergency_failed));
                    proceedToEmriWebserviceCall();
                    break;
            }
        }
    }

    private void proceedToEmriWebserviceCall() {
        stopCounter();
        unregisterSmsReceivers();
        if (userNotInEmergency.getPatientId() != 0) {
            notifyEmri();
        } else {
            broadCastCallEmergencyNumberStatus(userNotInEmergency);
        }
    }

    private void stopCounter() {
        if (wCounter != null) {
            wCounter.cancel();
        }
    }

    private void unregisterSmsReceivers() {
        if (smsSentReceiver != null) {
            unregisterReceiver(smsSentReceiver);
            Log.d(TAG, "smsSentReceiver unregistered");
        }
        if (smsDeliveredReceiver != null) {
            Log.d(TAG, "smsDeliveredReceiver unregistered");
            unregisterReceiver(smsDeliveredReceiver);
        }
    }

    private void notifyEmri() {

        sendBroadcast(
                Constant.ACTION_NOTIFY_CONTACTS,
                "", Constant.STATUS_CODE_COMPLETED, "Notifying contacts failed");
        setNotifiedContactStatus(false);
        mDatabaseHandler.updateUserTable(userNotInEmergency.getPatientId(), "", false);
        mDatabaseHandler.updateEmergencyTableWithWebserviceCallStatus(userNotInEmergency.getPatientId(), Constant.EMERGENCY_WEBSERVICE_FAILED);
        String emergencyToken = mDatabaseHandler.getEmergencyTokenOfUser(userNotInEmergency.getPatientId());
        broadCastEmriAlert(emergencyToken);
    }

    public class SmsDeliveredReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "SmsDeliveredReceiver-->onReceive");
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    stopCounter();
                    unregisterSmsReceivers();
                    Log.d(TAG, "SmsDeliveredReceiver-->RESULT_OK");
                    Toast.makeText(getBaseContext(), getString(R.string.emergency_invoked_through_sms), Toast.LENGTH_SHORT).show();
                    if (userNotInEmergency.getPatientId() != 0) {
                        sendSMSToContacts();
                        setNotifiedContactStatus(true);
                        mDatabaseHandler.updateUserTable(userNotInEmergency.getPatientId(), "", true);
                        mDatabaseHandler.updateEmergencyTableWithWebserviceCallStatus(userNotInEmergency.getPatientId(), Constant.EMERGENCY_WEBSERVICE_SUCCESS);
                        sendBroadcast(
                                Constant.ACTION_NOTIFY_CONTACTS,
                                "", Constant.STATUS_CODE_COMPLETED, "Notified Contacts");
                        broadCastEmriAlert(mDatabaseHandler.getEmergencyTokenOfUser(userNotInEmergency.getPatientId()));
                    } else {
                        sendBroadcast(
                                Constant.ACTION_NOTIFY_CONTACTS,
                                "", Constant.STATUS_CODE_COMPLETED, "Notified Server");
                        broadCastCallEmergencyNumberStatus(userNotInEmergency);
                    }

                    break;
                case Activity.RESULT_CANCELED:
                    stopCounter();
                    unregisterSmsReceivers();
                    Log.d(TAG, "SmsDeliveredReceiver-->RESULT_CANCELED");
                    if (userNotInEmergency.getPatientId() != 0) {
                        Toast.makeText(getBaseContext(), "Emergency invoking through SMS failed.Please check if you have SMS balance", Toast.LENGTH_SHORT).show();
                        sendBroadcast(
                                Constant.ACTION_NOTIFY_CONTACTS,
                                "", Constant.STATUS_CODE_COMPLETED, "Notifying contacts failed");
                        setNotifiedContactStatus(false);
                        mDatabaseHandler.updateUserTable(userNotInEmergency.getPatientId(), "", false);
                        mDatabaseHandler.updateEmergencyTableWithWebserviceCallStatus(userNotInEmergency.getPatientId(), Constant.EMERGENCY_WEBSERVICE_FAILED);
                        broadCastEmriAlert(mDatabaseHandler.getEmergencyTokenOfUser(userNotInEmergency.getPatientId()));
                    } else {
                        sendBroadcast(
                                Constant.ACTION_NOTIFY_CONTACTS,
                                "", Constant.STATUS_CODE_COMPLETED, "Notifying Server Failed");
                        broadCastCallEmergencyNumberStatus(userNotInEmergency);
                    }

                    break;
            }
        }
    }


    public boolean checkWebserviceStatus(int status) {
        switch (status) {
            case Constant.EMERGENCY_WEBSERVICE_NOT_INITIATED:
                return true;
            case Constant.EMERGENCY_WEBSERVICE_INITIATED:
                return false;
            case Constant.EMERGENCY_WEBSERVICE_SUCCESS:
                return false;
            case Constant.EMERGENCY_WEBSERVICE_FAILED:
                return true;
        }
        return true;
    }

    public boolean checkSmsToServerStatus(int status) {
        switch (status) {
            case Constant.EMERGENCY_SMS_SERVER_NOT_INITIATED:
                return true;
            case Constant.EMERGENCY_SMS_SERVER_INITIATED:
                return false;
            case Constant.EMERGENCY_SMS_SERVER_SENT_FROM_DEVICE:
                return false;
            case Constant.EMERGENCY_SMS_SERVER_FAILED:
                return true;
            case Constant.EMERGENCY_SMS_SERVER_DELIVERED:
                return false;
        }
        return true;
    }

    public boolean checkSmsToContact(int status) {
        switch (status) {
            case Constant.EMERGENCY_SMS_CONTACT_NOT_INITIATED:
                return true;
            case Constant.EMERGENCY_SMS_CONTACT_INITIATED:
                return false;
            case Constant.EMERGENCY_SMS_CONTACT_SENT_FROM_DEVICE:
                return false;
            case Constant.EMERGENCY_SMS_CONTACT_FAILED:
                return true;
            case Constant.EMERGENCY_SMS_CONTACT_DELIVERED:
                return false;
        }
        return true;
    }


    public void updateEmergencyTable(EmergencyStatusVO mEmergencyStatusVO) {
        mDatabaseHandler.updateEmergencyTable(mEmergencyStatusVO);

    }

    public EmergencyStatusVO getEmergencyStatus(long patientId) {
        EmergencyStatusVO mStatusVO = new EmergencyStatusVO();
        mStatusVO = mDatabaseHandler.getEmergencyStatus(patientId);
        if (mStatusVO.getPatientId() != 0) {
            if (mStatusVO.getLaunchLocation() != null)
                Log.d(TAG, "launch location : " + mStatusVO.getLaunchLocation().toString());
            return mStatusVO;
        } else {

            return null;
        }
    }

    public ArrayList<EmergencyStatusVO> getAllPatientInEmergencyFromDevice() {
        ArrayList<EmergencyStatusVO> mEmergencyStatusVOs = new ArrayList<EmergencyStatusVO>();
        try {
            mEmergencyStatusVOs = mDatabaseHandler.getAllEmergencyInitiatedFromDevice();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mEmergencyStatusVOs;
    }

    private void broadCastLocationStatus() {
        Log.d(TAG, "******************* Location ******************* \n");
        if (mLastLocation != null) {
            Log.d(TAG, "LOCATION FOUND \n");
            userNotInEmergency = mDatabaseHandler.getUserNotInEmergency();
            if (userNotInEmergency.getPatientId() != 0) { // if user is not stranger
                userNotInEmergency.setCurrentLocation(mLastLocation);
                userNotInEmergency.setLaunchLocation(mLastLocation);
                userNotInEmergency.setLocationUpdateStatus(Constant.EMERGENCY_START_LOCATION_UPDATE);
                userNotInEmergency.setHasGotEmergencyLocation(true);
                updatePatientsEmergencyTableWithCurrentLocationDetails(userNotInEmergency);
            }
            sendBroadcast(
                    Constant.ACTION_LOCATION_BROADCAST,
                    "", Constant.STATUS_CODE_COMPLETED, "Location Found");
            broadCastAllSteps();
        } else {
            Log.d(TAG, "LOCATION NOT FOUND\n");
            sendBroadcast(
                    Constant.ACTION_LOCATION_BROADCAST,
                    "", Constant.STATUS_CODE_FAILED, "Location Not Found");
            broadCastAllSteps();

        }
        if (userNotInEmergency.getPatientId() != 0) { // if user is not stranger
            startPeriodicUpdates();
        }
    }

    private void updatePatientsEmergencyTableWithCurrentLocationDetails(EmergencyStatusVO mEmergencyStatusVO) {
        mDatabaseHandler.updateEmergencyTable(mEmergencyStatusVO);
    }


    public PatientUserVO getEmergencyPatientProfile(long patientId) {
        try {
            return mDatabaseHandler.getEmergencyProfile(patientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void sendBroadcast(String key, String value, int statusCode, String status) {
        Intent mIntent = new Intent(key);
        mIntent.putExtra("progressMsg", value);
        mIntent.putExtra("statusCode", statusCode);
        mIntent.putExtra("status", status);
        mIntent.putExtra("patientId", userNotInEmergency != null ? userNotInEmergency.getPatientId() : 0);
        mIntent.putExtra(Constant.EMERGENCY_STATUS_TEST, userNotInEmergency.getEmergencyStatus());
        mLocalBroadcastManager.sendBroadcastSync(mIntent);
    }

    private void startPeriodicUpdates() {
        Log.d(TAG, "********************* LOCATION UPDATE STARTED ********************* \n");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constant.INTERVAL_FOR_LOCATION_UPDATE);
        mLocationRequest.setFastestInterval(Constant.FASTEST_INTERVAL_FOR_LOCATION_UPDATED);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                for (int i = 0; i < 5; i++) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, this);
                }
                //return;
            } else {
                //show toast to saying no location permission granted
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(final Location location) {

    }

    public void sendLocationUpdate() {
        // For every minute this method will be called due to interval set as 1min for location update
        // if count==5 i.e if for every 5 min location will be sent to server
        //else we will update new location inside db
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.d(TAG, "************ sendLocationUpdate ************** \n");
        ArrayList<EmergencyStatusVO> allUsersCurrentlyInEmergency = getAllPatientInEmergencyFromDevice();
        if (allUsersCurrentlyInEmergency.size() != 0) {
            for (EmergencyStatusVO mStatusVO : allUsersCurrentlyInEmergency) {
                PatientUserVO patientInEmergencyVO = getEmergencyPatientProfile(mStatusVO.getPatientId());
                if (mStatusVO.getLocationUpdateStatus() == Constant.EMERGENCY_START_LOCATION_UPDATE) {
                    if (!checkLocationOld(location)) {
                        if (mStatusVO != null) {
                            mDatabaseHandler.updateCurrentLocation(mStatusVO.getPatientId(), location);
                        } else {
                            location = null;
                        }
                        String smsLoc = EmergencyUtil.prepareWebVOForServer(getApplicationContext(),
                                patientInEmergencyVO.getPatientId(),
                                loggedInPatientUserVO.getUserProfileId(), "loc",
                                location, 0, getApplicationContext(), emergencyProviderNumber);
                        mRequestQueue.add(createLocationUpdateRequest(userNotInEmergency, smsLoc, patientInEmergencyVO, location));
                    }
                }
            }
        } else {
            stopSelf();
        }

    }

    public boolean checkLocationOld(Location location) {
        long currentTime = new Date().getTime();
        long locationTime = 0;
        if (location != null)
            locationTime = location.getTime();

        if (currentTime - locationTime > (10 * 60 * 1000)) {

            return true;
        } else
            return false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "\nON DESTROY");
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        if (mHandler != null) {
            mHandler.removeMessages(EMG_UPDATE);
        }
        if (serviceBroadCastReceiver != null)
            mLocalBroadcastManager.unregisterReceiver(serviceBroadCastReceiver);

        super.onDestroy();
    }


    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
            }
        }
    }


    private StringRequest createLocationUpdateRequest(final EmergencyStatusVO emergencyStatusVO, final String smsServerWebService, final PatientUserVO mPatientUserVO, final Location mLocation) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.callEmergency;

        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ResponseVO responseVo = null;
                try {
                    if (response != null) {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<ResponseVO>() {
                        }.getType();

                        responseVo = gson.fromJson(response, objectType);

                        if (responseVo != null) {
                            responseHandlerLocationUpdate(responseVo, emergencyStatusVO, mPatientUserVO, mLocation);
                        } else {
                            responseHandlerLocationUpdate(responseVo, emergencyStatusVO, mPatientUserVO, mLocation);
                        }
                    } else {
                        responseHandlerLocationUpdate(responseVo, emergencyStatusVO, mPatientUserVO, mLocation);
                    }
                } catch (JsonParseException e) {
                    responseHandlerLocationUpdate(responseVo, emergencyStatusVO, mPatientUserVO, mLocation);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("webVO", smsServerWebService);
                params.put("emergencyAPIKey", mPatientUserVO.getEmergencyAPIKey());
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

    private void responseHandlerLocationUpdate(ResponseVO mResponseVO, EmergencyStatusVO emergencyStatusVO, PatientUserVO mPatientUserVO, Location mLocation) {
        if (mResponseVO != null) {
            int code = (int) mResponseVO.getCode();
            Log.d(TAG, "RESPONSE CODE=" + code);
            switch (code) {

                case Constant.EMERGENCY_LOCATION_UPDATE_SUCCESSFULLY:
                    Log.d(TAG, "Location updated successfully");

                    break;
                case Constant.EMERGENCY_REVOKED:
                    removeEmergencyFromTable(emergencyStatusVO.getPatientId());
                    try {
                        updatePatientTable(mPatientUserVO, "", false);
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                    //AppUtil.showToast(getApplicationContext(), mResponseVO.getResponse());
                    break;

            }

        } else {
            Log.d(TAG, "WEBSERVICE RESPONSE IS NULL");
        }
    }

    public void removeEmergencyFromTable(long patientId) {
        DatabaseHandler mDatabaseHandler = DatabaseHandler
                .dbInit(getApplicationContext());
        try {
            mDatabaseHandler.removeEmergency(patientId);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void updatePatientTable(PatientUserVO mUserVO, String token, boolean status) {
        PatientUserVO mPatientUserVO = AppUtil.getPatientVO(mUserVO.getPatientId(), getApplicationContext());
        mPatientUserVO.setEmergencyTokenUrl(token);
        mPatientUserVO.setUnderEmergency(status);
        DatabaseHandler mDatabaseHandler = DatabaseHandler
                .dbInit(getApplicationContext());
        try {
            mDatabaseHandler.insertUser(mPatientUserVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}