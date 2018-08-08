package com.patientz.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends BaseActivity {
    private final String TAG = "RegistrationActivity";
    private AutoCompleteTextView etEmailMobile;
    private String userName, mobileNumber;
    InputMethodManager imm;
    private RequestQueue mRequestQueue;
    private LinearLayout mStatusView;
    private View mFormView;
    private Button btSignUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String phoneNumberPattern = "^[789]\\d{9}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    private String PARAM_USERNAME = "username";
    private String PARAM_MOBILE_NUMBER = "mobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.tv_roboto_regular_white_normal);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etEmailMobile = (AutoCompleteTextView) findViewById(R.id.et_email_mobile);
        Intent mIntent=getIntent();
        if(mIntent!=null)
        {
            etEmailMobile.setText(!TextUtils.isEmpty(mIntent.getStringExtra(Constant.USER_ID))?mIntent.getStringExtra(Constant.USER_ID):"");
        }
        etEmailMobile.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signUp();
                    return true;
                }
                return false;
            }
        });
        populateAutoComplete();

        mStatusView = (LinearLayout) findViewById(R.id.progress);
        mFormView = findViewById(R.id.formView);
        TextView tvEnterEmail = (TextView) findViewById(R.id.tv_title_enter_email);
        btSignUp = (Button) findViewById(R.id.bt_signup);
        assert btSignUp != null;
        btSignUp.setOnTouchListener(new View.OnTouchListener() {
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
                        signUp();
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
        imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        etEmailMobile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    try {
                        long num = Long.parseLong(s.toString());
                        Log.i("", num + " is a number");
                        if (s.toString().length() > 0 && s.toString().trim().matches(phoneNumberPattern)) {
                        } else {
                            etEmailMobile.setError("Please enter 10 digit valid phone number");
                        }
                    } catch (NumberFormatException e) {
                        if (s.toString().length() > 0 && s.toString().trim().matches(emailPattern)) {
                        } else {
                            etEmailMobile.setError("Please enter valid email id");
                        }
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (s.toString().length() == 0) {
                    etEmailMobile.setError(null);
                    etEmailMobile.requestFocus();
//                    etEmailMobile.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    int maxLength = 30;
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(maxLength);
                    etEmailMobile.setFilters(fArray);
                } else {
                    try {
                        long num = Long.parseLong(s.toString());
                        Log.i("", num + " is a number");
                        if (s.toString().length() > 0) {
//                            etEmailMobile.setInputType(InputType.TYPE_CLASS_PHONE);
                            int maxLength = 10;
                            InputFilter[] fArray = new InputFilter[1];
                            fArray[0] = new InputFilter.LengthFilter(maxLength);
                            etEmailMobile.setFilters(fArray);
                        } else {
                            // etEmailMobile.setError("Invalid phone number");
                        }
                    } catch (NumberFormatException e) {
                        if (s.toString().length() > 0) {
//                            etEmailMobile.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            int maxLength = 30;
                            InputFilter[] fArray = new InputFilter[1];
                            fArray[0] = new InputFilter.LengthFilter(maxLength);
                            etEmailMobile.setFilters(fArray);
                        } else {
                            //etEmailMobile.setError("Invalid email id");
                        }
                    }
                }
            }
        });
        mRequestQueue = AppVolley.getRequestQueue();
    }

    private void populateAutoComplete() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] accounts = AccountManager.get(this).getAccounts();
        ArrayList<String> emailSet = new ArrayList<String>();
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }
        for (int i = 0; i < emailSet.size(); i++) {
            Log.e("mail--->", emailSet.get(i));
        }
        etEmailMobile.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
    }

    public void signUp() {
        imm.hideSoftInputFromWindow(btSignUp.getWindowToken(), 0);
        etEmailMobile.setError(null);
        if (!TextUtils.isEmpty(etEmailMobile.getText().toString())) {
            if (AppUtil.isOnline(getApplicationContext())) {
                userName = etEmailMobile.getText().toString();
                if (AppUtil.isValidEmail(userName)) {
                    callEmailRegisterWebService(userName);
                } else if (AppUtil.isValidMobileNumber(userName)) {
                    mobileNumber = userName;
                    Log.d(TAG, "MOBILE NUMBER=" + mobileNumber);
                    userName = "+91-" + userName;
                    callMobileRegistrationWebservice(userName);
                } else {
                    etEmailMobile.setError(getString(R.string.error_enter_email_mobile));
                }
            } else {
                AppUtil.showToast(getApplicationContext(), getString(R.string.offlineMode));
            }

        } else {
            etEmailMobile.setError(getString(R.string.enter_email_mobile));
        }
    }


    private void callMobileRegistrationWebservice(String mobile) {
        showProgress(true);
        mRequestQueue.add(createForgotRequest("emailOrMobile", mobile));
    }

    private void callEmailRegisterWebService(String email) {
        showProgress(true);
        mRequestQueue.add(createForgotRequest("emailOrMobile", email));
    }

    private StringRequest createForgotRequest(final String type, final String userName) {
        String szServerUrl =
                WebServiceUrls.serverUrl + WebServiceUrls.processForgotPassword;
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                ResponseVO mResponseVO = null;
                if (response != null) {
                    try {
                        if (response != null) {
                            Gson gson = new Gson();
                            Type objectType = new TypeToken<ResponseVO>() {
                            }.getType();

                            mResponseVO = gson.fromJson(
                                    response, objectType);

                            if (mResponseVO != null) {
                                responseHandler(mResponseVO);
                            } else {
                                responseHandler(mResponseVO);
                            }
                        } else {
                            responseHandler(mResponseVO);
                        }
                    } catch (JsonParseException e) {
                        responseHandler(mResponseVO);
                    }
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
                            AppUtil.showErrorCodeDialog(ForgotPasswordActivity.this);
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
                params.put(type, userName);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params1 = super.getHeaders();
                return AppUtil.addHeadersForApp(getApplicationContext(), params1);
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(20000,1,1));
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {
        int code = (int) mResponseVO.getCode();
        Log.d(TAG, "code = " + code);
        switch (code) {
            case Constant.RESPONSE_SUCCESS:

                if (userName.contains("@")) {
                    AppUtil.showToast(ForgotPasswordActivity.this, getString(R.string.pswrd_sent_to_mail));
                    Intent intent = new Intent();
                    intent.putExtra("userName", etEmailMobile.getText().toString());
                    setResult(5, intent);
                    finish();
                } else {
                    AppUtil.showToast(ForgotPasswordActivity.this, getString(R.string.pswrd_sent_to_mobile));
                    Intent i = new Intent(ForgotPasswordActivity.this, OTPRegisterActivity.class);
                    i.putExtra(PARAM_USERNAME, userName);
                    i.putExtra(PARAM_MOBILE_NUMBER, mobileNumber);
                    Log.d(TAG, "USER NAME=" + userName);
                    Log.d(TAG, "USER NAME=" + mobileNumber);
                    startActivity(i);
                    finish();

                }

                break;
            case Constant.RESPONSE_DUPLICATE_EMAIL:
                AppUtil.showToast(ForgotPasswordActivity.this, mResponseVO.getResponse());
                break;
            case Constant.RESPONSE_ACCESS_DENIED:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.accessDenied));
                break;
            case Constant.RESPONSE_SERVER_ERROR:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.server_error_msg));
                break;
            case Constant.RESPONSE_FAILURE:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.ioException));
                break;
            case Constant.TERMINATE:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.ioException));
                break;
            case Constant.RESPONSE_NULL:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.ioException));
                break;
            default:
                AppUtil.showDialog(this, mResponseVO.getResponse());
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
        finish();
        super.onBackPressed();
    }

}
