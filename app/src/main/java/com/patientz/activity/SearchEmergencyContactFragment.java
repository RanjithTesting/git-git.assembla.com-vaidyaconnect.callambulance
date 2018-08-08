package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.patientz.adapters.EmergencyContactsAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.Validator;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchEmergencyContactFragment extends ListFragment {

    public SearchEmergencyContactFragment() {

    }

    private static final String TAG = "SearchCareGiverFragment";
    Button searchContact;
    private String searchTerm, searchBy;
    private static String contactRole = "Emergency Contact";
    private ArrayList<EmergencyContactsVO> ecList = new ArrayList<EmergencyContactsVO>();
    EmergencyContactsVO mSelectedContact;
    private EditText editTextSearchTerm;
    private Spinner searchBySpinner, isdSpinner;
    private PatientUserVO mPatientUserVO;
    InputMethodManager imm;
    TextView message;
    private RequestQueue mRequestQueue;
    private View mSearchView;
    private View mLoaderStatusView;
    boolean noResult = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
        try {
            mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
            mRequestQueue = AppVolley.getRequestQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_care_giver, container, false);
        mLoaderStatusView = v.findViewById(R.id.loading_status);
        message = (TextView) v.findViewById(R.id.message);

        searchContact = (Button) v.findViewById(R.id.searchContact_button);

        if (noResult) {
            setMessage(ecList.size());
        }

        editTextSearchTerm = (EditText) v
                .findViewById(R.id.search_contact_email);

        imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        searchBySpinner = (Spinner) v
                .findViewById(R.id.search_contact_searchBySpinner);
        isdSpinner = (Spinner) v.findViewById(R.id.search_contact_isdSpinner);
        searchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Boolean result = validateSelectedValue();
                if (AppUtil.isOnline(getActivity())) {
                    if (result) {
                        HashMap<String, Object> upshotData = new HashMap<>();
                        upshotData.put(Constant.UpshotEvents.CT_EC_SEARCH_CLICKED,true);
                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_EC_SEARCH_CLICKED);
                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                        showProgress(true);
                        searchQuery(v);
                    }
                } else {

                    AppUtil.showDialog(getActivity(),
                            getString(R.string.offlineMode));

                }
                // TODO Auto-generated method stub

            }
        });
        searchBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                if (searchBySpinner.getSelectedItem().toString()
                        .equalsIgnoreCase(getString(R.string.phone_number))) {
                    Log.d("SCF", "inside setOnItemSelectedListener if part");
                    editTextSearchTerm.setText("");
                    editTextSearchTerm.setHint(getString(R.string.searchBy)
                            + searchBySpinner.getSelectedItem().toString());

                    isdSpinner.setVisibility(View.VISIBLE);
                    editTextSearchTerm.setInputType(0);
                    editTextSearchTerm.setInputType(InputType.TYPE_CLASS_PHONE);
                } else {
                    Log.d("SCF", "inside setOnItemSelectedListener else part");
                    editTextSearchTerm.setText("");
                    editTextSearchTerm.setInputType(0);
                    editTextSearchTerm
                            .setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    editTextSearchTerm
                            .setHint(getString(R.string.searchContactBy)
                                    + searchBySpinner.getSelectedItem()
                                    .toString());
                    isdSpinner.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Log.d("SCF", "inside onNothingSelected");

            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mSearchView = getListView();
        super.onViewCreated(view, savedInstanceState);
    }


    private boolean validateSelectedValue() {
        // TODO Auto-generated method stub
        Boolean validationResult = true;
        String phoneError = "";
        String selectedSpinnerValue = searchBySpinner.getSelectedItem()
                .toString().trim();
        String enteredEditTextVal = editTextSearchTerm.getText().toString()
                .trim();
        if (selectedSpinnerValue
                .equalsIgnoreCase(getString(R.string.phone_number))) {
            if (enteredEditTextVal.matches(Validator.phoneValidator) == false) {
                phoneError = getString(R.string.phoneno_invalid);
                editTextSearchTerm.setError(phoneError);
                validationResult = false;
            }
            if (enteredEditTextVal.length() < 10
                    || enteredEditTextVal.length() > 13) {

                phoneError = phoneError
                        + getString(R.string.phoneno_length_error_msg);
                editTextSearchTerm.setError(phoneError);
                validationResult = false;
            }
        } else if (selectedSpinnerValue
                .equalsIgnoreCase(getString(R.string.email))) {
            if (!(AppUtil.isValidEmail(enteredEditTextVal))) {
                editTextSearchTerm
                        .setError(getString(R.string.email_error_msg));
                validationResult = false;
            }
        }
        return validationResult;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        mSelectedContact = (EmergencyContactsVO) getListAdapter().getItem(
                position);
        ImageView ecInviteButton = (ImageView) v
                .findViewById(R.id.search_emergency_accept);

        ecInviteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "item clicked : " + AppUtil.convertToJsonString(mSelectedContact));
            }
        });
    }

    public void searchQuery(View view) {

        searchTerm = editTextSearchTerm.getText().toString().trim();
        searchBy = searchBySpinner.getSelectedItem().toString();

        if (searchTerm.length() != 0) {
            // new searchAsync(searchTerm, searchBy).execute();
            Map<String, String> params = new HashMap<String, String>();
            String searchResult = "";
            String info = null;
            Log.d("isdSpinner.getSelectedItem().toString()", isdSpinner
                    .getSelectedItem().toString());
            info = "?patientId="
                    + mPatientUserVO.getPatientId()
                    + "&searchBy="
                    + URLEncoder.encode(searchBy)
                    + "&isdCode="
                    + isdSpinner.getSelectedItem().toString()
                    .replace("+", "") + "&searchTerm="
                    + URLEncoder.encode(searchTerm) + "&contactRole="
                    + contactRole;
            Log.d(TAG, info + "\nParams:" + params);
            mRequestQueue.add(createSearchRequest(info));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.enter_search_text))
                    .setNegativeButton(getString(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                }
                            }).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private StringRequest createSearchRequest(final String params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.searchCareGiverOrEC + params.replaceAll(" ", "+");

        com.patientz.utils.Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getActivity(), super.getHeaders());
            }
        };
        /*For slow webservice response un-comment this*/
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {

        if (mResponseVO != null) {

            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_SUCCESS:
                    if (mResponseVO != null) {
                        int responseCode = (int) mResponseVO.getCode();
                        switch (responseCode) {
                            case 00:
                                ecList = new ArrayList();
                                ecList = mResponseVO.getSearchList();
                                EmergencyContactsAdapter mEmergencyContactsAdapter = new EmergencyContactsAdapter(
                                        getActivity(), ecList,
                                        mPatientUserVO.getPatientId(), contactRole);
                                setListAdapter(mEmergencyContactsAdapter);
                                setMessage(ecList.size());
                                noResult = true;
                                break;
                            case 1012:
                            case 1013:
                            case 501:
                                AppUtil.showDialog(getActivity(), mResponseVO.getResponse());
                                break;
                            default:
                                AppUtil.showDialog(getActivity(),
                                        getString(R.string.not_logged_in_error_msg));
                                break;
                        }
                    } else {
                        AppUtil.showDialog(getActivity(),
                                getString(R.string.networkError));
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
                    //finish();
                    break;
                default:
                    break;
            }
        }
        showProgress(false);
    }

    public void setMessage(int size) {
        if (size == 0) {
            message.setText(getActivity().getString(R.string.noContactMsgForEC));
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.INVISIBLE);
        }

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

            mSearchView.setVisibility(View.VISIBLE);
            mSearchView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mSearchView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoaderStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mRequestQueue!=null)
        {
            mRequestQueue.cancelAll(Constant.CANCEL_VOLLEY_REQUEST);

        }
    }
}
