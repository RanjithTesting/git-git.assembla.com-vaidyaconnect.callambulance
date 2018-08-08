package com.patientz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InsurancePolicyCoverageInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "InsurancePolicyCoverageInfoActivity";
    Map<String, String> paramMap;
    Map<String, String> paramMapPaytm;
    Button bt_buy_accident_policy;
    long patientId = 0;
    LinearLayout progressBar;
    private EditText etPromoCode;
    private ProgressDialog mDialog;
    private int finalAmount=Constant.APOLLO_MUNICH_INS_AMOUNT;
    private String couponCode="";
    private Button btApplyPromoCode;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_policy_coverage_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.policy_coverage_info);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directToInsuranceScreen();
            }
        });
        bt_buy_accident_policy = (Button) findViewById(R.id.bt_buy_accident_policy);
        progressBar = (LinearLayout) findViewById(R.id.progress_bar);
        TextView tvReadMore = findViewById(R.id.tv_read_more);
        TextView tvb1 = findViewById(R.id.b1);
        etPromoCode = findViewById(R.id.et_promocode);
        btApplyPromoCode = findViewById(R.id.bt_apply_promocode);
        btApplyPromoCode.setOnClickListener(this);

        TextView tvb2 = findViewById(R.id.b2);
        TextView tvb3 = findViewById(R.id.b3);
        TextView tvb4 = findViewById(R.id.b4);
        tvb1.setText(getString(R.string.label_benefit)+"1 (Sub Limit)");
        tvb1.setVisibility(View.GONE);
        tvb2.setText(getString(R.string.label_benefit)+"2 (Sub Limit)");
        tvb2.setVisibility(View.GONE);

        tvb3.setText(getString(R.string.label_benefit)+"3 (Sub Limit)");
        tvb3.setVisibility(View.GONE);

        tvb4.setText(getString(R.string.label_benefit)+"4 (Sub Limit)");
        tvb4.setVisibility(View.GONE);



        tvReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.INSURANCE_COVERAGE_INFO_TnC_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_COVERAGE_INFO_TnC_CLICKED);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebServiceUrls.apollo_munich_info));
                startActivity(browserIntent);
            }
        });
        bt_buy_accident_policy.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_buy_accident_policy:
                setAccidentBuyButtonClickable(false);
                mDialog= CommonUtils.showProgressDialogWithCustomMsg(InsurancePolicyCoverageInfoActivity.this,getString(R.string.processing_request));
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.INSURANCE_COVERAGE_INFO_BUY_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_COVERAGE_INFO_BUY_CLICKED);
                onStartTransaction();
                break;
            case R.id.bt_apply_promocode:
                if(TextUtils.isEmpty(couponCode))
                {
                    if(!TextUtils.isEmpty(etPromoCode.getText().toString()))
                    {
                        mDialog= CommonUtils.showProgressDialogWithCustomMsg(InsurancePolicyCoverageInfoActivity.this,getString(R.string.verifying_promo_code));
                        RequestQueue mRequestQueue=AppVolley.getRequestQueue();
                        mRequestQueue.add(verifyPromoCode());
                    }else
                    {
                        AppUtil.showToast(getApplicationContext(),"No Promo Code Entered");
                    }
                }else
                {
                    couponCode="";
                    finalAmount=Constant.APOLLO_MUNICH_INS_AMOUNT;
                    etPromoCode.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.outer_space));
                    btApplyPromoCode.setText("APPLY");
                    etPromoCode.getText().clear();
                    etPromoCode.setEnabled(true);
                    etPromoCode.setClickable(true);
                }



                break;
        }
    }

    private void setAccidentBuyButtonClickable(boolean result) {
        bt_buy_accident_policy.setClickable(result);
        bt_buy_accident_policy.setEnabled(result);
    }

    private StringRequest verifyPromoCode() {
        //Test Coupon code AOLDISC
        long patientId=AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.verifyPromoCode+etPromoCode.getText().toString();

        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissProgressDialog();

                if(!TextUtils.isEmpty(response))
                {
                    AppUtil.showToast(getApplicationContext(),"Promo Code Applied!!");
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int discount = jsonObject.getInt("discount");
                        Log.d(TAG,"discount="+discount);
                        couponCode = jsonObject.getString("couponCode");
                        Log.d(TAG,"couponCode="+couponCode);
                        finalAmount=Constant.APOLLO_MUNICH_INS_AMOUNT;
                        finalAmount= (Constant.APOLLO_MUNICH_INS_AMOUNT-discount);
                        Log.d(TAG,"finalAmount="+finalAmount);
                        etPromoCode.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.harvard_crimpson));
                        etPromoCode.setText(couponCode+" - "+"Total: ₹ "+finalAmount);
                        etPromoCode.setEnabled(false);
                        etPromoCode.setClickable(false);
                        btApplyPromoCode.setText("EDIT");
                        //etPromoCode.setText();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppUtil.showToast(getApplicationContext(),getString(R.string.networkError));
                    }
                }else
                {
                    AppUtil.showToast(getApplicationContext(),"Promo Code you had entered doesn't exist");
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
                            AppUtil.showToast(getApplicationContext(),"It seems our server is busy.Please try in sometime");
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
    @Override
    protected void onStop() {
        super.onStop();
        dismissProgressDialog();
    }



    public void onStartTransaction() {
        patientId = AppUtil.getCurrentSelectedPatientId(this);
        paramMap = new HashMap<String, String>();
        paramMapPaytm = new HashMap<String, String>();
        paramMap.put("skuCode", Constant.INSURANCEPAYMENT);
        paramMap.put("service", Constant.INSURANCE);
        paramMap.put("orgId", "");
        paramMap.put("txnRefId", "0");
        paramMap.put("targetProfileId", "" + patientId);
        paramMap.put("appPackageName", getString(R.string.package_name));
        paramMap.put("CUST_ID", "" + patientId);
        paramMap.put("ORDER_DETAILS", "Insurance Policy");
        paramMap.put("couponCode",couponCode);
        paramMap.put("TXN_AMOUNT", String.valueOf(finalAmount));
        paramMapPaytm.put("CUST_ID", "" + patientId);
        paramMapPaytm.put("ORDER_DETAILS", "Insurance Policy");
        paramMapPaytm.put("TXN_AMOUNT", String.valueOf(finalAmount));
        Log.e("paramMap-->", paramMap.toString());
        AppVolley.getRequestQueue().add(getCheckSumHash(paramMap, paramMapPaytm));
    }


    private StringRequest getCheckSumHash(final Map<String, String> paramMap, final Map<String, String> paramMapPaytm) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.generateChecksum;
        Log.e("url...", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST, szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e(TAG, "generateChecksum response : " + response);

                if (response != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String MID = jsonObject1.getString("MID");
                        String CHECKSUMHASH = jsonObject1.getString("CHECKSUMHASH");
                        String ORDER_ID = jsonObject1.getString("ORDER_ID");
                        String INDUSTRY_TYPE_ID = jsonObject1.getString("INDUSTRY_TYPE_ID");
                        String CHANNEL_ID = jsonObject1.getString("CHANNEL_ID");
                        String WEBSITE = jsonObject1.getString("WEBSITE");
                        String PAYMENT_TYPE_ID = jsonObject1.getString("PAYMENT_TYPE_ID");
                        String REQUEST_TYPE = jsonObject1.getString("REQUEST_TYPE");
                        String IS_USER_VERIFIED = jsonObject1.getString("IS_USER_VERIFIED");
                        String CALLBACK_URL = jsonObject1.getString("CALLBACK_URL");
                        String CUST_ID = jsonObject1.getString("CUST_ID");
                        String ORDER_DETAILS = jsonObject1.getString("ORDER_DETAILS");
                        String TXN_AMOUNT = jsonObject1.getString("TXN_AMOUNT");

                        paramMapPaytm.put("MID", MID);
                        paramMapPaytm.put("ORDER_ID", ORDER_ID);
                        paramMapPaytm.put("CHECKSUMHASH", CHECKSUMHASH);
                        paramMapPaytm.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
                        paramMapPaytm.put("CHANNEL_ID", CHANNEL_ID);
                        paramMapPaytm.put("WEBSITE", WEBSITE);

                        paramMapPaytm.put("CUST_ID", CUST_ID);
                        paramMapPaytm.put("ORDER_DETAILS", ORDER_DETAILS);
                        paramMapPaytm.put("TXN_AMOUNT", TXN_AMOUNT);

                        if (jsonObject1.has("EMAIL")) {
                            String EMAIL = jsonObject1.getString("EMAIL");
                            if (EMAIL != null && EMAIL.length() > 0) {
                                paramMapPaytm.put("EMAIL", EMAIL);
                            }
                        }
                        if (jsonObject1.has("MOBILE_NO")) {
                            String MOBILE_NO = jsonObject1.getString("MOBILE_NO");
                            if (MOBILE_NO != null && MOBILE_NO.length() > 0) {
                                paramMapPaytm.put("MOBILE_NO", MOBILE_NO);
                            }
                        }
                        paramMapPaytm.put("PAYMENT_TYPE_ID", PAYMENT_TYPE_ID);
                        paramMapPaytm.put("REQUEST_TYPE", REQUEST_TYPE);
                        paramMapPaytm.put("IS_USER_VERIFIED", IS_USER_VERIFIED);
                        paramMapPaytm.put("CALLBACK_URL", CALLBACK_URL);

                        PaytmOrder Order = new PaytmOrder(paramMapPaytm);
                        PaytmPGService Service;
                        if(BuildConfig.APPLICATION_ID.equalsIgnoreCase("com.patientz.activity"))
                        {
                            Service = PaytmPGService.getProductionService();
                        }else
                        {
                            Service = PaytmPGService.getStagingService();
                        }
                        Service.initialize(Order, null);
                        android.util.Log.e(TAG, "initialize-->");
                        Service.startPaymentTransaction(InsurancePolicyCoverageInfoActivity.this, true, true,
                                new PaytmPaymentTransactionCallback() {
                                    @Override
                                    public void someUIErrorOccurred(String inErrorMessage) {
                                        // Some UI Error Occurred in Payment Gateway Activity.
                                        // // This may be due to initialization of views in
                                        // Payment Gateway Activity or may be due to //
                                        // initialization of webview. // Error Message details
                                        // the error occurred.
                                        android.util.Log.e("111 inErrorMessage1", "" + inErrorMessage);
                                    }

                                    @Override
                                    public void onTransactionResponse(final Bundle bundle) {


                                        android.util.Log.e("111 Success", "" + bundle.toString());
                                        android.util.Log.e("111 orderid", "" + bundle.getString("ORDERID"));
                                        android.util.Log.e("111 status", "" + bundle.getString("STATUS"));
                                        android.util.Log.e("111 txnamount", "" + bundle.getString("TXNAMOUNT"));
                                        android.util.Log.e("111 respmsg", "" + bundle.getString("RESPMSG"));
                                        android.util.Log.e("111 txnid", "" + bundle.getString("TXNDATE"));
                                        android.util.Log.e("111 respcode", "" + bundle.getString("RESPCODE"));
                                        android.util.Log.e("111 banktxnid", "" + bundle.getString("TXNID"));
//                                        android.util.Log.e("111 dateCreated", "" + bundle.getString("dateCreated"));

                                        if (bundle.getString("RESPCODE").equals("01")) {
                                            savePaymentDetailsForRestartability(bundle); // Used to allow user to complete insurance purchase incase if he failed to do so during buying insurance
                                            final Dialog dialog_success = new Dialog(InsurancePolicyCoverageInfoActivity.this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth);
                                            dialog_success.setCancelable(false);
                                            dialog_success.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog_success.setContentView(R.layout.sucess_payment_dialog);
                                            dialog_success.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                                            final TextView closeDailog = (TextView) dialog_success.findViewById(R.id.close_dailog);
                                            TextView tvOrderid = (TextView) dialog_success.findViewById(R.id.tv_orderid);
                                            TextView tvTransid = (TextView) dialog_success.findViewById(R.id.tv_transid);
                                            TextView tvTransAmount = (TextView) dialog_success.findViewById(R.id.tv_trans_amount);
                                            TextView tvTransDate = (TextView) dialog_success.findViewById(R.id.tv_trans_date);
                                            TextView tvTransStatus = (TextView) dialog_success.findViewById(R.id.tv_trans_status);
                                            Button btOk =  dialog_success.findViewById(R.id.bt_ok);


                                            tvOrderid.setText("" + bundle.getString("ORDERID"));
                                            tvTransid.setText("" + bundle.getString("TXNID"));
                                            tvTransAmount.setText("" + bundle.getString("TXNAMOUNT"));
                                            // tvTransDate.setText("" + bundle.getString("dateCreated"));
                                            tvTransStatus.setText("" + bundle.getString("RESPMSG"));

                                            try {
                                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").parse(bundle.getString("TXNDATE"));
                                                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
                                                String date = formatter.format(date1);
                                                Log.e("date---->", date);
                                                tvTransDate.setText(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            btOk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog_success.dismiss();
                                                    Intent mIntent = new Intent(InsurancePolicyCoverageInfoActivity.this, AddInsuredDetailsActivity.class);
                                                    mIntent.putExtra("patientId",patientId);
                                                    mIntent.putExtra("paytm_ref_id",bundle.getString("ORDERID"));
                                                    Log.d("finalAmount",finalAmount+"");
                                                    mIntent.putExtra("totalAmount",finalAmount);
                                                    mIntent.putExtra("couponCode",couponCode);
                                                    startActivity(mIntent);
                                                    finish();
                                                }
                                            });
                                            dialog_success.show();
                                        } else if (bundle.getString("RESPCODE").equals("141") ||
                                                bundle.getString("RESPCODE").equals("227")||
                                                bundle.getString("RESPCODE").equals("8102")||
                                                bundle.getString("RESPCODE").equals("8103")||
                                                bundle.getString("RESPCODE").equals("269")
                                                ) {
                                            showErrorDialog(bundle.getString("RESPMSG"));
                                        }
                                    }

                                    @Override
                                    public void networkNotAvailable() { // If network is not
                                        // available, then this
                                        // method gets called.
                                        android.util.Log.e("111 inErrorMessage2", "inErrorMessage2");
                                    }

                                    @Override
                                    public void clientAuthenticationFailed(String inErrorMessage) {
                                        // This method gets called if client authentication
                                        // failed. // Failure may be due to following reasons //
                                        // 1. Server error or downtime. // 2. Server unable to
                                        // generate checksum or checksum response is not in
                                        // proper format. // 3. Server failed to authenticate
                                        // that client. That is value of payt_STATUS is 2. //
                                        // Error Message describes the reason for failure.
                                        android.util.Log.e("111 inErrorMessage3", "" + inErrorMessage);
                                    }

                                    @Override
                                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                                      String inErrorMessage, String inFailingUrl) {
                                        android.util.Log.e("111 inErrorMessage4", "" + inErrorMessage);
                                    }

                                    // had to be added: NOTE
                                    @Override
                                    public void onBackPressedCancelTransaction() {
                                        // TODO Auto-generated method stub
                                        android.util.Log.e("111 inErrorMessage5", "inErrorMessage5");
                                    }

                                    @Override
                                    public void onTransactionCancel(String s, Bundle bundle) {
                                        android.util.Log.e("111 Cancel", "" + s.toString());
                                    }

                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    AppUtil.showToast(InsurancePolicyCoverageInfoActivity.this, getString(R.string.networkError));
                }
                dismissProgressDialog();
                setAccidentBuyButtonClickable(true);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                setAccidentBuyButtonClickable(true);
                if (error!=null) {
                    int statuscode = -2;
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        statuscode = networkResponse.statusCode;
                        if(statuscode==503) {
                            AppUtil.showErrorCodeDialog(InsurancePolicyCoverageInfoActivity.this);
                        }else
                        {
                            AppUtil.showErrorDialog(InsurancePolicyCoverageInfoActivity.this,error);
                        }
                    }
                }
                android.util.Log.e(TAG, "generateChecksum error response : " + error.getMessage());
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG,"paramMap="+paramMap);
                return paramMap;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000,2,1));
        return mRequest;
    }

    private void savePaymentDetailsForRestartability(Bundle bundle) {
        SharedPreferences mSharedPreferences=getSharedPreferences(String.valueOf(patientId),MODE_PRIVATE);
        SharedPreferences.Editor mEditor=mSharedPreferences.edit();
        mEditor.putLong("patient_id",patientId);
        mEditor.putInt("amount_paid",finalAmount);
        mEditor.putString("coupon_code",couponCode);
        mEditor.putString("paytm_ref_id",bundle.getString("ORDERID"));
        mEditor.commit();
    }

    private void showErrorDialog(String errorMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InsurancePolicyCoverageInfoActivity.this);
        builder.setMessage(errorMsg);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        directToInsuranceScreen();

    }


    private void directToInsuranceScreen() {
        Intent intent = new Intent(InsurancePolicyCoverageInfoActivity.this, ProfileActivity.class);
        intent.putExtra("goTo", Constant.INSURANCE_VIEW_SCREEN);
        startActivity(intent);
        finish();
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

}
