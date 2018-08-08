package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.OrganisationVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.adapters.SpinnerAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
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


public class AddEmployerActivity extends BaseActivity implements View.OnClickListener{


    private static final String TAG ="AddEmployerActivity" ;
    private TextView tvEmployeeId;
    private EditText etEmployeeId;
    private TextView tvOrgBranch;
    private Spinner spinnerOrgBranch;
    private TextView tvOfficialEmailId;
    private EditText etOfficialEmailId;
    private TextView tvDesignation;
    private EditText etDesignation;
    private TextView tvDepartment,tvOrgBranchErrorDisplay;
    private EditText etDepartment;
    private TextView tvManagerName;
    private EditText etManagerName;
    private LinearLayout llManagerPno;
    private Spinner isdcodeSpinner;
    private EditText etManagerPno;
    private Button btAddEmployer;
    private LinearLayout progressBar,formLayout;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.tv_roboto_regular_white_normal);
        findViews();
        mIntent=getIntent();
        try {
            populateSpinnerOrg();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateSpinnerOrg() throws Exception {
        DatabaseHandlerAssetHelper dh= DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        ArrayList<OrgBranchVO> orgBranches = dh.getOrgBranches(mIntent.getLongExtra("orgId",0));
        Log.d(TAG, "Org Branches size=" + orgBranches.size());
        OrgBranchVO mOrgBranchVO=new OrgBranchVO();
        mOrgBranchVO.setDisplayName("Select");
        orgBranches.add(0,mOrgBranchVO);
        SpinnerAdapter dataAdapter=new SpinnerAdapter(getApplicationContext(),orgBranches);
        spinnerOrgBranch.setAdapter(dataAdapter);
    }

    private void findViews() {
        tvEmployeeId = (TextView)findViewById( R.id.tv_employee_id );
        etEmployeeId = (EditText)findViewById( R.id.et_employee_id );
        spinnerOrgBranch = (Spinner)findViewById( R.id.spinner_org_branch );
        tvOfficialEmailId = (TextView)findViewById( R.id.tv_official_email_id );
        etOfficialEmailId = (EditText)findViewById( R.id.et_official_email_id );
        tvDesignation = (TextView)findViewById( R.id.tv_designation );
        etDesignation = (EditText)findViewById( R.id.et_designation );
        tvDepartment = (TextView)findViewById( R.id.tv_department );
        etDepartment = (EditText)findViewById( R.id.et_department );
        tvManagerName = (TextView)findViewById( R.id.tv_manager_name );
        tvOrgBranchErrorDisplay = (TextView)findViewById( R.id.tvOrgBranchErrorDisplay );

        etManagerName = (EditText)findViewById( R.id.et_manager_name );
        llManagerPno = (LinearLayout)findViewById( R.id.ll_manager_pno );
        isdcodeSpinner = (Spinner)findViewById( R.id.isdcode_spinner );
        AppUtil.setSpinnerValues(this, isdcodeSpinner,
                R.array.select_country_isdCode);
        etManagerPno = (EditText)findViewById( R.id.et_manager_pno );
        btAddEmployer = (Button)findViewById( R.id.bt_add_employer );
        btAddEmployer.setOnClickListener(this);
        progressBar = (LinearLayout)findViewById(R.id.progress_view);
        formLayout = (LinearLayout)findViewById(R.id.form_layout);


    }
    private boolean isValidationSatisfied()
    {
        boolean result=true;
        if(TextUtils.isEmpty(etEmployeeId.getText().toString()))
        {
            etEmployeeId.setError(getString(R.string.employee_id_mandatory_msg));
            result=false;
        }
        if(spinnerOrgBranch.getSelectedItemPosition()==0)
        {
          tvOrgBranchErrorDisplay.setError(getString(R.string.org_branch_mandatory_msg));
            result=false;
        }
        if(etOfficialEmailId.getText().toString().length()!=0)
        {
            if(!AppUtil.isValidEmail(etOfficialEmailId.getText().toString()))
            {
                etOfficialEmailId.setError(getString(R.string.email_error_msg));
                result=false;
            }
        }
        if(etManagerPno.getText().toString().length()!=0)
        {
            if (etManagerPno.getText().toString().matches(Validator.phoneValidator) == false || etManagerPno.getText().toString().length()!=10) {
                etManagerPno.setError(getString(R.string.phone_validation_error));
                result=false;
            }
        }

        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        spinnerOrgBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvOrgBranchErrorDisplay.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private StringRequest createAddEmployerRequest() {
        showProgress(true);
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.addEmployer;
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                if (response != null) {
                    try {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<OrganisationVO>() {
                        }.getType();
                        OrganisationVO organisationVO = gson.fromJson(
                                    response, objectType);
                        DatabaseHandler mDatabaseHandler=DatabaseHandler.dbInit(getApplicationContext());
                        mDatabaseHandler.insertUserOrgRecord(organisationVO,AppUtil.getCurrentSelectedPatientId(getApplicationContext()));
                        finish();

                    } catch (Exception e) {
                        Log.e(TAG, "Exception Parsing Response VO : "
                                + e.getMessage());
                    }
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddEmployerActivity.this);
                            builder.setCancelable(false);
                            LayoutInflater inflater =getLayoutInflater();
                            View view = inflater.inflate(R.layout. fragment_server_busy_dialog, null);
                            builder.setView(view);
                            TextView mButton= (TextView)view.findViewById(R.id.bt_done);
                            final AlertDialog mAlertDialog=builder.create();
                            mButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlertDialog.dismiss();
                                }
                            });
                            mAlertDialog.show();
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

    private Map<String,String> getParamsToPass()  {
        Map<String,String> params= new HashMap<String, String>();
        long patientId= 0;
        try {
            patientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("patientId", String.valueOf(patientId));
        params.put("orgId", String.valueOf(mIntent.getLongExtra("orgId",0)));
        params.put("employeeNumber", etEmployeeId.getText().toString());

                if(spinnerOrgBranch.getSelectedItemPosition()!=0)
                {
                    OrgBranchVO mOrgBranchVO=(OrgBranchVO)spinnerOrgBranch.getSelectedItem();
                    Log.d(TAG,"orgBranchid="+mOrgBranchVO.getOrgBranchId());
                    params.put("orgBranchId", String.valueOf(mOrgBranchVO.getOrgBranchId()));

                }
        params.put("email", etOfficialEmailId.getText().toString());
        params.put("designation", etDesignation.getText().toString());
        params.put("department", etDepartment.getText().toString());
        params.put("managerName", etManagerName.getText().toString());
        params.put("managerPhone", etManagerPno.getText().toString());
        params.put("managerPhoneISDCode",isdcodeSpinner.getSelectedItem().toString());
        Log.d(TAG,"Params="+params);
        return params;
    }

    private void responseHandler(ResponseVO mResponseVO) {
        int code = (int) mResponseVO.getCode();
        switch (code) {
            case Constant.RESPONSE_SUCCESS:
                ArrayList<PatientUserVO> mPatientUserVOs = mResponseVO.getPatientUserVO();
                for (PatientUserVO mPatientUserVO : mPatientUserVOs) {
                    try {
                        SyncUtil.saveUserRecord(getApplicationContext(), mPatientUserVO, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.patientAddedSuccessfully));
                finish();
                break;
            default:
                AppUtil.showToast(getApplicationContext(),
                        getString(R.string.failedToAddEmployerToast));
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
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

            formLayout.setVisibility(View.VISIBLE);
            formLayout.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            formLayout.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            formLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(isValidationSatisfied())
        {
            RequestQueue requestQueue=AppVolley.getRequestQueue();
            requestQueue.add(createAddEmployerRequest());
        }
    }
}
