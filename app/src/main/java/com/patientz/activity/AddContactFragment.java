package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.patientz.VO.EmergencyContactsVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.SyncUtil;
import com.patientz.utils.Validator;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddContactFragment extends Fragment {
    private static final String TAG = "AddContactFragment";
    Button addECButton;
    String mode = "";
    long contactId;
    String[] phoneNoIsdCode;
    EmergencyContactsVO selectedContact;
    String contact_type;
    Spinner ecRelationSpinner, ecRoleSpinner, spinnerCountry,
            countryIsdCodeSpinner;
    private EditText editTextFirstname, editTextLastname, editTextEmail,
            editTextPhoneNumber, editTextAddress, editTextPincode,
            editTextCity, editTextState;
    EmergencyContactsVO mContactsVO;
    PatientUserVO mPatientUserVO;
    private RequestQueue mRequestQueue;
    private String ecCity, ecState, ecCountry, ecAddress, ecPincode,
            ecRelation;
    private View mFormView;
    private View mStatusView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRequestQueue = AppVolley.getRequestQueue();
    }

    @Override
    public void onResume() {
        //trackAnalytics();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_ec, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            loadValues();

            if (getActivity().getIntent() != null) {
                if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra("name"))) {
                    editTextFirstname.setText(getActivity().getIntent().getStringExtra("name"));
                }
                if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra("number"))) {
                    editTextPhoneNumber.setText(getActivity().getIntent().getStringExtra("number"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadValues() throws Exception {
        // Mode = E for Edit and A for Add
        initializeLayoutVariables();
        onClickAddECButton();
    }

    private void onClickAddECButton() {
        // TODO Auto-generated method stub
        addECButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    addEmergencyContact();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void onSavePressed() {
        if (AppUtil.isOnline(getActivity())) {
            try {
                addEmergencyContact();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            AppUtil.showDialog(getActivity(), getString(R.string.offlineMode));

        }
    }

    private void initializeLayoutVariables() throws Exception {
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
        mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
        editTextFirstname = (EditText) getView()
                .findViewById(R.id.ec_firstname);
        editTextLastname = (EditText) getView().findViewById(R.id.ec_lastname);
        editTextEmail = (EditText) getView().findViewById(R.id.ec_email);
        editTextPhoneNumber = (EditText) getView().findViewById(
                R.id.ec_phonenumber);
        editTextAddress = (EditText) getView().findViewById(R.id.ec_address);
        editTextPincode = (EditText) getView().findViewById(R.id.ec_pincode);
        editTextCity = (EditText) getView().findViewById(R.id.ec_city);
        editTextState = (EditText) getView().findViewById(R.id.ec_state);
        spinnerCountry = (Spinner) getView().findViewById(R.id.spinner_country);
        ecRelationSpinner = (Spinner) getView().findViewById(R.id.ec_relation);
        editTextFirstname.requestFocus();

        countryIsdCodeSpinner = (Spinner) getView().findViewById(
                R.id.isdcode_spinner);
        AppUtil.setSpinnerValues(getActivity(), countryIsdCodeSpinner,
                R.array.select_country_isdCode);

        addECButton = (Button) getView().findViewById(R.id.ec_add_button);
        mFormView = getView().findViewById(R.id.form_layout);
        mStatusView = getView().findViewById(R.id.progress_view);
    }

    public void addEmergencyContact() throws Exception {
        // TODO Auto-generated method stub

        Boolean result = validateFields();
        if (result == true) {

            editTextPhoneNumber.setError(null);
            editTextPincode.setError(null);
            editTextEmail.setError(null);
            editTextLastname.setError(null);
            editTextFirstname.setError(null);

            ecAddress = editTextAddress.getText().toString().trim();
            ecPincode = editTextPincode.getText().toString().trim();
            ecCity = editTextCity.getText().toString().trim();
            ecState = editTextState.getText().toString().trim();
            String mState = "";
            if (ecState.length() != 0) {
                mState = "," + ecState;
            }
            int[] countryCode = getResources().getIntArray(
                    R.array.select_country_id);
            ecCountry = String.valueOf(countryCode[spinnerCountry
                    .getSelectedItemPosition()]);
            ecRelation = ecRelationSpinner.getSelectedItem().toString();
            Map<String, String> params = new HashMap<String, String>();

            params.put(Constant.firstName,
                    editTextFirstname.getText().toString().trim());
            params.put(Constant.lastName,
                    editTextLastname.getText().toString().trim());
            params.put("email", editTextEmail
                    .getText().toString().trim());
            if (editTextPhoneNumber.getText().toString().trim().length() != 0) {
                params.put(Constant.phoneNumber,
                        countryIsdCodeSpinner.getSelectedItem().toString()
                                + "-"
                                + editTextPhoneNumber.getText().toString()
                                .trim());
            }
            params.put(Constant.address,
                    ecAddress);
            params.put(Constant.pinCode,
                    ecPincode);
            params.put(Constant.city, ecCity
                    + mState + ","
                    + spinnerCountry.getSelectedItem().toString());
            params.put(Constant.state, ecState);
            params.put("country", ecCountry);
            params.put(Constant.relation,
                    ecRelation);
            params.put(
                    Constant.emergencyContactId, String.valueOf(contactId));
            params.put(Constant.lastSyncId,
                    String.valueOf(AppUtil.getEventLogID(getActivity())));
            showProgress(true);
            mRequestQueue.add(createUpdateRequest(params));
            Log.d("TAG", params.toString());
        } else {

        }
    }

    private void inflateEditView(long contactId) {
        ArrayList<EmergencyContactsVO> mContactsVO = mPatientUserVO
                .getRecordVO().getEmergencyContactVO();
        for (int i = 0; i < mContactsVO.size(); i++) {
            if (mContactsVO.get(i).getContactId() == contactId) {
                selectedContact = mContactsVO.get(i);
                break;
            }
        }
        setIsdValues();

        if (selectedContact != null) {
            addECButton.setText(getString(R.string.save));

            populateEditText(
                    editTextFirstname,
                    selectedContact.getFirstName() != null ? selectedContact
                            .getFirstName() : "");
            populateEditText(
                    editTextLastname,
                    selectedContact.getLastName() != null ? selectedContact
                            .getLastName() : "");
            populateEditText(
                    editTextEmail,
                    selectedContact.getEmailID() != null ? selectedContact
                            .getEmailID() : "");

            populateEditText(
                    editTextAddress,
                    selectedContact.getAddress() != null ? selectedContact
                            .getAddress() : "");
            populateEditText(
                    editTextPincode,
                    selectedContact.getPinCode() != null ? selectedContact
                            .getPinCode() : "");
            populateEditText(
                    editTextCity,
                    selectedContact.getCity() != null ? selectedContact
                            .getCity() : "");
            populateEditText(
                    editTextState,
                    selectedContact.getState() != null ? selectedContact
                            .getState() : "");
        }
    }

    public void setIsdValues() {
        if (selectedContact.getPhoneNumber() != null) {
            phoneNoIsdCode = selectedContact.getPhoneNumber().trim().split("-");
            for (int i = 0; i < countryIsdCodeSpinner.getCount(); i++) {

                if (countryIsdCodeSpinner.getItemAtPosition(i).equals(
                        phoneNoIsdCode[0])) {
                    countryIsdCodeSpinner.setSelection(i);
                }
                if (phoneNoIsdCode.length == 1) {
                    editTextPhoneNumber.setText(phoneNoIsdCode[0]);
                } else {
                    editTextPhoneNumber.setText(phoneNoIsdCode[1]);
                }
            }
        }
    }

    public void populateEditText(EditText et, String value) {
        et.setText(value);
    }


    private StringRequest createUpdateRequest(final Map<String, String> params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.saveUnregisteredEC
                + mPatientUserVO.getPatientId();
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
                    switch (networkResponse.statusCode) {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            if(isAdded()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setCancelable(false);
                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View view = inflater.inflate(R.layout.fragment_server_busy_dialog, null);
                                builder.setView(view);
                                TextView mButton = (TextView) view.findViewById(R.id.bt_done);
                                final AlertDialog mAlertDialog = builder.create();
                                mButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAlertDialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            }
                            break;
                    }
                } else {
                    if(isAdded())
                    {
                        AppUtil.showErrorDialog(getActivity(),error);
                    }
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
                if(isAdded()) {

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
                }
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
        if(isAdded()) {
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

    public boolean validateFields() {
        Boolean result = true;
        String phoneError = "";
        String emailError = "";
        String pincodeError = "";
        String ecPhoneNumber = editTextPhoneNumber.getText().toString().trim();
        String ecFirstName = editTextFirstname.getText().toString().trim();
        String ecLastName = editTextLastname.getText().toString().trim();
        if (ecFirstName.length() == 0) {
            editTextFirstname
                    .setError(getString(R.string.fname_mandatory_error_msg));
            result = false;
        } else if ((ecFirstName.length() < 3 || ecFirstName.length() > 25)) {
            editTextFirstname
                    .setError(getString(R.string.fname_length_error_msg));
            result = false;
        }
        if (ecLastName.length() == 0) {
            editTextLastname
                    .setError(getString(R.string.lname_mandatory_error_msg));

            result = false;
        } else if ((ecLastName.length() < 1 || ecLastName.length() > 25)) {
            editTextLastname
                    .setError(getString(R.string.lname_length_error_msg));
            result = false;
        }
        if (ecPhoneNumber.length() == 0) {
            editTextPhoneNumber
                    .setError(getString(R.string.phone_number_mandatory));
        } else {
            if (ecPhoneNumber.matches(Validator.phoneValidator) == false) {
                phoneError = getString(R.string.phoneno_invalid);
                editTextPhoneNumber.setError(phoneError);
                result = false;
            }
            if (ecPhoneNumber.length() < 10 || ecPhoneNumber.length() > 13) {

                phoneError = phoneError
                        + getString(R.string.phoneno_length_error_msg);
                editTextPhoneNumber.setError(phoneError);
                result = false;
            }
        }
        return result;
    }

}
