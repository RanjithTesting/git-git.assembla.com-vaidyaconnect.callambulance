package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import java.util.HashMap;
import java.util.Map;


public class ChangePassWordActivity extends BaseActivity {

    private static final String TAG = "ChangePassWordActivity";
    private RequestQueue mRequestQueue;
    private String userName;
    private EditText newPassEditText, confirmPassEditText, old_password;
    private View mFormView;
    private View mStatusView;
    //private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_word);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.action_change_pwd);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userName = CommonUtils.getSP(this).getString(Constant.USER_ID, "");

        mRequestQueue = AppVolley.getRequestQueue();
        mFormView = findViewById(R.id.form_layout);
        mStatusView = findViewById(R.id.progress_view);
        old_password = (EditText) findViewById(R.id.old_password);
        newPassEditText = (EditText) findViewById(R.id.new_password);
        confirmPassEditText = (EditText) findViewById(R.id.re_enter_password);
        confirmPassEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE
                        || id == EditorInfo.IME_NULL
                        || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(confirmPassEditText.getWindowToken(), 0);
                    launchUpdatePassword();
                    return true;
                } else {
                    return false;
                }
            }
        });
        Button submitButton = (Button) findViewById(R.id.update_button);
        if (getIntent().getStringExtra("old_password") != null) {
            if (getIntent().getStringExtra("old_password").length() > 0) {
                old_password.setText(getIntent().getStringExtra("old_password"));
                Log.d(TAG,"old_password="+getIntent().getStringExtra("old_password"));
                old_password.setEnabled(false);
            }
        }


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

