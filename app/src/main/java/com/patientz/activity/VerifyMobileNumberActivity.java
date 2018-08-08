package com.patientz.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.patientz.VO.InsuranceVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.util.HashMap;
import java.util.Map;

public class VerifyMobileNumberActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG ="VerifyMobileNumberActivity" ;
    private static final String ACTION_OTP_RECEIVED ="OTPONRECEIVE" ;
    private ImageView ivCancel;
    private TextView tvTime,tvAutoVerify;
    private ProgressBar progressBar;
    private EditText etOtp;
    private CountDownTimer timer;
    private Button btSubmit;
    private RequestQueue mRequestQueue;
    private InsuranceVO mInsuranceVO;
    private ProgressDialog mDialog;
    private TextView tvResend;

    private void findViews() throws Exception {
        ivCancel = (ImageView)findViewById( R.id.iv_cancel );
        tvTime = (TextView)findViewById( R.id.tv_time );
        progressBar = (ProgressBar)findViewById( R.id.progress_bar );
        btSubmit = findViewById( R.id.bt_submit );
        btSubmit.setOnClickListener(this);
        tvResend = findViewById(R.id.tv_resend);
        etOtp = (EditText)findViewById( R.id.et_otp );
        tvAutoVerify = (TextView) findViewById( R.id.tv_auto_verify );
        progressBar.setMax(30);
        ivCancel.setOnClickListener(this);
        long insuranceId=getIntent().getLongExtra("id",0);
        DatabaseHandler databaseHandler=DatabaseHandler.dbInit(getApplicationContext());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        mInsuranceVO=databaseHandler.getInsurance(currentSelectedPatientId,insuranceId);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.verify_otp_layout);
        this.setFinishOnTouchOutside(false);
        try {
            findViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(messageReceiver,
                new IntentFilter(ACTION_OTP_RECEIVED));
        timer = new CountDownTimer(30 * 1000, 1000) {
            int progress = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText((millisUntilFinished / 1000) + " " + "s");
                progressBar.setProgress(progress);
                progress++;
            }
            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                tvResend.setVisibility(View.VISIBLE);
            }
        };
        timer.start();
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvResend.setClickable(false);
                tvResend.setEnabled(false);
                mDialog= CommonUtils.showProgressDialogWithCustomMsg(VerifyMobileNumberActivity.this,getString(R.string.request_for_otp));
                RequestQueue mRequestQueue=AppVolley.getRequestQueue();
                mRequestQueue.add(verifyMobile());
            }
        });

    }

    private StringRequest verifyMobile() {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.verifyMobile+"patientId="+mInsuranceVO.getPatientId()+"&id="+mInsuranceVO.getInsuranceUploadId();
        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tvResend.setVisibility(View.GONE);
                dismissProgressDialog();

                timer = new CountDownTimer(30 * 1000, 1000) {
                    int progress = 0;
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvTime.setText((millisUntilFinished / 1000) + " " + "s");
                        progressBar.setProgress(progress);
                        progress++;
                    }
                    @Override
                    public void onFinish() {
                        progressBar.setProgress(100);
                        tvResend.setVisibility(View.VISIBLE);
                    }
                };
                timer.start();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                tvResend.setClickable(true);
                tvResend.setEnabled(true);
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                if(etOtp.getText().toString()!=null)
                {
                    btSubmit.setClickable(false);
                    btSubmit.setEnabled(false);
                    mDialog= CommonUtils.showProgressDialog(VerifyMobileNumberActivity.this);
                    RequestQueue mRequestQueue=AppVolley.getRequestQueue();
                    mRequestQueue.add(confirmMobile());
                }else
                {
                    AppUtil.showToast(getApplicationContext(),"Please enter OTP");
                }

                break;
            case R.id.iv_cancel:
                finish();
                VerifyMobileNumberActivity.this.overridePendingTransition(R.anim.bottom_top,R.anim.slide_down);
                break;
        }
    }
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_OTP_RECEIVED)) {
                String otpRecived = intent.getStringExtra("OTP");
                if (!TextUtils.isEmpty(otpRecived)) {

                    if (otpRecived.contains("OTP:")) {
                        int string2 = otpRecived.indexOf("OTP:");
                        String otp = otpRecived.substring(string2 + 4, string2 + 10);
                        Log.e("otpRecived - > ", "" + otpRecived);
                        Log.e("otp - > ", "" + otp);
                        progressBar.setVisibility(View.GONE);
                        tvTime.setVisibility(View.GONE);
                        tvAutoVerify.setVisibility(View.GONE);
                        etOtp.setText(otp);//setText(otp);
                        timer.cancel();
                    }

                }
            }
        }
    };
    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        dismissProgressDialog();
        super.onStop();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }

    private StringRequest confirmMobile() {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.confirmMobile+"patientId="+mInsuranceVO.getPatientId()+"&id="+mInsuranceVO.getInsuranceUploadId()+"&otp="+etOtp.getText().toString();

        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE \n" + response);
                btSubmit.setClickable(true);
                btSubmit.setEnabled(true);
                dismissProgressDialog();
                if(response!=null) {
                    VerifyMobileNumberActivity.this.overridePendingTransition(R.anim.bottom_top,R.anim.slide_down);
                    setResult(RESULT_OK);
                    finish();
                    Log.d(TAG, "RESPONSE \n" + response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "******************* onErrorResponse ******************* \n");
                btSubmit.setClickable(true);
                btSubmit.setEnabled(true);
                dismissProgressDialog();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_BAD_REQUEST:
                            AppUtil.showToast(getApplicationContext(),getString(R.string.enter_correct_otp));
                            break;
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showToast(getApplicationContext(),"It seems our Server ");
                            break;
                    }
                }else
                {
                    AppUtil.showToast(getApplicationContext(),getString(R.string.networkError));
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
                15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }
}
