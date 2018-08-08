package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.fragments.FragmentDashBoard;
import com.patientz.service.IntentServiceFetchAddress;
import com.patientz.service.SaveUserLogIntentService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ProfileRegistrationActivity extends LocationActivity2 implements View.OnClickListener {

    private static final String TAG = "ProfileRegistrationActivity";
    private RequestQueue mRequestQueue;
    private String mEmail, oldPassword;
    private EditText firstNameEditText, lastNameEditText, mobileNumberEditText, testedit;
    private Button genderButton, /*DOBButton, */
            submitButton, btSelectBloodGroup, btWillingToDonateBlood;
    private EditText dob_button;
    private View mFormView;
    private View mStatusView;
    private Calendar dob;
    private int genderType;

    private Spinner spinnerBloodGroup, spinnerWillingToDonateBlood;
    private String MOBILE_NUMBER = "mobilenumber";
    private String PARAM_USERNAME = "username";
    private String PARAM_PASSWORD = "password";
    String userName;
    String userPass;
    AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("basicLocationRequest", true);
        mBundle.putBundle("savedInstanceState", savedInstanceState);
        super.onCreate(mBundle);
        setContentView(R.layout.activity_profile_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.tv_roboto_regular_white_normal);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.update_profile);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mResultReceiver = new AddressResultReceiver(new Handler());
        userName = getIntent().getStringExtra(Constant.PARAM_KEY_USERNAME);
        userPass = getIntent().getStringExtra(Constant.PARAM_KEY_PASSWORD);

        Log.e("userName", "" + userName);
        Log.e("userPass", "" + userPass);

        mRequestQueue = AppVolley.getRequestQueue();
        mFormView = findViewById(R.id.form_layout);
        mStatusView = findViewById(R.id.progress_view);
        testedit = (EditText) findViewById(R.id.testedit);
        firstNameEditText = (EditText) findViewById(R.id.first_name);
        lastNameEditText = (EditText) findViewById(R.id.last_name);
        mobileNumberEditText = (EditText) findViewById(R.id.mobile_number);
        genderButton = (Button) findViewById(R.id.gender_button);
        btSelectBloodGroup = (Button) findViewById(R.id.bt_select_blood_group);
        btWillingToDonateBlood = (Button) findViewById(R.id.bt_willing_to_donated_blood);
