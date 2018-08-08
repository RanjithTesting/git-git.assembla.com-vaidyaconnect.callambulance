package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.Validator;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddNewCareGiverFragment extends Fragment {
    private static final String TAG = "AddNewCareGiverFragment";
    EditText fName, lName, email, phoneNo;
    Map<String, String> params = new HashMap<String, String>();

    Button inviteButton;
    Spinner countryIsdCodeSpinner;
    ProgressDialog dialog;
    String number;
    PatientUserVO mPatientUserVO;
    long patientId;
    String role;
    String toMessage;
    RequestQueue mRequestQueue;
    View mLoaderStatusView, rootView;
    private DatabaseHandler mDatabaseHandler;

    public AddNewCareGiverFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        role = "Care Giver";
        mDatabaseHandler  = DatabaseHandler.dbInit(getActivity());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
        try {
            mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
            patientId = mPatientUserVO.getPatientId();
            mRequestQueue = AppVolley.getRequestQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_new_care_giver, null);
        mLoaderStatusView = v.findViewById(R.id.loading_status);
        rootView = v.findViewById(R.id.form_layout);
        fName = (EditText) v.findViewById(R.id.fName_edittext);
        lName = (EditText) v.findViewById(R.id.lname_edittext);
        phoneNo = (EditText) v.findViewById(R.id.phone_number_edittext);
        email = (EditText) v.findViewById(R.id.email_edittext);
        inviteButton = (Button) v.findViewById(R.id.submit);
        countryIsdCodeSpinner = (Spinner) v.findViewById(R.id.isdcode_spinner);
        AppUtil.setSpinnerValues(getActivity(), countryIsdCodeSpinner,
                R.array.select_country_isdCode);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        inviteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
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
        if (getActivity().getIntent() != null) {
            if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra("name"))) {
                fName.setText(getActivity().getIntent().getStringExtra("name"));
            }
            if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra("number"))) {
                phoneNo.setText(getActivity().getIntent().getStringExtra("number"));
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void sendDataToWeb() {
        Boolean result = validatePatientBasicFields();
        if (result == true) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.CT_CG_UNREGISTERED_CONTACT_SUBMIT_CLICKED,true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_CG_UNREGISTERED_CONTACT_SUBMIT_CLICKED);
            Log.d(TAG,"upshot data="+upshotData.entrySet());
            setInviteMsg();
            params.put("patientId", String
                    .valueOf(patientId));
            params.put("firstName", fName
                    .getText().toString());
            params.put("lastName", lName
                    .getText().toString());
            params.put("phoneNumber",
                    countryIsdCodeSpinner.getSelectedItem().toString()
                            + "-" + phoneNo.getText().toString().trim());
            params.put("email", email.getText()
                    .toString());
            params.put("lastSyncId", String
                    .valueOf(AppUtil.getEventLogID(getActivity())));
            params.put("roleOfInvite", role);
            Log.d("check sending details: ", params.toString());
            showProgress(true);
            mRequestQueue.add(createAddCareGiverRequest(params));
        } else {

        }
    }

    private void setInviteMsg() {
        toMessage = "Hi "
                + AppUtil.convertToCamelCase(fName.getText().toString()
                .trim())
                + " "
                + AppUtil.convertToCamelCase(lName.getText().toString()
                .trim())
                + " "
                + getString(R.string.CGInvitationMsg)
                + getString(R.string.regards)
                + AppUtil.convertToCamelCase(mPatientUserVO.getFirstName()
                .toString().trim()
                + " "
                + AppUtil.convertToCamelCase(mPatientUserVO
                .getLastName().toString().trim()));
    }

    public void sendMsg() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"
                + phoneNo.getText().toString().trim()));
        sendIntent.putExtra("sms_body", toMessage);
        startActivity(sendIntent);
    }

    public boolean validatePatientBasicFields() {
        Boolean result = true;
        String phoneError = "";
       /* if ((phoneNo.getText().toString().length() != 0))

        {*/
        if (phoneNo.getText().toString().matches(Validator.phoneValidator) == false) {
            phoneError = getString(R.string.phoneno_invalid);
            phoneNo.setError(phoneError);
            result = false;
        }
        if ((phoneNo.getText().toString().length() < 10 || phoneNo
                .getText().toString().length() > 13)) {

            phoneError = phoneError
                    + getString(R.string.phoneno_length_error_msg);
            phoneNo.setError(phoneError);
            result = false;
        }
        //    }

        if ((fName.getText().toString().trim().length() < 3 || fName.getText()
                .toString().trim().length() > 25)) {
            fName.setError(getString(R.string.fname_length_error_msg));
            result = false;
        }
        if ((lName.getText().toString().trim().length() < 3 || lName.getText()
                .toString().trim().length() > 25)) {
            lName.setError(getString(R.string.lname_length_error_msg));
            result = false;
        }
        if (email.getText().toString().length() > 0)
        {
            if(!(AppUtil.isValidEmail(email.getText().toString().trim())))
            {
                email.setError(getString(R.string.email_error_msg));
                result = false;
            }

        }
        return result;
    }


    private StringRequest createAddCareGiverRequest(final Map<String, String> params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.savePatientDoctorCGInvite;
        com.patientz.utils.Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                com.patientz.utils.Log.d(TAG, "******************* onErrorResponse ******************* \n");
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            if(isAdded())
                            {
                            AppUtil.showErrorCodeDialog(getActivity());
                            }
                            break;
                    }
                }else
                {
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

        if (mResponseVO != null) {

            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_SUCCESS:
                    try {
                        Intent startIntent = new Intent(getActivity(), StickyNotificationForeGroundService.class);
                        startIntent.setAction(Constant.ACTION.EMERGENCY_PREPAREDNESS);
                        getActivity().startService(startIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    AppUtil.showToast(getActivity(),
                            getString(R.string.request_care_giver));
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
                    getActivity().finish();
                    break;
                default:
                    break;
            }
        }
        showProgress(false);
    }


    /**
     * Shows the progress UI and hides the list.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if(isAdded()) {
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
    }
}
