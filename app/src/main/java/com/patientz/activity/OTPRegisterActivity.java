package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class OTPRegisterActivity extends BaseActivity {

    private static final String TAG = "OTPRegisterActivity";
    private EditText OTPEditText;
    private CountDownTimer timer;
    private TextView gettingOTP, tvOtpCount;
    private String userName;
    private String received_OTP;
    public final static String ACTION_OTP_RECEIVED = "OTPONRECEIVE";
    private String PARAM_PASSWORD = "password";
    private String PARAM_USERNAME = "username";
    private String MOBILE_NUMBER = "mobilenumber";
    private ProgressBar progressBar;
    private RequestQueue mRequestQueue;
    private View mFormView;
    private View mStatusView;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_register);
       // AppUtil.requestPermissions(OTPRegisterActivity.this, android.Manifest.permission.READ_SMS, 1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.tv_roboto_regular_white_normal);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.otp_verification);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFormView = findViewById(R.id.otp_layout);
        mStatusView = findViewById(R.id.progress_view);
        mRequestQueue = AppVolley.getRequestQueue();
        OTPEditText = (EditText) findViewById(R.id.et_OTP);
        OTPEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6) {
                    submitButton.setVisibility(View.VISIBLE);
                }
            }
        });
        gettingOTP = (TextView) findViewById(R.id.tv_title_enter_otp);
        tvOtpCount = (TextView) findViewById(R.id.tv_title_enter_otp_count);
        progressBar = (ProgressBar) findViewById(R.id.otpProgress);
        progressBar.setMax(60);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        OTPEditText.setClickable(false);
        OTPEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendOtp();
                    return true;
                }
                return false;
            }
        });
        userName = getIntent().getStringExtra(PARAM_USERNAME);
        submitButton = (Button) findViewById(R.id.bt_submit);
        assert submitButton != null;
        submitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Button view = (Button) v;
                        view.getBackground().setColorFilter(Color.parseColor("#A51400"), PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        Button view1 = (Button) v;
                        view1.getBackground().clearColorFilter();
                        view1.invalidate();
                        sendOtp();
                        break;
                    case MotionEvent.ACTION_CANCEL: {
                        Button view = (Button) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(messageReceiver,
                new IntentFilter(ACTION_OTP_RECEIVED));
        timer = new CountDownTimer(60 * 1000, 1000) {
            int progress = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                tvOtpCount.setText((millisUntilFinished / 1000) + " " + getString(R.string.secs));
                progressBar.setProgress(progress);
                progress++;
            }

            @Override
            public void onFinish() {
                OTPEditText.setClickable(true);
                tvOtpCount.setVisibility(View.GONE);
                gettingOTP.setText(getString(R.string.failed_to_get_otp));
                progressBar.setProgress(100);
            }
        };


        timer.start();
    }

    private void sendOtp() {
        if (timer != null)
            timer.cancel();
        if (!TextUtils.isEmpty(OTPEditText.getText().toString())) {
            showProgress(true);
            Log.d(TAG,"mist");
            mRequestQueue.add(createLoginAPIRequest(userName, OTPEditText.getText().toString()));
        } else {
            AppUtil.showToast(getApplicationContext(), getString(R.string.error_otp));
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_OTP_RECEIVED)) {
                String otpRecived = intent.getStringExtra("OTP");
                if (!TextUtils.isEmpty(otpRecived)) {

                    if (otpRecived.contains("OTP:")) {
                        submitButton.setVisibility(View.VISIBLE);
                        HashMap<String, Object> upshotData = new HashMap<>();
                        upshotData.put(Constant.UpshotEvents.OTP_RECEIVED, true);
                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.OTP_RECEIVED);
                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                        int string2 = otpRecived.indexOf("OTP:");
                        String otp = otpRecived.substring(string2 + 4, string2 + 10);
                        Log.e("otpRecived - > ", "" + otpRecived);
                        Log.e("otp - > ", "" + otp);
                        //String otp = otpRecived.substring(71, 77);
                        OTPEditText.setText(otp);//setText(otp);
                        timer.cancel();
                        tvOtpCount.setVisibility(View.GONE);
                        gettingOTP.setText(getString(R.string.recivedotp));
                        received_OTP = otp;
                        Log.d(TAG, "OTP FROM MSG : " + otp);
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }
        }
    };

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        if (timer != null)
            timer.cancel();
        super.onStop();
    }


    private void callLoginForVerification() {
        Intent intent = new Intent(this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_PASSWORD, OTPEditText.getText().toString().trim());
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private JsonObjectRequest createLoginAPIRequest(final String userId, final String userPass) {

        Log.d(TAG, "createLoginAPIRequest");
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

                                    if (responseObject.getString("roles").contains(Constant.NEW_USER_ROLE) || responseObject.getString("roles").contains(Constant.ROLE)) {
                                        HashMap<String, Object> upshotData = new HashMap<>();
                                        upshotData.put(Constant.UpshotEvents.NEW_USER, true);
                                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NEW_USER);
                                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                                        storeCredentialsInSP(responseObject);
                                        //mRequestQueue.add(createLoginRequest(userName, userPass));
                                        Log.d("Response 1 : ", responseObject.get("roles").toString());
                                        Intent intent3 = new Intent(OTPRegisterActivity.this, ProfileRegistrationActivity.class);
                                        intent3.putExtra(PARAM_USERNAME, userName);
                                        intent3.putExtra(PARAM_PASSWORD, OTPEditText.getText().toString().trim());
                                        intent3.putExtra(MOBILE_NUMBER, userName);
                                        finish();
                                        startActivityForResult(intent3, Constant.RESPONSE_CODE_INCOMPLETE_REGISTRATION);
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
                showProgress(false);
                NetworkResponse networkResponse = volleyError.networkResponse;
                int statuscode = -2;
                try {
                    statuscode = networkResponse.statusCode;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("statuscode: - ", "" + statuscode);
                if(statuscode==503 ||volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError)
                {
                   AppUtil.showErrorCodeDialog(OTPRegisterActivity.this);
                }
                else {
                    if (statuscode == Constant.CREDENTIALS_INCORRECT) {
                        CommonUtils.getSP(OTPRegisterActivity.this).edit().putString(Constant.USER_ID, userName).commit();
                        Intent mIntent = new Intent(OTPRegisterActivity.this, ChangePassWordActivity.class);
                        getIntent().getStringExtra(Constant.PARAM_KEY_USERNAME);
                        mIntent.putExtra("mobilenumber", getIntent().getStringExtra("mobile"));
                        mIntent.putExtra("old_password", OTPEditText.getText().toString().trim());
                        startActivity(mIntent);
                        finish();
                    } else {
                            Log.i("error3", "error3");
                            AppUtil.showErrorDialog(OTPRegisterActivity.this,volleyError);
                    }
                }
            }
        });
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000,1,1));
        return mRequest;
    }

    private void storeCredentialsInSP(JSONObject responseObject) {
        try {

            CommonUtils.getSP(this).edit().putString(Constant.USER_ID, userName)
                    .putString(Constant.TOKEN_TYPE, (String) responseObject.get(Constant.TOKEN_TYPE))
                    .putString(Constant.ACCESS_TOKEN, (String) responseObject.get(Constant.ACCESS_TOKEN))
                    .putBoolean(Constant.IS_USER_LOGGED_IN, false)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
