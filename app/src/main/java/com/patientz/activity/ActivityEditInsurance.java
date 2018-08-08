package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityEditInsurance extends BaseActivity {
    private static final String TAG = "ActivityEditInsurance";
    private static final int REQUEST_CODE_INSURANCE_UPDATE = 3;
    Button submitButton;
    EditText insPolicyCompany, insPolicyName, insPolicyNo, insPolicyCoverage,
            insPolicyClaimNo;
    ImageView policyStartDateButton, policyEndDateButton;
    Date policyStartDate, policyEndDate;
    TextView policyStartDateTextView, policyEndDateTextView;
    InsuranceVO insuranceVO = new InsuranceVO();
    DatePicker datePicker;
    private PatientUserVO patientUserVO;
    Spinner countryIsdCodeSpinner;
    String keyValue;
    String[] phoneNoIsdCode;
    private LinearLayout mLoaderStatusView,llStartDate1,llStartDate2,llEndDate1,llEndDate2;
    private ScrollView rootView;
    private RequestQueue mRequestQueue;
    private static final String INSURANCE_ID = "id";
    private long insuranceId;
    Calendar startDateCal;
    Calendar endDateCal;
    private TextView tvDate;
    private TextView tvMonth;
    private TextView tvYear;
    private TextView tvEndDate;
    private TextView tvEndMonth;
    private TextView tvEndYear,tvPolicyValidity;
    private boolean startDateSelected,endDateSelected;
    private LinearLayout llPolicyStartDate,llPolicyEndDate;
    private DatabaseHandler mDatabaseHandler;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-12-15 13:58:55 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        tvDate = (TextView)findViewById( R.id.tv_date );
        tvMonth = (TextView)findViewById( R.id.tv_month );
        tvYear = (TextView)findViewById( R.id.tv_year );
        tvEndDate = (TextView)findViewById( R.id.tv_end_date );
        tvEndMonth = (TextView)findViewById( R.id.tv_end_month );
        tvEndYear = (TextView)findViewById( R.id.tv_end_year );

        llStartDate1 = findViewById( R.id.ll_start_date1 );
        llStartDate2 = findViewById( R.id.ll_start_date2 );
        llEndDate1 = findViewById( R.id.ll_end_date1 );
        llEndDate2 = findViewById( R.id.ll_end_date2 );
        tvPolicyValidity = findViewById( R.id.tv_policy_validity );

        llPolicyStartDate =
                findViewById(R.id.ll_policy_start_date);
        llPolicyEndDate =
                findViewById(R.id.ll_policy_end_date);
        insPolicyCompany = (EditText)
                findViewById(R.id.et_policy_provider);
        insPolicyName = (EditText)
                findViewById(R.id.et_policy_name);
        insPolicyNo = (EditText)
                findViewById(R.id.et_policy_number);
        insPolicyClaimNo = (EditText)
                findViewById(R.id.et_policy_claim_no);
        policyStartDateButton = (ImageView)
                findViewById(R.id.policy_start_date_button);

        policyEndDateButton = (ImageView)
                findViewById(R.id.policy_end_date_button);
        insPolicyCoverage = (EditText)
                findViewById(R.id.et_policy_coverage_amount);
        policyStartDateTextView = (TextView)
                findViewById(R.id.policy_start_date);
        policyEndDateTextView = (TextView)
                findViewById(R.id.policy_end_date);
        mLoaderStatusView = (LinearLayout) findViewById(R.id.progress_view);
        rootView = (ScrollView) findViewById(R.id.root_view);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_insurance_layout);
         mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());

        findViews();
        mRequestQueue = AppVolley.getRequestQueue();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.title_activity_activity_edit_insurance);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent() != null) {
            insuranceId = getIntent().getLongExtra(INSURANCE_ID, 0);
        } else {
            insuranceId = savedInstanceState.getLong(INSURANCE_ID, 0);
        }
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        try {
            patientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(INSURANCE_ID, insuranceId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        try {
            insuranceVO = mDatabaseHandler.getInsurance(currentSelectedPatientId, insuranceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent mIntent = getIntent();
        keyValue = mIntent.getStringExtra("key");
        Log.d(TAG, "key=" + keyValue);
        if (TextUtils.equals(keyValue, "edit") && insuranceVO != null) {
            populateAlreadyAddedInsuranceValues();
        }
        setButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_save_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case R.id.action_save:
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.INSURANCE_EXISTING_SAVE_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_EXISTING_SAVE_CLICKED);
                onSavePressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSavePressed() {
        if (AppUtil.isOnline(getApplicationContext())) {
            try {
                sendInsuranceDataToWeb();
            } catch (Exception e) {
                Log.d(TAG, "EXCEPTION=" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            AppUtil.showToast(getApplicationContext(), getString(R.string.offlineMode));
        }
    }

    public void setButton() {

        llPolicyStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG,"startDateCal1="+startDateCal);
                if(startDateCal==null)
                {
                    startDateCal=Calendar.getInstance();
                    Log.d(TAG,"startDateCal2="+startDateCal);
                }
                new DatePickerDialog(ActivityEditInsurance.this,AlertDialog.THEME_HOLO_LIGHT, mDateSetListener,
                        startDateCal.get(Calendar.YEAR), startDateCal
                        .get(Calendar.MONTH), startDateCal
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        llPolicyEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG,tvYear.getText().toString());
                if(!TextUtils.isEmpty(tvYear.getText().toString()))
                {
                    if(endDateCal==null) {
                        endDateCal = Calendar.getInstance();
                    }
                    new DatePickerDialog(ActivityEditInsurance.this, AlertDialog.THEME_HOLO_LIGHT,mDateSetListener1,
                            endDateCal.get(Calendar.YEAR), endDateCal
                            .get(Calendar.MONTH), endDateCal
                            .get(Calendar.DAY_OF_MONTH)).show();
                }else
                {
                    AppUtil.showToast(getApplicationContext(),getString(R.string.select_start_date));
                }

            }
        });
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            tvPolicyValidity.setError(null);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            Log.d(TAG,"startDateCal3="+startDateCal.getTime());
            Log.d(TAG,"Calendar.getInstance()="+Calendar.getInstance().getTime());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar currentTime=Calendar.getInstance();
            currentTime.set(Calendar.HOUR_OF_DAY, 0);
            currentTime.set(Calendar.MINUTE, 0);
            currentTime.set(Calendar.SECOND, 0);
            currentTime.set(Calendar.MILLISECOND, 0);

            if(calendar.after(currentTime))
            {
                AppUtil.showToast(getApplicationContext(),getString(R.string.start_date_cannot_b_future_date));
            }else
            {
                llStartDate1.setVisibility(View.VISIBLE);
                llStartDate2.setVisibility(View.GONE);
                startDateCal=calendar;
                setStartDate(calendar,tvDate,tvYear,tvMonth);
            }

        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            tvPolicyValidity.setError(null);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar currentTime=startDateCal;
            currentTime.set(Calendar.HOUR_OF_DAY, 0);
            currentTime.set(Calendar.MINUTE, 0);
            currentTime.set(Calendar.SECOND, 0);
            currentTime.set(Calendar.MILLISECOND, 0);

            Log.d("selected date=",calendar.toString());
            Log.d("start date=",startDateCal.toString());


            if(calendar.before(startDateCal) || calendar.equals(startDateCal))
            {
                AppUtil.showToast(getApplicationContext(),getString(R.string.end_date_must_b_after_start_date));
            }else
            {
                endDateCal=calendar;
                llEndDate1.setVisibility(View.VISIBLE);
                llEndDate2.setVisibility(View.GONE);
                setEndDate(calendar,tvEndDate,tvEndYear,tvEndMonth);
            }
        }
    };

    private void setStartDate(Calendar calendar, TextView date, TextView year, TextView month) {
        startDateSelected=true;
        date.setText(new SimpleDateFormat("dd").format(calendar.getTime()));
        year.setText(new SimpleDateFormat("yyyy").format(calendar.getTime()));
        month.setText(new SimpleDateFormat("MMM").format(calendar.getTime()));
    }
    private void setEndDate(Calendar calendar, TextView date, TextView year, TextView month) {
        endDateSelected=true;
        date.setText(new SimpleDateFormat("dd").format(calendar.getTime()));
        year.setText(new SimpleDateFormat("yyyy").format(calendar.getTime()));
        month.setText(new SimpleDateFormat("MMM").format(calendar.getTime()));
    }



    private StringRequest createUpdateInsuranceRequest(final Map<String, String> params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.saveInsuranceFromMobile;
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
                            AppUtil.showErrorCodeDialog(ActivityEditInsurance.this);
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
                    AppUtil.showToast(getApplicationContext(),getString(R.string.insAddedSucessfully));
                    for (PatientUserVO mPatientUserVO : mResponseVO.getPatientUserVO()) {
                        if (mPatientUserVO.getPatientId() == patientUserVO.getPatientId()) {
                            try {
                                InsuranceVO mInsuranceVO = mResponseVO.getiVO();
                                if (mInsuranceVO != null) {
                                    mDatabaseHandler.insertUserInsuranceDetails(mInsuranceVO);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                Intent startIntent = new Intent(ActivityEditInsurance.this, StickyNotificationForeGroundService.class);
                                startIntent.setAction(Constant.ACTION.EMERGENCY_PREPAREDNESS);
                                startService(startIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                                Intent mIntent = new Intent();
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                                setResult(RESULT_OK, mIntent);
                            //}
                            finish();
                            break;
                        }
                    }
                    break;
                case Constant.RESPONSE_FAILURE:
                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.failure_msg));
                case Constant.RESPONSE_NOT_LOGGED_IN:
                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.not_logged_in_error_msg));
                    break;
                case Constant.RESPONSE_SERVER_ERROR:
                    AppUtil.showToast(getApplicationContext(),
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

    public  boolean isValidMobileNumber(String mobile) {
        String regEx = "^(?!(0))[0-9]{11}$";
        return mobile.matches(regEx);
    }

    public boolean validateInsuranceFields() {
        Boolean result = true;
        String phoneError = "";
      if(insPolicyClaimNo.getText().toString().length()<10 ||
              insPolicyClaimNo.getText().toString().length()>11
              )
       {
        phoneError = getString(R.string.pno_validation_error_insurance);
        insPolicyClaimNo.setError(phoneError);
        result = false;
       }
        if (insPolicyCompany.getText().toString().trim().length() == 0) {
            insPolicyCompany
                    .setError(getString(R.string.ins_policy_provider_mandatory_error_msg));
            result = false;
        }
        if (insPolicyName.getText().toString().trim().length() == 0) {
            insPolicyName
                    .setError(getString(R.string.ins_policyname_mandatory_error_msg));
            result = false;
        }
        if (insPolicyNo.getText().toString().trim().length() == 0) {
            insPolicyNo
                    .setError(getString(R.string.ins_policyNum_mandatory_error_msg));
            result = false;
        }
        if (insPolicyCoverage.getText().toString().trim().length() == 0) {
            insPolicyCoverage
                    .setError(getString(R.string.ins_policy_coverage_mandatory_error_msg));
            result = false;
        }
        if(!startDateSelected || !endDateSelected)
        {
            tvPolicyValidity
                    .setError(getString(R.string.ins_validity_date_mandatory));
            result = false;
        }
        return result;

    }

    public void populateAlreadyAddedInsuranceValues() {
        //setIsdValues();

        if (insuranceVO.getInsPolicyCompany() != null) {
            insPolicyCompany.setText(insuranceVO.getInsPolicyCompany());
        }
        if (insuranceVO.getInsPolicyCoverage() != null) {
            insPolicyCoverage.setText(insuranceVO.getInsPolicyCoverage());
        }
        if (insuranceVO.getInsPolicyStartDate() != null) {
            llStartDate1.setVisibility(View.VISIBLE);
            llStartDate2.setVisibility(View.GONE);
            startDateCal=Calendar.getInstance();
            startDateCal.setTime(insuranceVO
                    .getInsPolicyStartDate());
            setStartDate(startDateCal,tvDate,tvYear,tvMonth);
        }
        if (insuranceVO.getInsPolicyEndDate() != null) {
            endDateCal=Calendar.getInstance();
            llEndDate1.setVisibility(View.VISIBLE);
            llEndDate2.setVisibility(View.GONE);
            endDateCal.setTime(insuranceVO
                    .getInsPolicyEndDate());
            setEndDate(endDateCal,tvEndDate,tvEndYear,tvEndMonth);
        }
        if (insuranceVO.getInsPolicyName() != null) {
            insPolicyName.setText(insuranceVO.getInsPolicyName());
        }
        if (insuranceVO.getInsPolicyNo() != null) {
            insPolicyNo.setText(insuranceVO.getInsPolicyNo());
        }

        insuranceId = insuranceVO.getInsuranceId();
        if (insuranceVO.getClaimPhoneNumber() != null) {
            String claimPhoneNumberWithoutIsd;
            String claimPhoneNumber=insuranceVO.getClaimPhoneNumber();
            if(claimPhoneNumber.contains("-"))
            {
                String[] splitMobileNO=claimPhoneNumber.split("-");
                claimPhoneNumberWithoutIsd=splitMobileNO[1];
            }else
                {
                    claimPhoneNumberWithoutIsd=claimPhoneNumber;
            }
            insPolicyClaimNo.setText(claimPhoneNumberWithoutIsd);
        }
    }


    public void sendInsuranceDataToWeb() throws Exception {
        Boolean result = validateInsuranceFields();
        if (result == true) {
            insPolicyCoverage.setError(null);
            insPolicyName.setError(null);
            insPolicyNo.setError(null);
            insPolicyCompany.setError(null);
            insPolicyClaimNo.setError(null);
            Map<String, String> params = new HashMap<String, String>();
            params.put(
                    Constant.insPolicyCompanyKey, insPolicyCompany.getText()
                            .toString());
            params.put(
                    Constant.insPolicyCoverageKey, insPolicyCoverage.getText()
                            .toString());
            if(startDateCal!=null)
            {
                params.put(
                        Constant.insPolicyStartDateKey, (String) DateFormat.format(
                                getString(R.string.ins_date_format, Locale.ENGLISH), startDateCal));
            }
            if(endDateCal!=null)
            {
                params.put(
                        Constant.insPolicyEndDateKey, (String) (DateFormat.format(
                                getString(R.string.ins_date_format, Locale.ENGLISH), endDateCal)));
            }

            params.put(
                    Constant.insPolicyNameKey, insPolicyName.getText()
                            .toString());
            params.put(Constant.insPolicyNoKey,
                    insPolicyNo.getText().toString());
            params.put(Constant.insuranceIdKey,
                    String.valueOf(insuranceId));
            params.put(
                    Constant.claimPhoneNumberKey, "+91-"
                            + String.valueOf(insPolicyClaimNo.getText()
                            .toString()));
            params.put(Constant.patientIdKey,
                    String.valueOf(patientUserVO.getPatientId()));
            params.put(Constant.lastSyncIdKey,
                    String.valueOf(AppUtil.getEventLogID(getApplicationContext())));
            Log.d(TAG, "REQUEST DATA=" + params.entrySet().toString());
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.INSURANCE_EDIT_SUBMIT_CLICKED,true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_EDIT_SUBMIT_CLICKED);
            Log.d(TAG,"upshot data="+upshotData.entrySet());
            showProgress(true);
            mRequestQueue.add(createUpdateInsuranceRequest(params));
        } else {

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