//        DOBButton = (Button) findViewById(R.id.dob_button);
        dob_button = (EditText) findViewById(R.id.dob_button);
        submitButton = (Button) findViewById(R.id.update_button);
        submitButton.setOnClickListener(this);
        testedit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_NULL
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(testedit.getWindowToken(), 0);
                    validateFieldAndLaunchUpdate();
                    btSelectBloodGroup.requestFocus();
                    btWillingToDonateBlood.requestFocus();
                    submitButton.requestFocus();
                    testedit.requestFocus();
                    testedit.setEnabled(false);
                    return true;
                } else {
                    return false;
                }
            }
        });

        genderButton.setOnClickListener(this);
        btSelectBloodGroup.setOnClickListener(this);
        btWillingToDonateBlood.setOnClickListener(this);

        String mobileNumber = getIntent().getStringExtra(MOBILE_NUMBER);
        if (!TextUtils.isEmpty(mobileNumber) && !mobileNumber.contains("@")) {
            if (mobileNumber.contains("+91-")) {
                String[] mobileNumber1 = mobileNumber.split("-");
                mobileNumberEditText.setText(mobileNumber1[1]);
            } else {
                mobileNumberEditText.setText(mobileNumber);
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();
    }


    private StringRequest createUpdateRequest(final String fName, final String lName, final String gender, final String dob, final String mobileNumer) {
        String szServerUrl =
                WebServiceUrls.serverUrl + WebServiceUrls.resumeSaveSimplePatientRegistration;
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                ResponseVO mResponseVO = null;
                if (response != null) {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<ResponseVO>() {
                    }.getType();
                    try {
                        mResponseVO = gson.fromJson(
                                response, objectType);

                    } catch (JsonParseException e) {
                        Log.e(TAG, "Exception Parsing Response VO : "
                                + e.getMessage());
                    }

                    if (mResponseVO != null) {
                        responseHandlerBasicRegDetails(mResponseVO);
                    } else {
                        showProgress(false);

                        AppUtil.showToast(getApplicationContext(),
                                getString(R.string.networkError));
                    }
                } else {
                    showProgress(false);

                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.networkError));
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(ProfileRegistrationActivity.this);
                            break;
                    }
                } else {
                    AppUtil.showErrorDialog(getApplicationContext(), error);

                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstName", fName);
                params.put("lastName", lName);
                params.put("gender", gender);
                params.put("dateOfBirth", dob);
                params.put("phoneNumber", mobileNumer);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }


    private void responseHandlerBasicRegDetails(ResponseVO mResponseVO) {
        int code = (int) mResponseVO.getCode();
        switch (code) {
            case Constant.RESPONSE_SUCCESS:
                String userName = getIntent().getStringExtra(Constant.PARAM_KEY_USERNAME);
                String userPass = getIntent().getStringExtra(Constant.PARAM_KEY_PASSWORD);

                Log.e("userName", "" + userName);
                Log.e("userPass", "" + userPass);

                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPass)) {
                    showProgress(true);
                    mRequestQueue.add(createLoginRequest(userName, userPass));
                } else {
                    showProgress(false);
                    Intent intent = new Intent(ProfileRegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                break;
            default:
                AppUtil.showToast(getApplicationContext(),
                        mResponseVO.getResponse());
                showProgress(false);
                break;
        }
    }

    private Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        PatientUserVO selfProfile = AppUtil.getLoggedPatientVO(getApplicationContext());
        params.put("patientId", selfProfile.getPatientId() + "");
        if (btSelectBloodGroup.getText().toString().equalsIgnoreCase(getString(R.string.label_select_blood_group))) {
            params.put(Constant.BLOOD_GROUP, "");
        } else {
            params.put(Constant.BLOOD_GROUP,
                    btSelectBloodGroup.getText().toString());
        }
        if (btWillingToDonateBlood.getText().toString().equalsIgnoreCase(getString(R.string.label_select_willing_to_donate_blood))) {
            params.put(Constant.DONATE_BLOOD, "");
        } else {
            params.put(Constant.DONATE_BLOOD,
                    btWillingToDonateBlood.getText().toString());
        }
        params.put(Constant.relation,
                "Self");
        params.put(Constant.firstName,
                firstNameEditText.getText().toString());
        params.put(Constant.lastName,
                lastNameEditText.getText().toString());
        params.put(Constant.genderKey,
                String.valueOf(genderType));
        params.put(Constant.dateOfBirthKey,
                getDob(dob.getTime()));
        if (mobileNumberEditText.getText().toString().trim().length() != 0)
            params.put(Constant.phoneNumber,
                    "+91-" + mobileNumberEditText.getText().toString());

        Log.d(TAG, "PARAMS=" + params.toString());
        return params;
    }

    private StringRequest createUpdateProfileRequest(final Map<String, String> params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.savePatientOwnerFromMobile;

        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE \n" + response);
                ResponseVO responseVo = null;
                try {
                    if (response != null) {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<ResponseVO>() {
                        }.getType();

                        responseVo = gson.fromJson(
                                response, objectType);

                        if (responseVo != null) {
                            responseHandler(responseVo);
                        } else {
                            responseHandler(responseVo);
                        }
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
                Log.d(TAG, "******************* onErrorResponse ******************* \n");
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(ProfileRegistrationActivity.this);
                            break;
                    }
                } else {
                    AppUtil.showErrorDialog(getApplicationContext(), error);

                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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


    private void responseHandler(ResponseVO mResponseVO) {
        if (mResponseVO != null) {

            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_SUCCESS:
                    HashMap<String, Object> upshotData = new HashMap<>();
                    upshotData.put(Constant.UpshotEvents.NU_BASIC_PROFILE_COMPLETED, true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NU_BASIC_PROFILE_COMPLETED);
                    Log.d(TAG, "upshot data=" + upshotData.entrySet());
                    Intent intent = new Intent(ProfileRegistrationActivity.this,
                            ActivitySplashScreen.class);
                    startActivity(intent);
                    finish();
                    break;
                case Constant.RESPONSE_FAILURE:
                    AppUtil.showToast(ProfileRegistrationActivity.this,
                            getString(R.string.failure_msg));
                case Constant.RESPONSE_NOT_LOGGED_IN:
                    AppUtil.showToast(ProfileRegistrationActivity.this,
                            getString(R.string.not_logged_in_error_msg));
                    break;
                case Constant.RESPONSE_SERVER_ERROR:
                    AppUtil.showToast(ProfileRegistrationActivity.this,
                            getString(R.string.server_error_msg));
                    break;
                case Constant.TERMINATE:
                    finish();
                    break;
                default:
                    break;
            }
        }
        showProgress(false);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mStatusView.setVisibility(View.VISIBLE);
            mStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mFormView.setVisibility(View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private JsonObjectRequest createLoginRequest(final String userId, final String userPass) {

        Log.d(TAG, "createLoginRequest1");
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.LOGIN_API_URL;

        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("username", userId);
            jsonobject.put("password", userPass);
            Log.d("json--->", jsonobject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, szServerUrl, jsonobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("Response: - ", String.valueOf(response));

                        if (response != null)
                            try {
                                JSONObject responseObject = new JSONObject(String.valueOf(response));

                                if (responseObject.has("access_token")) {

                                    Log.d("Response key access_token : ", responseObject.get("access_token").toString());

                                    if (!responseObject.getString("roles").contains(Constant.ROLE) || !responseObject.getString("roles").contains(Constant.NEW_USER_ROLE)) {
                                        storeCredentialsInSP(responseObject);
                                        mRequestQueue.add(createSessionVerifyRequest(ProfileRegistrationActivity.this));
                                        Log.d("Response 1 : ", responseObject.get("roles").toString());
                                    } else {
                                        AppUtil.showToast(getApplicationContext(), getString(R.string.uare_not_auth_access));
                                        showProgress(false);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Log.e("Error: ", error.toString());
                /*AppUtil.showToast(getApplicationContext(),
                        getString(R.string.invalid_credentials));*/
                showProgress(false);
                NetworkResponse networkResponse = volleyError.networkResponse;
                int statuscode = -2;
                try {
                    statuscode = networkResponse.statusCode;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("statuscode: - ", "" + statuscode);
                if (statuscode == 503 || volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileRegistrationActivity.this);
                    builder.setCancelable(false);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.fragment_server_busy_dialog, null);
                    builder.setView(view);
                    TextView mButton = (TextView) view.findViewById(R.id.bt_done);
                    final AlertDialog mAlertDialog = builder.create();
                    mButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                            finish();
                        }
                    });
                    builder.create().show();
                } else {
                    if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                        HurlStack hurlStack = new HurlStack() {
                            @Override
                            public HttpResponse performRequest(final Request<?> request, final Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
                                try {
                                    return super.performRequest(request, additionalHeaders);
                                    //Display.DisplayToast(context.getApplicationContext(),context.getResources().getString(R.string.auth_error));
                                } catch (IOException e) {
                                    return new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 401, e.getMessage());
                                }
                            }
                        };
                        Volley.newRequestQueue(getApplicationContext(), hurlStack);  // only for gingerbread and newer versions
                    }
                    AppUtil.showErrorDialog(getApplicationContext(), volleyError);
                }
            }
        });
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1));
        return mRequest;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gender_button:
                genderType = 0;
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Select Gender:");
                String[] types = {"Male", "Female"};
                b.setItems(types, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                genderButton.setText(getString(R.string.male));
                                genderType = 1;
                                break;
                            case 1:
                                genderButton.setText(getString(R.string.female));
                                genderType = 2;
                                break;
                        }
                    }

                });

                b.show();
                break;
            case R.id.bt_select_blood_group:
                showBloodGroupsList();
                break;
            case R.id.bt_willing_to_donated_blood:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.label_select_willing_to_donate_blood));
                String[] mStrings = {getString(R.string.yes), getString(R.string.no)};
                builder.setItems(mStrings, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                btWillingToDonateBlood.setText(getString(R.string.yes));
                                break;
                            case 1:
                                btWillingToDonateBlood.setText(getString(R.string.no));
                                break;
                        }
                    }

                });

                builder.create().show();
                break;
            case R.id.update_button:
                validateFieldAndLaunchUpdate();
                break;
        }
    }

    public void showBloodGroupsList() {

        final String[] mStrings = getResources().getStringArray(R.array.array_bloodgroup);
        AlertDialog.Builder ad = new AlertDialog.Builder(ProfileRegistrationActivity.this);
        ad.setTitle(getString(R.string.select_blood));
        final AlertDialog mAlertDialog = ad.create();
        ad.setSingleChoiceItems(mStrings, 0, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                btSelectBloodGroup.setText(mStrings[arg1]);
                arg0.dismiss();
            }
        });
        ad.show();
    }

    private void storeCredentialsInSP(JSONObject responseObject) {
        try {
            CommonUtils.getSP(this).edit().putString(Constant.USER_ID, getIntent().getStringExtra(PARAM_USERNAME))
                    .putString(Constant.TOKEN_TYPE, (String) responseObject.get(Constant.TOKEN_TYPE))
                    .putString(Constant.ACCESS_TOKEN, (String) responseObject.get(Constant.ACCESS_TOKEN))
                    .putBoolean(Constant.IS_USER_LOGGED_IN, true)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void validateFieldAndLaunchUpdate() {
        firstNameEditText.setError(null);
        lastNameEditText.setError(null);
        mobileNumberEditText.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid first name.
        String firstname = firstNameEditText.getText().toString();
        if (TextUtils.isEmpty(firstname)) {
            firstNameEditText.setError(getString(R.string.error_field_required));
            focusView = firstNameEditText;
            cancel = true;
        } else if (firstname.length() < 3 || firstname.length() > 25) {
            firstNameEditText.setError(getString(R.string.sel_lenght_nw_3to25));
            focusView = firstNameEditText;
            cancel = true;
        }

        // Check for a valid last name.
        String lastName = lastNameEditText.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError(getString(R.string.error_field_required));
            focusView = lastNameEditText;
            cancel = true;
        } else if (lastName.length() < 1 || lastName.length() > 25) {
            lastNameEditText.setError(getString(R.string.lenght_should_1to25));
            focusView = lastNameEditText;
            cancel = true;
        }

        if (genderType == 0) {
            focusView = genderButton;
            cancel = true;
            AppUtil.showToast(getApplicationContext(), getString(R.string.sel_gender_type));
        }

        if (dob_button.getText().toString().trim().length() == 0) {
            focusView = dob_button;
            cancel = true;
            AppUtil.showToast(getApplicationContext(), getString(R.string.sel_age));
        }

        // Check for a valid mobile name.
        String mobileNumber = mobileNumberEditText.getText().toString();
        if (TextUtils.isEmpty(mobileNumber)) {
            mobileNumberEditText.setError(getString(R.string.error_field_required));
            focusView = mobileNumberEditText;
            cancel = true;
        } else if (mobileNumber.length() < 10) {
            mobileNumberEditText.setError(getString(R.string.mobile_no_should_10_digits));
            focusView = mobileNumberEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (AppUtil.isOnline(getApplicationContext())) {
                showProgress(true);
                dob = Calendar.getInstance();
                dob.set(Calendar.YEAR, (dob.get(Calendar.YEAR) - Integer.parseInt(dob_button.getText().toString().trim())));
                dob.set(Calendar.MONTH, 0);
                dob.set(Calendar.DAY_OF_MONTH, 1);
                dob.set(Calendar.HOUR_OF_DAY, 00);
                dob.set(Calendar.MINUTE, 00);
                dob.set(Calendar.SECOND, 00);
                String dobString = AppUtil.getDate(dob.getTimeInMillis(), "EEE, MMM dd, yyyy");
                Log.d(TAG, "first name : " + firstname + ", last name : " + lastName);
                Log.d(TAG, "Gender type : " + genderType + ", Dob : " + dob.getTime() + "\n mobile : " + mobileNumberEditText.getText());
                Log.d(TAG, "dobString : " + dobString);
                String mobileNumberWithCode = "+91-" + mobileNumberEditText.getText();
                mRequestQueue.add(createUpdateRequest(firstname, lastName, "" + genderType, dobString, mobileNumberWithCode));
            } else {
                AppUtil.showDialog(this, getString(R.string.networkError));
            }
        }
    }

    public String getDob(Date date) {

        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
        String formattedDateq = fmt.format(date);
        Log.e("formattedDateq--->", "" + formattedDateq);
        return formattedDateq;
    }

    public StringRequest createSessionVerifyRequest(final Context context) {
        Log.d(TAG, "createSessionVerifyRequest");
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.LOGIN_VERIFY;
        Log.d(TAG, "szServerUrl - " + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "login response : " + response);
                if (response != null) {
                    Log.d(TAG, "Got webservice response");
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<ResponseVO>() {
                    }.getType();
                    ResponseVO responseVO = gson.fromJson(response, objectType);
                    if (responseVO != null && responseVO.getCode() == Constant.RESPONSE_SUCCESS) {
                        Log.d(TAG, "responseVO : " + responseVO);
                        AppUtil.storeUserVO(getApplicationContext(), responseVO.getUser());
                        loginResponseHandler(responseVO);
                    } else {
                        AppUtil.showToast(context, responseVO.getResponse());
                    }
                } else {
                    AppUtil.showToast(context, getString(R.string.networkError));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtil.showToast(context, context.getResources()
                        .getString(R.string.networkError));
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(ProfileRegistrationActivity.this);
                            break;
                    }
                } else {
                    AppUtil.showErrorDialog(getApplicationContext(), error);
                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(context, super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1));
        return mRequest;
    }

    private void loginResponseHandler(ResponseVO mResponseVO) {
        Log.d(TAG, "Inside responseHandler");
        int code = (int) mResponseVO.getCode();
        switch (code) {
            case Constant.RESPONSE_SUCCESS:

                saveUserDetails(mResponseVO);
                PatientUserVO selfProfile = AppUtil.getLoggedPatientVO(getApplicationContext());
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                defaultSharedPreferences.edit().putLong("current_selected_user", selfProfile.getPatientId()).commit();

                AppUtil.sendCampaignDetails(getApplicationContext(), Constant.EVENT_REGISTRATION);
                mRequestQueue.add(createUpdateProfileRequest(getParams()));


                break;

            case Constant.RESPONSE_WRONG_EMAILID_PASSWORD:
                AppUtil.showToast(getApplicationContext(),
                        mResponseVO.getResponse());
                break;
            case Constant.RESPONSE_ACCESS_DENIED:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.accessDenied));
                break;
            case Constant.RESPONSE_SERVER_ERROR:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.server_error_msg));
                break;
            case Constant.RESPONSE_CODE_INCOMPLETE_REGISTRATION:
                break;
            default:
                AppUtil.showToast(getApplicationContext(),
                        mResponseVO.getResponse());
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
            editor.putBoolean(Constant.LOGIN_STATUS, true);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    void OnReceivedLocation(Location location) {
        super.OnReceivedLocation(location);
        if (location != null) {
            startIntentServiceFetchAddress(location);
        }
    }

    protected void startIntentServiceFetchAddress(Location mLastLocation) {
        Intent intent = new Intent(ProfileRegistrationActivity.this, IntentServiceFetchAddress.class);
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
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            try {
                if (resultData != null) {
                    Intent mIntent = new Intent(ProfileRegistrationActivity.this, SaveUserLogIntentService.class);
                    mIntent.putExtra("loggedInLatitude", String.valueOf(mSharedPreferences.getString("lat", "0")));
                    mIntent.putExtra("loggedInLongitude", String.valueOf(mSharedPreferences.getString("lon", "0")));
                    mIntent.putExtra("city", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.CITY, ""));
                    mIntent.putExtra("district", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.DISTRICT, ""));
                    mIntent.putExtra("state", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.STATE, ""));
                    mIntent.putExtra("pinCode", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.POSTAL_CODE, ""));
                    mIntent.putExtra("country", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.COUNTRY, ""));
                    mIntent.putExtra(Constant.LOCALITY, resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.LOCALITY, ""));
                    mIntent.putExtra(Constant.SUB_LOCALITY, resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.ADDRESS_LINE_1, ""));
                    startService(mIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
