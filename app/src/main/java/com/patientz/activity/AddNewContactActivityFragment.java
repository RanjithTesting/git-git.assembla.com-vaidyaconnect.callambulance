package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.KeyValueObject;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.adapters.CountryListAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.SyncUtil;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddNewContactActivityFragment extends Fragment {

    public AddNewContactActivityFragment() {
    }

    Button sendDetails;
    TextView showDobTextview, genderErrorIndicator, tvDateOfBirthMandatory;
    EditText firstName, companyName, lastName, email, phoneNumber, homePhone, address, city,
            familyHistory, habits, remarks, state, pinCode, organDonateEt;
    ImageView dateOfBirthImageView;
    ProgressDialog dialog;
    String[] phoneNoIsdCode, alPhoneNoIsdCode;
    Calendar c;
    String country;
    Spinner countrySpinner, countryIsdCodeSpinner, countryAltIsdCodeNumber,
            addPatientGenderSpinner, addPatientRelationSpinner,
            addPatientFinancialStatusSpinner, maritalStatusSpinner,
            foodHabitsSpinner;
    List<KeyValueObject> valuesToPrePopulate;
    CountryListAdapter countryAdapter;
    UserInfoVO mInfoVO;
    PatientUserVO mPatientUserVO;
    Date dateOfBirth;
    RadioGroup radioGroupDonateBlood;
    RadioButton radioYes, radioNo;
    public static final String TAG = "AddNewContactActivityFragment";
    LocationManager manager;
    private ScrollView mFormView;
    private View mStatusView;
    private RequestQueue mRequestQueue;
    private DatabaseHandler mDatabaseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDatabaseHandler = DatabaseHandler.dbInit(getActivity());

        mRequestQueue = AppVolley.getRequestQueue();
        c = Calendar.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_add_new_contact, null);
        mFormView = (ScrollView) view.findViewById(R.id.form_layout);
        mStatusView = view.findViewById(R.id.progress_view);
        tvDateOfBirthMandatory = (TextView) view
                .findViewById(R.id.tv_isGenderMandatory);

        sendDetails = (Button) view.findViewById(R.id.add_patient_button_next);
        companyName = (EditText) view.findViewById(R.id.et_company_name);

        firstName = (EditText) view.findViewById(R.id.add_patient_first_name);
        organDonateEt = (EditText) view.findViewById(R.id.organ_donation_et);
        lastName = (EditText) view.findViewById(R.id.add_patient_last_name);
        dateOfBirthImageView = (ImageView) view
                .findViewById(R.id.dob_imageview);
        email = (EditText) view.findViewById(R.id.add_patient_email);
        address = (EditText) view.findViewById(R.id.add_patient_address);
        city = (EditText) view.findViewById(R.id.add_patient_city);
        state = (EditText) view.findViewById(R.id.add_patient_state);
        familyHistory = (EditText) view
                .findViewById(R.id.add_patient_familyHistory);
        habits = (EditText) view.findViewById(R.id.add_patient_habits);
        remarks = (EditText) view.findViewById(R.id.add_patient_remarks);
        countryIsdCodeSpinner = (Spinner) view
                .findViewById(R.id.isdcode_spinner);
        countryAltIsdCodeNumber = (Spinner) view
                .findViewById(R.id.alt_isdcode_spinner);
        pinCode = (EditText) view.findViewById(R.id.add_patient_pincode);
        phoneNumber = (EditText) view.findViewById(R.id.add_patient_phoneno);
        homePhone = (EditText) view.findViewById(R.id.add_patient_alt_phoneno);
        countrySpinner = (Spinner) view
                .findViewById(R.id.add_patient_country_spinner);
        showDobTextview = (TextView) view.findViewById(R.id.show_dob_textview);
        addPatientRelationSpinner = (Spinner) view
                .findViewById(R.id.add_patient_relation_spinner);
        addPatientGenderSpinner = (Spinner) view
                .findViewById(R.id.add_patient_gender_spinner);
        addPatientFinancialStatusSpinner = (Spinner) view
                .findViewById(R.id.add_patient_financial_status_spinner);
        maritalStatusSpinner = (Spinner) view
                .findViewById(R.id.marital_status_spinner);
        AppUtil.setSpinnerValues(getActivity(), maritalStatusSpinner,
                R.array.marital_status_values);
        AppUtil.setSpinnerValues(getActivity(), addPatientGenderSpinner,
                R.array.gender);

        foodHabitsSpinner = (Spinner) view
                .findViewById(R.id.food_habits_spinner);
        AppUtil.setSpinnerValues(getActivity(), foodHabitsSpinner,
                R.array.food_habits_values);

        radioGroupDonateBlood = (RadioGroup) view
                .findViewById(R.id.radioDonateBlood);
        radioYes = (RadioButton) view.findViewById(R.id.radioYes);
        radioNo = (RadioButton) view.findViewById(R.id.radioNo);

        AppUtil.setSpinnerValues(getActivity(), addPatientRelationSpinner,
                R.array.add_patient_relation_entry);
        AppUtil.setSpinnerValues(getActivity(),
                addPatientFinancialStatusSpinner,
                R.array.add_patient_financial_status_spinner_with_select);
        AppUtil.setSpinnerValues(getActivity(), countryIsdCodeSpinner,
                R.array.select_country_isdCode);
        AppUtil.setSpinnerValues(getActivity(), countryAltIsdCodeNumber,
                R.array.select_country_isdCode);

        addPatientRelationSpinner.setPrompt(getString(R.string.relation));
        addPatientGenderSpinner
                .setPrompt(getString(R.string.gender));
        addPatientFinancialStatusSpinner
                .setPrompt(getString(R.string.financial_status));
        dateOfBirth = new Date();
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // loadData();
        dateOfBirthImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, mDateSetListener, c
                        .get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        sendDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (AppUtil.isOnline(getActivity())) {
                    try {
                        sendDataToWeb();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    AppUtil.showDialog(getActivity(),
                            getString(R.string.offlineMode));

                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public void updateDisplay(Date dateOfBirth) {
        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
        String formattedDateq = fmt.format(dateOfBirth);
        Log.e("formattedDateq--->", "" + formattedDateq);
        showDobTextview.setText(formattedDateq);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        inflater.inflate(R.menu.menu_add_new_contact, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case R.id.menu_save:
                onSavePressed();
                break;
            /*case R.id.menu_cancel:
                getActivity().getSupportFragmentManager().popBackStack();
                break;*/
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSavePressed() {

        if (AppUtil.isOnline(getActivity())) {
            try {
                sendDataToWeb();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            AppUtil.showDialog(getActivity(), getString(R.string.offlineMode));

        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }


    private StringRequest createUpdateRequest(final Map<String, String> params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.savePatientOwnerFromMobile;
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

                        AppUtil.showToast(getActivity(),
                                getString(R.string.networkError));
                    }
                } else {

                    AppUtil.showToast(getActivity(),
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
                            AppUtil.showErrorCodeDialog(getActivity());
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
                return AppUtil.addHeadersForApp(getActivity(), super.getHeaders());
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
            case Constant.RESPONSE_SUCCESS:
                ArrayList<PatientUserVO> mPatientUserVOs = mResponseVO.getPatientUserVO();
                for (PatientUserVO mPatientUserVO : mPatientUserVOs) {
                    try {
                        SyncUtil.saveUserRecord(getActivity(), mPatientUserVO, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Intent startIntent = new Intent(getActivity(), StickyNotificationForeGroundService.class);
                    startIntent.setAction(Constant.ACTION.EMERGENCY_PREPAREDNESS);
                    getActivity().startService(startIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppUtil.showToast(getActivity(),
                        getString(R.string.patientAddedSuccessfully));
                getActivity().finish();
                break;
            case Constant.RESPONSE_FAILURE:
                AppUtil.showToast(getActivity(),
                        getString(R.string.failure_msg));
            case Constant.RESPONSE_NOT_LOGGED_IN:
                AppUtil.showToast(getActivity(),
                        getString(R.string.not_logged_in_error_msg));
                break;
            case Constant.RESPONSE_SERVER_ERROR:
                AppUtil.showToast(getActivity(),
                        getString(R.string.server_error_msg));
                break;
            case Constant.TERMINATE:
                break;
            default:
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
            RadioButton selectedRadioButton = (RadioButton) getView()
                    .findViewById(selectedId);
            int[] countryCode = getResources().getIntArray(
                    R.array.select_country_id);
            country = String.valueOf(countryCode[countrySpinner
                    .getSelectedItemPosition()]);
            String mState = "";
            if (state.getText().toString().trim().length() != 0) {
                mState = "," + state.getText().toString().trim();
            }
            long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
            mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
            Map<String, String> params = new HashMap<String, String>();
            String patientId = String.valueOf(mPatientUserVO.getPatientId());
          /*  params.put(Constant.patientIdKey,patientId!=null?
                    patientId:"0");*/
            // params.put(Constant.patientIdKey,"null");
            params.put(Constant.firstName,
                    firstName.getText().toString());
            params.put(Constant.lastName,
                    lastName.getText().toString());
            params.put(Constant.ORGAN_DONATE,
                    organDonateEt.getText().toString());
            if (phoneNumber.getText().toString().trim().length() != 0) {
                params.put(Constant.phoneNumber,
                        countryIsdCodeSpinner.getSelectedItem().toString()
                                + "-" + phoneNumber.getText().toString());
            }
            if (homePhone.getText().toString().trim().length() != 0) {

                params.put(
                        Constant.ALT_PHONE_NO, countryAltIsdCodeNumber
                                .getSelectedItem().toString()
                                + "-"
                                + homePhone.getText().toString());
            }
            params.put(Constant.dateOfBirthKey,
                    showDobTextview.getText().toString());
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
            params.put(Constant.relation,
                    addPatientRelationSpinner.getSelectedItem().toString());
            if (maritalStatusSpinner.getSelectedItem().toString()
                    .equalsIgnoreCase("Select One")) {
                params.put(
                        Constant.MARITAL_STATUS, "");
            } else {
                params.put(
                        Constant.MARITAL_STATUS, maritalStatusSpinner
                                .getSelectedItem().toString());
            }
            if (foodHabitsSpinner.getSelectedItem().toString()
                    .equalsIgnoreCase("Select One")) {
                params.put(Constant.FOOD_HABITS,
                        "");
            } else {
                params.put(Constant.FOOD_HABITS,
                        foodHabitsSpinner.getSelectedItem().toString());
            }

            if (addPatientFinancialStatusSpinner.getSelectedItem().toString()
                    .equalsIgnoreCase("Select One")) {
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
                    .valueOf(AppUtil.getEventLogID(getActivity())));
            params.put(Constant.genderKey,
                    String.valueOf(addPatientGenderSpinner
                            .getSelectedItemPosition()));
            Log.d("check sending details: ", params.toString());
            showProgress(true);
            mRequestQueue.add(createUpdateRequest(params));
        } else {

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
            tvDateOfBirthMandatory.requestFocus();
            tvDateOfBirthMandatory.setError(getActivity().getString(
                    R.string.dob_mandatory));
            errorProneEdittext.add(tvDateOfBirthMandatory);
            result = false;
        } else {
            tvDateOfBirthMandatory.setError(null);
        }

        if (addPatientGenderSpinner.getSelectedItemPosition() == 0) {
            AppUtil.showDialog(getActivity(),
                    getString(R.string.gender_mandatory));
            result = false;
        }
        if (errorProneEdittext != null && errorProneEdittext.size() != 0) {
            Log.d(TAG, "error views>>" + errorProneEdittext.size());
            errorProneEdittext.get(0).requestFocus();
            errorProneEdittext.get(0).requestFocusFromTouch();
            mFormView.smoothScrollTo(0, errorProneEdittext.get(0).getBottom());
        }
        return result;
    }

    public void populateAlreadyAddedBasicValues() {
        setIsdValues();
        for (int i = 0; i < addPatientRelationSpinner.getCount(); i++) {
            if (addPatientRelationSpinner.getItemAtPosition(i).equals(
                    mInfoVO.getRelationship())) {
                addPatientRelationSpinner.setSelection(i);
                addPatientRelationSpinner.setEnabled(false);
            }
        }

        if (mInfoVO.getBloodDonation() != null) {

            if (mInfoVO.getBloodDonation().equalsIgnoreCase(Constant.YES)) {
                radioYes.setChecked(true);
            } else if (mInfoVO.getBloodDonation().equalsIgnoreCase(Constant.NO)) {
                radioNo.setChecked(true);
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

        if (mInfoVO.getDateOfBirth() != null) {
            dateOfBirth = mInfoVO.getDateOfBirth();
            updateDisplay(dateOfBirth);
        }
        if (mInfoVO.getFirstName() != null) {
            firstName.setText(mInfoVO.getFirstName());
        }
        if (mInfoVO.getCompanyName() != null) {
            companyName.setText(mInfoVO.getCompanyName());
        }
        if (mInfoVO.getOrganDonation() != null) {
            organDonateEt.setText(mInfoVO.getOrganDonation());
        }
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
    }

    public void setSpinnerValues() {
        if (mInfoVO.getRelationship()
                .equalsIgnoreCase(getString(R.string.self))) {
            AppUtil.setSpinnerValues(getActivity(), addPatientRelationSpinner,
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
            tvDateOfBirthMandatory.setError(null);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            if (calendar.after(Calendar.getInstance())) {
                AppUtil.showDialog(getActivity(),
                        getString(R.string.dob_error_msg));

            } else {
                dateOfBirth = calendar.getTime();
                updateDisplay(dateOfBirth);
            }
        }
    };

    public void setDateOfBirthIfExist() {
        if (mInfoVO.getDateOfBirth() != null) {
            c.setTime(mInfoVO.getDateOfBirth());
        }
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    public class Validator {
        public static final String phoneValidator = "^([+]|[0-9-])+$";
        public static final String PINCODE_PATTERN = "^([0-9-])+$";
        public static final String PIN_PATTERN = "^([a-zA-Z0-9])+$";
    }
}
