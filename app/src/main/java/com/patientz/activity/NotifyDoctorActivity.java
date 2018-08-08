package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.patientz.VO.OrganisationVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.VO.UserProfile;
import com.patientz.adapters.NotifyDoctorsListAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.SyncUtil;
import com.patientz.webservices.WebServiceUrls;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotifyDoctorActivity extends BaseActivity implements NotifyDoctorsListAdapter.RecyclerViewOnclick {
    private static final String TAG = "NotifyDoctorActivity";
    private LinearLayout rootView;
    private Spinner spinnerOrgsToolbar;
    private RecyclerView recyclerView;
    private TextView emptyListView;
    private LinearLayout progressBar;
    private TextView loadingStatusMessage;
    private DatabaseHandler dh;
    private EditText etFilterDoctor;
    private NotifyDoctorsListAdapter mAdapter;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        //toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spinnerOrgsToolbar = (Spinner) toolbar.findViewById(R.id.spinner_my_orgs);
        mRequestQueue = AppVolley.getRequestQueue();
        dh = DatabaseHandler.dbInit(getApplicationContext());

        findViews();


    }


    @Override
    protected void onResume() {
        super.onResume();
        final long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        try {
            populateSpinnerOrg(currentSelectedPatientId);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        spinnerOrgsToolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                OrganisationVO organisationVO = (OrganisationVO) adapterView.getAdapter().getItem(i);
                Log.d(TAG, "OrgId=" + organisationVO.getOrgId());
                Map<String, String> params = new HashMap<String, String>();
                params.put("patientId", String.valueOf(currentSelectedPatientId));
                OrganisationVO currentSelectedOrganisation = (OrganisationVO) spinnerOrgsToolbar.getSelectedItem();
                params.put("orgId", String.valueOf(currentSelectedOrganisation.getOrgId()));
                mRequestQueue.add(getDoctorsRequest(currentSelectedPatientId, currentSelectedOrganisation.getOrgId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void populateSpinnerOrg(long currentSelectedPatientId) throws Exception {
        ArrayList<OrganisationVO> orgsList = dh.getAllHospitalsAndClinic(currentSelectedPatientId);
        Log.d(TAG, "Orgs size=" + orgsList.size());
        //.spinnerOrgsToolbar.setAdapter(new NotifySpinnerAdapter(getApplicationContext(),orgsList));
        ArrayAdapter<OrganisationVO> dataAdapter = new ArrayAdapter<OrganisationVO>(this,
                R.layout.textview_style, orgsList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                v = super.getDropDownView(position, null, parent);
                TextView tv = (TextView) v.findViewById(R.id.tv);
                tv.setTextColor(ContextCompat.getColor(v.getContext(), R.color.black));
                return v;
            }
        };
        spinnerOrgsToolbar.setAdapter(dataAdapter);
    }

    private void findViews() {
        rootView = (LinearLayout) findViewById(R.id.root_view);
        spinnerOrgsToolbar = (Spinner) findViewById(R.id.spinner_my_orgs);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        emptyListView = (TextView) findViewById(R.id.empty_list_view);
        progressBar = (LinearLayout) findViewById(R.id.progressBar);
        loadingStatusMessage = (TextView) findViewById(R.id.loading_status_message);
        etFilterDoctor = (EditText) findViewById(R.id.edittext_filter_doctors);
        etFilterDoctor.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = etFilterDoctor.getText().toString().toLowerCase(Locale.getDefault()).trim();
                if (mAdapter != null) {
                    mAdapter.filter(text);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }


    private StringRequest getDoctorsRequest(final long currentSelectedPatientId, long orgId) {
        showProgress(true);
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.getAllDoctorsInOrganisation + "?patientId=" + currentSelectedPatientId + "&orgId=" + orgId;
        Log.d("Webservice:getDoctorsRequest:url ", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);
                Log.d(TAG, "Webservice:getDoctorsRequest:response" + response);
                try {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<ArrayList<UserProfile>>() {
                    }.getType();
                    ArrayList<UserProfile> doctorsList = gson.fromJson(
                            response, objectType);
                    if (doctorsList.size() > 0) {
                        etFilterDoctor.setVisibility(View.VISIBLE);
                        emptyListView.setVisibility(View.GONE);
                        mAdapter = new NotifyDoctorsListAdapter(NotifyDoctorActivity.this, doctorsList);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        emptyListView.setVisibility(View.VISIBLE);
                        etFilterDoctor.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    recyclerView.setAdapter(null);
                    emptyListView.setVisibility(View.VISIBLE);
                }

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
                            AppUtil.showErrorCodeDialog(NotifyDoctorActivity.this);
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
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            progressBar.setVisibility(View.VISIBLE);
            progressBar.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressBar.setVisibility(show ? View.VISIBLE
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
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            rootView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void getDoctorDetails(final UserProfile doctorProfile) {


        final long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        final OrganisationVO currentSelectedOrganisation = (OrganisationVO) spinnerOrgsToolbar.getSelectedItem();
        boolean alreadyANotifyingDoctor = false;
        try {
            ArrayList<EmergencyContactsVO> notifyingDoctorsList = dh.getAllContacts(currentSelectedPatientId, Constant.NOTIFYING_DOCTOR);
            for (EmergencyContactsVO notifyingDoctor : notifyingDoctorsList) {
                Log.d(TAG, "doctorProfile.getId()=" + doctorProfile.getId());
                Log.d(TAG, "notifyingDoctor.getUserProfileId()=" + notifyingDoctor.getUserProfileId());

                if (doctorProfile.getId() == notifyingDoctor.getUserProfileId()) {
                    alreadyANotifyingDoctor = true;
                    break;
                }
            }
            if (!alreadyANotifyingDoctor) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        NotifyDoctorActivity.this);
                builder.setMessage(getString(R.string.label_notify_doctor_dialog_part1) + " Dr." + doctorProfile.getFirstName() + " " + doctorProfile.getLastName() + getString(R.string.label_notify_doctor_dialog_part2));
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        HashMap<String, Object> upshotData = new HashMap<>();
                        upshotData.put(Constant.UpshotEvents.CT_MY_DOCTORS_ADD_CLICKED,true);
                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_MY_DOCTORS_ADD_CLICKED);
                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                        try {
                            mRequestQueue.add(requestDoctorToBeANotifyingDoctor(doctorProfile, currentSelectedPatientId, currentSelectedOrganisation.getOrgId(), doctorProfile.getId()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            } else {
                AppUtil.showToast(getApplicationContext(), "Dr." + doctorProfile.getFirstName() + " " + getString(R.string.label_already_a_notifying_doctor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StringRequest requestDoctorToBeANotifyingDoctor(final UserProfile doctorProfile, long currentSelectedPatientId, long orgId, long doctorProfileId) throws UnsupportedEncodingException {
        showProgress(true);
        //Log.d(TAG, "Webservice:getDoctorsRequest:params" + params.toString());
        String urlEncode = "?patientId=" + currentSelectedPatientId
                + "&patientUserProfileId=" + doctorProfileId
                + "&contactRole="
                + URLEncoder.encode(Constant.NOTIFYING_DOCTOR, "UTF-8")
                + "&relation=" + URLEncoder.encode(Constant.NOTIFYING_DOCTOR, "UTF-8");
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.saveCareGiverOrEC + urlEncode;
        Log.d("Webservice:getDoctorsRequest:url ", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Webservice:getDoctorsRequest:response" + response);
                ResponseVO responseVo = null;
                try {
                    if (response != null) {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<ResponseVO>() {
                        }.getType();

                        responseVo = gson.fromJson(
                                response, objectType);

                        if (responseVo != null) {
                            if (responseVo.getCode() == Constant.RESPONSE_SUCCESS) {
                                HashMap<String, Object> upshotData = new HashMap<>();
                                upshotData.put(Constant.UpshotEvents.DOCTOR_ADDED, true);
                                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.DOCTOR_ADDED);
                                Log.d(TAG,"upshot data="+upshotData.entrySet());
                                ArrayList<PatientUserVO> mPatientUserVOs = responseVo.getPatientUserVO();
                                if (mPatientUserVOs != null) {
                                    for (PatientUserVO mPatientUserVO : mPatientUserVOs) {
                                        try {
                                            SyncUtil.saveUserRecord(getApplicationContext(), mPatientUserVO, null);
                                            break;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                AppUtil.showToast(getApplicationContext(), getString(R.string.label_doctor_notified_successfully_part1) + " " + doctorProfile.getFirstName() + " " + doctorProfile.getLastName() + " " + getString(R.string.label_doctor_notified_successfully_part2));

                            } else if (responseVo.getCode() == 104) {
                                AppUtil.showToast(getApplicationContext(), "Dr." + doctorProfile.getFirstName() + " " + getString(R.string.label_already_a_notifying_doctor));

                            }
                        } else {
                        }
                    } else {
                    }
                } catch (JsonParseException e) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.label_no_more_doctors_to_add));
                }

                showProgress(false);
                finish();
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
                            AppUtil.showErrorCodeDialog(NotifyDoctorActivity.this);
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
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }
}