/*        TextView tv=(TextView)findViewById(R.id.title);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Lobster.ttf");
        tv.setTypeface(face);*/

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void launchUpdatePassword() {
        // Reset errors.
        newPassEditText.setError(null);
        confirmPassEditText.setError(null);
        old_password.setError(null);
        // Store values at the time of the login attempt.
        String oldPwd = old_password.getText().toString().trim();
        String newPassWord = newPassEditText.getText().toString();
        String confirmPassWord = confirmPassEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(oldPwd)) {
            old_password.setError(getString(R.string.error_field_required));
            focusView = old_password;
            cancel = true;
        } else if (oldPwd.length() < 6 || oldPwd.length() > 20) {
            old_password.setError(getString(R.string.error_invalid_password));
            focusView = old_password;
            cancel = true;
        } else if (TextUtils.isEmpty(newPassWord)) {
            newPassEditText.setError(getString(R.string.error_field_required));
            focusView = newPassEditText;
            cancel = true;
        } else if (newPassWord.length() < 6 || newPassWord.length() > 20) {
            newPassEditText.setError(getString(R.string.error_invalid_password));
            focusView = newPassEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(confirmPassWord)) {
            confirmPassEditText.setError(getString(R.string.error_field_required));
            focusView = confirmPassEditText;
            cancel = true;
        } else if (confirmPassWord.length() < 6 || confirmPassWord.length() > 20) {
            confirmPassEditText.setError(getString(R.string.error_invalid_password));
            focusView = confirmPassEditText;
            cancel = true;
        } else if (!TextUtils.equals(newPassWord, confirmPassWord)) {
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
                mRequestQueue.add(createUpdateRequest(userName, oldPwd, newPassWord, confirmPassWord));
                Log.d(TAG, "Email : " + userName + ", Old pass : " + oldPwd + ", newPass : " + newPassWord + ", confirm pass: " + confirmPassWord);
            } else {
                AppUtil.showDialog(this,
                        getString(R.string.network_error));
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
                            AppUtil.showErrorCodeDialog(ChangePassWordActivity.this);
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
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000,1,1));
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {
        showProgress(false);
        int code = (int) mResponseVO.getCode();
        switch (code) {

            case Constant.RESPONSE_CODE_PASSWORD_UPDATE:
            case Constant.RESPONSE_CODE_INCOMPLETE_REGISTRATION:
                //AppUtil.showToast(getApplicationContext(), getString(R.string.password_updated_success));
                mRequestQueue.add(createLoginRequest1(userName, confirmPassEditText.getText().toString().trim()));
                break;
            default:
                AppUtil.showToast(getApplicationContext(),
                        mResponseVO.getResponse());
                break;
        }
    }

    private JsonObjectRequest createLoginRequest1(final String userId, final String userPass) {
        showProgress(true);
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
                        showProgress(false);
                        Log.e("Response: - ", String.valueOf(response));

                        if (response != null)
                            try {
                                JSONObject responseObject = new JSONObject(String.valueOf(response));

                                if (responseObject.has("access_token")) {

                                    Log.d("Response key access_token : ", responseObject.get("access_token").toString());

                                    if (!responseObject.getString("roles").contains(Constant.ROLE) || !responseObject.getString("roles").contains(Constant.NEW_USER_ROLE)) {
                                        storeCredentialsInSP(responseObject);
                                        mRequestQueue.add(createSessionVerifyRequest(ChangePassWordActivity.this));
                                        Log.d("Response 1 : ", responseObject.get("roles").toString());
                                    } else {
                                        AppUtil.showToast(getApplicationContext(),
                                                "You are not authorized to access this application.");
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

                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(ChangePassWordActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),volleyError);
                }
            }
        });
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000,1,1));
        return mRequest;
    }

    public StringRequest createSessionVerifyRequest(final Context context) {
        Log.d(TAG, "createSessionVerifyRequest");
        showProgress(true);
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.LOGIN_VERIFY;
        Log.d(TAG, "szServerUrl - " + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);
                Log.d(TAG, "login response : " + response);
                if (response != null) {
                    Log.d(TAG, "Got webservice response");
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<ResponseVO>() {
                    }.getType();
                    ResponseVO responseVO = gson.fromJson(response, objectType);
                    if (responseVO != null) {
                        Log.d(TAG, "responseVO : " + responseVO);
                        loginResponseHandler(responseVO);
                    }
                } else {
                    AppUtil.showToast(context, getString(R.string.networkError));
                }
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
                            AppUtil.showErrorCodeDialog(ChangePassWordActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),error);
                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(context, super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000,1,1));
        return mRequest;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void loginResponseHandler(ResponseVO mResponseVO) {
        Log.d(TAG, "Inside responseHandler");
        int code = (int) mResponseVO.getCode();
        switch (code) {
            case Constant.RESPONSE_SUCCESS:

                saveUserDetails(mResponseVO);
                PatientUserVO selfProfile = AppUtil.getLoggedPatientVO(getApplicationContext());
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                defaultSharedPreferences.edit().putLong("current_selected_user", selfProfile.getPatientId()).commit();

//                finishAffinity();
                Intent intent = new Intent(ChangePassWordActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

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
                AppUtil.showToast(getApplicationContext(),mResponseVO.getResponse());
                Intent intent3 = new Intent(this, ProfileRegistrationActivity.class);
                intent3.putExtra(Constant.PARAM_KEY_USERNAME, userName);
                intent3.putExtra(Constant.PARAM_KEY_PASSWORD, newPassEditText.getText().toString());
                intent3.putExtra(Constant.MOBILE_NO, getIntent() != null ? getIntent().getStringExtra(Constant.MOBILE_NO) : "");
                Log.d(TAG, intent3.getExtras().toString());
                startActivity(intent3);
                finish();
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

    private void storeCredentialsInSP(JSONObject responseObject) {
        try {
            CommonUtils.getSP(this).edit().putString(Constant.USER_ID, userName)
                    .putString(Constant.TOKEN_TYPE, (String) responseObject.get(Constant.TOKEN_TYPE))
                    .putString(Constant.ACCESS_TOKEN, (String) responseObject.get(Constant.ACCESS_TOKEN))
                    .putBoolean(Constant.IS_USER_LOGGED_IN, true)
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
