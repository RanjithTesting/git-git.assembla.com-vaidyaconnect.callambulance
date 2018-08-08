package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.services.CallAmbulanceSyncService;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.Validator;
import com.patientz.webservices.WebServiceUrls;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityEditProfile extends BaseActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,Filterable {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    Button sendDetails;
    TextView showDobTextview, tvLastDonatedBloodDate, tvGenderMandatory, tvIsDobMandatory, tvBloodGroupMandatory;
    EditText firstName, companyName, lastName, email, phoneNumber, homePhone, address, city,
            familyHistory, habits, remarks, state, pinCode, organDonateEt;
    ImageView dateOfBirthImageView,ivLastDonatedBloodDateCal;
    private AutoCompleteTextView etPreferredBloodBank;
    SharedPreferences sp;

    DatePicker dp;
    Calendar c1,c;
    String[] phoneNoIsdCode, alPhoneNoIsdCode;
    String country;
    Spinner countrySpinner, countryIsdCodeSpinner, countryAltIsdCodeNumber,
            addPatientGenderSpinner, addPatientRelationSpinner,
            addPatientFinancialStatusSpinner, maritalStatusSpinner,
            foodHabitsSpinner, spinnerBloodGroup;
    UserInfoVO mInfoVO;
    LinearLayout parentRelationship;
    RadioButton selectedRadioButton;
    PatientUserVO mPatientUserVO;
    Date dateOfBirth,dateOfLastUpdatedBlood;
    String keyValue, patientId;
    RadioGroup radioGroupDonateBlood, radioDonateOrgan,radioLastBloodDonationReq;
    RadioButton radioYes, radioNo, radioDonateOrganYes, radioDonateOrganNo,radioYesLastBloodDonationReq,radioNoLastBloodDonationReq;
    public static final String TAG = "AddPatientInfoFragment";
    LocationManager manager;
    LinearLayout mLoaderStatusView;
    ScrollView rootView;
    CallAmbulanceSyncService mService;
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mReceiver = null;
    private RequestQueue mRequestQueue;
    boolean etPreferredOrgBranchStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        buildGoogleApiClient();

        mRequestQueue = AppVolley.getRequestQueue();

        mLocalBroadcastManager = LocalBroadcastManager
                .getInstance(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_DONE);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                showProgress(false);
                if (action.equalsIgnoreCase(Constant.ACTION_DONE)) {
                    Log.d(TAG, "ACTION_DONE");
                    finish();
                }
                if (action.equalsIgnoreCase("close")) {
                    Log.d(TAG, "ACTION CLOSE");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_msg_refresh));
                }
            }
        };

        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        try {
            mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.title_activity_update_profile);
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.actionbar_back);
            // toolbar.inflateMenu(R.menu.menu_toolbar_cancel);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            try {
                mInfoVO = mDatabaseHandler.getUserInfo(mPatientUserVO.getPatientId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("mInfoVO--->", mInfoVO.toString());

            tvGenderMandatory = (TextView)
                    findViewById(R.id.tv_isGenderMandatory);
            tvIsDobMandatory = (TextView)
                    findViewById(R.id.tv_isDobMandatory);
            tvBloodGroupMandatory = (TextView)
                    findViewById(R.id.tv_blood_group_mandatory);


            companyName = (EditText) findViewById(R.id.et_company_name);

            firstName = (EditText) findViewById(R.id.add_patient_first_name);
            // organDonateEt = (EditText) findViewById(R.id.organ_donation_et);
            lastName = (EditText) findViewById(R.id.add_patient_last_name);
            parentRelationship = (LinearLayout) findViewById(R.id.parent_relationship);

            dateOfBirthImageView = (ImageView)
                    findViewById(R.id.dob_imageview);
            ivLastDonatedBloodDateCal = (ImageView)
                    findViewById(R.id.iv_last_donated_blood_date_cal);


            dp = (DatePicker) findViewById(R.id.datePicker);
            email = (EditText) findViewById(R.id.add_patient_email);
            address = (EditText) findViewById(R.id.add_patient_address);
            city = (EditText) findViewById(R.id.add_patient_city);
            state = (EditText) findViewById(R.id.add_patient_state);
            familyHistory = (EditText)
                    findViewById(R.id.add_patient_familyHistory);
            habits = (EditText) findViewById(R.id.add_patient_habits);
            remarks = (EditText) findViewById(R.id.add_patient_remarks);
            countryIsdCodeSpinner = (Spinner)
                    findViewById(R.id.isdcode_spinner);
            countryAltIsdCodeNumber = (Spinner)
                    findViewById(R.id.alt_isdcode_spinner);
            pinCode = (EditText) findViewById(R.id.add_patient_pincode);
            phoneNumber = (EditText) findViewById(R.id.add_patient_phoneno);
            homePhone = (EditText) findViewById(R.id.add_patient_alt_phoneno);
            countrySpinner = (Spinner)
                    findViewById(R.id.add_patient_country_spinner);
            showDobTextview = (TextView) findViewById(R.id.show_dob_textview);
            tvLastDonatedBloodDate = (TextView) findViewById(R.id.tv_last_blood_donated_date);

            addPatientRelationSpinner = (Spinner)
                    findViewById(R.id.add_patient_relation_spinner);
            addPatientGenderSpinner = (Spinner)
                    findViewById(R.id.add_patient_gender_spinner);
            addPatientFinancialStatusSpinner = (Spinner)
                    findViewById(R.id.add_patient_financial_status_spinner);
            maritalStatusSpinner = (Spinner)
                    findViewById(R.id.marital_status_spinner);
            AppUtil.setSpinnerValues(this, maritalStatusSpinner,
                    R.array.marital_status_values);
            AppUtil.setSpinnerValues(this, addPatientGenderSpinner,
                    R.array.gender);

            foodHabitsSpinner = (Spinner)
                    findViewById(R.id.food_habits_spinner);
            AppUtil.setSpinnerValues(this, foodHabitsSpinner,
                    R.array.food_habits_values);

            radioGroupDonateBlood = (RadioGroup)
                    findViewById(R.id.radioGroupDonateBlood);
            radioLastBloodDonationReq = (RadioGroup)
                    findViewById(R.id.radioReceiveBloodRequest);

            radioYes = (RadioButton) findViewById(R.id.radioYes);
            radioNo = (RadioButton) findViewById(R.id.radioNo);

            radioYesLastBloodDonationReq = (RadioButton) findViewById(R.id.radioYesLastBloodDonationReq);
            radioNoLastBloodDonationReq = (RadioButton) findViewById(R.id.radioNoLastBloodDonationReq);

            radioDonateOrgan = (RadioGroup)
                    findViewById(R.id.radioDonateOrgan);
            radioDonateOrganYes = (RadioButton) findViewById(R.id.radioDonateOrganYes);
            radioDonateOrganNo = (RadioButton) findViewById(R.id.radioDonateOrganNo);
            spinnerBloodGroup = (Spinner) findViewById(R.id.spinner_blood_group);
            etPreferredBloodBank = (AutoCompleteTextView) findViewById(R.id.et_preferred_blood_bank);

            AppUtil.setSpinnerValues(this, spinnerBloodGroup,
                    R.array.array_bloodgroup_contacts);
            AppUtil.setSpinnerValues(this, addPatientRelationSpinner,
                    R.array.add_patient_relation_entry);

            AppUtil.setSpinnerValues(this,
                    addPatientFinancialStatusSpinner,
                    R.array.add_patient_financial_status_spinner_with_select);
            AppUtil.setSpinnerValues(this, countryIsdCodeSpinner,
                    R.array.select_country_isdCode);
            AppUtil.setSpinnerValues(this, countryAltIsdCodeNumber,
                    R.array.select_country_isdCode);

            addPatientRelationSpinner.setPrompt(getString(R.string.relation));
            addPatientGenderSpinner
                    .setPrompt(getString(R.string.gender));
            addPatientFinancialStatusSpinner
                    .setPrompt(getString(R.string.financial_status));
            dateOfBirth = new Date();
            dateOfLastUpdatedBlood = new Date();
            mLoaderStatusView = (LinearLayout) findViewById(R.id.loading_status);
            rootView = (ScrollView) findViewById(R.id.root_view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        populateBloodBanksAutoComplete();

        etPreferredBloodBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etPreferredOrgBranchStatus = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPreferredOrgBranchStatus = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void updateDisplay(Date date,TextView displayTv) {

        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
        String formattedDateq = fmt.format(date);
        Log.e("formattedDateq--->", "" + formattedDateq);
        displayTv.setText(formattedDateq);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_save_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                Log.d(TAG, "SAVE CLICKED");
                try {
                    sendDataToWeb();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSavePressed() {

        if (AppUtil.isOnline(getApplicationContext())) {
            try {
                sendDataToWeb();
            } catch (Exception e) {
                Log.d(TAG, "EXCEPTION=" + e.getMessage());
            }
        } else {
            AppUtil.showDialog(this, getString(R.string.offlineMode));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onPostDataLoad()");
        c = Calendar.getInstance();
        c1=Calendar.getInstance();
        Intent mIntent = new Intent();
        mIntent = getIntent();
        keyValue = mIntent.getStringExtra("key");
        if (keyValue.equals("edit")) {
            //setSpinnerValues();
            setDateOfBirthIfExist();
            setLastBloodDonatedDateIfExist();
            populateAlreadyAddedBasicValues();
        }
        addPatientGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    tvGenderMandatory.setError(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    tvBloodGroupMandatory.setError(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dateOfBirthImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                new DatePickerDialog(ActivityEditProfile.this, AlertDialog.THEME_HOLO_LIGHT,mDateSetListener, c
                        .get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        ivLastDonatedBloodDateCal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ActivityEditProfile.this,AlertDialog.THEME_HOLO_LIGHT, mDateSetListenerLastDonatedBloodDate, c1
                        .get(Calendar.YEAR), c1.get(Calendar.MONTH), c1
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void sendDataToWeb() throws Exception {

        Boolean result = validatePatientBasicFields();
        if (result == true) {
            email.setError(null);
            firstName.setError(null);
            lastName.setError(null);
            phoneNumber.setError(null);
            homePhone.setError(null);
            pinCode.setError(null);
            int selectedId = radioGroupDonateBlood.getCheckedRadioButtonId();
            int selectedIdOrgan = radioDonateOrgan.getCheckedRadioButtonId();
            int radioLastBloodDonationReqCheckedRadioButtonId = radioLastBloodDonationReq.getCheckedRadioButtonId();



            RadioButton selectedRadioButton = (RadioButton) findViewById(selectedId);
            RadioButton selectedRadioButtonOrganDonation = (RadioButton) findViewById(selectedIdOrgan);
            RadioButton  notifyBloodDonationReq= (RadioButton) findViewById(radioLastBloodDonationReqCheckedRadioButtonId);

            int[] countryCode = getResources().getIntArray(
                    R.array.select_country_id);
            country = String.valueOf(countryCode[countrySpinner
                    .getSelectedItemPosition()]);
            String mState = "";
            if (state.getText().toString().trim().length() != 0) {
                mState = "," + state.getText().toString().trim();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("patientId", mInfoVO.getPatientId() + "");
            params.put(Constant.firstName,
                    firstName.getText().toString());
            params.put(Constant.lastName,
                    lastName.getText().toString());
            params.put(Constant.ORGAN_DONATE,
                    selectedRadioButtonOrganDonation.getText().toString());

            if(notifyBloodDonationReq.getText().toString().equalsIgnoreCase(getString(R.string.yes)))
            {
                params.put(Constant.NOTIFY_BLOOD_DONATION_REQUEST,
                        "true");

            }else
            {
                params.put(Constant.NOTIFY_BLOOD_DONATION_REQUEST,
                        "false");
            }



            if (phoneNumber.getText().toString().trim().length() != 0) {
                params.put(Constant.phoneNumber,
                        countryIsdCodeSpinner.getSelectedItem().toString()
                                + "-" + phoneNumber.getText().toString());
            }
            if (homePhone.getText().toString().trim().length() != 0) {

                params.put(
                        Constant.ALT_PHONE_NO, countryAltIsdCodeNumber.getSelectedItem().toString()
                                + "-" + homePhone.getText().toString());
            }
            params.put(Constant.dateOfBirthKey,
                    showDobTextview.getText().toString());

            params.put(Constant.lastBloodDonatedDate,
                    tvLastDonatedBloodDate.getText().toString());
            params.put(Constant.emailIdKey,
                    email.getText().toString());
            params.put(Constant.address, address
                    .getText().toString());
            params.put(
                    Constant.familyHistoryKey, familyHistory.getText()
                            .toString());
            params.put(Constant.habitsKey,
                    habits.getText().toString());
            params.put(Constant.state, state
                    .getText().toString());
            params.put(Constant.city, city
                    .getText().toString().trim()
                    + mState
                    + ","
                    + countrySpinner.getSelectedItem().toString());

            params.put(Constant.remarksKey,
                    remarks.getText().toString());
            params.put(Constant.pinCode, pinCode
                    .getText().toString());
            params.put(Constant.DONATE_BLOOD,
                    selectedRadioButton.getText().toString());
            if (addPatientRelationSpinner.getSelectedItem().toString() != null && !addPatientRelationSpinner.getSelectedItem().toString().equalsIgnoreCase("")) {
                params.put(Constant.relation,
                        addPatientRelationSpinner.getSelectedItem().toString());
            } else {
                params.put(Constant.relation, getString(R.string.self));
            }
            if (maritalStatusSpinner.getSelectedItem().toString()
                    .equalsIgnoreCase(getString(R.string.select))) {
                params.put(
                        Constant.MARITAL_STATUS, "");
            } else {
                params.put(
                        Constant.MARITAL_STATUS, maritalStatusSpinner
                                .getSelectedItem().toString());
            }
            if (foodHabitsSpinner.getSelectedItem().toString()
                    .equalsIgnoreCase(getString(R.string.select))) {
                params.put(Constant.FOOD_HABITS,
                        "");
            } else {
                params.put(Constant.FOOD_HABITS,
                        foodHabitsSpinner.getSelectedItem().toString());
            }
            if (spinnerBloodGroup.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.label_select_blood_group))) {
                params.put(Constant.BLOOD_GROUP, "");
            } else {
                params.put(Constant.BLOOD_GROUP,
                        spinnerBloodGroup.getSelectedItem().toString());
            }

            if (addPatientFinancialStatusSpinner.getSelectedItem().toString()
                    .equalsIgnoreCase(getString(R.string.select))) {
                params.put(
                        Constant.financialStatusKey, "");
            } else {
                params.put(
                        Constant.financialStatusKey,
                        addPatientFinancialStatusSpinner.getSelectedItem()
                                .toString());
            }
            params.put(
                    Constant.phoneNumberIsdCodeKey, countryIsdCodeSpinner
                            .getSelectedItem().toString());

            params.put(
                    Constant.COMPANY_NAME_KEY, companyName
                            .getText().toString());

            params.put(
                    Constant.altPhoneNumberIsdCodeKey, countryAltIsdCodeNumber
                            .getSelectedItem().toString());
            params.put("country.id", country);
            params.put("lastSyncId", String
                    .valueOf(AppUtil.getEventLogID(getApplicationContext())));
            Log.d("check sending details: ", params.entrySet().toString());
            params.put(Constant.genderKey,
                    String.valueOf(addPatientGenderSpinner
                            .getSelectedItemPosition()));

            if (etPreferredOrgBranchStatus == true && sp.getLong(Constant.SP_KEY_PREFERRED_BLOOD_BANK_BRANCH_ID, 0) != 0 && !TextUtils.isEmpty(etPreferredBloodBank.getText().toString())) {
                params.put("preferredBloodBankId", "" + sp.getLong(Constant.SP_KEY_PREFERRED_BLOOD_BANK_BRANCH_ID, 0));
            }
            Log.d(TAG,"PARAMS="+params);
            showProgress(true);
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.PROFILE_EDIT_SUBMIT_CLICKED,true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.PROFILE_EDIT_SUBMIT_CLICKED);
            Log.d(TAG,"upshot data="+upshotData.entrySet());
            mRequestQueue.add(createUpdateProfileRequest(params));

        }

    }


    public boolean validatePatientBasicFields() {
        Boolean result = true;
        String phoneError = "";
        String altPhoneNumberError = "";
        String emailError = "";
        String pincodeError = "";
        String fstName = "";
        String lstName = "";
        String phNo = phoneNumber.getText().toString().trim();
        String hPhone = homePhone.getText().toString().trim();
        String fName = firstName.getText().toString().trim();
        ArrayList<View> errorProneEdittext = new ArrayList<View>();

        if (fName.length() == 0) {
            firstName.setError(getString(R.string.fname_mandatory_error_msg));
            errorProneEdittext.add(firstName);
            result = false;
        } else {
            if (fName.length() < 3 || fName.length() > 25) {
                fstName = getString(R.string.fname_length_error_msg);
                firstName.setError(fstName);
                errorProneEdittext.add(firstName);
                result = false;

            }
        }
        if (lastName.getText().toString().trim().length() == 0) {
            lastName.setError(getString(R.string.lname_mandatory_error_msg));
            errorProneEdittext.add(lastName);

            result = false;
        } else {
            if (lastName.getText().toString().trim().length() < 1
                    || lastName.getText().toString().trim().length() > 25) {
                lstName = getString(R.string.lname_length_error_msg);
                lastName.setError(lstName);
                errorProneEdittext.add(lastName);

                result = false;

            }
        }

        if (phNo.matches(Validator.phoneValidator) == false) {
            phoneError = getString(R.string.phoneno_invalid);
            phoneNumber.setError(phoneError);
            errorProneEdittext.add(phoneNumber);
            result = false;
        }
        if ((phNo.length() < 10 || phNo.length() > 13)) {

            phoneError = phoneError
                    + getString(R.string.phoneno_length_error_msg);
            phoneNumber.setError(phoneError);
            errorProneEdittext.add(phoneNumber);

            result = false;
        }
        // }
        if (hPhone.length() != 0) {
            if (hPhone.matches(Validator.phoneValidator) == false) {
                altPhoneNumberError = getString(R.string.alt_phoneno_invalid);
                homePhone.setError(altPhoneNumberError);
                errorProneEdittext.add(homePhone);

                result = false;
            }
            if (hPhone.length() < 10 || hPhone.length() > 13) {

                altPhoneNumberError = altPhoneNumberError
                        + getString(R.string.alt_phoneno_length_error_msg);
                homePhone.setError(altPhoneNumberError);
                errorProneEdittext.add(homePhone);

                result = false;
            }
        }
        if ((email.getText().toString().length() != 0)) {
            if ((email.getText().toString().trim().length() < 6)
                    || (email.getText().toString().trim().length() > 50)) {
                emailError = getString(R.string.email_length_validate);
                email.setError(emailError);
                errorProneEdittext.add(email);

                result = false;
            }
            if (!(AppUtil.isValidEmail(email.getText().toString().trim()))) {
                emailError = emailError
                        + getString(R.string.email_invalid_error_msg);
                email.setError(emailError);
                errorProneEdittext.add(email);
                result = false;
            }

        }

        if (city.getText().toString().trim().length() < 3
                || city.getText().toString().trim().length() > 100) {
            city.setError(getString(R.string.cityStateLenValMsg));
            errorProneEdittext.add(city);

            result = false;
        }
        if (state.getText().toString().trim().length() < 3
                || state.getText().toString().trim().length() > 100) {
            state.setError(getString(R.string.cityStateLenValMsg));
            errorProneEdittext.add(state);
            result = false;
        }
        if ((pinCode.getText().toString().trim().length() != 0)
                && (pinCode.getText().toString().trim().length() < 5 || pinCode
                .getText().toString().trim().length() > 10)) {
            pinCode.setError(getString(R.string.pincode_length_error_msg));
            errorProneEdittext.add(pinCode);

            result = false;
        }

        if ((pinCode.getText().toString().length() != 0)) {
            if ((pinCode.getText().toString().trim().length() < 5)
                    || (pinCode.getText().toString().trim().length() > 10)) {
                pincodeError = getString(R.string.pincode_length_error_msg);
                pinCode.setError(pincodeError);
                errorProneEdittext.add(pinCode);

                result = false;
            }
            if (pinCode.getText().toString().trim()
                    .matches(Validator.PINCODE_PATTERN) == false) {
                pincodeError = pincodeError
                        + getString(R.string.pincode_validate);
                pinCode.setError(pincodeError);
                errorProneEdittext.add(pinCode);

                result = false;
            }

        }
        if (showDobTextview.getText().length() == 0) {
            tvIsDobMandatory.requestFocus();
            tvIsDobMandatory.setError(getString(
                    R.string.dob_mandatory));
            errorProneEdittext.add(tvIsDobMandatory);
            result = false;
        } else {
            tvIsDobMandatory.setError(null);
        }

        if (addPatientGenderSpinner.getSelectedItemPosition() == 0) {
            /*AppUtil.showDialog(ActivityAddMember.this,
                    getString(R.string.gender_mandatory));*/
            tvGenderMandatory.requestFocus();
            tvGenderMandatory.setError(getString(
                    R.string.gender_mandatory));
            errorProneEdittext.add(tvGenderMandatory);
            result = false;
        } else {
            tvGenderMandatory.setError(null);
        }

        if (errorProneEdittext != null && errorProneEdittext.size() != 0) {
            Log.d(TAG, "error views>>" + errorProneEdittext.size());
            errorProneEdittext.get(0).requestFocus();
            errorProneEdittext.get(0).requestFocusFromTouch();
        }
        return result;
    }

    public void populateAlreadyAddedBasicValues() {
        setIsdValues();
        setRelationshipSpinner();
        if(mInfoVO.getPreferredBloodBankId()!=0)
        {
        DatabaseHandlerAssetHelper mDatabaseHandlerAssetHelper=DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        OrgBranchVO preferredBloodBank=mDatabaseHandlerAssetHelper.getPreferredOrgBranch(mInfoVO.getPreferredBloodBankId());
        etPreferredBloodBank.setText(preferredBloodBank.getDisplayName()+(!TextUtils.isEmpty(preferredBloodBank.getTelephone())?(","+preferredBloodBank.getTelephone()):""));
        }
        etPreferredOrgBranchStatus = true;

        if (mInfoVO.getBloodDonation() != null) {
            Log.d(TAG, "****** BLOOD DONATION ********" + mInfoVO.getBloodDonation());

            if (mInfoVO.getBloodDonation().equalsIgnoreCase("yes")) {
                radioYes.setChecked(true);
            } else if (mInfoVO.getBloodDonation().equalsIgnoreCase("no")) {
                radioNo.setChecked(true);
            } else {

            }
        }



            if (mInfoVO.isNotifyBloodDonationRequest()) {
                radioYesLastBloodDonationReq.setChecked(true);
            } else  {
                radioNoLastBloodDonationReq.setChecked(true);
            }


        if (mInfoVO.getOrganDonation() != null) {
            Log.d(TAG, "****** ORGAN DONATION ********" + mInfoVO.getOrganDonation());

            if (mInfoVO.getOrganDonation().equalsIgnoreCase("yes")) {
                radioDonateOrganYes.setChecked(true);
            } else if (mInfoVO.getOrganDonation().equalsIgnoreCase("no")) {
                radioDonateOrganNo.setChecked(true);
            } else {

            }
        }
        for (int i = 0; i < countrySpinner.getCount(); i++) {
            if (countrySpinner.getItemAtPosition(i)
                    .equals(mInfoVO.getCountry())) {
                countrySpinner.setSelection(i);
            }
        }
        Log.d(TAG, "GENDER=" + mInfoVO.getGender());
        addPatientGenderSpinner.setSelection(mInfoVO.getGender());// gender


        if (mInfoVO.getBloodGroup() != null) {
//            spinnerBloodGroup.setSelection(1);
            Log.d(TAG, "BloodGroup=" + mInfoVO.getBloodGroup());
        }

        if (mInfoVO.getDateOfBirth() != null) {
            updateDisplay(mInfoVO.getDateOfBirth(),showDobTextview);
        }
        Log.d(TAG,"populateAlreadyAddedBasicValues>>Last blood donated date"+mInfoVO.getLastBloodDonationDate());
        if (mInfoVO.getLastBloodDonationDate() != null) {
            updateDisplay(mInfoVO.getLastBloodDonationDate(),tvLastDonatedBloodDate);
        }


        if (mInfoVO.getFirstName() != null) {
            firstName.setText(mInfoVO.getFirstName());
        }
        if (mInfoVO.getCompanyName() != null) {
            companyName.setText(mInfoVO.getCompanyName());
        }
        /*if (mInfoVO.getOrganDonation() != null) {
            organDonateEt.setText(mInfoVO.getOrganDonation());
        }*/
        if (mInfoVO.getLastName() != null) {
            lastName.setText(mInfoVO.getLastName());
        }
        if (mInfoVO.getEmailId() != null) {
            email.setText(mInfoVO.getEmailId());
        }
        if (mInfoVO.getAddress() != null) {
            address.setText(mInfoVO.getAddress());
        }
        if (mInfoVO.getCity() != null) {
            city.setText(mInfoVO.getCity());
        }
        if (mInfoVO.getState() != null) {
            state.setText(mInfoVO.getState());
        }
        if (mInfoVO.getFamilyHistory() != null) {
            familyHistory.setText(mInfoVO.getFamilyHistory());
        }
        if (mInfoVO.getHabits() != null) {
            habits.setText(mInfoVO.getHabits());
        }
        if (mInfoVO.getRemarks() != null) {
            remarks.setText(mInfoVO.getRemarks());
        }
        if (mInfoVO.getPinCode() != null) {
            pinCode.setText(mInfoVO.getPinCode());
        }
        if (mInfoVO.getAltPhoneNumber() != null) {
            homePhone.setText(mInfoVO.getAltPhoneNumber());
        }
        if (mInfoVO.getPhoneNumber() != null) {
            phoneNumber.setText(mInfoVO.getPhoneNumber());
        }
        for (int i = 0; i < addPatientFinancialStatusSpinner.getCount(); i++) {
            if (addPatientFinancialStatusSpinner.getItemAtPosition(i).equals(
                    mInfoVO.getFinancialStatus())) {
                addPatientFinancialStatusSpinner.setSelection(i);
            }
        }
        for (int i = 0; i < maritalStatusSpinner.getCount(); i++) {
            if (maritalStatusSpinner.getItemAtPosition(i).equals(
                    mInfoVO.getMaritalStatus())) {
                maritalStatusSpinner.setSelection(i);
            }
        }
        for (int i = 0; i < foodHabitsSpinner.getCount(); i++) {
            if (foodHabitsSpinner.getItemAtPosition(i).equals(
                    mInfoVO.getFoodHabits())) {
                foodHabitsSpinner.setSelection(i);
            }
        }
        for (int i = 0; i < spinnerBloodGroup.getCount(); i++) {
            if (spinnerBloodGroup.getItemAtPosition(i).equals(
                    mInfoVO.getBloodGroup())) {
                spinnerBloodGroup.setSelection(i);
            }
        }
        if(mInfoVO.getPreferredBloodBankId()!=0) {
            sp.edit().putLong(Constant.SP_KEY_PREFERRED_BLOOD_BANK_BRANCH_ID, mInfoVO.getPreferredBloodBankId()).commit();
        }

    }

    private void setRelationshipSpinner() {
        if(!TextUtils.isEmpty(mInfoVO.getRelationship())) {
            if (mInfoVO.getRelationship().equalsIgnoreCase("Self")) {
                parentRelationship.setVisibility(View.GONE);
            } else {
                parentRelationship.setVisibility(View.VISIBLE);
                for (int i = 0; i < addPatientRelationSpinner.getCount(); i++) {
                    if (addPatientRelationSpinner.getItemAtPosition(i).equals(
                            mInfoVO.getRelationship())) {
                        parentRelationship.setVisibility(View.VISIBLE);
                        addPatientRelationSpinner.setClickable(false);
                        addPatientRelationSpinner.setEnabled(false);
                    }
                }
            }
        }
    }

    public void setSpinnerValues() {
        if (mInfoVO.getRelationship()
                .equalsIgnoreCase(getString(R.string.self))) {
            AppUtil.setSpinnerValues(this, addPatientRelationSpinner,
                    R.array.add_patient_relation_self_entry);
            addPatientRelationSpinner.setEnabled(true);
        } else {
            addPatientRelationSpinner.setEnabled(true);
        }

    }

    public void setIsdValues()

    {
        if (mInfoVO.getPhoneNumber() != null) {
            phoneNoIsdCode = mInfoVO.getPhoneNumber().trim().split("-");
            for (int i = 0; i < countryIsdCodeSpinner.getCount(); i++) {

                if (countryIsdCodeSpinner.getItemAtPosition(i).equals(
                        phoneNoIsdCode[0])) {
                    countryIsdCodeSpinner.setSelection(i);
                }
                if (phoneNoIsdCode.length == 1) {
                    phoneNumber.setText(phoneNoIsdCode[0]);
                } else {
                    phoneNumber.setText(phoneNoIsdCode[1]);

                }

            }
        }
        if (mInfoVO.getAltPhoneNumber() != null) {

            alPhoneNoIsdCode = mInfoVO.getAltPhoneNumber().trim().split("-");
            for (int i = 0; i < countryAltIsdCodeNumber.getCount(); i++) {

                if (countryAltIsdCodeNumber.getItemAtPosition(i).equals(
                        alPhoneNoIsdCode[0])) {
                    countryAltIsdCodeNumber.setSelection(i);
                }
                if (alPhoneNoIsdCode.length == 1) {
                    homePhone.setText(alPhoneNoIsdCode[0]);
                } else if (alPhoneNoIsdCode.length == 2) {
                    homePhone.setText(alPhoneNoIsdCode[1]);

                }

            }
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            tvIsDobMandatory.setError(null);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            if (calendar.after(Calendar.getInstance())) {
                AppUtil.showDialog(ActivityEditProfile.this,
                        getString(R.string.dob_error_msg));
            } else {
                dateOfBirth = calendar.getTime();
                updateDisplay(dateOfBirth,showDobTextview);
            }
        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListenerLastDonatedBloodDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            if (calendar.after(Calendar.getInstance())) {
                AppUtil.showDialog(ActivityEditProfile.this,
                        getString(R.string.dob_error_msg));
            } else {
                dateOfLastUpdatedBlood = calendar.getTime();
                updateDisplay(dateOfLastUpdatedBlood,tvLastDonatedBloodDate);
            }
        }
    };

    public void setDateOfBirthIfExist() {
        if (mInfoVO.getDateOfBirth() != null) {
            c.setTime(mInfoVO.getDateOfBirth());
        }
    }
    public void setLastBloodDonatedDateIfExist() {
        if (mInfoVO.getLastBloodDonationDate() != null) {
            c1.setTime(mInfoVO.getLastBloodDonationDate());
        }
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    /**
     * Shows the progress UI and hides the list.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoaderStatusView.setVisibility(View.VISIBLE);
            mLoaderStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoaderStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            rootView.setVisibility(View.VISIBLE);
            rootView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rootView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoaderStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            rootView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(ActivityEditProfile.this);
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
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {
        if (mResponseVO != null) {

            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_SUCCESS:

                    AppUtil.showToast(ActivityEditProfile.this,
                            getString(R.string.patientAddedSuccessfully));
                    long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
                    for (PatientUserVO mPatientUserVO : mResponseVO.getPatientUserVO()) {
                        if (mPatientUserVO.getPatientId() == currentSelectedPatientId) {
                            DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                            try {
                                mDatabaseHandler.insertUserInfo(mPatientUserVO.getRecordVO(), mPatientUserVO.getBloodGroup(), mPatientUserVO.getPatientHandle());
                                mDatabaseHandler.insertUser(mPatientUserVO);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                    }
                    sp.edit().putLong(Constant.SP_KEY_PREFERRED_BLOOD_BANK_BRANCH_ID,0).commit();

                    try {
                        Intent startIntent = new Intent(ActivityEditProfile.this, StickyNotificationForeGroundService.class);
                        startIntent.setAction(Constant.ACTION.EMERGENCY_PREPAREDNESS);
                        startService(startIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                    break;
                case Constant.RESPONSE_FAILURE:
                    AppUtil.showToast(ActivityEditProfile.this,
                            getString(R.string.failure_msg));
                case Constant.RESPONSE_NOT_LOGGED_IN:
                    AppUtil.showToast(ActivityEditProfile.this,
                            getString(R.string.not_logged_in_error_msg));
                    break;
                case Constant.RESPONSE_SERVER_ERROR:
                    AppUtil.showToast(ActivityEditProfile.this,
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        if (mGoogleApiClient.isConnected()) {
            stopPeriodicUpdates();
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        startPeriodicUpdates();

    }

    private void startPeriodicUpdates() {
        Log.d(TAG, "********************* startLocationUpdates ********************* \n");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(300000);
        mLocationRequest.setFastestInterval(300000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } else {
                return;
            }

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        mLastLocation = location;
        Log.d(TAG, "getLatitude - " + mLastLocation.getLatitude());
        Log.d(TAG, "getLongitude - " + mLastLocation.getLongitude());

        if (location != null) {

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                if(addresses.size() > 0)
                {
                if (addresses.get(0).getMaxAddressLineIndex() > 0) {
                    String addressis = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    if (address.getText().toString().trim().length() == 0 && addressis != null) {
                        address.setText("" + addressis);
                        Log.d(TAG, "addressis - " + addressis);
                    }
                }

                String cityis = addresses.get(0).getLocality();
                if (city.getText().toString().trim().length() == 0 && cityis != null) {
                    city.setText("" + cityis);
                    Log.d(TAG, "cityis - " + cityis);
                }

                String stateis = addresses.get(0).getAdminArea();
                if (state.getText().toString().trim().length() == 0 && stateis != null) {
                    state.setText("" + stateis);
                    Log.d(TAG, "stateis - " + stateis);
                }

                String country = addresses.get(0).getCountryName();
                for (int i = 0; i < countrySpinner.getCount(); i++) {
                    if (countrySpinner.getItemAtPosition(i).toString().toLowerCase().equals(country.trim().toLowerCase())) {
                        countrySpinner.setSelection(i);
                        Log.d(TAG, "country - " + country);
                    }
                }

                String postalCode = addresses.get(0).getPostalCode();
                if (pinCode.getText().toString().trim().length() == 0 && postalCode != null) {
                    pinCode.setText("" + postalCode);
                    Log.d(TAG, "postalCode - " + postalCode);
                }
            }
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void stopPeriodicUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    private void populateBloodBanksAutoComplete() {
        DatabaseHandlerAssetHelper mDatabaseHandler = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        ItemAutoTextAdapter adapter = this.new ItemAutoTextAdapter(mDatabaseHandler);
        etPreferredBloodBank.setAdapter(adapter);
        etPreferredBloodBank.setOnItemClickListener(adapter);
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    class ItemAutoTextAdapter extends CursorAdapter
            implements AdapterView.OnItemClickListener {

        private DatabaseHandlerAssetHelper databaseHandler;


        public ItemAutoTextAdapter(DatabaseHandlerAssetHelper databaseHandler) {
            // Call the CursorAdapter constructor with a null Cursor.
            super(ActivityEditProfile.this, null);
            this.databaseHandler = databaseHandler;
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }

            Cursor cursor = databaseHandler.getMatchingStates(ActivityEditProfile.this,
                    (constraint != null ? constraint.toString() : null),Constant.ORG_TYPE_URL_BLOOD_BANK);

            return cursor;
        }


        @Override
        public String convertToString(Cursor cursor) {
            final int columnIndex = cursor.getColumnIndexOrThrow("display_name");
            final int columnIndex1 = cursor.getColumnIndexOrThrow("telephone");
            final String orgBranch = cursor.getString(columnIndex);
            final String telephone = cursor.getString(columnIndex1);
            String str = orgBranch + "," + (!TextUtils.isEmpty(telephone) ? telephone : "");
            return str;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final String text = convertToString(cursor);
            ((TextView) view).setText(text);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view =
                    inflater.inflate(android.R.layout.simple_dropdown_item_1line,
                            parent, false);

            return view;
        }


        @Override
        public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
            // Get the cursor, positioned to the corresponding row in the result set
            Cursor cursor = (Cursor) listView.getItemAtPosition(position);
            sp.edit().putLong(Constant.SP_KEY_PREFERRED_BLOOD_BANK_BRANCH_ID, cursor.getInt(cursor.getColumnIndexOrThrow("_id"))).commit();
            final int columnIndex = cursor.getColumnIndexOrThrow("display_name");
            final int columnIndex1 = cursor.getColumnIndexOrThrow("telephone");
            final String orgBranch = cursor.getString(columnIndex);
            final String telephone = cursor.getString(columnIndex1);
            String str = orgBranch + "," + (!TextUtils.isEmpty(telephone) ? "," + telephone : "");
            etPreferredBloodBank.setText(str);
            etPreferredOrgBranchStatus = true;

        }
    }
}
