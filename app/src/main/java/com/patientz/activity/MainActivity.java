package com.patientz.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.EmergencyInfoVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.VO.UserVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.fragments.FragmentDashBoard;
import com.patientz.fragments.FragmentDialogAlertWithTwoButtons;
import com.patientz.gcm.GcmRegistrationIntentService;
import com.patientz.service.SaveUserLogIntentService;
import com.patientz.services.BloodyFriendsSyncService;
import com.patientz.services.CallAmbulanceSyncService;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.services.StickyNotificationInsuranceFGService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CircleTransform;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.ImageUtil;
import com.patientz.utils.Log;
import com.patientz.utils.MultipartRequest;
import com.patientz.utils.SortPatientUserVO;
import com.patientz.webservices.WebServiceUrls;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentDashBoard.OnFragmentInteractionListener, FragmentDialogAlertWithTwoButtons.ButtonsClickCallBack, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    TextView tvLeftMenuUserName, tvSharePoints;
    SharedPreferences defaultSharedPreferences;
    private ImageView ivProfilePic;
    //private Tracker mTracker;
    private File storedImageFile;
    private String mCurrentPhotoPath;
    private ProgressBar progressBar;
    private PatientUserVO mPatientUserVO;
    private long currentSelectedUserId;
    private NavigationView navigationView;
    private Uri outputFileUri;
    private Dialog progressDialog;
    private Snackbar mSnackbar;
    private FrameLayout frameLayout;
    private static final int PICK_IMAGE_ID = 234; // the number doesn't matter
    private ProgressDialog logoutProgressDialog;

    private BroadcastReceiver mFitStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //mResultReceiver = new AddressResultReceiver(new Handler());
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        changeLang(defaultSharedPreferences.getString("sel_language", "en"));
        setContentView(R.layout.activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        if (Build.VERSION.SDK_INT >= 11) {
            VersionHelper.refreshActionBarMenu(this);
        }

        Log.d(TAG, "*************** OnCreate ***************");
        currentSelectedUserId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        Log.d(TAG, "CURRENT SELECTED PROFILE=" + currentSelectedUserId);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ivProfilePic = (ImageView) headerView.findViewById(R.id.iv_leftmenu_user_icon);
        ivProfilePic.setOnClickListener(this);
        progressBar = (ProgressBar) headerView.findViewById(R.id.progress_bar);
        tvLeftMenuUserName = (TextView) headerView.findViewById(R.id.tv_leftmenu_user_name);
        tvSharePoints = (TextView) headerView.findViewById(R.id.tv_share_points);


        if (AppUtil.checkLoginDetails(this)) {
            if (checkSyncTimeout()) {
                Log.d(TAG, "Sync started");
                Intent iService = new Intent(this, CallAmbulanceSyncService.class);
                startService(iService);
                Intent mIntent = new Intent(MainActivity.this, SaveUserLogIntentService.class);
                startService(mIntent);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_grant_storage_permission), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSIONS_REQUEST_CODE_STORAGE);
            }
            DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
            try {
                mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedUserId);//mDatabaseHandler.getLoggedInUserSlidingScreenDetails();
                setNavMenuItemsVisbility();
            } catch (Exception e) {
                //Log.d(TAG, e.getMessage());
            }
            if (mPatientUserVO != null && !mPatientUserVO.equals("")) {
                setProfileImage(mPatientUserVO);
            }
            if (mDatabaseHandler.getPhoneContactsInfo().size() > 0) {
                startService(new Intent(MainActivity.this, BloodyFriendsSyncService.class));
            }
            FragmentDashBoard mFragmentDashBoard = FragmentDashBoard.newInstance();
            switchFragmentWithoutAddingToBackStack(mFragmentDashBoard);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.title_activity_main);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    Log.d(TAG, "onDrawerClosed");

                }

                public void onDrawerOpened(View drawerView) {
                    //set user profile image from locally if exists or download and display
                    Log.d(TAG, "onDrawerOpened");
                    DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                    try {
                        PatientUserVO patientUserVO = mDatabaseHandler.getProfile(currentSelectedUserId);//mDatabaseHandler.getLoggedInUserSlidingScreenDetails();
                        setLeftMenuUserName(tvLeftMenuUserName, patientUserVO);
                        setSharePoints(tvSharePoints, patientUserVO);


                    } catch (Exception e) {
                    }

                    super.onDrawerOpened(drawerView);
                }
            };
            drawer.setDrawerListener(toggle);

            toggle.syncState();
            Intent gcmRegService = new Intent(this, GcmRegistrationIntentService.class);
            startService(gcmRegService);

            //TODO Call service to get the users current location
            // This code starts the service to get the current address and display in this screen
            // startIntentServiceFetchAddress();
            // This code starts the service to get the current address and display in this screen

        /*Initiating Google Analytics*/

        /*Sending Initial Screen*/

        /*Initiating Facebook Analytics*/
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);

        /*Upshot user initialization*/

            AppUtil.updateUserInfoUpshot(getApplicationContext());
            Log.d(TAG, "EMERGENCY NOS LIST COUNT=" + mDatabaseHandler.getEmergencyNosCount());
            if (mDatabaseHandler.getEmergencyNosCount() == 0) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            readEmergencyNumbersFromCSVFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        } else {
            Log.d(TAG, "Not logged in");
            Intent startIntent = new Intent(MainActivity.this, StickyNotificationForeGroundService.class);
            startIntent.setAction(Constant.ACTION.COMPLETE_REGISTRATION);
            startService(startIntent);
            AppUtil.callLoginScreen(getApplicationContext());
            finish();
        }
        AppCenter.start(getApplication(), "cb30f37a-2fff-456b-a415-a03e0bcbeb13",
                Analytics.class, Crashes.class);
    }

    private void setSharePoints(TextView tvSharePoints, PatientUserVO mPatientUserVO) {
        if (mPatientUserVO.getSharePoints() != 0) {
            tvSharePoints.setVisibility(View.VISIBLE);
            tvSharePoints.setText(mPatientUserVO.getSharePoints() + " Points");
        } else {
            tvSharePoints.setVisibility(View.INVISIBLE);
        }
    }


    private void readEmergencyNumbersFromCSVFile() throws IOException {
        InputStream inputStream = getAssets().open("Emergency_numbers.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
        try {
            mDatabaseHandler.insertEmergencyNumbers(reader);
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        Log.d("emergency numbers count=", mDatabaseHandler.getEmergencyNosCount() + "");

    }

    private boolean checkSyncTimeout() {
        Log.e("sync--->0", "success");
        int timeout = 30 * 60 * 1000;
        long syncTimeOut = defaultSharedPreferences.getLong("sync_timeout", 0);
        Log.d(TAG, "syncTimeOut=" + syncTimeOut);
        if (syncTimeOut == 0 || ((System.currentTimeMillis() - syncTimeOut)) > timeout) {
            Log.d(TAG, "syncTimeOut=" + syncTimeOut);
            return true;
        }
        return false;
    }

    private void setNavMenuItemsVisbility() {
        Menu navMenu = navigationView.getMenu();
        Log.d(TAG, "Role=" + mPatientUserVO.getRole());
        if (mPatientUserVO.getRole().equalsIgnoreCase(Constant.EMERGENCY_CONTACT)) {
            navMenu.findItem(R.id.nav_care_team).setVisible(false);
            //navMenu.findItem(R.id.nav_invitation).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_care_team).setVisible(true);
            //navMenu.findItem(R.id.nav_invitation).setVisible(true);
        }
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


    public void listLanguagesPopup(final Context mContext) {

        final String[] languageslist = {getString(R.string.english), getString(R.string.hindi), getString(R.string.telugu), getString(R.string.urdu), getString(R.string.kannada), getString(R.string.tamil), getString(R.string.oriya)};
        final String[] languageslistvalue = {"en", "hi", "te", "ur", "kn", "ta", "or"};


        final AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
        ad.setTitle(R.string.select_language);
        ad.setSingleChoiceItems(languageslist, defaultSharedPreferences.getInt("sel_position", 0), new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.DASHBOARD_LANGUAGE_CHANGE, languageslistvalue[arg1]);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LANGUAGE_CHANGED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                defaultSharedPreferences.edit().putString("sel_language", languageslistvalue[arg1]).commit();
                defaultSharedPreferences.edit().putInt("sel_position", arg1).commit();
                //finishAffinity();
                startActivity(new Intent(mContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        ad.show();
    }

    private void setLeftMenuUserName(TextView tvLeftMenuUserName, PatientUserVO mPatientUserVO) {
        tvLeftMenuUserName.setText(mPatientUserVO.getFirstName() + " " + mPatientUserVO.getLastName());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, getSupportFragmentManager().getBackStackEntryCount() + "");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem menu_item_share = menu.findItem(R.id.menu_item_share);
        MenuItem menu_item_info = menu.findItem(R.id.menu_item_info);
        MenuItem menuItemSettings = menu.findItem(R.id.action_settings);
        Drawable drawable = menu_item_share.getIcon();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            //drawable.setAlpha(1);
        }
        Drawable drawable1 = menu_item_info.getIcon();
        if (drawable1 != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable1.mutate();
            drawable1.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            //drawable.setAlpha(1);
        }

        menuItemSettings.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        HashMap<String, Object> upshotData = new HashMap<>();

        switch (id) {
            case R.id.action_refresh:
               /* mSnackbar = Snackbar.make(frameLayout, "Sync is in progress", Snackbar.LENGTH_LONG);
                mSnackbar.show();*/
                defaultSharedPreferences.edit().remove("sync_timeout").commit();
                /////sync//////
                currentSelectedUserId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
                Log.d(TAG, "CURRENT SELECTED PROFILE=" + currentSelectedUserId);

                if (AppUtil.checkLoginDetails(this)) {
                    DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                    try {
                        mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedUserId);//mDatabaseHandler.getLoggedInUserSlidingScreenDetails();
                        setNavMenuItemsVisbility();
                    } catch (Exception e) {
                        //Log.d(TAG, e.getMessage());
                    }
                    if (mPatientUserVO != null && !mPatientUserVO.equals("")) {
                        setProfileImage(mPatientUserVO);
                        setLeftMenuUserName(tvLeftMenuUserName, mPatientUserVO);
                    }

                    // if (checkSyncTimeout()) {
                    Log.e("sync--->2", "success");
                    Intent iService = new Intent(this, CallAmbulanceSyncService.class);
                    startService(iService);
                    // }


                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                        public void onDrawerClosed(View view) {
                            super.onDrawerClosed(view);
                        }

                        /** Called when a drawer has settled in a completely open state. */
                        public void onDrawerOpened(View drawerView) {
                            //set user profile image from locally if exists or download and display
                            //setProfileImage(mPatientUserVO);
                            super.onDrawerOpened(drawerView);
                        }
                    };
                    drawer.setDrawerListener(toggle);
                    toggle.syncState();

                } else {
                    AppUtil.callLoginScreen(getApplicationContext());
                    finish();
                }

        /*Initiating Facebook Analytics*/
                FacebookSdk.sdkInitialize(getApplicationContext());
                AppEventsLogger.activateApp(this);


                break;
            case R.id.action_changelanguage:
                listLanguagesPopup(MainActivity.this);
                break;
            case R.id.action_add_member:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_ADD_MEMBER_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_ADD_MEMBER_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                Intent mIntent = new Intent(MainActivity.this, ActivityAddMember.class);
                startActivity(mIntent);
                break;
            case R.id.action_switch_member:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_SWITCH_MEMBER_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_SWITCH_MEMBER_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                showPatientList();

                break;

            case R.id.action_settings:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_SETTINGS_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_SETTINGS_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                Intent intentSettings = new Intent(MainActivity.this, SettingActivity2.class);
                startActivity(intentSettings);

                break;
            case R.id.action_change_pwd:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_CHANGE_PWD_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_CHANGE_PWD_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                startActivity(new Intent(this, ChangePassWordActivity.class).putExtra("old_password", ""));
                break;
            case R.id.action_logout:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_LOGOUT_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_LOGOUT_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(getString(R.string.dialog_exit_app_title_msg))
                        .setPositiveButton(getString(R.string.label_exit),
                                new DialogInterface.OnClickListener() {
                                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                                        mRequestQueue.add(getLogoutStringRequest());
                                    }
                                })
                        .setNegativeButton(getString(R.string.label_cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                break;
            case R.id.menu_item_share:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_SHARE_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_SHARE_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                setShareIntent();
                break;
            case R.id.menu_item_info:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_INFO_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_INFO_CLICKED);
                Intent mIntentinfo = new Intent(MainActivity.this, InfoSliderActivity.class);
                startActivity(mIntentinfo);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private StringRequest getLogoutStringRequest() {
        logoutProgressDialog = CommonUtils.showProgressDialog(MainActivity.this);
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.LOGOUT_API_URL;
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                logoutProgressDialog.dismiss();
                if (response != null) {
                    AppUtil.deleteUserDetails(getApplicationContext());
                    HashMap<String, Object> upshotData = new HashMap<>();
                    upshotData.put(Constant.UpshotEvents.HAS_LOGGED_OUT, true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.HAS_LOGGED_OUT);
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                logoutProgressDialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                            builder.setCancelable(false);
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.fragment_server_busy_dialog, null);
                            builder.setView(view);
                            TextView mButton = (TextView) view.findViewById(R.id.bt_done);
                            final android.support.v7.app.AlertDialog mAlertDialog = builder.create();
                            mButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlertDialog.dismiss();
                                }
                            });
                            mAlertDialog.show();
                            break;
                    }
                } else {
                    AppUtil.showErrorDialog(getApplicationContext(), error);
                }

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return getParamsToPass();
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

    @SuppressLint("MissingPermission")
    private Map<String, String> getParamsToPass() {
        HashMap<String, String> params = new HashMap<String, String>();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        params.put(Constant.APP_PACKAGE_NAME, getPackageName());
        params.put(Constant.imei, telephonyManager.getDeviceId());
        Log.d(TAG, "LOGOUT_API_URL_PARAMS=" + params);
        return params;
    }

    private void setShareIntent(Intent shareIntent) {

    }

    public void showPatientList() {
        try {
            long currentSelectedUserId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
            if (currentSelectedUserId == 0) {
                currentSelectedUserId = -1;
            }
            TextView mTextView = new TextView(this);
            ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mTextView.setLayoutParams(lparams);
            mTextView.setGravity(Gravity.CENTER);
            mTextView.setPadding(16, 16, 16, 16);
            mTextView.setTextSize(22);
            mTextView.setText(getString(R.string.selmember));
            mTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
            DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
            final ArrayList<PatientUserVO> uList = mDatabaseHandler.getAllUser();
            SortPatientUserVO mSortPatientUserVO = new SortPatientUserVO();
            Collections.sort(uList, mSortPatientUserVO);
            CharSequence[] names = new String[uList.size()];
            if (uList.size() != 1) {
                for (int i = 0; i < names.length; i++) {
                    Spanned name = Html.fromHtml(AppUtil.convertToCamelCase(uList
                            .get(i).getFirstName())
                            + " "
                            + AppUtil.convertToCamelCase(uList.get(i).getLastName())
                            + "<br></br>"
                            + uList.get(i).getRole()
                            + ", "
                            + uList.get(i).getRelationship());
                    names[i] = name.toString();
                    if (currentSelectedUserId == uList.get(i).getPatientId()) {
                        currentSelectedUserId = i;
                    }
                }
                new android.app.AlertDialog.Builder(this)
                        .setCustomTitle(mTextView)
                        .setSingleChoiceItems(names, (int) currentSelectedUserId,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    /*new LoadProfileData(uList.get(whichButton))
                                            .execute();*/
                                        defaultSharedPreferences.edit().putLong("current_selected_user", uList.get(whichButton).getPatientId()).commit();
                                        defaultSharedPreferences.edit().remove("sync_timeout").commit();
                                        //clearInsuranceStickyNotification();

                                        //defaultSharedPreferences.edit().putBoolean("logoStatus", true).commit();                                        finish();
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        dialog.cancel();
                                    }
                                }).create().show();
            } else {
                AppUtil.showToast(getApplicationContext(), getString(R.string.label_add_member));
            }
        } catch (Exception exc) {

        }
    }

    private void clearInsuranceStickyNotification() {
        if (AppUtil.isMyServiceRunning(getApplicationContext(), StickyNotificationInsuranceFGService.class)) {
            Log.d("stopping service", "stopping service");
            Intent startIntent = new Intent(MainActivity.this, StickyNotificationInsuranceFGService.class);
            startIntent.setAction(Constant.ACTION.STOP);
            startService(startIntent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Log.d(TAG, "onNavigationItemSelected");
        Log.d(TAG, getSupportFragmentManager().getBackStackEntryCount() + "");
        int id = item.getItemId();

        if (id == R.id.nav_emergency) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_EMERGENCY, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_EMERGENCY);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
        } else if (id == R.id.nav_profile) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_PROFILE, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_PROFILE);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            defaultSharedPreferences.edit().remove("verifyDialogActionTaken").commit();
            startActivity(profileIntent);
            // UpshotManager.getTutorial(this,"Welcome");
        } else if (id == R.id.nav_care_team) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_CARE_TEAM, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_CARE_TEAM);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                Intent contactsIntent = new Intent(this, ContactsActivity.class);
                startActivity(contactsIntent);
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.GET_ACCOUNTS},
                        Constant.PERMISSIONS_REQUEST_CODE_CONTACTS_CARE_TEAM);
            }
        } else if (id == R.id.nav_nearby_hospitals) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_NEARBY, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_NEARBY);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Intent intentOrgType = new Intent(this, NearbyOrgsActivity.class);
                startActivity(intentOrgType);
            } else {
                ActivityCompat.requestPermissions(
                        MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }


        } else if (id == R.id.nav_nearby_ambulance_providers) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_NEARBY_AMBULANCES, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_NEARBY_AMBULANCES);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "LOCATION PERMISSION GRANTED");
                if (AppUtil.isOnline(getApplicationContext())) {
                    RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                    mRequestQueue.add(getCurrentAmbulanceRequest());
                } else {
                    Intent ambulanceIntent = new Intent(MainActivity.this, NearbyAmbulancesListActivityNew.class);
                    startActivity(ambulanceIntent);
                }
            } else {
                Log.d(TAG, "LOCATION PERMISSION NOT GRANTED");
                ActivityCompat.requestPermissions(
                        MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        11);
            }
        } else if (id == R.id.nav_become_first_responder) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_FIRST_RESPONDER, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_FIRST_RESPONDER);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            Intent aboutActivity = new Intent(this, FirstResponderHelpWebViewActivity.class);
            startActivity(aboutActivity);
        } else if (id == R.id.nav_bb) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_BLOODY_FRIENDS, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_BLOODY_FRIENDS);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
               /* Intent aboutActivity = new Intent(this, BloodyFriendsLandingActivity.class);
                startActivity(aboutActivity);*/
                Intent aboutActivity = new Intent(this, BloodyFriendsActivity.class);
                startActivity(aboutActivity);
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.GET_ACCOUNTS},
                        Constant.PERMISSIONS_REQUEST_CODE_CONTACTS_BLOODY_FRIENDS);
            }

        } else if (id == R.id.nav_rep_hazard) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_REPORT_HAZARDS, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_REPORT_HAZARDS);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            Intent aboutActivity = new Intent(this, ReportRoadHazardActivity.class);
            startActivity(aboutActivity);

        } else if (id == R.id.nav_vedioes) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_VIDEOS, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_VIDEOS);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            Intent aboutActivity = new Intent(this, ActivityWebViewVedioes.class);
            startActivity(aboutActivity);
        } else if (id == R.id.nav_share) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_SHARE, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_SHARE);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            setShareIntent();
        } else if (id == R.id.nav_about) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_ABOUT, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_ABOUT);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            Intent aboutActivity = new Intent(this, AboutActivity.class);
            startActivity(aboutActivity);
        } else if (id == R.id.nav_uploadreports) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.LN_MY_REPORTS, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.LN_MY_REPORTS);
            Log.d(TAG, "upshot data=" + upshotData.entrySet());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent uploadActivity = new Intent(this, MyUploadReportsActivity.class);
                startActivity(uploadActivity);
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.PERMISSIONS_REQUEST_CODE_STORAGE_REPORTS);
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private StringRequest getCurrentAmbulanceRequest() {
        progressDialog = CommonUtils.showProgressDialogWithCustomMsg(MainActivity.this, getString(R.string.get_current_amb_request));
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.getCurrentAmbulanceRequest;
        Log.d("Webservice:getCurrentAmbulanceRequest:url ", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissProgressDialog();
                Log.d(TAG, "Webservice:getCurrentAmbulanceRequest:response" + response);
                if (!TextUtils.isEmpty(response)) {
                    try {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<EmergencyInfoVO>() {
                        }.getType();
                        EmergencyInfoVO mEmergencyInfoVO = gson.fromJson(
                                response, objectType);
                        Intent mIntent = new Intent(MainActivity.this, CurrentAmbulanceRequestActivity.class);
                        mIntent.putExtra(Constant.KEY_SERIALIZED_EIVO, (Serializable) mEmergencyInfoVO);
                        startActivity(mIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "Calling NearbyAmbulancesListActivityNew");
                    Intent ambulanceIntent = new Intent(MainActivity.this, NearbyAmbulancesListActivityNew.class);
                    startActivity(ambulanceIntent);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(40000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        return mRequest;
    }

    public void dismissProgressDialog() {

        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public void setShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        UserVO user = AppUtil.getLoggedUser(getApplicationContext());
        String url = getString(R.string.send_invite_new);
        //String url = getString(R.string.send_invite);
      /*  if (user != null) {
            if (user.getUserId() != 0) {
                url = url + "  \n" + generateDynamicLink(user.getUserId());//url + "?referralCode=" +user.getUserId();
            } else {
                url = url + "\n  http://bit.ly/callambulance";
            }
        } else {
            url = url + "\n  http://bit.ly/callambulance";
        }*/
        final String STORAGE_PATH = getApplicationContext()
                .getResources().getString(
                        R.string.profileImagePath);
        File cnxDir = new File(Environment
                .getExternalStorageDirectory(), STORAGE_PATH);
        if (!cnxDir.exists()) {
            cnxDir.mkdirs();
        }
        File file = new File(cnxDir, "ca_share.jpg");
        if (!AppUtil.checkIfFileExists(getApplicationContext(), "ca_share.jpg")) {
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ca_share);
            try {
                OutputStream outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        outStream);
                outStream.flush();
                outStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("image/*");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sendIntent, "Share using"));
        HashMap<String, Object> upshotData = new HashMap<>();
        upshotData.put(Constant.UpshotEvents.SHARE_CLICKED, true);
        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.SHARE_CLICKED);
        Log.d(TAG, "upshot data=" + upshotData.entrySet());
    }

    private String generateDynamicLink(long referralID) {
        String link = "https://s8geh.app.goo.gl/?link=https://callambulance.in?" +
                "referralCode=" + referralID
                + "&utm_campaign=invite" + "&utm_source=ca_app" + "&utm_medium=in_app"
                + "&apn=com.patientz.activity" + "&referralCode=" + referralID;
        SharedPreferences sharedPreferences = getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        String deepLink = sharedPreferences.getString(Constant.DYNAMIC_LINK_2, "");
        Log.d(TAG, "DYNAMIC_LINK_2=" + deepLink);
        if (TextUtils.isEmpty(deepLink)) {
            return link;
        } else {
            return deepLink;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void dismissSyncSnackBar() {

    }

    public void switchFragmentAddingToBackStack(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    public void switchFragmentWithoutAddingToBackStack(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, Constant.FRAGMENT_DASHBOARD_TAG_NAME);
        ft.commit();
    }

    @Override
    public void button1Clicked() {
        // Alert dialog button1
        defaultSharedPreferences.edit().putBoolean("setLater", true).commit();

    }

    @Override
    public void button2Clicked() {
        // Alert dialog button2
        Intent mIntent = new Intent(this, ActivityEHRUpdate.class);
        mIntent.putExtra("key", "edit");
        startActivity(mIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_leftmenu_user_icon:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "GRANTED");
                    onPickImage();
                } else {
                    Log.d(TAG, "NOT GRANTED");
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constant.PERMISSIONS_REQUEST_CODE_CAMERA);
                }
                break;
        }
    }

    public void onPickImage() {
        Intent chooseImageIntent = getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    public Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getUri());
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);
        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Choose Image From");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }
        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.d(TAG, "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return list;
    }

    private Uri getUri() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = null;
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                if (photoFile != null)
                    uri = Uri.fromFile(photoFile);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
        }
        return uri;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, Constant.REQUEST_CODE_CAMERA_INTENT);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        final String STORAGE_PATH = getApplicationContext()
                .getResources().getString(
                        R.string.profileImagePath);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        File storageDir = new File(Environment
                .getExternalStorageDirectory(), STORAGE_PATH);
        Log.d(TAG, "storageDir=" + storageDir);
        storedImageFile = File.createTempFile(
                imageFileName,   //prefix
                ".jpg",          //suffix
                storageDir       //directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = storedImageFile.getAbsolutePath();
        return storedImageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult" + "requestcode=" + requestCode + " ,resultCode=" + resultCode + " ,data=" + data);

        switch (requestCode) {
            case Constant.REQUEST_CHECK_SETTINGS:
                FragmentDashBoard fragment = (FragmentDashBoard) getSupportFragmentManager()
                        .findFragmentById(R.id.content_frame);
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d(TAG, "REQUEST_CHECK_SETTINGS");
                break;
            default:
                if (resultCode == RESULT_OK) {
                    if (resultCode != RESULT_OK)
                        return;
                    Uri mImageCaptureUri = null;
                    if (requestCode == PICK_IMAGE_ID) {
                        if (data != null && data.getData() != null) {
                            mImageCaptureUri = data == null ? null : data.getData();
                            mCurrentPhotoPath = getRealPathFromURI(mImageCaptureUri); // from Gallery
                        } else {
                            mImageCaptureUri = Uri.fromFile(storedImageFile);
                            mCurrentPhotoPath = mImageCaptureUri.getPath();
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                        mRequestQueue.add(createFileRequest(mCurrentPhotoPath));
                    }
                    break;
                }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null,
                null, null);

        if (cursor == null)
            return null;

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private MultipartRequest createFileRequest(String mCurrentPhotoPathh) {

        boolean isExternalStorageWriteable = ImageUtil.isExternalStorageWriteable();
        // String filePath = "";
        //File[] mFiles;
        MultipartRequest multipartRequest = null;
        String szServerUrl =
                WebServiceUrls.serverUrl + WebServiceUrls.insertPatientProfilePicture;

        if (isExternalStorageWriteable) {

            multipartRequest = new MultipartRequest(getApplicationContext(), mPatientUserVO.getPatientId(), mCurrentPhotoPathh,
                    szServerUrl, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    com.patientz.utils.Log.d(TAG, "RESPONSE \n" + response);
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
                    progressBar.setVisibility(View.INVISIBLE);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        switch (networkResponse.statusCode) {
                            case Constant.HTTP_CODE_SERVER_BUSY:
                                AppUtil.showErrorCodeDialog(MainActivity.this);
                                break;
                        }
                    } else {
                        AppUtil.showErrorDialog(getApplicationContext(), error);

                    }
                }
            });
        } else {
            Log.d(TAG, "sd card not writeable");
            progressBar.setVisibility(View.INVISIBLE);
        }
        return multipartRequest;
    }

    private void responseHandler(ResponseVO mResponseVO) {
        progressBar.setVisibility(View.INVISIBLE);
        if (mResponseVO != null) {

            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_SUCCESS:
                    AppUtil.showToast(getApplicationContext(), "Profile Pic Uploaded");
                    DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                    try {
                        mDatabaseHandler.insertUsers(mResponseVO.getPatientUserVO());
                        mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedUserId);
                        setProfileImage(mPatientUserVO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    AppUtil.showToast(getApplicationContext(), mResponseVO.getResponse());
                    break;
            }
        }
    }

    private void setProfileImage(PatientUserVO mPatientUserVO) {
        boolean isFileExists = false;
        if (mPatientUserVO != null) {
            isFileExists = AppUtil.checkIfFileExists(getApplicationContext(),
                    mPatientUserVO.getPicName());
            Log.d(TAG, "File Exists ???" + isFileExists);
            if (!isFileExists) {
                if (mPatientUserVO.getPicId() > 0 && AppUtil.isOnline(getApplicationContext())) {
                    // call webservice to download the image if exists
                    String imageUrl = WebServiceUrls.getPatientAttachment + mPatientUserVO.getPicId() + "&patientId=" + mPatientUserVO.getPatientId() + "&moduleType=" + Constant.IMAGE_MODULE_TYPE_PROFILE_PIC;
                    Log.d(TAG, "PROFILE_IMAGE_URL=" + imageUrl);
                    AppUtil.downloadImage(MainActivity.this, imageUrl, ivProfilePic, mPatientUserVO.getPicName());
                } else {
                    Picasso.with(this).load(R.drawable.emergency_default_profile).transform(new CircleTransform()).into(ivProfilePic);
                }
            } else {
                String localPath = Environment.getExternalStorageDirectory()
                        .getPath() + getResources().getString(R.string.profileImagePath);
                File f = new File(localPath + mPatientUserVO.getPicName());
                Picasso.with(this).load(f).transform(new CircleTransform()).into(ivProfilePic);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google connection " + connectionResult.getErrorMessage());
    }

    private void checkAndUpdateSimNumber() {
        String tag = "MainActivity-setSimInfo";
        DatabaseHandler dh = DatabaseHandler.dbInit(getApplicationContext());
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();
        String phonenumber = tm.getLine1Number();
        String mobileNumberInDB = "";
        String simserialno = tm.getSimSerialNumber();
        Log.d(tag, "deviceid: " + deviceid + "\n phonenumber: " + phonenumber
                + "\n simserialno: " + simserialno);
        ContentValues mContentValues;
        try {
            mContentValues = dh.getDeviceDetails();
            if (mContentValues.get("sim_serial_no") != null) {
                String oldSimSerialNumber = (String) mContentValues
                        .get("sim_serial_no");
                if (!TextUtils.isEmpty(oldSimSerialNumber)) {
                    if (!TextUtils.equals(simserialno, oldSimSerialNumber)) {
                        Log.d(TAG, "SIM SERIAL NUMBER IN DB NOT MATCHING WITH SIM SERIAL NUMBER");
                        askNumber();
                    } else {
                        if (mContentValues.get("mobile_no") != null) {
                            mobileNumberInDB = (String) mContentValues
                                    .get("mobile_no");
                            if (TextUtils.isEmpty(mobileNumberInDB)) {
                                Log.d(TAG, "NUMBER DOES NOT EXISTS IN DB");
                                // askNumber(tm, dh, null);
                                askNumber();
                            } else {
                                Log.d(TAG,
                                        "mobile_no: "
                                                + mContentValues
                                                .get("mobile_no"));
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "sim is empty");
                    askNumber();
                }
            } else {
                Log.d(TAG, "SIM SERIAL NUMBER DOESNT EXISTS IN DB");

                askNumber();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void askNumber() {
        Log.d(TAG, "IN askNumber");
        Intent intent = new Intent(this, RegistrationEMRIActivity.class);
        Bundle mBundle = new Bundle();
        intent.putExtras(mBundle);
        startActivityForResult(intent, Constant.REQUEST_CODE_REGISTRATION_EMRI);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {


            case 10:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intentOrgType = new Intent(this, NearbyOrgsActivity.class);
                    startActivity(intentOrgType);
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_nearby_grant_msg), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
                } else {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                }
                break;
            case 11:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                    mRequestQueue.add(getCurrentAmbulanceRequest());
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_nearby_grant_msg), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
                } else {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                }
                break;

            case Constant.PERMISSIONS_REQUEST_CODE_STORAGE_REPORTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent aboutActivity = new Intent(this, MyUploadReportsActivity.class);
                    startActivity(aboutActivity);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                } else {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_grant_storage_permission), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSIONS_REQUEST_CODE_STORAGE_REPORTS);
                }
                break;


            case Constant.PERMISSIONS_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "PERMISSION GRANTED");

                    //getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                    FragmentDashBoard mFragmentDashBoard = FragmentDashBoard.newInstance();
                    Bundle mBundle = new Bundle();
                    mBundle.putBoolean(Constant.INITIATE_EMERGENCY, true);
                    mFragmentDashBoard.setArguments(mBundle);
                    switchFragmentAddingToBackStack(mFragmentDashBoard);
                    //fragment.startEmergencyAfterPermission(true);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.d(TAG, "PERMISSION NOT GRANTED/NEVER ASK CLICKED");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                } else {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_location_grant_msg), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.PERMISSIONS_REQUEST_CODE_LOCATION);
                }
                break;

            case Constant.PERMISSIONS_REQUEST_CODE_CONTACTS_BLOODY_FRIENDS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "PERMISSION GRANTED");
                    Intent aboutActivity = new Intent(this, BloodyFriendsActivity.class);
                    startActivity(aboutActivity);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.GET_ACCOUNTS)) {
                    Log.d(TAG, "PERMISSION NOT GRANTED/NEVER ASK CLICKED");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                } else {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_contacts_msg), new String[]{Manifest.permission.GET_ACCOUNTS}, Constant.PERMISSIONS_REQUEST_CODE_CONTACTS_BLOODY_FRIENDS);
                }
                break;

            case Constant.PERMISSIONS_REQUEST_CODE_DASHBOARD_BFRIENDS_SYNC:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FragmentDashBoard dFragment = (FragmentDashBoard) getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame);
                    dFragment.startBloodyFriendsSync();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.GET_ACCOUNTS)) {
                    Log.d(TAG, "PERMISSION NOT GRANTED/NEVER ASK CLICKED");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                } else {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_contacts_msg), new String[]{Manifest.permission.GET_ACCOUNTS}, Constant.PERMISSIONS_REQUEST_CODE_DASHBOARD_BFRIENDS_SYNC);
                }
                break;

            case Constant.PERMISSIONS_REQUEST_CODE_CONTACTS_CARE_TEAM:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "PERMISSION GRANTED");
                    Intent aboutActivity = new Intent(this, ContactsActivity.class);
                    startActivity(aboutActivity);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.GET_ACCOUNTS)) {
                    Log.d(TAG, "PERMISSION NOT GRANTED/NEVER ASK CLICKED");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                } else {

                }
                break;
            case Constant.PERMISSIONS_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPickImage();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.d(TAG, "PERMISSION NOT GRANTED/NEVER ASK CLICKED");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                } else {

                }
                break;
            case Constant.PERMISSIONS_REQUEST_CODE_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FragmentDashBoard fragment = (FragmentDashBoard) getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame);
                    //fragment.startEmergency();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.SEND_SMS)) {
                    Log.d(TAG, "PERMISSION NOT GRANTED/NEVER ASK CLICKED");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(MainActivity.this);
                } else {
                    showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_grant_sms_permission), new String[]{Manifest.permission.SEND_SMS}, Constant.PERMISSIONS_REQUEST_CODE_SMS);
                }
                break;

            case Constant.PERMISSIONS_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   /* FragmentDashBoard mFragmentDashBoard = FragmentDashBoard.newInstance();
                    switchFragmentWithoutAddingToBackStack(mFragmentDashBoard);*/
                } else {

                }
                break;


        }
    }

    private void showDialogWithMoreSpecificPermissionInfo(String message, final String[] permissions, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message);
        builder.setTitle("");
        builder.setPositiveButton(R.string.give_permission, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(
                        MainActivity.this, permissions,
                        permissionRequestCode);
            }
        });
        builder.setNegativeButton(R.string.lable_later, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (permissionRequestCode == Constant.PERMISSIONS_REQUEST_CODE_LOCATION) {
                    FragmentDashBoard mFragmentDashBoard = FragmentDashBoard.newInstance();
                    Bundle mBundle = new Bundle();
                    mBundle.putBoolean(Constant.INITIATE_EMERGENCY, true);
                    mFragmentDashBoard.setArguments(mBundle);
                    switchFragmentWithoutAddingToBackStack(mFragmentDashBoard);
                } else if (permissionRequestCode == Constant.PERMISSIONS_REQUEST_CODE_DASHBOARD_BFRIENDS_SYNC) {
                    FragmentDashBoard dFragment = (FragmentDashBoard) getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame);
                    dFragment.askForEmergencyHealthRecordSetup();

                } else {


                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (logoutProgressDialog != null) {
            logoutProgressDialog.dismiss();
        }
    }

    static class VersionHelper {
        static void refreshActionBarMenu(Activity activity) {
            activity.invalidateOptionsMenu();
        }
    }

}
