package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.ResponseVO;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class UpdatePassWordActivity extends BaseActivity {

    private static final String TAG = "UpdatePassWordActivity";
    private RequestQueue mRequestQueue;
    private String userName, oldPassword;
    private EditText newPassEditText, confirmPassEditText;
    private View mFormView;
    private View mStatusView;
    //private Tracker mTracker;
    private String PARAM_USERNAME = "username";
    private String PARAM_PASSWORD = "password";
    private String PARAM_NEW_PASSWORD = "newPass";
    private String PARAM_OLD_PASSWORD = "oldPass";
    private String PARAM_MOBILE_NUMBER = "mobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upadate_pass_word);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.enter_new_password);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.tv_roboto_regular_white_normal);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRequestQueue = AppVolley.getRequestQueue();
        mFormView = findViewById(R.id.form_layout);
        mStatusView = findViewById(R.id.progress_view);
        userName = getIntent().getStringExtra(PARAM_USERNAME);
        oldPassword = getIntent().getStringExtra(PARAM_OLD_PASSWORD);
        newPassEditText = (EditText) findViewById(R.id.new_password);
        confirmPassEditText = (EditText) findViewById(R.id.re_enter_password);
        Button submitButton = (Button) findViewById(R.id.update_button);
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
                        launchUpdatePassword();
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
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    private void launchUpdatePassword() {
        {

            // Reset errors.
            newPassEditText.setError(null);
            confirmPassEditText.setError(null);

            // Store values at the time of the login attempt.
            String newPassWord = newPassEditText.getText().toString();
            String confirmPassWord = confirmPassEditText.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password.
            if (TextUtils.isEmpty(newPassWord)) {
                newPassEditText.setError(getString(R.string.error_field_required));
                focusView = newPassEditText;
                cancel = true;
            } else if (newPassWord.length() < 4) {
                newPassEditText.setError(getString(R.string.error_invalid_password));
                focusView = newPassEditText;
                cancel = true;
            }

            // Check for a valid email address.
            if (!TextUtils.equals(newPassWord, confirmPassWord)) {
                confirmPassEditText.setError(getString(R.string.error_mismatch_password));
                focusView = confirmPassEditText;
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
                    mRequestQueue.add(createUpdateRequest(userName, oldPassword, newPassWord, confirmPassWord));
                    Log.d(TAG, "Email : " + userName + ", Old pass : " + oldPassword + ", newPass : " + newPassWord + ", confirm pass: " + confirmPassWord);
                } else {
                    AppUtil.showDialog(this,
                            getString(R.string.network_error));
                }
            }
        }
    }


    private StringRequest createUpdateRequest(final String userName, final String oldPassword, final String newPassWord, final String confirmPassWord) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.saveUpdatePassword;

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
                        responseHandler(mResponseVO);
                    } else {
                        AppUtil.showToast(getApplicationContext(),
                                getString(R.string.networkError));
                    }
                } else {

                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.networkError));
                }

                showProgress(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(UpdatePassWordActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),error);
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("oldPassword", oldPassword);
                params.put("password", newPassWord);
                params.put("confirm", confirmPassWord);
                params.put("username", userName);
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
        int code = (int) mResponseVO.getCode();
        switch (code) {

            case Constant.RESPONSE_CODE_PASSWORD_UPDATE:
            case Constant.RESPONSE_CODE_INCOMPLETE_REGISTRATION:
                showProgress(true);
                break;
            default:
                AppUtil.showToast(getApplicationContext(),
                        mResponseVO.getResponse());
                break;
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
