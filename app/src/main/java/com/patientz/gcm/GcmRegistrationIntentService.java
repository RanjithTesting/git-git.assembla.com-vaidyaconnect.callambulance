package com.patientz.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brandkinesis.BKUserInfo;
import com.brandkinesis.BrandKinesis;
import com.brandkinesis.callback.BKUserInfoCallback;
import com.brandkinesis.utils.BKUtilLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.activity.BuildConfig;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GcmRegistrationIntentService extends IntentService {
    public static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private RequestQueue mRequestQueue;
    private static final String TAG = "GcmRegistrationIntentService";
    private Activity mActivity;

    public GcmRegistrationIntentService() {
        super("GcmRegistrationIntentService");
    }

    public GcmRegistrationIntentService(Activity mActivity) {
        super("GcmRegistrationIntentService");
        mActivity = mActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = AppVolley.getRequestQueue();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // if (checkPlayServices()) {
        InstanceID mInstanceID = InstanceID.getInstance(getApplicationContext());
        try {
            String token = mInstanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            sendGCMIdToBK(token);
            if (!isTokenAlreadySentToServer()) {
                sendRegistrationIdToAppServer(token);
            } else {
                Log.d(TAG, "Registration token already sent to server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isTokenAlreadySentToServer() {
        final SharedPreferences prefs = AppUtil.getGcmPreferences(getApplicationContext());
        boolean result = prefs.getBoolean(Constant.GCM_TOKEN_SENT_TO_SERVER_STATUS, false);
        return result;
    }

    /* private String getRegistrationToken(Context context) {
         final SharedPreferences prefs = AppUtil.getGcmPreferences(context);
         boolean registrationToken = prefs.getBoolean(Constant.GCM_TOKEN_SENT_TO_SERVER_STATUS,false);
         if (!registrationToken) {
             Log.i(TAG, "Registration Token Not Send To Server");
             return "";
         }
         return registrationToken;
     }*/
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public void sendGCMIdToBK(final String regId) {
        Bundle gcmToken = new Bundle();
        gcmToken.putString(BKUserInfo.BKExternalIds.GCM, regId);
        if (BrandKinesis.getBKInstance() != null) {
            BrandKinesis.getBKInstance().setUserInfoBundle(gcmToken, new BKUserInfoCallback() {
                @Override
                public void onUserInfoUploaded(boolean status) {
                    if (status)
                        BKUtilLogger.showErrorLog(BKUtilLogger.BK_DEBUG, "GCM=======================" + regId);
                }
            });
        }
    }

    private void sendRegistrationIdToAppServer(String token) {
        Log.d(TAG, "Sending Registration token to server - " + token);
        String apiKey = getApplicationContext().getString(R.string.API_KEY);
        PatientUserVO mPatientUserVO = AppUtil.getLoggedPatientVO(getApplicationContext());
        long userID = 0;
        try {
            userID = mPatientUserVO.getUserProfileId();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("registrationId", token);
        params.put("apiKey", apiKey);
        params.put("userProfileId", "" + userID);
        params.put("appPackageName", BuildConfig.APPLICATION_ID);
        Log.d(TAG, "REQUEST DATA=" + params.entrySet());
        if (userID != 0) {
            mRequestQueue.add(createRegistrationRequest(userID, token, apiKey));
        }
    }

    private StringRequest createRegistrationRequest(final long userProfileId, final String token, final String serverApiKey) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.saveGCMInfo;

        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "login response : " + response);
                if (response != null) {
                    Log.d(TAG, "Got webservice response");
                    Gson gson = AppUtil.getGson();
                    Type objectType = new TypeToken<ResponseVO>() {
                    }.getType();
                    try {

                        Log.d(TAG, "Parsing Response VO");
                        ResponseVO responseObject = gson.fromJson(
                                response, objectType);
                        Log.d(TAG, "RESPONSE CODE=" + responseObject.getCode());
                        if (responseObject.getCode() == Constant.RESPONSE_SUCCESS) {
                            storeTokenAsSentToServer();
                        }
                    } catch (JsonParseException e) {
                        Log.e(TAG, "Exception Parsing Response VO : "
                                + e.getMessage());
                    }
                } else {

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
                params.put("registrationId", token);
                params.put("apiKey", serverApiKey);
                params.put("userProfileId", "" + userProfileId);
                TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                params.put("deviceId", telephonyManager.getDeviceId());
                params.put("appPackageName", BuildConfig.APPLICATION_ID);

                Log.d(TAG, "REQUEST DATA=" + params.entrySet());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        return mRequest;
    }

    private void storeTokenAsSentToServer() {
        final SharedPreferences prefs = AppUtil.getGcmPreferences(getApplicationContext());
        int appVersion = getAppVersion(getApplicationContext());
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constant.GCM_TOKEN_SENT_TO_SERVER_STATUS, true);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /* private void storeRegistrationId(String regId) {
         final SharedPreferences prefs = AppUtil.getGcmPreferences(getApplicationContext());
         int appVersion = getAppVersion(getApplicationContext());
         Log.i(TAG, "Saving regId on app version " + appVersion);
         SharedPreferences.Editor editor = prefs.edit();
         editor.putString(Constant.PROPERTY_REG_ID, regId);
         editor.putInt(PROPERTY_APP_VERSION, appVersion);
         editor.commit();
     }*/
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
