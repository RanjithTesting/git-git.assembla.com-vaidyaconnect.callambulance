package com.patientz.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.ResponseVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor>, TextureView.SurfaceTextureListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_READ_CONTACTS = 0;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    private static final String TAG = "LoginActivity";
    private String PARAM_PASSWORD = "password";
    // UI references.
    private TextView loadingTextView;
    private AutoCompleteTextView mEmailMobileView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;
    private RequestQueue mRequestQueue;
    private String PARAM_USERNAME = "username";
    private String PARAM_OLD_PASSWORD = "oldPass";
    private String userName, userPass;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    private GoogleApiClient mGoogleApiClient;
    private String MOBILE_NUMBER = "mobilenumber";
    TextView textchangelanguage, textsetlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        changeLang(defaultSharedPreferences.getString("sel_language", "en"));
        setContentView(R.layout.activity_login);
        Log.d(TAG, "oncreate");
        mPasswordView = (EditText) findViewById(R.id.password);

        mRequestQueue = AppVolley.getRequestQueue();
        // Set up the login form.
        mEmailMobileView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        textchangelanguage = (TextView) findViewById(R.id.textchangelanguage);
        textchangelanguage.setPaintFlags(textchangelanguage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textsetlang = (TextView) findViewById(R.id.textsetlang);

        textsetlang.setText(" ( " + defaultSharedPreferences.getString("sel_languagetext", "English") + " ) ");

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE
                        || id == EditorInfo.IME_NULL
                        || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                    attemptLogin();
                    return true;
                } else {
                    return false;
                }
            }
        });
        final Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        loadingTextView = (TextView) findViewById(R.id.loading_text);

        findViewById(R.id.bt_forgot_pwd).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                            callForgotPasswordScreen();
                        } else {
                            AppUtil.requestPermissions(LoginActivity.this, Manifest.permission.SEND_SMS, Constant.PERMISSIONS_REQUEST_CODE_SMS);
                        }

                    }
                });

        findViewById(R.id.bt_createAccount).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivityForResult(i, 5);
                //finish();
            }
        });

        textchangelanguage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listLanguagesPopup(LoginActivity.this);
            }
        });
    }

    private void callForgotPasswordScreen() {
        Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        i.putExtra(Constant.USER_ID,mEmailMobileView.getText().toString());
        startActivityForResult(i, 5);
    }

    public void listLanguagesPopup(final Context mContext) {

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String[] languageslist = {getString(R.string.english), getString(R.string.hindi), getString(R.string.telugu), getString(R.string.urdu), getString(R.string.kannada), getString(R.string.tamil),getString(R.string.oriya)};
        final String[] languageslistvalue = {"en", "hi", "te", "ur", "kn", "ta","or"};

        final AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
        ad.setTitle(R.string.select_language);
        ad.setSingleChoiceItems(languageslist, defaultSharedPreferences.getInt("sel_position", 0), new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                defaultSharedPreferences.edit().putString("sel_language", languageslistvalue[arg1]).commit();
                defaultSharedPreferences.edit().putString("sel_languagetext", languageslist[arg1]).commit();
                defaultSharedPreferences.edit().putInt("sel_position", arg1).commit();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        ad.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

    private void populateAutoComplete() {
/*        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
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
        mEmailMobileView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailMobileView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateAutoComplete();
                }
                break;
            case Constant.PERMISSIONS_REQUEST_CODE_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   callForgotPasswordScreen();
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                        Manifest.permission.SEND_SMS)) {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_sms_grant_msg), new String[]{Manifest.permission.SEND_SMS}, Constant.PERMISSIONS_REQUEST_CODE_SMS);
                } else {

                }
                break;
        }
    }
    private void showDialogWithMoreSpecificPermissionInfo(String message, final String[] permissions, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("");
        builder.setPositiveButton(R.string.give_permission, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(
                        LoginActivity.this, permissions,
                        permissionRequestCode);
            }
        });
        builder.setNegativeButton(R.string.lable_later, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callForgotPasswordScreen();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

        /**
         * Attempts to sign in or register the account specified by the login form.
         * If there are form errors (invalid email, missing fields, etc.), the
         * errors are presented and no actual login attempt is made.
         */

    private void attemptLogin() {
        mEmailMobileView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        userName = mEmailMobileView.getText().toString();
        userPass = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(userPass) && !isPasswordValid(userPass)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userPass)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(userName)) {
            mEmailMobileView.setError(getString(R.string.error_field_required));
            focusView = mEmailMobileView;
            cancel = true;
        }

        if (AppUtil.isValidMobileNumber(userName)) {
            userName = "+91-" + userName;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            if(AppUtil.isOnline(getApplicationContext()))
            {
                showProgress(true);
                mRequestQueue.add(createLoginAPIRequest(userName, userPass));
            }else
            {
                AppUtil.showToast(getApplicationContext(),getString(R.string.network_error));
            }
        }

    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailMobileView.setAdapter(adapter);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Surface s = new Surface(surface);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.emergencies_640_480_2);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(getApplicationContext(), video);
            mMediaPlayer.setSurface(s);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {

                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                }
            });
            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                }
            });
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("LoginActivity", "Google connection " + connectionResult.getErrorMessage());
    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private JsonObjectRequest createLoginAPIRequest(final String userId, final String userPass) {

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

                                    if (responseObject.getString("roles").contains(Constant.ROLE) || responseObject.getString("roles").contains(Constant.NEW_USER_ROLE)) {
                                        storeCredentialsInSP(responseObject);
                                        mRequestQueue.add(createSessionVerifyRequest(LoginActivity.this));
                                        Log.d("Response 1 : ", responseObject.get("roles").toString());
                                        Answers.getInstance().logLogin(new LoginEvent()
                                                .putMethod("Email/Mobile")
                                                .putCustomAttribute("issue", "no issue")
                                                .putSuccess(true));
                                    } else {
                                        AppUtil.showToast(getApplicationContext(),
                                                "You are not authorized to access this application.");
                                        showProgress(false);
                                        Answers.getInstance().logLogin(new LoginEvent()
                                                .putMethod("Email/Mobile")
                                                .putCustomAttribute("issue", "access denied")
                                                .putSuccess(false));
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

                if (networkResponse != null) {
                    statuscode = networkResponse.statusCode;
                }
                if(statuscode==503 ||volleyError instanceof TimeoutError)
                {
                    AppUtil.showErrorCodeDialog(LoginActivity.this);
                }
                else {
                    if (statuscode == Constant.CREDENTIALS_INCORRECT) {
                        CommonUtils.getSP(LoginActivity.this).edit().putString(Constant.USER_ID, mEmailMobileView.getText().toString()).apply();
                        startActivity(new Intent(LoginActivity.this, ChangePassWordActivity.class).putExtra("old_password", mPasswordView.getText().toString().trim()));
                        Answers.getInstance().logLogin(new LoginEvent()
                                .putMethod("Email/Mobile")
                                .putCustomAttribute("issue", "wrong credentials")
                                .putSuccess(false));
                    } else {
                        Log.i("error3", "error3");
                        AppUtil.showErrorDialog(getApplicationContext(),volleyError);
                    }
                }
            }
        });
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000,2,1));
        return mRequest;
    }


    private void storeCredentialsInSP(JSONObject responseObject) {
        try {
            CommonUtils.getSP(this).edit().putString(Constant.USER_ID, mEmailMobileView.getText().toString())
                    .putString(Constant.TOKEN_TYPE, (String) responseObject.get(Constant.TOKEN_TYPE))
                    .putString(Constant.ACCESS_TOKEN, (String) responseObject.get(Constant.ACCESS_TOKEN))
                    .putBoolean(Constant.IS_USER_LOGGED_IN, true)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    responseHandler(responseVO);
                } else {
                    AppUtil.showToast(context, getString(R.string.server_error));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                Log.i("error1 - ", "" + Build.VERSION.SDK_INT);
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(LoginActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),volleyError);

                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(context, super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000,2,1));
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {
        Log.d(TAG, "Inside responseHandler");
        if (mResponseVO != null) {
            int code = (int) mResponseVO.getCode();
            showProgress(false);
            switch (code) {
                case Constant.RESPONSE_SUCCESS:

                    AppUtil.sendCampaignDetails(getApplicationContext(), Constant.EVENT_LOGIN);

                    saveUserDetails(mResponseVO);
                    saveDestHospRecord(mResponseVO);
                    Intent intent = new Intent(LoginActivity.this, ActivityTermsAndConditions.class);
                    startActivity(intent);
                    //finish();
                    //   new SaveDataOnBackground(mResponseVO).execute();
                    HashMap<String, Object> bkDATA = new HashMap<>();
                    bkDATA.put("status", "Success");
                    UpshotEvents.createCustomEvent(bkDATA, 3);
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
                case Constant.RESPONSE_CODE_PASSWORD_EXPIRE:
                    //AppUtil.showDialog(this,mResponseVO.getResponse());
                    Intent intent2 = new Intent(this, UpdatePassWordActivity.class);
                    intent2.putExtra(PARAM_USERNAME, userName);
                    intent2.putExtra(PARAM_OLD_PASSWORD, userPass);
                    startActivityForResult(intent2, Constant.RESPONSE_CODE_PASSWORD_EXPIRE);
                    break;
                case Constant.RESPONSE_CODE_INCOMPLETE_REGISTRATION:
                    // saveUserDetails(mResponseVO);
                    Intent intent3 = new Intent(this, ProfileRegistrationActivity.class);
                    intent3.putExtra(PARAM_USERNAME, userName);
                    intent3.putExtra(PARAM_PASSWORD, userPass);
                    intent3.putExtra(MOBILE_NUMBER, mEmailMobileView.getText().toString());
                    startActivity(intent3);
                    HashMap<String, Object> bkDATA2 = new HashMap<>();
                    bkDATA2.put("status", "Fail");
                    UpshotEvents.createCustomEvent(bkDATA2, 3);
                    break;
                default:
                    AppUtil.showToast(getApplicationContext(),
                            mResponseVO.getResponse());
                    break;
            }
        } else {
            AppUtil.showToast(LoginActivity.this, getString(R.string.server_error));
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "requestCode = " + requestCode);
        if (requestCode == 5 && data != null) {


            String userNameis = data.getStringExtra("userName");
            Log.d(TAG, "userNameis = " + userNameis);
            mEmailMobileView.setText(userNameis);

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

    private class SaveDataOnBackground extends AsyncTask<String, String, String> {

        ResponseVO responseVO;

        private SaveDataOnBackground(ResponseVO responseVO) {
            this.responseVO = responseVO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            saveUserDetails(responseVO);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            showProgress(false);
            Intent intent = new Intent(LoginActivity.this,
                    SyncDataActivity.class);
            startActivity(intent);
            finish();
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPasswordView.setText("");
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
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


}

