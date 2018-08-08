package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.patientz.VO.InsuranceUpload;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class InsuranceDetailsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "InsuranceDetailsFragment";
    private static final int REQUEST_CODE_MOBILE_VERIFY_CONFIRMATION = 12;
    TextView message;
    private static final String ACTION_MSG_RECEIVED = "ACTION_MSG_RECEIVED";
    private TextView tvPolicyStartDate;
    private TextView tvPolicyStartMonth;
    private TextView tvPolicyStartYear;
    private TextView tvPolicyEndDate,tvDob;
    private TextView tvPolicyEndMonth;
    private TextView tvPolicyEndYear;
    private View mView;
    private long currentSelectedPatientId;
    private  CountDownTimer toastCountDown;
    private boolean resendClicked;

    private long insuranceId;
    private static final String INSURANCE_ID = "id";
    InsuranceVO insuranceVO;
    private ProgressDialog mDialog;
    private RequestQueue mRequestQueue;
    private TextView tvPaytmRefId;
    private LinearLayout llViewDownloadPolicy;
    private TextView tvVerifyMobile;
    private TextView tvVerifyEmail;
    private TextView tvPolicyProvider,tvPolicyUploadInfo;
    private TextView tvPolicyName;
    private TextView tvPolicyNo;
    private LinearLayout llStartDate;
    private LinearLayout llStartDate2;
    private LinearLayout llEndDate;
    private LinearLayout llEndDate2;
    private TextView tvPolicyCoverageAmt;
    private TextView tvPolicyClaimNo,tvStatus;
    private LinearLayout progressView;
    private Toolbar toolbar;
    private ImageView ivInsuranceInfo;
    private NestedScrollView scroll_view;
    private TextView tvNameOfInsured,tvVerifyAadhar;
    private  AlertDialog mAlertDialog;
    private LinearLayout llVerifyAadhar,llVerifyEmail,llVerifyMobile;
    private  ProgressBar progressBar;
    private TextView tvPhoneNo;
    private TextView tvEmail;
    private Button btVerifyEmail,btVerifyPno;
    private TextView tvPhoneNoVerified,tvEmailVerified;

    private void findViewsExistingInsurances() {
        toolbar = (Toolbar)findViewById( R.id.toolbar );
        prepareActionBar();

        tvPolicyProvider = (TextView)findViewById( R.id.tv_policy_provider );
        tvPolicyName = (TextView)findViewById( R.id.tv_policy_name );
        ivInsuranceInfo = (ImageView)findViewById( R.id.iv_insurance_info );
        tvPolicyNo = (TextView)findViewById( R.id.tv_policy_no );
        llStartDate = (LinearLayout)findViewById( R.id.ll_start_date );
        llStartDate2 = (LinearLayout)findViewById( R.id.ll_start_date2 );
        llEndDate = (LinearLayout)findViewById( R.id.ll_end_date );
        llEndDate2 = (LinearLayout)findViewById( R.id.ll_end_date2 );
        tvPolicyCoverageAmt = (TextView)findViewById( R.id.tv_policy_coverage_amt );
        tvPolicyClaimNo = (TextView)findViewById( R.id.tv_policy_claim_no );
        progressView = (LinearLayout)findViewById( R.id.progress_view );
        scroll_view =findViewById( R.id.scroll_view );
        tvPolicyStartDate = (TextView)findViewById( R.id.tv_date );
        tvPolicyStartMonth = (TextView)findViewById( R.id.tv_month );
        tvPolicyStartYear = (TextView)findViewById( R.id.tv_year );
        tvPolicyEndDate = (TextView)findViewById( R.id.tv_end_date );
        tvPolicyEndMonth = (TextView)findViewById( R.id.tv_end_month );
        tvPolicyEndYear = (TextView)findViewById( R.id.tv_end_year);
        ivInsuranceInfo.setOnClickListener(this);
    }


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-01-06 13:22:40 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViewsBoughtInsurances() {
        toolbar = (Toolbar)findViewById( R.id.toolbar );
        prepareActionBar();
        // toolbar.inflateMenu(R.menu.menu_toolbar_cancel);


        LinearLayout llPolicyValidity = (LinearLayout)findViewById( R.id.ll_policy_validity );
        setPolicyValidityViewVisibility(llPolicyValidity);
        LinearLayout llPolicyNumber = (LinearLayout)findViewById( R.id.ll_policy_number);

        setPolicyNoViewVisibility(llPolicyNumber);


        tvPolicyProvider = (TextView)findViewById( R.id.tv_policy_provider );
        tvPolicyUploadInfo = (TextView)findViewById( R.id.tv_policy_upload_info );
        if(insuranceVO.getStatus()==Constant.INSURANCE_STATUS_ISSUED)
        {
            tvPolicyUploadInfo.setVisibility(View.GONE);
        }
        tvPolicyName = (TextView)findViewById( R.id.tv_policy_name );
        ivInsuranceInfo = (ImageView)findViewById( R.id.iv_insurance_info );
        ivInsuranceInfo.setOnClickListener(this);
        tvPolicyNo = (TextView)findViewById( R.id.tv_policy_no );
        llStartDate = (LinearLayout)findViewById( R.id.ll_start_date );
        llStartDate2 = (LinearLayout)findViewById( R.id.ll_start_date2 );
        llEndDate = (LinearLayout)findViewById( R.id.ll_end_date );
        llEndDate2 = (LinearLayout)findViewById( R.id.ll_end_date2 );
        tvPolicyCoverageAmt = (TextView)findViewById( R.id.tv_policy_coverage_amt );
        tvPaytmRefId = (TextView)findViewById( R.id.tv_paytm_ref_id );
        tvPolicyClaimNo = (TextView)findViewById( R.id.tv_policy_claim_no );
        llViewDownloadPolicy = (LinearLayout)findViewById( R.id.ll_view_download_policy );
        tvStatus = (TextView)findViewById( R.id.tv_status );
        tvDob = (TextView)findViewById( R.id.tv_dob );



        llViewDownloadPolicy.setOnClickListener(this);
        progressView = (LinearLayout)findViewById( R.id.progress_view );
        tvNameOfInsured = findViewById( R.id.tv_name_of_insured );
        tvPolicyStartDate = (TextView)findViewById( R.id.tv_date );
        tvPolicyStartMonth = (TextView)findViewById( R.id.tv_month );
        tvPolicyStartYear = (TextView)findViewById( R.id.tv_year );
        tvPolicyEndDate = (TextView)findViewById( R.id.tv_end_date );
        tvPolicyEndMonth = (TextView)findViewById( R.id.tv_end_month );
        tvPolicyEndYear = (TextView)findViewById( R.id.tv_end_year);
       // tvVerifyAadhar = (TextView)findViewById( R.id.tv_verify_aadhar);
        tvPhoneNo = (TextView)findViewById( R.id.tv_phone_no);
        tvEmail = (TextView)findViewById( R.id.tv_email);
        btVerifyPno = (Button)findViewById( R.id.bt_verify_phone_no);
        btVerifyEmail = (Button)findViewById( R.id.bt_verify_email);
        tvPhoneNoVerified = (TextView)findViewById( R.id.tv_phone_no_verified );
        tvEmailVerified = (TextView)findViewById( R.id.tv_email_verified);

        btVerifyPno.setOnClickListener(this);
        btVerifyEmail.setOnClickListener(this);
    }

    private void setPolicyNoViewVisibility(LinearLayout llPolicyNumber) {
        if(insuranceVO.getStatus()==Constant.INSURANCE_STATUS_ISSUED)
        {
            llPolicyNumber.setVisibility(View.VISIBLE);
        }
    }

    private void setPolicyValidityViewVisibility(LinearLayout llPolicyValidity) {
        if(insuranceVO.getStatus()==Constant.INSURANCE_STATUS_ISSUED)
        {
            llPolicyValidity.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* Intent in = getIntent();
        Uri data = in.getData();
        Log.d("data=",data+"");*/
        if (getIntent() != null) {
            insuranceId = getIntent().getLongExtra(INSURANCE_ID, 0);
        } else {
            insuranceId = savedInstanceState.getLong(INSURANCE_ID, 0);
        }
        DatabaseHandler dh = DatabaseHandler.dbInit(getApplicationContext());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        Log.d(TAG,"onCreate");

        try {
            insuranceVO = dh.getInsurance(currentSelectedPatientId, insuranceId);
            Log.d(TAG,"insuranceId="+insuranceId);

            if(insuranceVO.getInsuranceUploadId()!=0)
            {
                Log.d(TAG,"is not existing policy");
                Log.d(TAG,"InsuranceUploadId="+insuranceVO.getInsuranceUploadId());

                setContentView(R.layout.content_activity_insurance_details_purchased_ins);
                findViewsBoughtInsurances();
                populateBasicDetails();
                populateInsuranceDetailsForPurchasedIns();
                //showDialogtoVerifyEmailPhone();
            }else
            {
                Log.d(TAG,"is existing policy");
                setContentView(R.layout.insurance_details_layout);
                findViewsExistingInsurances();
                populateBasicDetails();
                tvPolicyCoverageAmt.setText("₹"+AppUtil.getFormattedAmount(insuranceVO.getInsPolicyCoverage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(messageReceiver,
                new IntentFilter(ACTION_MSG_RECEIVED));
        mRequestQueue= AppVolley.getRequestQueue();
    }

    private void populateInsuranceDetailsForPurchasedIns() {
        setVerifyButtonStatus();
        tvNameOfInsured.setText((insuranceVO.getFirstName()!=null?insuranceVO.getFirstName():"")+" "+(insuranceVO.getLastName()!=null?insuranceVO.getLastName():""));
        tvPaytmRefId.setText(insuranceVO.getPaytmRefId());
        if(insuranceVO.getPolicyDoc()!=null)
        {
            llViewDownloadPolicy.setVisibility(View.VISIBLE);
        }
        setStatus();
        tvPhoneNo.setText(insuranceVO.getMobileNumber());
        tvEmail.setText(insuranceVO.getEmail());
        Log.d("DateOfBirth",insuranceVO.getDateOfBirth()+"");
        if(insuranceVO.getDateOfBirth()!=null)
        {
            tvDob.setText(new SimpleDateFormat("dd-MMM-yyyy").format(insuranceVO.getDateOfBirth()));

        }
    }

    private void setStatus() {
        String[] insuranceUploadStatuses=getApplicationContext().getResources().getStringArray(R.array.insurance_upload_status);
        tvStatus.setText(insuranceUploadStatuses[insuranceVO.getStatus()]);
        switch (insuranceVO.getStatus())
        {
            case Constant.INSURANCE_STATUS_ISSUED:
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.em_pass,0,0,0);
                break;
                default:
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pending_1,0,0,0);
                    break;
        }
    }

    private void setVerifyButtonStatus() {
        Log.d("isMobileNumberVerified",insuranceVO.isMobileNumberVerified()+"");
        Log.d("isEmailVerified",insuranceVO.isEmailVerified()+"");
        Log.d("isMobileNumberVerified",insuranceVO.isMobileNumberVerified()+"");

        if(insuranceVO.isMobileNumberVerified())
        {
            tvPhoneNoVerified.setVisibility(View.VISIBLE);
            btVerifyPno.setVisibility(View.GONE);
        }
        if(insuranceVO.isEmailVerified())
        {
            tvEmailVerified.setVisibility(View.VISIBLE);
            btVerifyEmail.setVisibility(View.GONE);
        }
        Log.d("isAadharVerified",insuranceVO.isAadharVerified()+"");
        Log.d("Aadhar",insuranceVO.getAadharNo()+"");

    }


    private void prepareActionBar() {
        toolbar.setTitle(R.string.title_activity_update_profile);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.title_activity_insurance_details);
        setSupportActionBar(toolbar);
        // toolbar.inflateMenu(R.menu.menu_toolbar_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(INSURANCE_ID, insuranceId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        DatabaseHandler dh = DatabaseHandler.dbInit(getApplicationContext());
         currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        try {
            InsuranceVO mInsuranceVO=dh.getInsurance(currentSelectedPatientId,insuranceId);
            PatientUserVO mPatientUserVO = dh.getProfile(currentSelectedPatientId);
            Log.d(TAG,"isNotExisitingPolicy="+mInsuranceVO.isNotExisitingPolicy());
            if(mInsuranceVO.getInsuranceUploadId()!=0)
            {
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_insurance_details_screen, menu);
            }
            else if(mInsuranceVO.getInsuranceUploadId()==0){
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_profile, menu);
            }else
            {
                    menu.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_edit:
                Intent intent = new Intent(this, ActivityEditInsurance.class);
                intent.putExtra("key", "edit");
                intent.putExtra(INSURANCE_ID, insuranceId);
                startActivityForResult(intent, Constant.REQUEST_CODE_INSURANCE_UPDATE);
                finish();
                break;
            case R.id.action_refresh:
                mView=findViewById(R.id.action_refresh);

                RotateAnimation rotate = new RotateAnimation(
                        0, 360,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                rotate.setDuration(100);
                rotate.setRepeatCount(Animation.INFINITE);
                mView.startAnimation(rotate);
                RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                mRequestQueue.add(getInsuranceUpload(insuranceVO));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public StringRequest getInsuranceUpload(InsuranceVO insuranceVO) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.insuranceUploadList+"?id="+insuranceVO.getInsuranceUploadId()+"&patientId="+insuranceVO.getPatientId();
        Log.d(TAG,"getInsuranceUpload>>url="+szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "getInsuranceUpload>>response:" + response);
                if(response!=null) {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<InsuranceUpload>() {
                    }.getType();
                    InsuranceUpload insuranceUpload = gson.fromJson(response, objectType);
                    DatabaseHandler mDatabaseHandler=DatabaseHandler.dbInit(getApplicationContext());
                    mDatabaseHandler.insertUserUploadInsurance(insuranceUpload);
                    try {
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(mView!=null)
                {
                    mView.clearAnimation();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(mView!=null)
                {
                    mView.clearAnimation();
                }
                if(volleyError!=null) {
                    NetworkResponse networkResponse = volleyError.networkResponse;
                    if (networkResponse != null) {
                        switch (networkResponse.statusCode) {
                            case Constant.HTTP_CODE_SERVER_BUSY:
                                AppUtil.showErrorCodeDialog(InsuranceDetailsActivity.this);
                                break;
                        }
                    } else {
                        AppUtil.showErrorDialog(getApplicationContext(), volleyError);
                    }
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000,2,2));
        return mRequest;
    }
    @Override
    public void onClick(View view) {
        HashMap<String, Object> upshotData = new HashMap<>();

        switch (view.getId())
        {
            case R.id.ll_view_download_policy:
                startActivity(new Intent(InsuranceDetailsActivity.this, PDFView.class)
                        .putExtra("fileid",insuranceVO.getPolicyDoc().getId())
                        .putExtra("moduleType",Constant.IMAGE_MODULE_TYPE_INSURANCE_POLICY)
                        .putExtra("content_type", insuranceVO.getPolicyDoc().getContentType()));
                break;
            case R.id.bt_verify_phone_no:
                btVerifyPno.setClickable(false);
                btVerifyPno.setEnabled(false);
                upshotData.put(Constant.UpshotEvents.INSURANCE_VERIFY_MOBILE_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_VERIFY_MOBILE_CLICKED);
                    if(!insuranceVO.isMobileNumberVerified())
                    {
                        mDialog= CommonUtils.showProgressDialogWithCustomMsg(InsuranceDetailsActivity.this,getString(R.string.request_for_otp));
                        mRequestQueue.add(verifyMobile());
                    }
                break;
            case R.id.bt_verify_email:
                btVerifyEmail.setClickable(false);
                btVerifyEmail.setEnabled(false);
                upshotData.put(Constant.UpshotEvents.INSURANCE_VERIFY_EMAIL_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_VERIFY_EMAIL_CLICKED);
                if(!insuranceVO.isEmailVerified())
                {
                   /* mDialog=CommonUtils.showProgressDialogWithCustomMsg(InsuranceDetailsActivity.this,"Sending Token to your Email");
                    mRequestQueue.add(tvVerifyEmail());*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsuranceDetailsActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View v = inflater.inflate(R.layout.confirm_email, null);
                    builder.setView(v);
                    builder.setCancelable(false);
                    final EditText etToken = v.findViewById(R.id.et_token);
                    final Button btConfirmEmail = v.findViewById(R.id.bt_confirm_email);
                    ImageView ivCancel = v.findViewById(R.id.iv_cancel);
                    ivCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            btVerifyEmail.setClickable(true);
                            btVerifyEmail.setEnabled(true);
                            mAlertDialog.cancel();
                        }
                    });
                    final TextView tvResend = v.findViewById(R.id.tv_resend);
                    tvResend.setVisibility(View.VISIBLE);
                    tvResend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvResend.setClickable(false);
                            tvResend.setEnabled(false);
                            mDialog=CommonUtils.showProgressDialogWithCustomMsg(InsuranceDetailsActivity.this,"Sending OTP to your Email");
                            mRequestQueue.add(tvVerifyEmail(tvResend));
                        }
                    });
                    progressBar = v.findViewById(R.id.progress_bar);
                    mAlertDialog = builder.create();
                    btConfirmEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isTokenEntered(etToken.getText().toString())) {
                                btVerifyEmail.setClickable(true);
                                btVerifyEmail.setEnabled(true);
                                btConfirmEmail.setClickable(false);
                                btConfirmEmail.setEnabled(false);
                                progressBar.setVisibility(View.VISIBLE);
                               // mDialog=CommonUtils.showProgressDialogWithCustomMsg(InsuranceDetailsActivity.this,getString(R.string.verifying_email));
                                mRequestQueue.add(confirmEmail(etToken.getText().toString(),btConfirmEmail));
                            }
                        }
                    });
                    mAlertDialog.show();
                }
                break;
            case R.id.tv_verify_aadhar:
                if(TextUtils.isEmpty(insuranceVO.getAadharNo()))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsuranceDetailsActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View v = inflater.inflate(R.layout.edittext_with_button, null);
                    builder.setView(v);
                    final EditText etAadhar = v.findViewById(R.id.et_aadhar);
                    Button btAddAadhar = v.findViewById(R.id.bt_add_aadhar);
                    progressBar = v.findViewById(R.id.progress_bar);
                    mAlertDialog = builder.create();
                    btAddAadhar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isAadharValidated(etAadhar.getText().toString())) {
                                progressBar.setVisibility(View.VISIBLE);
                                mRequestQueue.add(verifyAadhar(etAadhar.getText().toString()));
                            }
                        }
                    });
                    mAlertDialog.show();
                }

                break;
            case R.id.iv_insurance_info:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebServiceUrls.apollo_munich_info));
                startActivity(browserIntent);
                break;

        }
    }
    public void showToast() {
        int toastDurationInMilliSeconds = 8000;
        final Toast mToastToShow = Toast.makeText(this, "Please follow the instructions sent to your Email ID to verify your Email and do a refresh in this screen", Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };
        mToastToShow.show();
        toastCountDown.start();
    }
    private boolean isAadharValidated(String aadharNo) {
        if(TextUtils.isEmpty(aadharNo) || aadharNo.length()!=12)
        {
            AppUtil.showToast(getApplicationContext(),getString(R.string.enter_12digit_aadhar_no));
            return false;
        }
        return true;
    }
    private boolean isTokenEntered(String token) {
        if(TextUtils.isEmpty(token))
        {
            AppUtil.showToast(getApplicationContext(),getString(R.string.enter_token));
            return false;
        }
        return true;
    }

    private StringRequest tvVerifyEmail(final TextView tvResend) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.verifyEmail+"patientId="+insuranceVO.getPatientId()+"&id="+insuranceVO.getInsuranceUploadId();

        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE \n" + response);
                tvResend.setClickable(true);
                tvResend.setEnabled(true);
                if(response!=null)
                {
                    AppUtil.showToastShort(getApplicationContext(),"OTP sent to your Email");
                }else
                {
                    AppUtil.showToastLong(getApplicationContext(),"Failed to send OTP to your Email.Please try again");
                }
                dismissProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "******************* onErrorResponse ******************* \n");
                tvResend.setClickable(true);
                tvResend.setEnabled(true);
                dismissProgressDialog();
                if(mAlertDialog!=null)
                {
                    if(mAlertDialog.isShowing())
                    {
                        mAlertDialog.cancel();
                    }
                }
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(InsuranceDetailsActivity.this);
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
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }
    private StringRequest verifyMobile() {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.verifyMobile+"patientId="+insuranceVO.getPatientId()+"&id="+insuranceVO.getInsuranceUploadId();
        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                btVerifyPno.setClickable(true);
                btVerifyPno.setEnabled(true);
                dismissProgressDialog();
                if(response!=null)
                {
                    Intent mIntent=new Intent(InsuranceDetailsActivity.this,VerifyMobileNumberActivity.class);
                    mIntent.putExtra(INSURANCE_ID,getIntent().getLongExtra(INSURANCE_ID,0));
                    startActivityForResult(mIntent,REQUEST_CODE_MOBILE_VERIFY_CONFIRMATION);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                btVerifyPno.setClickable(true);
                btVerifyPno.setEnabled(true);
                dismissProgressDialog();
                AppUtil.showToast(getApplicationContext(),"Request for Mobile verification failed,please try again");
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }
    private StringRequest verifyAadhar(String aadharNo) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.verifyAadhar+"?patientId="+insuranceVO.getPatientId()+"&id="+insuranceVO.getInsuranceUploadId()+"&aadharNo="+aadharNo;
        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"response="+response);
                Log.d(TAG,"mAlertDialog="+mAlertDialog);
                Log.d(TAG,"mAlertDialog.isShowing()="+mAlertDialog.isShowing());
                progressBar.setVisibility(View.GONE);
                if(mAlertDialog!=null)
                {
                    mAlertDialog.dismiss();
                    mAlertDialog.cancel();
                }
                if(response!=null)
                {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<InsuranceUpload>() {
                    }.getType();
                    InsuranceUpload insuranceUpload = gson.fromJson(response, objectType);
                    DatabaseHandler mDatabaseHandler=DatabaseHandler.dbInit(getApplicationContext());
                    mDatabaseHandler.insertUserUploadInsurance(insuranceUpload);
                    tvVerifyAadhar.setText("Aadhar No. Added");
                    tvVerifyAadhar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.aadhar,0,R.drawable.invitation_pass,0);
                }else
                {
                    AppUtil.showToast(getApplicationContext(),"Failed to Add Aadhar.Please try again");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"response="+error);
                if(mAlertDialog!=null)
                {
                        mAlertDialog.dismiss();
                }
                AppUtil.showToast(getApplicationContext(),"Failed to Add Aadhar.Please try again");
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }
    private StringRequest confirmEmail(String token, final Button btConfirmEmail) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.confirmEmail+"?patientId="+insuranceVO.getPatientId()+"&id="+insuranceVO.getInsuranceUploadId()+"&otp="+token;
        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"response="+response);
                Log.d(TAG,"mAlertDialog="+mAlertDialog);
                Log.d(TAG,"mAlertDialog.isShowing()="+mAlertDialog.isShowing());
                progressBar.setVisibility(View.GONE);
                btConfirmEmail.setClickable(true);
                btConfirmEmail.setEnabled(true);
                if(mAlertDialog!=null)
                {
                    if(mAlertDialog.isShowing())
                    {
                       mAlertDialog.dismiss();
                    }
                }
                //dismissProgressDialog();
                if(response!=null)
                {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<InsuranceUpload>() {
                    }.getType();
                    InsuranceUpload insuranceUpload = gson.fromJson(response, objectType);
                    DatabaseHandler mDatabaseHandler=DatabaseHandler.dbInit(getApplicationContext());
                    mDatabaseHandler.insertUserUploadInsurance(insuranceUpload);
                    try {
                        insuranceVO=mDatabaseHandler.getInsurance(currentSelectedPatientId,insuranceId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvEmailVerified.setVisibility(View.VISIBLE);
                    btVerifyEmail.setVisibility(View.GONE);
//                    tvVerifyEmail.setText("Email Verified");
//                    tvVerifyEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.verify_mail,0,R.drawable.invitation_pass,0);
                }else
                {
                    AppUtil.showToast(getApplicationContext(),"Failed to verify your Email.Please try again");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"response="+error);
                btConfirmEmail.setClickable(true);
                btConfirmEmail.setEnabled(true);
                dismissProgressDialog();
                progressBar.setVisibility(View.GONE);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(InsuranceDetailsActivity.this);
                            break;
                        case Constant.HTTP_CODE_FORBIDDEN:
                            AppUtil.showToast(getApplicationContext(),getString(R.string.enter_correct_token));
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
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_MSG_RECEIVED)) {
                String messageReceived = intent.getStringExtra("messageReceived");
                Log.d(TAG,"messageReceived="+messageReceived);
                if(messageReceived.equalsIgnoreCase("This message is for verifying your provided mobile number"))
                {
                    //mDialog= CommonUtils.showProgressDialogWithCustomMsg(InsuranceDetailsActivity.this,"Confirming mobile number");
                    mRequestQueue.add(confirmMobile());
                }
            }
        }
    };
    private StringRequest confirmMobile() {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.confirmMobile+"patientId="+insuranceVO.getPatientId()+"&id="+insuranceVO.getInsuranceId();

        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE \n" + response);
                if(response!=null) {
                    AppUtil.showToast(getApplicationContext(),"Mobile Verified");
                    DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                    mDatabaseHandler.updateIsMobileVerified(insuranceVO);
                    Log.d(TAG, "RESPONSEEEEEEE \n" + response);
                    tvVerifyMobile.setText(getString(R.string.phone_number_verified));
                    tvVerifyMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.verify_mobile,0,R.drawable.invitation_pass,0);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "******************* onErrorResponse ******************* \n");
                dismissProgressDialog();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(InsuranceDetailsActivity.this);
                            break;
                        case Constant.HTTP_CODE_FORBIDDEN:
                              AppUtil.showToast(getApplicationContext(),getString(R.string.enter_correct_token));
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
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }


    private void dismissProgressDialog() {
        if(mDialog!=null)
        {
            if(mDialog.isShowing())
            {
                mDialog.dismiss();
            }
        }
    }

    private void populateBasicDetails() {
        tvPolicyProvider.setText(insuranceVO.getInsPolicyCompany());
        tvPolicyName.setText(insuranceVO.getInsPolicyName());
        if(insuranceVO.getInsPolicyNo()!=null)
        {
            tvPolicyNo.setText(insuranceVO.getInsPolicyNo());
        }
        setPolicyValidity();
        tvPolicyClaimNo.setText(insuranceVO.getClaimPhoneNumber());
    }
    private void setPolicyValidity() {
        Log.d("insuranceVO.getInsPolicyStartDate()",insuranceVO.getInsPolicyStartDate()+"");
        if(insuranceVO.getInsPolicyStartDate()!=null)
        {
            llStartDate.setVisibility(View.VISIBLE);
            llStartDate2.setVisibility(View.GONE);
            tvPolicyStartDate.setText(new SimpleDateFormat("dd").format(insuranceVO.getInsPolicyStartDate()));
            tvPolicyStartMonth.setText(new SimpleDateFormat("MMM").format(insuranceVO.getInsPolicyStartDate()));
            tvPolicyStartYear.setText(new SimpleDateFormat("yyyy").format(insuranceVO.getInsPolicyStartDate()));
        }
        if(insuranceVO.getInsPolicyEndDate()!=null)
        {
            llEndDate.setVisibility(View.VISIBLE);
            llEndDate2.setVisibility(View.GONE);
            tvPolicyEndDate.setText(new SimpleDateFormat("dd").format(insuranceVO.getInsPolicyEndDate()));
            tvPolicyEndMonth.setText(new SimpleDateFormat("MMM").format(insuranceVO.getInsPolicyEndDate()));
            tvPolicyEndYear.setText(new SimpleDateFormat("yyyy").format(insuranceVO.getInsPolicyEndDate()));
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

            progressView.setVisibility(View.VISIBLE);
            progressView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            scroll_view.setVisibility(View.VISIBLE);
            scroll_view.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            scroll_view.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            scroll_view.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        dismissProgressDialog();
        if(toastCountDown!=null)
        {
            toastCountDown.cancel();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            Log.d("done","done");
            DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
            mDatabaseHandler.updateIsMobileVerified(insuranceVO);
            try {
                insuranceVO=mDatabaseHandler.getInsurance(currentSelectedPatientId,insuranceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvPhoneNoVerified.setVisibility(View.VISIBLE);
            btVerifyPno.setVisibility(View.GONE);
        }
    }

}
