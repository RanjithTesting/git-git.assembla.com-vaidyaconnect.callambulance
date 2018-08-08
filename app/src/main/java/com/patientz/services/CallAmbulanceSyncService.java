package com.patientz.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.AvailabilityCapabilityVO;
import com.patientz.VO.Facility;
import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.InsuranceUpload;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.RecordSchemaAttributes;
import com.patientz.VO.ResponseVO;
import com.patientz.VO.Speciality;
import com.patientz.VO.UserRecordVO;
import com.patientz.VO.UserVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.GetSchemaKeys;
import com.patientz.utils.Log;
import com.patientz.utils.SchemaUtils;
import com.patientz.webservices.WebServiceUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallAmbulanceSyncService extends IntentService {

    private static final String TAG = "CallAmbulanceSyncService";
    RequestQueue mRequestQueue;
    private final IBinder mBinder = new LocalBinder();
    private int count = 0;
    private int errorCount = 0;
    private LocalBroadcastManager mLocalBroadcastManager;
    private DatabaseHandler dh;
    private DatabaseHandlerAssetHelper mDatabaseHandlerAssetHelper;
    private long currentSelectedProfile;
    private int offset;


    public CallAmbulanceSyncService() {
        super("CallAmbulanceSyncService");
    }

    @Override
    public void onCreate() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        mRequestQueue = AppVolley.getRequestQueue();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            mDatabaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
            Intent mIntent = new Intent(String.valueOf(Constant.ACTION_SYNC_IN_PROGREES));
            mLocalBroadcastManager.sendBroadcast(mIntent);
            errorCount = 0;
            count = 0;
            SharedPreferences sharedPreferences = getSharedPreferences(
                    Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
            String deepLink = sharedPreferences.getString(Constant.DYNAMIC_LINK_2, "");
            String deepLink_incoming = sharedPreferences.getString(Constant.INCOMING_DYNAMIC_LINK, "");
                /*Create a unique dynamic link for the current user to share with other users*/
            if (TextUtils.isEmpty(deepLink))
                generateDynamicLink();
            Log.d(TAG, "DEEP LINK INCOMING=" + deepLink_incoming);
            getSessionAndCallUpdate(getApplicationContext());
        }
    }

    public class LocalBinder extends Binder {
        public CallAmbulanceSyncService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CallAmbulanceSyncService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public void getSessionAndCallUpdate(Context context) {
        dh = DatabaseHandler.dbInit(getApplicationContext());
        if (AppUtil.isOnline(context)) {
            try {
                startUpdateRequests();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            errorCount++;
            broadCastServiceStatus("Please check your internet connection and try again");
        }
    }

    public StringRequest createSessionVerifyRequest() {
        Log.d(TAG, "createSessionVerifyRequest");
        count++;
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.LOGIN_VERIFY;
        Log.d(TAG, "szServerUrl - " + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE_LOGIN_VERIFY" + response);
                DatabaseHandler dh = DatabaseHandler
                        .dbInit(getApplicationContext());
                dh.deleteAllPendingUsers();
                if (response != null) {
                    broadCastLoginVerifyComplete(Constant.SUCCESS);
                    Log.d(TAG, "Got webservice response");
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<ResponseVO>() {
                    }.getType();
                    ResponseVO responseVO = gson.fromJson(response, objectType);
                    responseHandler(responseVO);
                    broadCastLoginVerifyComplete(Constant.SUCCESS);
                    broadCastServiceStatus();
                } else {
                    errorCount++;
                    broadCastServiceStatus();
                    broadCastLoginVerifyComplete(Constant.FAILED);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorCount++;
                broadCastServiceStatus();
                broadCastLoginVerifyComplete(Constant.FAILED);
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 1));
        return mRequest;
    }

    private void broadCastLoginVerifyComplete(int result) {
        Intent mIntent = new Intent(Constant.ACTION_LOGIN_VERIFY_COMPLETE);
        mIntent.putExtra("result", result);
        mLocalBroadcastManager.sendBroadcast(mIntent);
    }

    private void responseHandler(ResponseVO mResponseVO) {
        Log.d(TAG, "Inside responseHandler");
        int code = (int) mResponseVO.getCode();
        switch (code) {
            case Constant.RESPONSE_SUCCESS:
                AppUtil.sendCampaignDetails(getApplicationContext(), Constant.EVENT_LOGIN);
                saveUserDetails(mResponseVO);
                saveDestHospRecord(mResponseVO);
                currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
                Log.d(TAG, "CURRENT_SELECTED_PATIENT_ID>>>" + currentSelectedProfile);
                mRequestQueue.add(createProfileDetailsRequest(currentSelectedProfile));
                try {
                    callGetOrgBranchesListWebservice();

                    mRequestQueue.add(getOrgBranchAvailabilityCapabilityList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mRequestQueue.add(getMasterFacilities());
                mRequestQueue.add(getMasterSpecialities());


             /*Get Schema key */
                String version = SchemaUtils.getVersionFromSp(getApplicationContext(),
                        Constant.EHR_SCHEMA_KEYS_PREF_FILENAME,
                        Constant.VERSION_KEY);
                boolean SpExists = checkIfSpExists(
                        Constant.EHR_SCHEMA_ATTR_PREF_FILENAME,
                        Constant.EHR_SCHEMA_ATTR_PREF_FILENAME_KEY);

                if (SpExists) {
                    String urlForGettingDoctorVisitSchema = "v1/webservices/getSchemaKeys?recordType=EHR"
                            + "&version=" + version.replaceAll(" ", "+");
                    GetSchemaKeys mGetSchemaKeys = new GetSchemaKeys(getApplicationContext(),
                            Constant.EHR_SCHEMA_KEYS_PREF_FILENAME,
                            Constant.EHR_SCHEMA_ATTR_PREF_FILENAME,
                            Constant.EHR_SCHEMA_ATTR_PREF_FILENAME_KEY,
                            urlForGettingDoctorVisitSchema);
                }
                break;
            default:
                errorCount++;
                broadCastServiceStatus();
                break;
        }
    }

    private void saveUserDetails(ResponseVO mResponseVO) {

        DatabaseHandler dh = DatabaseHandler
                .dbInit(getApplicationContext());
        dh.insertUsers(mResponseVO.getPatientUserVO());
        SharedPreferences sharedPreferences = getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constant.K_REG_ID, mResponseVO.getDefaultRegId());
            editor.putBoolean(Constant.LOGIN_STATUS, true);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppUtil.storeUserVO(getApplicationContext(), mResponseVO.getUser());
    }

    private static Type entityType = new TypeToken<ResponseVO>() {
    }.getType();

    public void saveDestHospRecord(ResponseVO mResponseVO) {

        SharedPreferences sharedPreferences = getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String data = gson.toJson(mResponseVO, entityType);
        editor.putString("mResponseVO", data).commit();
    }

    private void startUpdateRequests() {
        try {
            mRequestQueue.add(createSessionVerifyRequest());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }


    private boolean checkIfSpExists(String spFileName, String key) {

        ArrayList<RecordSchemaAttributes> mRecordSchemaAttributes = SchemaUtils
                .getSchemaAttributesFromSharedPreference(getApplicationContext(),
                        spFileName, key);
        return mRecordSchemaAttributes == null;
    }

    private StringRequest getOrgBranchesList(String timeStamp) {
        count++;
        String url = WebServiceUrls.serverUrl + WebServiceUrls.getOrgBranches + timeStamp + "&max=" + Constant.MAX_ORG_BRANCH_LIMIT + "&offset=" + offset;
        Log.d(TAG, "getOrgBranchesList=" + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ResponseVO responseVO = new ResponseVO();
                Log.d(TAG, "RESPONSE_GET_ORG_BRANCHES_LIST" + response);
                if (response != null) {
                    Gson gson = new GsonBuilder().setDateFormat("EEE MMM dd hh:mm:ss zzz yyyy").create();
                    Type objectType = new TypeToken<ResponseVO>() {
                    }.getType();
                    try {
                        responseVO = gson.fromJson(
                                response, objectType);
                        if (responseVO.getCode() == Constant.RESPONSE_SUCCESS) {
                            ArrayList<OrgBranchVO> orgBranchList = responseVO.getOrgBranchesList();
                            if (orgBranchList != null) {
                                Log.e("orgBranchList>>size", orgBranchList.size() + "");
                                int orgBranchesListSize = orgBranchList.size();
                                mDatabaseHandlerAssetHelper.insertOrgBranchesList(orgBranchList);
                                if (orgBranchesListSize >= 50) {
                                    offset++;
                                    callGetOrgBranchesListWebservice();
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception Parsing Response VO : "
                                + e.getMessage());
                    }
                } else {
                    errorCount++;
                }
                broadCastServiceStatus();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorCount++;
                broadCastServiceStatus();
                Log.d(TAG, "getOrgBranchesList");
                Log.d(TAG, "****** onErrorResponse **********");
                Log.d(TAG, "errorCount=" + errorCount);
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                    Log.d(TAG, "NoConnectionError/TimeoutError/NetworkError");
                } else if (volleyError instanceof AuthFailureError) {
                    Log.d(TAG, "AuthFailureError");
                } else {
                    Log.d(TAG, "Server Error");
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 2));
        return mRequest;
    }

    private StringRequest getOrgBranchAvailabilityCapabilityList() {
        Date timeStamp = null;
        String tStamp = null;
        try {
            timeStamp = dh.getAvailabilityCapabilityTblLastUpdatedTimeStamp();
            tStamp = URLEncoder.encode("" + timeStamp, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        count++;
        String url;
        Log.d("tStamp=", tStamp);
        if (tStamp != null && !tStamp.equalsIgnoreCase("null")) {
            url = WebServiceUrls.serverUrl + WebServiceUrls.getOrgBranchAvailabilityCapabilityList + "?timeStamp=" + tStamp;
        } else {
            url = WebServiceUrls.serverUrl + WebServiceUrls.getOrgBranchAvailabilityCapabilityList;
        }
        Log.d(TAG, "getOrgBranchAvailabilityCapabilityList=" + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE_GET_ORG_BRANCHES_AVAILABILITY_CAPABILITY_LIST" + response);
                if (response != null) {
                    ArrayList<AvailabilityCapabilityVO> mAvailabilityCapabilityVOS = new ArrayList<AvailabilityCapabilityVO>();
                    try {
                        JSONArray mJsonArray = new JSONArray(response);
                        Log.d(TAG, "getOrgBranchAvailabilityCapabilityList>>mJsonArray= " + mJsonArray.length());

                        for (int i = 0; i < mJsonArray.length(); i++) {
                            AvailabilityCapabilityVO mAvailabilityCapabilityVO = new AvailabilityCapabilityVO();
                            mAvailabilityCapabilityVO.setOrgId(mJsonArray.getJSONObject(i).getJSONObject("org").getLong("id"));
                            mAvailabilityCapabilityVO.setOrgBranchId(mJsonArray.getJSONObject(i).getJSONObject("orgBranch").getLong("id"));
                            if (mJsonArray.getJSONObject(i).getString("speciality") != null && !mJsonArray.getJSONObject(i).getString("speciality").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setSpecialityId(mJsonArray.getJSONObject(i).getJSONObject("speciality").getLong("id"));
                            }
                            if (!TextUtils.isEmpty(mJsonArray.getJSONObject(i).getString("facility")) && !mJsonArray.getJSONObject(i).getString("facility").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setFacilityId(mJsonArray.getJSONObject(i).getJSONObject("facility").getLong("id"));
                            }
                            if (mJsonArray.getJSONObject(i).getString("userProfile") != null && !mJsonArray.getJSONObject(i).getString("userProfile").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setProfileId(mJsonArray.getJSONObject(i).getJSONObject("userProfile").getLong("id"));
                            }
                            if (mJsonArray.getJSONObject(i).getString("dayOfWeek") != null && !mJsonArray.getJSONObject(i).getString("dayOfWeek").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setDayOfWeek(mJsonArray.getJSONObject(i).getString("dayOfWeek"));
                            }
                            if (mJsonArray.getJSONObject(i).getString("startTime") != null && !mJsonArray.getJSONObject(i).getString("startTime").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setStartTime(mJsonArray.getJSONObject(i).getString("startTime"));
                            }
                            if (mJsonArray.getJSONObject(i).getString("endTime") != null && !mJsonArray.getJSONObject(i).getString("endTime").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setEndTime(mJsonArray.getJSONObject(i).getString("endTime"));
                            }
                            if (mJsonArray.getJSONObject(i).getString("certification") != null && !mJsonArray.getJSONObject(i).getString("certification").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setCertificationId(mJsonArray.getJSONObject(i).getJSONObject("certification").getLong("id"));
                            }
                            if (mJsonArray.getJSONObject(i).getString("insuranceCompany") != null && !mJsonArray.getJSONObject(i).getString("insuranceCompany").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setInsuranceCompanyId(mJsonArray.getJSONObject(i).getJSONObject("insuranceCompany").getLong("id"));
                            }
                            if (mJsonArray.getJSONObject(i).getString("diagnosticTest") != null && !mJsonArray.getJSONObject(i).getString("diagnosticTest").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setDiagnosticTestsId(mJsonArray.getJSONObject(i).getJSONObject("diagnosticTest").getLong("id"));
                            }
                            if (mJsonArray.getJSONObject(i).getString("availabilityStatus") != null && !mJsonArray.getJSONObject(i).getString("availabilityStatus").equalsIgnoreCase("null")) {
                                mAvailabilityCapabilityVO.setAvailabilityStatus((mJsonArray.getJSONObject(i).getString("availabilityStatus")).equalsIgnoreCase("true") ? true : false);
                            }
                            if (mJsonArray.getJSONObject(i).getString("lastUpdated") != null && !mJsonArray.getJSONObject(i).getString("lastUpdated").equalsIgnoreCase("null")) {
                                SimpleDateFormat sdff = new SimpleDateFormat(getString(R.string.date_format4));
                                mAvailabilityCapabilityVO.setLastUpdated(sdff.parse(mJsonArray.getJSONObject(i).getString("lastUpdated")));
                            }
                            mAvailabilityCapabilityVOS.add(mAvailabilityCapabilityVO);
                        }
                        dh.insertAvailabilityCapabilityVOList(mAvailabilityCapabilityVOS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    errorCount++;
                }
                broadCastServiceStatus();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorCount++;
                broadCastServiceStatus();
                Log.d(TAG, "getOrgBranchAvailabilityCapabilityList");
                Log.d(TAG, "****** onErrorResponse **********");
                Log.d(TAG, "errorCount=" + errorCount);
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                    Log.d(TAG, "NoConnectionError/TimeoutError/NetworkError");
                } else if (volleyError instanceof AuthFailureError) {
                    Log.d(TAG, "AuthFailureError");
                } else {
                    Log.d(TAG, "Server Error");
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }


        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 2, 2));
        return mRequest;
    }

    private StringRequest getMasterFacilities() {
        count++;
        String url = WebServiceUrls.serverUrl + WebServiceUrls.master_facilities;
        Log.d(TAG, "getMasterFacilities=" + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE_MASTER_FACILITIES" + response);
                if (response != null && !response.equalsIgnoreCase("No Facilities got added Extra")) {
                    List<Facility> facilities = Arrays.asList(new Gson().fromJson(response, Facility[].class));
                    android.util.Log.e("facilities", facilities.size() + "");
                    try {
                        dh.insertMasterDataFacilities(facilities);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    errorCount++;
                }
                broadCastServiceStatus();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorCount++;
                broadCastServiceStatus();
                Log.d(TAG, "getMasterFacilities");
                Log.d(TAG, "****** onErrorResponse **********");
                Log.d(TAG, "errorCount=" + errorCount);
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                    Log.d(TAG, "NoConnectionError/TimeoutError/NetworkError");
                } else if (volleyError instanceof AuthFailureError) {
                    Log.d(TAG, "AuthFailureError");
                } else {
                    Log.d(TAG, "Server Error");
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                Date timeStamp = null;
                try {
                    timeStamp = dh.getSpecialitiesLastUpdatedTimeStamp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "getMasterFacilities_timestamp=" + timeStamp);
                return params;
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 2));
        return mRequest;
    }

    private StringRequest getMasterSpecialities() {
        count++;
        String url = WebServiceUrls.serverUrl + WebServiceUrls.master_specialities;
        Log.d(TAG, "getMasterSpecialities=" + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE_MASTER_SPECIALITIES" + response);
                if (response != null) {
                    List<Speciality> specialities = Arrays.asList(new Gson().fromJson(response, Speciality[].class));
                    android.util.Log.e("specialities", specialities.size() + "");
                    try {
                        dh.insertMasterDataSpecialities(specialities);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    errorCount++;
                }
                broadCastServiceStatus();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorCount++;
                broadCastServiceStatus();
                Log.d(TAG, "getMasterSpecialities");
                Log.d(TAG, "****** onErrorResponse **********");
                Log.d(TAG, "errorCount=" + errorCount);
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                    Log.d(TAG, "NoConnectionError/TimeoutError/NetworkError");
                } else if (volleyError instanceof AuthFailureError) {
                    Log.d(TAG, "AuthFailureError");
                } else {
                    Log.d(TAG, "Server Error");
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                Date timeStamp = null;
                try {
                    timeStamp = dh.getSpecialitiesLastUpdatedTimeStamp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "getMasterFacilities_timestamp=" + timeStamp);
                return params;
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 2));
        return mRequest;
    }


    private StringRequest getFacilitiesList(final long orgBranchId) {
        count++;
        String url = WebServiceUrls.serverUrl + WebServiceUrls.getOrgBranchAvailabilityCapabilityList;
        Log.d(TAG, "getFacilitiesList=" + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "getFacilitiesList>>response= " + response);
                if (response != null && !response.equalsIgnoreCase("No Facilities got added Extra")) {
                    List<Facility> facilities = Arrays.asList(new Gson().fromJson(response, Facility[].class));
                    android.util.Log.e("facilities", facilities.size() + "");
                    try {
                        dh.insertFacilities(facilities);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    errorCount++;
                }
                broadCastServiceStatus();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorCount++;
                broadCastServiceStatus();
                Log.d(TAG, "getOrgBranchesList");
                Log.d(TAG, "****** onErrorResponse **********");
                Log.d(TAG, "errorCount=" + errorCount);
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                    Log.d(TAG, "NoConnectionError/TimeoutError/NetworkError");
                } else if (volleyError instanceof AuthFailureError) {
                    Log.d(TAG, "AuthFailureError");
                } else {
                    Log.d(TAG, "Server Error");
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                Date timeStamp = null;
                try {
                    timeStamp = dh.getFacilitiesLastUpdatedTimeStamp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "facilities_timestamp=" + timeStamp);
                try {
                    params.put("timeStamp", URLEncoder.encode("" + timeStamp, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.put("facility", String.valueOf(true));
                params.put("orgBranchId", String.valueOf(orgBranchId));
                Log.d("facilities>>params=", params.toString());
                return params;
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 2));
        return mRequest;
    }


    private void callGetOrgBranchesListWebservice() throws Exception {
        Date timeStamp = mDatabaseHandlerAssetHelper.getLastUpdatedRecordTimeStamp();
        Log.d(TAG, "TIME STAMP=" + timeStamp);
        mRequestQueue.add(getOrgBranchesList(URLEncoder.encode("" + timeStamp, "UTF-8")));
    }

    private StringRequest createProfileDetailsRequest(final long patientID) {
        count++;
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.patientCompleteRecord + patientID;
        Log.d(TAG, "patientCompleteRecord>>url" + szServerUrl);

        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ResponseVO responseVO = new ResponseVO();
                Log.d(TAG, "RESPONSE_PATIENT_COMPLETE_RECORD" + response);
                if (!TextUtils.isEmpty(response)) {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<ResponseVO>() {
                    }.getType();
                    try {
                        responseVO = gson.fromJson(
                                response, objectType);
                        if (responseVO.getCode() == Constant.RESPONSE_SUCCESS) {
                            for (PatientUserVO patientUserVO : responseVO.getPatientUserVO()) {
                                if (patientID == patientUserVO.getPatientId()) {
                                    Log.d(TAG, "patientCompleteRecord>>patientId=" + patientID);
                                    saveUsersData(patientUserVO);
                                }
                            }
                            broadCastPatientRecordSyncComplete();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception Parsing Response VO : "
                                + e.getMessage());
                    }
                } else {
                    errorCount++;
                }
                broadCastServiceStatus();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorCount++;
                broadCastServiceStatus();
                Log.d(TAG, "createProfileDetailsRequest");
                Log.d(TAG, "****** onErrorResponse **********");
                Log.d(TAG, "errorCount=" + errorCount);
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                    Log.d(TAG, "NoConnectionError/TimeoutError/NetworkError");
                } else if (volleyError instanceof AuthFailureError) {
                    Log.d(TAG, "AuthFailureError");
                } else {
                    Log.d(TAG, "Server Error");
                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1));
        return mRequest;
    }

    private void saveUsersData(PatientUserVO mPatientUserVO) throws Exception {
        try {
            dh.insertContacts(mPatientUserVO);
            if (mPatientUserVO.getRecordVO() != null && mPatientUserVO.getRecordVO().getUserInfoVO() != null) {
                dh.insertUserInfo(mPatientUserVO.getRecordVO(), mPatientUserVO.getBloodGroup(), mPatientUserVO.getPatientHandle());
            }
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION=" + e.getMessage());
            e.printStackTrace();
        }
        UserRecordVO userRecord = mPatientUserVO.getRecordVO();
        if (userRecord != null && userRecord.getInsuranceVOs() != null) {
            for (InsuranceVO insuranceVO : userRecord.getInsuranceVOs()) {
                dh.insertUserInsuranceDetails(insuranceVO);
                RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                if (insuranceVO.getInsuranceUploadId() != 0) {
                    mRequestQueue.add(getInsuranceUpload(insuranceVO));
                }
            }
        }
        if (userRecord != null && userRecord.getHealthRecordVO() != null) {
            HealthRecordVO healthRecordVO = userRecord.getHealthRecordVO();
            if (healthRecordVO.getHealthRecord() != null) {
                SchemaUtils.saveEmergencyHealthRecord(healthRecordVO.getHealthRecord(), getApplicationContext());
                dh.insertUserHealthRecord(healthRecordVO, mPatientUserVO.getPatientId());
            }
        }
        dh.updateUserOrgRecord(userRecord, mPatientUserVO.getPatientId());
        //startInsuranceStickyNorification();
    }

    private void startInsuranceStickyNorification() {
        Intent startIntent = new Intent(getApplicationContext(), StickyNotificationInsuranceFGService.class);
        startIntent.setAction(Constant.ACTION.START);
        startService(startIntent);
    }

    public StringRequest getInsuranceUpload(InsuranceVO insuranceVO) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.insuranceUploadList + "?id=" + insuranceVO.getInsuranceUploadId() + "&patientId=" + insuranceVO.getPatientId();
        Log.d(TAG, "getInsuranceUpload>>url=" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE_INSURANCE_UPLOAD_LIST" + response);
                if (response != null) {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<InsuranceUpload>() {
                    }.getType();
                    InsuranceUpload insuranceUpload = gson.fromJson(response, objectType);
                    dh.insertUserUploadInsurance(insuranceUpload);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorCount++;
                broadCastServiceStatus();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 2, 2));
        return mRequest;
    }

    private void broadCastPatientRecordSyncComplete() {
        Intent mIntent = new Intent(Constant.ACTION_PROFILE_SYNC_COMPLETE);
        mLocalBroadcastManager.sendBroadcast(mIntent);
    }


    public void broadCastServiceStatus() {
        count--;
        Log.d("broadCastServiceStatus>>count", count + "");
        if (count == 0) {
            Intent mIntent = new Intent("ACTION_TASKS_PERFORMED");
            mIntent.putExtra("errorCount", errorCount);
            mIntent.putExtra("no_network", "");
            mLocalBroadcastManager.sendBroadcast(mIntent);
        }
    }

    public void broadCastServiceStatus(String noNetwork) {
        if (count == 0) {
            Intent mIntent = new Intent("ACTION_TASKS_PERFORMED");
            mIntent.putExtra("errorCount", errorCount);
            mIntent.putExtra("no_network", noNetwork);
            mLocalBroadcastManager.sendBroadcast(mIntent);
        }
    }

    private void generateDynamicLink() {
        UserVO user = AppUtil.getLoggedUser(getApplicationContext());
        String link = "";
        if (user != null) {
            if (user.getUserId() != 0) {
                long referralID = user.getUserId();
                link = "https://s8geh.app.goo.gl/?link=https://callambulance.in?" +
                        "referralCode=" + referralID
                        + "&utm_campaign=invite" + "&utm_source=ca_app" + "&utm_medium=in_app"
                        + "&apn=com.patientz.activity" + "&referralCode=" + referralID;
            }
        }

        if (!TextUtils.isEmpty(link)) {
            mRequestQueue.add(createFireBaseShortURLRequest(link));
        }
    }

    public JsonObjectRequest createFireBaseShortURLRequest(final String link) {
        String szServerUrl = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=" + getString(R.string.firebase_key);//WebServiceUrls.serverUrl + WebServiceUrls.LOGIN_API_URL;

        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("longDynamicLink", link);
            // jsonobject.put("suffix", "SHORT");
            Log.d("json--->", jsonobject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, szServerUrl, jsonobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("RESPONSE_FIREBASE", String.valueOf(response));

                        if (response != null)
                            try {
                                JSONObject responseObject = new JSONObject(String.valueOf(response));

                                if (responseObject.has("shortLink")) {

                                    Log.d("Response key access_token : ", responseObject.get("shortLink").toString());
                                    SharedPreferences sharedPreferences = getSharedPreferences(
                                            Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
                                    sharedPreferences.edit().putString(Constant.DYNAMIC_LINK_2, responseObject.get("shortLink").toString()).commit();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1));

        return mRequest;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
