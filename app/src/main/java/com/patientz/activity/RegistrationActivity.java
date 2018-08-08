package com.patientz.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.ResponseVO;
import com.patientz.gcm.GcmRegistrationIntentService;
import com.patientz.service.IntentServiceFetchAddress;
import com.patientz.service.SaveUserLogIntentService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "RegistrationActivity";
    private AutoCompleteTextView etEmailMobile;
    private String userName, mobileNumber;
    InputMethodManager imm;
    AlertDialog.Builder builder;
    private String PARAM_USERNAME = "username";
    private RequestQueue mRequestQueue;
    private LinearLayout mStatusView;
    private View mFormView;
    AlertDialog alertDialog = null;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String phoneNumberPattern = "^[789]\\d{9}$";
    private TextView textchangelanguage, textsetlang;
    private CheckBox termsConditionCheckBox;
    private String PARAM_MOBILE_NUMBER = "mobile";
    private Button btSignUp, btExistingUser, btSelectLang;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    SharedPreferences defaultSharedPreferences;
    protected static boolean mResolvingError = false;
    protected LocationRequest mLocationRequest;
    protected static final String STATE_RESOLVING_ERROR = "resolving_error";
    protected static final int REQUEST_RESOLVE_ERROR = 1001;
    private GoogleApiClient mGoogleApiClient;
    AddressResultReceiver mResultReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        changeLang(defaultSharedPreferences.getString("sel_language", "en"));
        setContentView(R.layout.activity_registration);
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(RegistrationActivity.this)
                .addConnectionCallbacks(RegistrationActivity.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        AppUtil.sendCampaignDetails(getApplicationContext(), "");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.tv_roboto_regular_white_normal);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.create_account);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etEmailMobile = (AutoCompleteTextView) findViewById(R.id.et_email_mobile);
        populateAutoComplete();
        textchangelanguage = (TextView) findViewById(R.id.textchangelanguage);
        textchangelanguage.setPaintFlags(textchangelanguage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textsetlang = (TextView) findViewById(R.id.textsetlang);
        textchangelanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialogChooseLanguage();
            }
        });
        textsetlang.setText(" ( " + defaultSharedPreferences.getString("sel_languagetext", "English") + " ) ");
        termsConditionCheckBox = (CheckBox) findViewById(R.id.termsAndConditionsCheckbox);
        TextView agreementTextView = (TextView) findViewById(R.id.registration_user_agreement_part2);
        agreementTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mStatusView = (LinearLayout) findViewById(R.id.progress);
        mFormView = findViewById(R.id.formView);
        TextView tvEnterEmail = (TextView) findViewById(R.id.tv_title_enter_email);
        btSignUp = (Button) findViewById(R.id.bt_signup);


        if (defaultSharedPreferences.getBoolean("showLangDialog", false) != true) {
            customDialogChooseLanguage();
        }
        btExistingUser = (Button) findViewById(R.id.bt_existing_user);
        btExistingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForGCMMessages();
                Intent mIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(mIntent);
                finish();

            }
        });
        etEmailMobile.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_NULL
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etEmailMobile.getWindowToken(), 0);
                    signUp();
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (!defaultSharedPreferences.getBoolean("showLangDialog", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!checkPermissions()) {//&& !defaultSharedPreferences.getBoolean(Constant.PERMISSION_ASKED_STATUS,false)) {
                    askUserToGrantRequiredPermissions();
                }
            }
        }
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
                //}
            }
        });
        imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        etEmailMobile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    if (s.toString().length() > 0 && s.toString().trim().matches(phoneNumberPattern)) {
                    } else {
                        etEmailMobile.setError("Please enter 10 digit valid phone number");
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    etEmailMobile.setError(null);
                    etEmailMobile.requestFocus();
                    int maxLength = 30;
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(maxLength);
                    etEmailMobile.setFilters(fArray);
                } else {
                    try {
                        long num = Long.parseLong(s.toString());
                        Log.i("", num + " is a number");
                        if (s.toString().length() > 0) {
                            int maxLength = 10;
                            InputFilter[] fArray = new InputFilter[1];
                            fArray[0] = new InputFilter.LengthFilter(maxLength);
                            etEmailMobile.setFilters(fArray);
                        } else {
                        }
                    } catch (NumberFormatException e) {
                        if (s.toString().length() > 0) {
                            int maxLength = 30;
                            InputFilter[] fArray = new InputFilter[1];
                            fArray[0] = new InputFilter.LengthFilter(maxLength);
                            etEmailMobile.setFilters(fArray);
                        } else {
                        }
                    }
                }
            }
        });
        mRequestQueue = AppVolley.getRequestQueue();

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void showDialogToAskPhoneStatePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.phone_state_permission_mandatory_msg))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.OK)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        RegistrationActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                        Constant.PERMISSIONS_REQUEST_CODE_PHONE);
                            }
                        }
                )
                .create();
        builder.create().show();
    }

    private void showDialogToAskUserForStoragePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.permission_grant_storage_permission))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.OK)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        RegistrationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        Constant.PERMISSIONS_REQUEST_CODE_STORAGE);
                            }
                        }
                )
                .create();
        builder.create().show();
    }


    private boolean checkPermissions() {

        boolean permissionStatus = false;
        PackageManager pm = getPackageManager();
        int a = pm.checkPermission(
                android.Manifest.permission.READ_PHONE_STATE,
                getPackageName());
        Log.d(TAG, "READ_PHONE_STATE=" + a + "");
        int b = pm.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                getPackageName());
        Log.d(TAG, "ACCESS_FINE_LOCATION=" + b + "");

        int c = pm.checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getPackageName());
        Log.d(TAG, "WRITE_EXTERNAL_STORAGE=" + c + "");

        int d = pm.checkPermission(
                android.Manifest.permission.GET_ACCOUNTS,
                getPackageName());
        Log.d(TAG, "GET_ACCOUNTS=" + d + "");

        if (a + b + c + d /*+ e*/ == PackageManager.PERMISSION_GRANTED) {
            permissionStatus = true;
        }
        Log.d(TAG, "permissionStatus : " + permissionStatus + "\n " + a + b + c + d /*+ e*/);
        return permissionStatus;
    }

    private void askUserToGrantRequiredPermissions() {
        ActivityCompat.requestPermissions(
                this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, Constant.PERMISSIONS_REQUEST_CODE_PHONE);
    }

    private void launchSettingPermissionScreen() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            if (termsConditionCheckBox.isChecked()) {
                if (AppUtil.isOnline(getApplicationContext())) {
                    registerForGCMMessages();
                    userName = etEmailMobile.getText().toString();
                    if (AppUtil.isValidEmail(userName)) {
                        callEmailRegisterWebService(userName);
                    } else if (AppUtil.isValidMobileNumber(userName)) {
                        mobileNumber = userName;
                        Log.d(TAG, "MOBILE NUMBER=" + mobileNumber);
                        userName = "+91-" + userName;
                        if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                            callMobileRegistrationWebservice(userName);
                        } else {
                            AppUtil.requestPermissions(RegistrationActivity.this, Manifest.permission.SEND_SMS, Constant.PERMISSIONS_REQUEST_CODE_SMS);
                        }

                    } else {
                        etEmailMobile.setError(getString(R.string.error_enter_email_mobile));
                    }
                } else {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.offlineMode));
                }
            } else {
                AppUtil.showToast(getApplicationContext(), getString(R.string.error_terms_condition));
            }
        } else {
            etEmailMobile.setError(getString(R.string.titleMobileNumber));
        }
    }

    private void registerForGCMMessages() {
        Intent gcmRegService = new Intent(RegistrationActivity.this, GcmRegistrationIntentService.class);
        startService(gcmRegService);
    }


    private void callMobileRegistrationWebservice(String mobile) {
        showProgress(true);
        mRequestQueue.add(createRegistrationRequest("phoneNumber", mobile));
    }

    private void callEmailRegisterWebService(String email) {
        showProgress(true);
        mRequestQueue.add(createRegistrationRequest("email", email));
    }

    private StringRequest createRegistrationRequest(final String type, final String userName) {
        String szServerUrl =
                WebServiceUrls.serverUrl + WebServiceUrls.simplePatientRegistration;
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
                        responseHandler(type, mResponseVO);
                    } else {
                        AppUtil.showToast(getApplicationContext(),
                                getString(R.string.networkError));
                        Answers.getInstance().logSignUp(new SignUpEvent()
                                .putMethod(type)
                                .putCustomAttribute("issue", "responseVO null")
                                .putSuccess(false));
                    }
                } else {
                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.networkError));
                    Answers.getInstance().logSignUp(new SignUpEvent()
                            .putMethod(type)
                            .putCustomAttribute("issue", "response empty")
                            .putSuccess(false));
                }

                showProgress(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(type)
                        .putCustomAttribute("issue", "server connection issue")
                        .putSuccess(false));
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(RegistrationActivity.this);
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
                params.put(type, userName);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params1 = super.getHeaders();
                return AppUtil.addHeadersForApp(getApplicationContext(), params1);
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 1));
        return mRequest;
    }


    private void responseHandler(final String loginType, ResponseVO mResponseVO) {
        int code = (int) mResponseVO.getCode();
        Log.d(TAG, "code = " + code);
        switch (code) {
            case Constant.RESPONSE_SUCCESS:
                saveRegistrationDetails(mResponseVO);
                if (userName.contains("@")) {
                    AppUtil.showToast(RegistrationActivity.this, "Thank you for registering, Please check your Email for your OTP to login.");
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    intent.putExtra("userName", etEmailMobile.getText().toString());
                    finish();
                    startActivity(intent);
                } else {

                    Intent i = new Intent(RegistrationActivity.this, OTPRegisterActivity.class);
                    i.putExtra(PARAM_USERNAME, userName);
                    i.putExtra(PARAM_MOBILE_NUMBER, mobileNumber);
                    Log.d(TAG, "USER NAME=" + userName);
                    Log.d(TAG, "USER NAME=" + mobileNumber);
                    HashMap<String, Object> upshotData = new HashMap<>();
                    upshotData.put(Constant.UpshotEvents.OTP_REQUESTED, true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.OTP_REQUESTED);
                    Log.d(TAG, "upshot data=" + upshotData.entrySet());
                    startActivity(i);
                    finish();
                }
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(loginType)
                        .putCustomAttribute("issue", "no issue")
                        .putSuccess(true));
                break;
            case Constant.RESPONSE_DUPLICATE_EMAIL:
                AppUtil.showToast(RegistrationActivity.this, mResponseVO.getResponse());
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(loginType)
                        .putCustomAttribute("issue", "already registered")
                        .putSuccess(false));
                break;
            case Constant.RESPONSE_ACCESS_DENIED:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.accessDenied));
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(loginType)
                        .putCustomAttribute("issue", "access denied")
                        .putSuccess(false));
                break;
            case Constant.RESPONSE_SERVER_ERROR:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.server_error_msg));
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(loginType)
                        .putCustomAttribute("issue", "server ISE")
                        .putSuccess(false));
                break;
            case Constant.RESPONSE_FAILURE:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.ioException));
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(loginType)
                        .putCustomAttribute("issue", "response failure")
                        .putSuccess(false));
                break;
            case Constant.TERMINATE:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.ioException));
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(loginType)
                        .putCustomAttribute("issue", "response terminate")
                        .putSuccess(false));
                break;
            case Constant.RESPONSE_NULL:
                AppUtil.showToast(getApplicationContext(),
                        getApplicationContext().getString(R.string.responseNull));
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(loginType)
                        .putCustomAttribute("issue", "response null")
                        .putSuccess(false));
                break;
            default:
                AppUtil.showDialog(this, mResponseVO.getResponse());
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(loginType)
                        .putCustomAttribute("issue", "unknown issue")
                        .putSuccess(false));
                break;
        }
    }

    private void saveRegistrationDetails(ResponseVO mResponseVO) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constant.K_REG_ID, mResponseVO.getDefaultRegId());
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppUtil.storeUserVO(getApplicationContext(), mResponseVO.getUser());
        AppUtil.updateUserInfoUpshot(getApplicationContext());
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

    public void customDialogChooseLanguage() {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_choose_language, null);
        Button btStart = (Button) layout.findViewById(R.id.bt_start);
        btSelectLang = (Button) layout.findViewById(R.id.bt_select_lang);

        btSelectLang.setText(defaultSharedPreferences.getString("sel_languagetext", "English"));

        builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setCancelable(false);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
        btSelectLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listLanguagesPopup();
            }
        });
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultSharedPreferences.edit().putBoolean("showLangDialog", true).commit();
                alertDialog.dismiss();
                finish();
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void listLanguagesPopup() {

        final String[] languageslist = {getString(R.string.english), getString(R.string.hindi), getString(R.string.telugu), getString(R.string.urdu), getString(R.string.kannada), getString(R.string.tamil), getString(R.string.oriya)};
        final String[] languageslistvalue = {"en", "hi", "te", "ur", "kn", "ta", "or"};
        final String[] languages = {"English", "Hindi", "Telugu", "Urdu", "Kannada", "Tamil", "Oriya"};


        AlertDialog.Builder ad = new AlertDialog.Builder(RegistrationActivity.this);
        ad.setTitle(R.string.select_language);
        final AlertDialog mAlertDialog = ad.create();
        ad.setSingleChoiceItems(languageslist, defaultSharedPreferences.getInt("sel_position", 0), new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.SIGNUP_PAGE_CHANGE_LANGUAGE, languageslistvalue[arg1]);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.SIGNUP_PAGE_CHANGE_LANGUAGE);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                defaultSharedPreferences.edit().putString("sel_language", languageslistvalue[arg1]).commit();
                defaultSharedPreferences.edit().putInt("sel_position", arg1).commit();
                defaultSharedPreferences.edit().putString("sel_languagetext", languageslist[arg1]).commit();
                btSelectLang.setText(languages[arg1]);
                arg0.dismiss();
            }
        });
        ad.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "permissions size=" + permissions.length);
        switch (requestCode) {
            case Constant.PERMISSIONS_REQUEST_CODE_PHONE:
                Log.d(TAG, " Manifest.permission.READ_PHONE_STATE=" + Manifest.permission.READ_PHONE_STATE);
                Log.d(TAG, "shouldShowRequestPermissionRationale =" + ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this,
                        Manifest.permission.READ_PHONE_STATE));
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "READ PERMISSION GRANTED");
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.PROCESS_OUTGOING_CALLS}, Constant.PERMISSIONS_REQUEST_CODE_STORAGE);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this,
                        Manifest.permission.READ_PHONE_STATE)) {
                    Log.d(TAG, "CULPRIT");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    launchSettingPermissionScreen();
                    finish();
                } else {
                    showDialogToAskPhoneStatePermission();
                }
                break;

            case Constant.PERMISSIONS_REQUEST_CODE_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callMobileRegistrationWebservice(userName);
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this,
                        Manifest.permission.SEND_SMS)) {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_sms_grant_msg), new String[]{Manifest.permission.SEND_SMS}, Constant.PERMISSIONS_REQUEST_CODE_SMS);
                } else {
                }
                break;
            case Constant.PERMISSIONS_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "STORAGE PERMISSION GRANTED");
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.GET_ACCOUNTS/*,Manifest.permission.SEND_SMS*/}, Constant.PERMISSIONS_REQUEST_CODE_LOCATION);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    launchSettingPermissionScreen();
                    finish();
                } else {
                    Log.d(TAG, "STORAGE PERMISSION NOT GRANTED /NEVER ASK CHECKED");
                    showDialogToAskUserForStoragePermission();
                }
                break;
            case Constant.PERMISSIONS_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "LOCATION PERMISSION GRANTED");
                    if (mGoogleApiClient != null) {
                        if (mGoogleApiClient.isConnected()) {
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mCurrentLocation != null) {
                                mResultReceiver = new AddressResultReceiver(new Handler());
                                startIntentServiceFetchAddress(mCurrentLocation);
                            }
                            Log.d(TAG, "mGoogleApiClient.isConnected() =" + mGoogleApiClient.isConnected());
                        }
                    }
                }
                break;

        }

    }

    protected void startIntentServiceFetchAddress(Location mLastLocation) {
        Intent intent = new Intent(RegistrationActivity.this, IntentServiceFetchAddress.class);
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
            try {
                if (resultData != null) {
                    HashMap<String, Object> upshotData = new HashMap<>();
                    upshotData.put("sub_locality", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.ADDRESS_LINE_1, ""));
                    upshotData.put("locality", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.LOCALITY, ""));
                    upshotData.put("city", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.CITY, ""));
                    upshotData.put("state", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.STATE, ""));
                    upshotData.put("district", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.DISTRICT, ""));
                    upshotData.put("postal_code", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.POSTAL_CODE, ""));
                    Log.d("LAUNCH_ADDRESS_UPSHOT=", upshotData != null ? upshotData.toString() : "");
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.APP_LAUNCH_ADDRESS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialogWithMoreSpecificPermissionInfo(String message, final String[] permissions, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("");
        builder.setPositiveButton(R.string.give_permission, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(
                        RegistrationActivity.this, permissions,
                        permissionRequestCode);
            }
        });
        builder.setNegativeButton(R.string.lable_later, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callMobileRegistrationWebservice(userName);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            mResultReceiver = new AddressResultReceiver(new Handler());
            startIntentServiceFetchAddress(mCurrentLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


