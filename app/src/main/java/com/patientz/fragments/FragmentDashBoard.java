package com.patientz.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.ResultReceiver;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.activity.ActivityCurrentEmergencies;
import com.patientz.activity.ActivityEHRUpdate;
import com.patientz.activity.ActivityEditProfile;
import com.patientz.activity.ActivitySelectPatientInEmergency;
import com.patientz.activity.BloodyFriendsActivity;
import com.patientz.activity.BuildConfig;
import com.patientz.activity.ContactsActivity;
import com.patientz.activity.ProfileActivity;
import com.patientz.activity.R;
import com.patientz.adapters.DashboardGridAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.service.IntentServiceFetchAddress;
import com.patientz.service.IntentServiceFetchContacts;
import com.patientz.service.SaveUserLogIntentService;
import com.patientz.services.CallAmbulanceSyncService;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.ApplicationLifecycleManager;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.patientz.activity.R.string.location;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDashBoard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDashBoard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDashBoard extends BaseFragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FragmentDialogAlertWithTwoButtons.ButtonsClickCallBack, LocationListener, DashboardGridAdapter.ItemClickListener {
    private static final String TAG = "FragmentDashBoard";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final int REQUEST_CODE_CHECK_LOCATION_ENABLED = 112;
    // Bool to track whether the app is already resolving an error
    private static boolean mResolvingError = false;
    //private AddressResultReceiver mResultReceiver;
    private TextView tvCurrentAddress, text_lastupdateddate, tvEmergencyPreparedness;
    private OnFragmentInteractionListener mListener;
    private ProgressBar progressBar;
    private ImageView ivEmhr, ivInsurance;
    private RelativeLayout s;
    private LinearLayout syncProgressBarLayout;
    private CardView rlCardView2;
    private BroadcastReceiver dbChangesReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private ImageView ivAmbulanceProvider;
    private Location mLastLocation;
    //private Tracker mTracker;
    private LocationRequest mLocationRequest;
    private Button btCurrentEmergencies;
    private FrameLayout frameLayout;
    private ImageView ivEmergencyButton;
    private int syncInProgressStatus;
    private PendingIntent mGeofencePendingIntent;
    FragmentDialogAlertWithTwoButtons mFragmentDialogAlertWithTwoButtons;
    SharedPreferences defaultSharedPreferences;
    DatabaseHandler dh;
    AddressResultReceiver mResultReceiver;
    GoogleApiClient mGoogleApiClient;
    boolean originPermissionGranted;
    private boolean raiseEmergency;
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private ProgressBar progressEmergencyPreparedness;
    private boolean isTestEmergency;
    //ContactsResultReceiver contactResultReceiver;
    private AlertDialog testEmergencyClickDialog;


    public FragmentDashBoard() {
    }

    public static FragmentDashBoard newInstance() {
        FragmentDashBoard fragment = new FragmentDashBoard();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        dh = DatabaseHandler.dbInit(getActivity());
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mGeofencePendingIntent = null;
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mResultReceiver = new AddressResultReceiver(new Handler());
    }


    private void setUserDetails(UserInfoVO mUserInfoVO) {
        if (isAdded()) {
            View view = getView();
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvAge = (TextView) view.findViewById(R.id.tv_age);
            TextView tvGender = (TextView) view.findViewById(R.id.tv_gender);
            tvName.setText(mUserInfoVO.getFirstName() + " " + mUserInfoVO.getLastName());
            tvAge.setText(AppUtil.convertToAge(mUserInfoVO.getDateOfBirth(), getActivity()));
            tvGender.setText((mUserInfoVO.getGender() == 2 ? getString(R.string.female) : getString(R.string.male)) + "  |  " + mUserInfoVO.getPatientHandle());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_raise_emergency, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressEmergencyPreparedness = (ProgressBar) view.findViewById(R.id.progress_bar_emergency_prepardness);
        tvEmergencyPreparedness = (TextView) view.findViewById(R.id.tv_emergency_preparedness);
        NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        frameLayout = (FrameLayout) view.findViewById(R.id.parentLayout);
        text_lastupdateddate = (TextView) view.findViewById(R.id.text_lastupdateddate);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        Log.d(TAG, "Setting adapter");
        btCurrentEmergencies = (Button) view.findViewById(R.id.bt_current_emergencies);
        btCurrentEmergencies.setOnClickListener(this);
        ivEmergencyButton = (ImageView) view.findViewById(R.id.iv_emergency_button);
        rlCardView2 = (CardView) view.findViewById(R.id.card_view2);
        syncProgressBarLayout = (LinearLayout) view.findViewById(R.id.progress_bar);
        tvCurrentAddress = (TextView) view.findViewById(R.id.tv_current_address);
        tvCurrentAddress.setOnClickListener(this);
        ivEmergencyButton.setOnClickListener(this);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Log.d(TAG, "LastKnownLocation=" + mSharedPreferences.getString("address", ""));
        tvCurrentAddress.setText(mSharedPreferences.getString("address", ""));
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        return view;
    }

    private void setGridData() {
        if (isAdded()) {
            Integer[] images = {R.drawable.dashboard_contacts, R.drawable.dashboard_bloodgroup, R.drawable.dashboard_health_record, R.drawable.dashboard_emri,
                    R.drawable.dashboard_insurance,
                    R.drawable.test_emergency};
            String[] imageDescriptions = {getActivity().getString(R.string.emergency_contact), getActivity().getString(R.string.label_blood_group), getActivity().getString(R.string.label_health_record), getActivity().getString(R.string.label_emergency_provider), getActivity().getString(R.string.title_activity_insurance_details),
                    getActivity().getString(R.string.label_test_emergency)};
            DashboardGridAdapter adapter = new DashboardGridAdapter(getActivity(), images, imageDescriptions);
            adapter.setClickListener(this);
            recyclerView.setAdapter(null);
            recyclerView.setAdapter(adapter);
        }
    }


    public void askForSync() {
        if (isAdded()) {
            Log.d("sync_status", "" + defaultSharedPreferences.getBoolean("sync_status", false));
            Log.d("getPhoneContactsInfoCount", "count" + dh.getPhoneContactsInfoCount());
            if (defaultSharedPreferences.getBoolean("sync_status", false) == false && dh.getPhoneContactsInfoCount() == 0) {
                ///Sync
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.sync_homeact_msg);
                    builder.setCancelable(false);
                    builder.setNegativeButton(R.string.lable_later, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            defaultSharedPreferences.edit().putBoolean("sync_status", true).commit();
                            dialog.dismiss();
                            askForEmergencyHealthRecordSetup();
                        }
                    });
                    builder.setPositiveButton(getString(R.string.sync_now),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(getActivity(), BloodyFriendsActivity.class));
                                    //startBloodyFriendsSync();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    if (isAdded()) {
                        dialog.show();
                    }
                }
                ////
            }
        }
    }

    public void startBloodyFriendsSync() {
        if (isAdded()) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                if (tracker != null) {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("BloodyFriendsSyncClicked")
                            .build());
                }
                AppUtil.sendCampaignDetails(getActivity(), Constant.EVENT_BLOODY_FRIENDS);
                AppUtil.showCustomDurationToast(getActivity(), "Sync in progress.This will take a few minutes. We will notify you once the process is completed.");
                //contactResultReceiver = new ContactsResultReceiver(new Handler());
                startIntentServiceFetchContacts();
                defaultSharedPreferences.edit().putBoolean("sync_status", true).commit();

                    /*Upshot event capture*/
                HashMap<String, Object> bkDATA = new HashMap<>();
                bkDATA.put("BFSync", "Yes");
                Log.d("BFSync bk data", bkDATA.toString());
                UpshotEvents.createCustomEvent(bkDATA, 11);
                askForEmergencyHealthRecordSetup();
            } else {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[]{Manifest.permission.GET_ACCOUNTS},
                        Constant.PERMISSIONS_REQUEST_CODE_DASHBOARD_BFRIENDS_SYNC);
            }
        }
    }

    public void askForEmergencyHealthRecordSetup() {
        if (isAdded()) {
            Log.d(TAG, "USER CHOOSED TO ADD LATER?" + defaultSharedPreferences.getBoolean("setLater", false));
            if (!defaultSharedPreferences.getBoolean("setLater", false)) {
                try {
                    long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(getActivity());
                    PatientUserVO patientUserVO = dh.getProfile(currentSelectedProfile);
                    HealthRecordVO emergencyHealthRecord = dh.getUserHealthRecord(Constant.RECORD_TYPE_EHR, patientUserVO.getPatientId());
                    Log.d(TAG, "PATIENT ID=" + patientUserVO.getPatientId());

                    Log.d(TAG, "EHR=" + emergencyHealthRecord);

                    if (emergencyHealthRecord.getHealthRecord() == null || emergencyHealthRecord.getHealthRecord().size() == 0) {
                        Log.d(TAG, "HEALTH RECORD=" + emergencyHealthRecord.getHealthRecord());
                        mFragmentDialogAlertWithTwoButtons = FragmentDialogAlertWithTwoButtons.newInstance(getString(R.string.yes), getString(R.string.lable_later), getString(R.string.dialog_title_setup_ehr), getString(R.string.dialog_msg_setup_ehr));
                        if (!mFragmentDialogAlertWithTwoButtons.isVisible()) {
                            mFragmentDialogAlertWithTwoButtons.show(getActivity().getFragmentManager(), "DialogAlertWithTwoButtons");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            progressEmergencyPreparedness.setProgress(AppUtil.getEmergencyPreparednessStatus(getActivity()));
            tvEmergencyPreparedness.setText(AppUtil.getEmergencyPreparednessStatus(getActivity()) + "% Complete");
            setGridData();
            Intent startIntent = new Intent(getActivity(), StickyNotificationForeGroundService.class);
            startIntent.setAction(Constant.ACTION.EMERGENCY_PREPAREDNESS);
            getActivity().startService(startIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient.isConnected() == false) {
            Log.d(TAG, "mGoogleApiClient is connected");
            mGoogleApiClient.connect();
        } else {
            Log.d(TAG, "is not connected");
            setCurrentLocation();
        }
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_INSURANCE_DATA_CHANGED);
        filter.addAction(Constant.ACTION_SYNC_STARTED);
        filter.addAction(String.valueOf(Constant.ACTION_SYNC_IN_PROGREES));
        filter.addAction(Constant.ACTION_PROFILE_SYNC_COMPLETE);
        filter.addAction("ACTION_TASKS_PERFORMED");


        dbChangesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isAdded()) {
                    ivEmergencyButton.setClickable(true);
                    ivEmergencyButton.setEnabled(true);
                    String action = intent.getAction();
                    int errorCount = intent.getIntExtra("errorCount", 0);
                    String noNetwork = intent.getStringExtra("no_network");
                    if (action.equalsIgnoreCase(String.valueOf(Constant.ACTION_SYNC_IN_PROGREES))) {
                        syncInProgressStatus = Constant.ACTION_SYNC_IN_PROGREES;
                    }
                    if (action.equalsIgnoreCase(Constant.ACTION_PROFILE_SYNC_COMPLETE)) {
                        Log.d(TAG, Constant.ACTION_PROFILE_SYNC_COMPLETE + "Complete");
                        Log.d(TAG, "ERROR COUNT=" + errorCount);
                        if (ApplicationLifecycleManager.isAppInForeground()) {
                            try {
                                progressEmergencyPreparedness.setProgress(AppUtil.getEmergencyPreparednessStatus(getActivity()));
                                tvEmergencyPreparedness.setText(AppUtil.getEmergencyPreparednessStatus(getActivity()) + "% Complete");
                                setGridData();
                                try {
                                    Intent startIntent = new Intent(getActivity(), StickyNotificationForeGroundService.class);
                                    startIntent.setAction(Constant.ACTION.EMERGENCY_PREPAREDNESS);
                                    getActivity().startService(startIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            askForSync();
                        }
                    }
                    if (action.equalsIgnoreCase("ACTION_TASKS_PERFORMED")) {
                        Log.d(TAG, "ACTION_TASKS_PERFORMED");
                        syncInProgressStatus = 0;
                        Log.d(TAG, "SYNC COMPLETE");
                        Log.d(TAG, "ERROR COUNT=" + errorCount);
                        if (ApplicationLifecycleManager.isAppInForeground()) {
                            if (errorCount != 0) {
                                Snackbar mSnackbar = Snackbar.make(frameLayout, (!TextUtils.isEmpty(noNetwork) ? noNetwork : "Sync proceed failed,please try refresh after sometime"), Snackbar.LENGTH_LONG);
                                mSnackbar.show();
                            } else {
                                defaultSharedPreferences.edit().putLong("sync_timeout", System.currentTimeMillis()).commit();
                            }
                        }
                    }

                }
            }
        };
        mLocalBroadcastManager.registerReceiver(dbChangesReceiver, filter);
        long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(getActivity());
        UserInfoVO mUserInfoVO = null;
        try {
            mUserInfoVO = dh.getUserInfo(currentSelectedProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setUserDetails(mUserInfoVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        showCurrentEmergenciesButton();
    }

    private void setCurrentLocation() {
        startLocationUpdates();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    Location mLocation = getCurrentLocation();
                    if (mLocation != null) {
                        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(Constant.COMMON_SP_FILE, MODE_PRIVATE);
                        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                        Log.d(TAG, "LAST KNOWN LOCATION=" + location);
                        mEditor.putString("lat", String.valueOf(mLocation.getLatitude()));
                        mEditor.putString("lon", String.valueOf(mLocation.getLongitude()));
                        mEditor.commit();
                        if (mLocation != null) {
                            startIntentServiceFetchAddress(mLocation);
                        }
                    } else {
                        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(Constant.COMMON_SP_FILE, MODE_PRIVATE);
                        if (mSharedPreferences != null) {
                            if (!mSharedPreferences.getString("lat", "0").equalsIgnoreCase("0")) {
                                Location location = new Location("");
                                location.setLatitude(Double.parseDouble(mSharedPreferences.getString("lat", "0")));
                                location.setLongitude(Double.parseDouble(mSharedPreferences.getString("lon", "0")));
                                Log.d(TAG, "location=" + location);
                                startIntentServiceFetchAddress(location);
                            } else {
                                tvCurrentAddress.setText("Location Not Found");
                            }
                        } else {
                            tvCurrentAddress.setText("Location Not Found");
                        }
                    }
                }
            }
        }, 2000);
    }


    private void showCurrentEmergenciesButton() {
        if (isAdded()) {
            ArrayList<PatientUserVO> usersList = dh.getUsersInEmergencies();
            Log.d(TAG, "USERS IN EMERGENCY=" + (usersList != null ? usersList.size() : 1000));
            if (usersList.size() != 0) {
                btCurrentEmergencies.setVisibility(View.VISIBLE);
            } else {
                btCurrentEmergencies.setVisibility(View.INVISIBLE);
            }
            btCurrentEmergencies.setOnClickListener(this);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_emergency_button:
                sendThisEventToGoogleTracker();
                AppUtil.sendThisEventToUpshot(Constant.UpshotEvents.DASHBOARD_EMERGENCY_BUTTON_CLICKED, Constant.UpshotEventsId.DASHBOARD_EMERGENCY_BUTTON_CLICKED);
                isTestEmergency = false;
                startEmergency();
                break;
            case R.id.bt_current_emergencies:
                AppUtil.sendThisEventToUpshot(Constant.UpshotEvents.DASHBOARD_CURRENT_EMERGENCIES_BUTTON_CLICKED, Constant.UpshotEventsId.DASHBOARD_CURRENT_EMERGENCIES_BUTTON_CLICKED);
                Intent activityCurrentEmergencies = new Intent(getActivity(), ActivityCurrentEmergencies.class);
                startActivity(activityCurrentEmergencies);
                break;
            case R.id.tv_current_address:
                AppUtil.sendThisEventToUpshot(Constant.UpshotEvents.DASHBOARD_CURRENT_ADDRESS_BUTTON_CLICKED, Constant.UpshotEventsId.DASHBOARD_CURRENT_ADDRESS_BUTTON_CLICKED);
                if (AppUtil.isOnline(getActivity())) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient.isConnected() == false) {
                            mGoogleApiClient.connect();
                        } else {
                            checkIfLocationPermissionsSatisfied();
                        }
                    } else {
                        Log.d(TAG, "Location permission not granted");
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 27);
                    }
                } else {
                    AppUtil.showToast(getActivity(), "Please check your internet connection and try again");
                }
                break;


        }
    }

    private void sendThisEventToGoogleTracker() {
        if (tracker != null) {
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("EmergencyRaised")
                    .build());
        }
    }


    public void startEmergency() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 26);
        } else {
            Intent intent = new Intent(getActivity(), CallAmbulanceSyncService.class);
            getActivity().stopService(intent);
            Intent mIntent = new Intent(getActivity(), ActivitySelectPatientInEmergency.class);
            mIntent.putExtra(Constant.IS_TEST_EMERGENCY, isTestEmergency);
            startActivity(mIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "inside onRequestPermissionsResult");
        Log.d(TAG, "requestCode" + requestCode);


        switch (requestCode) {
            case 26:
                Intent intent = new Intent(getActivity(), CallAmbulanceSyncService.class);
                getActivity().stopService(intent);
                Intent mIntent = new Intent(getActivity(), ActivitySelectPatientInEmergency.class);
                mIntent.putExtra(Constant.IS_TEST_EMERGENCY, isTestEmergency);
                startActivity(mIntent);
                UpshotEvents.createCustomEvent(null, 5);
                Answers.getInstance().logCustom(new CustomEvent("Emergency launched"));


                break;
            case 27:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!AppUtil.isGpsEnabled2(getActivity())) {
                        if (!mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.connect();
                        }
                        checkIfLocationPermissionsSatisfied();
                    } else {
                        if (!mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.connect();
                        } else {
                            setCurrentLocation();
                        }
                    }
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.d("location_denied_never_Allow", "location_denied_never_Allow");
                    showDialogToAskExternalStatePermissionForNeverAllow("Location permission is mandatory to get the current location.Tap on GRANT to give this permissions to CallAmbulance");
                } else {
                }

                break;
        }
    }

    private void showDialogToAskExternalStatePermissionForNeverAllow(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.grant)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                AppUtil.showToast(getActivity(), getString(R.string.toast_switch_on_permissions));
                                launchSettingPermissionScreen();
                            }
                        }
                )
                .setNegativeButton(getString(R.string.deny)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        }
                )
                .create().show();
    }

    private void launchSettingPermissionScreen() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void askForPermissions(String msg, final String[] permissions, final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.grant)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                requestPermissions(permissions, requestCode);
                            }
                        }
                )
                .setNegativeButton(getString(R.string.deny)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        }
                )
                .create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult" + requestCode + " " + resultCode);
        switch (requestCode) {
            case REQUEST_CODE_CHECK_LOCATION_ENABLED:
                break;
        }
    }


    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");
        if (isAdded()) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Log.d(TAG, "location update started");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setCurrentLocation();
    }

    public Location getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setNumUpdates(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "OnConnectionFailed");
        if (mResolvingError) {
            Log.d(TAG, "ALREADY RESOLVING ERROR");

            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                Log.d(TAG, "There was an error with the resolution intent");
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "errorDialog");
    }


    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "********* grid item clicked ***********");
        final long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        final HashMap<String, Object> upshotData = new HashMap<>();

        switch (position) {
            case 1:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_BLOOD_GROUP_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_BLOOD_GROUP_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                try {
                    UserInfoVO mInfoVO = dh.getUserInfo(currentSelectedProfile);
                    if (!TextUtils.isEmpty(mInfoVO.getBloodGroup())) {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        defaultSharedPreferences.edit().remove("verifyDialogActionTaken").commit();
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), ActivityEditProfile.class);
                        intent.putExtra("key", "edit");
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 0:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_EC_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_EC_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                Intent contactsIntent = new Intent(getActivity(), ContactsActivity.class);
                contactsIntent.putExtra("source", getApplicationContext().getString(R.string.lable_emergency_prepardness));
                startActivity(contactsIntent);
                break;
            case 2:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_HEALTH_RECORD_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_HEALTH_RECORD_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                try {
                    HealthRecordVO emergencyHealthRecord = dh.getUserHealthRecord(Constant.RECORD_TYPE_EHR, currentSelectedProfile);
                    if (emergencyHealthRecord.getHealthRecord() == null || emergencyHealthRecord.getHealthRecord().size() == 0) {
                        navigateToEditEhrScreen();
                    } else {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        defaultSharedPreferences.edit().remove("verifyDialogActionTaken").commit();
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("goTo", Constant.EMHR_VIEW_SCREEN);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_EMERGENCY_PROVIDER_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_EMERGENCY_PROVIDER_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                try {
                    UserInfoVO mInfoVO = dh.getUserInfo(currentSelectedProfile);
                    if (mInfoVO.getPreferredOrgBranchId() != 0) {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        defaultSharedPreferences.edit().remove("verifyDialogActionTaken").commit();
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("goTo", Constant.EMHR_VIEW_SCREEN);
                        startActivity(intent);
                    } else {
                        Intent intent1 = new Intent(getActivity(), ActivityEHRUpdate.class);
                        intent1.putExtra("key", "edit");
                        intent1.putExtra(Constant.FOCUS_PREFERRED_HOSPITAL, true);
                        startActivity(intent1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                upshotData.put(Constant.UpshotEvents.DASHBOARD_INSURANCE_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DASHBOARD_INSURANCE_CLICKED);
                Log.d(TAG, "upshot data=" + upshotData.entrySet());
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                defaultSharedPreferences.edit().remove("verifyDialogActionTaken").commit();
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("goTo", Constant.INSURANCE_VIEW_SCREEN);
                startActivity(intent);

                break;
            case 5:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("");
                builder.setMessage(R.string.dialog_msg_test_emergency_click);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        AppUtil.sendThisEventToUpshot(Constant.UpshotEvents.DASHBOARD_TEST_EMERGENCY_CLICKED, Constant.UpshotEventsId.DASHBOARD_TEST_EMERGENCY_CLICKED);
                        isTestEmergency = true;
                        startEmergency();
                    }
                });
                testEmergencyClickDialog = builder.create();
                testEmergencyClickDialog.show();
                break;
        }
    }

    private void navigateToEditEhrScreen() {
        Intent intent1 = new Intent(getActivity(), ActivityEHRUpdate.class);
        intent1.putExtra("key", "edit");
        startActivity(intent1);
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            mResolvingError = false;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");

    }

    public void checkIfLocationPermissionsSatisfied() {
        Log.d(TAG, "mGoogleApiClient=" + mGoogleApiClient);
        Log.d(TAG, "mLocationRequest=" + mLocationRequest);
        Log.d(TAG, "mGoogleApiClient is connected" + mGoogleApiClient.isConnected());


        if (mGoogleApiClient != null && mLocationRequest != null) {
            Log.d(TAG, "in=" + mLocationRequest);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            final PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    Log.d(TAG, "onResult");
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.d(TAG, "LocationSettingsStatusCodes.SUCCESS");

                            if (!mGoogleApiClient.isConnected()) {
                                mGoogleApiClient.connect();
                            } else {
                                setCurrentLocation();
                            }
                            break;

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            askToSwitchOnLocation();
                            break;
                    }
                }
            });
        }
    }

    protected void askToSwitchOnLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("It seems your location service is turned off or not set to High Accuracy mode in your device settings.Setting these up will help us to locate you precisely")
                .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_CHECK_LOCATION_ENABLED);
                    }
                })
                .setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setCurrentLocation();
                    }
                });

        alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void dismissSyncSnackBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (dbChangesReceiver != null) {
            mLocalBroadcastManager.unregisterReceiver(dbChangesReceiver);
        }
        raiseEmergency = false;
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
        if (testEmergencyClickDialog != null) {
            if (testEmergencyClickDialog.isShowing()) {
                testEmergencyClickDialog.dismiss();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void button1Clicked() {
        // Alert dialog button1
        defaultSharedPreferences.edit().putBoolean("setLater", true).commit();

    }

    @Override
    public void button2Clicked() {
        Intent mIntent = new Intent(getActivity(), ActivityEHRUpdate.class);
        mIntent.putExtra("key", "edit");
        startActivity(mIntent);
    }


    protected void startIntentServiceFetchContacts() {
        Intent intent = new Intent(getActivity(), IntentServiceFetchContacts.class);
        getActivity().startService(intent);
    }

    protected void startIntentServiceFetchAddress(Location mLastLocation) {
        Intent intent = new Intent(getActivity(), IntentServiceFetchAddress.class);
        intent.putExtra(getActivity().getString(R.string.package_name) + "." + Constant.RECEIVER, mResultReceiver);
        intent.putExtra(getString(R.string.package_name) + "." + Constant.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (isAdded()) {
                SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                try {
                    if (resultData != null) {
                        /*Intent mIntent = new Intent(getActivity(), SaveUserLogIntentService.class);
                        mIntent.putExtra("loggedInLatitude", String.valueOf(mSharedPreferences.getString("lat", "0")));
                        mIntent.putExtra("loggedInLongitude", String.valueOf(mSharedPreferences.getString("lon", "0")));
                        mIntent.putExtra("city", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.CITY, ""));
                        mIntent.putExtra("district", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.DISTRICT, ""));
                        mIntent.putExtra("state", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.STATE, ""));
                        mIntent.putExtra("pinCode", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.POSTAL_CODE, ""));
                        mIntent.putExtra("country", resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.COUNTRY, ""));
                        mIntent.putExtra(Constant.LOCALITY, resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.LOCALITY, ""));
                        mIntent.putExtra(Constant.SUB_LOCALITY, resultData.getString(BuildConfig.APPLICATION_ID + "." + Constant.ADDRESS_LINE_1, ""));

                        Log.d("aaaaaa=",mIntent.getExtras()+"");
                        getActivity().startService(mIntent);*/
                        String currentAddress = resultData.getString(getString(R.string.package_name) + "." + Constant.FORMATTED_ADDRESS, "");
                        if (!TextUtils.isEmpty(currentAddress)) {
                            tvCurrentAddress.setText(currentAddress);
                        } else {
                            Log.d(TAG, "LastKnownLocation=" + mSharedPreferences.getString("address", ""));
                            if (!TextUtils.isEmpty(mSharedPreferences.getString("address", ""))) {
                                tvCurrentAddress.setText(mSharedPreferences.getString("address", ""));
                            } else {
                                tvCurrentAddress.setText("Location Not Found");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

