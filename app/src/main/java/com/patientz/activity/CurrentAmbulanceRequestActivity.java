package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.patientz.VO.EmergencyInfoVO;
import com.patientz.VO.ResponseVO;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

import static com.patientz.utils.Constant.STATUS_DROPPED;
import static com.patientz.utils.Constant.STATUS_IN_USE;
import static com.patientz.utils.Constant.STATUS_OFFLINE;
import static com.patientz.utils.Constant.STATUS_ON_MY_WAY;
import static com.patientz.utils.Constant.STATUS_PICKED_UP;
import static com.patientz.utils.Constant.STATUS_READY;

public class CurrentAmbulanceRequestActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "CurrentAmbulanceRequestActivity";
    private RelativeLayout parentLayoutCurrentStatus;
    private TextView tvOrgName;
    private TextView tvManagerName,tvCallDriver;
    private TextView tvDriverName, tvCurrentStatus,tvPriceFirst_5kms,tvPriceAfter_5kms,tvAmbulanceNo,tvLabelDriverDist,tvLabelDriverTimeToReach;
    private LinearLayout mLoaderStatusView;
    private EmergencyInfoVO mEmergencyInfoVO;
    private RelativeLayout rootView;
    private RequestQueue mRequestQueue;
    private ImageView ivLocation;
    private RelativeLayout rlAmbDetails;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_ambulance_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("Ambulance Request");

        Intent mIntent=getIntent();
        mEmergencyInfoVO=(EmergencyInfoVO) mIntent.getSerializableExtra(Constant.KEY_SERIALIZED_EIVO);
        mRequestQueue=AppVolley.getRequestQueue();
        parentLayoutCurrentStatus = (RelativeLayout) findViewById(R.id.parent_layout_current_status);
        tvOrgName = (TextView) findViewById(R.id.tv_org_name);
        tvManagerName = (TextView) findViewById(R.id.tv_manager_name);
        tvDriverName = (TextView) findViewById(R.id.tv_driver_name);
        tvCurrentStatus = (TextView) findViewById(R.id.tv_current_status);
        tvPriceFirst_5kms = (TextView) findViewById(R.id.tv_price_first_5kms);
        tvPriceAfter_5kms = (TextView) findViewById(R.id.tv_price_after_5kms);
        tvCallDriver = (TextView) findViewById(R.id.tv_call_driver);
        tvAmbulanceNo = (TextView) findViewById(R.id.tv_ambulance_no);
        tvLabelDriverDist = (TextView) findViewById(R.id.tv_label_driver_dist);
        tvLabelDriverTimeToReach = (TextView) findViewById(R.id.tv_label_driver_time_to_reach);
        rlAmbDetails = (RelativeLayout) findViewById(R.id.rl_ambulance_Details);

        ivLocation = (ImageView) findViewById(R.id.iv_location);
        ivLocation.setOnClickListener(this);




        mLoaderStatusView = (LinearLayout) findViewById(R.id.progressBar);
        rootView = (RelativeLayout) findViewById(R.id.root_view);
        populateData(mEmergencyInfoVO);
    }

    private StringRequest createTimeCalRequestToDestination(Double currentLocationLat, Double currentLocationLon,String destinationLat,String destinationLong) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+currentLocationLat+","+currentLocationLon+"&destination="+destinationLat+","+destinationLong+"&mode=driving"+"&key=" + getApplicationContext().getString(R.string.server_api_key);
        Log.d(TAG,"createTimeCalRequestToDestination>>"+url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ResponseVO responseVO = new ResponseVO();
                Log.d(TAG, "createTimeCalRequestToDestination response " + response);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equalsIgnoreCase("OK"))
                    {
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        JSONObject zero = routes.getJSONObject(0);
                        JSONArray legs = zero.getJSONArray("legs");
                        JSONObject duration=legs.getJSONObject(0).getJSONObject("duration");
                        JSONObject distance=legs.getJSONObject(0).getJSONObject("distance");

                        String timeToReachDest= (String) duration.get("text");
                        String distanceInKms= (String) distance.get("text");
                        tvLabelDriverDist.setText(getString(R.string.label_driver_dist)+":"+"\t"+distanceInKms);
                        tvLabelDriverTimeToReach.setText(getString(R.string.label_driver_time)+":"+"\t"+timeToReachDest);
                    }
                } catch (JSONException e1) {
                    Log.d(TAG, "exc=" + e1.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(CurrentAmbulanceRequestActivity.this);
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
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        return mRequest;
    }
    private void populateData(EmergencyInfoVO mEmergencyInfoVO) {
        setDriverStatus(mEmergencyInfoVO.getDriverStatus());
        tvOrgName.setText(mEmergencyInfoVO.getAmbulanceProvider().getName());
        tvManagerName.setText(mEmergencyInfoVO.getAmbulanceManagerProfile().getDisplayName());
        if (!TextUtils.isEmpty(mEmergencyInfoVO.getDriverName())) {
            tvDriverName.setText(mEmergencyInfoVO.getDriverName());
        }


        if (mEmergencyInfoVO.getMinFare() != 0) {
            tvPriceFirst_5kms.setText("Rs." + mEmergencyInfoVO.getMinFare());
        }
        if (mEmergencyInfoVO.getPriceRate() != 0) {
            tvPriceAfter_5kms.setText("Rs." + mEmergencyInfoVO.getPriceRate()+"/Km");
        }
        if(TextUtils.isEmpty(mEmergencyInfoVO.getDriverPhoneNo()))
        {
            tvCallDriver.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ambulance_request_call_disabled, 0, 0);
        }else
        {
            tvCallDriver.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.request_ambulance_call_enabled, 0, 0);
        }
        if(mEmergencyInfoVO.getDriverStatus()!=0) {
            rlAmbDetails.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mEmergencyInfoVO.getAmbulanceNo())) {
                tvAmbulanceNo.setText(mEmergencyInfoVO.getAmbulanceNo());
            }
            SharedPreferences mSharedPreferences = getApplicationContext()
                    .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
            Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
            Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
            mRequestQueue.add(createTimeCalRequestToDestination(currentLocationLat, currentLocationLon, mEmergencyInfoVO.getDriverCurrentLat(), mEmergencyInfoVO.getDriverCurrentLong()));
        }else
        {
            rlAmbDetails.setVisibility(View.GONE);
        }
    }

    private void setDriverStatus(int driverStatus) {

        switch (driverStatus)
        {
            case STATUS_OFFLINE:
                tvCurrentStatus.setText("OFFLINE");
                break;
            case STATUS_READY:
                tvCurrentStatus.setText("READY");
                break;
            case STATUS_IN_USE:
                tvCurrentStatus.setText("IN USE");
                break;
            case STATUS_ON_MY_WAY:
                tvCurrentStatus.setText("ON THE WAY");
                break;
            case STATUS_PICKED_UP:
                tvCurrentStatus.setText("PICKED UP THE PATIENT");
                break;
            case STATUS_DROPPED:
                tvCurrentStatus.setText("DROPPED PATIENT AT DESTINATION");
                break;
        }

    }
    public void callManager(View view)
    {
        AppUtil.call(getApplicationContext(),mEmergencyInfoVO.getAmbulanceManagerProfile().getTelMobile());
    }
    public void callDriver(View view)
    {
        if(!TextUtils.isEmpty(mEmergencyInfoVO.getDriverPhoneNo())) {
            AppUtil.call(getApplicationContext(), mEmergencyInfoVO.getDriverPhoneNo());
        }


    }
    public void cancelRequest(View view)
    {
        cancelAmbulanceRequest();
    }
    public void cancelAmbulanceRequest()
    {

        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        String params = "?latitude=" + mSharedPreferences.getString("lat", "0") + "&longitude=" + mSharedPreferences.getString("lon", "0") + "&revokeReason=" +"";
        mRequestQueue.add(cancelAmbulanceRequest(params));
    }

    private StringRequest cancelAmbulanceRequest(String params) {
        //showProgress(true);
        progressDialog= CommonUtils.showProgressDialogWithCustomMsg(CurrentAmbulanceRequestActivity.this,getString(R.string.wait_msg));
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.cancelAmbulanceRequest + params;
        Log.d("Webservice:cancelAmbulanceRequest:url ", szServerUrl);
        final StringRequest mRequest = new StringRequest(Request.Method.PUT,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //showProgress(false);
                progressDialog.dismiss();
                Log.d(TAG, "Webservice:cancelAmbulanceRequest:response" + response);
                AppUtil.showToast(getApplicationContext(), getString(R.string.cancel_ambulance_request_success));
                finish();
                Intent mIntent=new Intent(CurrentAmbulanceRequestActivity.this,NearbyAmbulancesListActivityNew.class);
                startActivity(mIntent);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(CurrentAmbulanceRequestActivity.this);
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
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);

        return mRequest;
    }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh:

                if (AppUtil.isOnline(getApplicationContext())) {
                    mRequestQueue.add(getCurrentAmbulanceRequest());
                    //mRequestQueue.add(getCurrentOfflineAmbulanceRequest());

                } else {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.network_error));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private StringRequest getCurrentAmbulanceRequest() {
        //showProgress(true);
        progressDialog= CommonUtils.showProgressDialog(CurrentAmbulanceRequestActivity.this);
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.getCurrentAmbulanceRequest;
        Log.d("Webservice:getCurrentAmbulanceRequest:url ", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //showProgress(false);
                progressDialog.dismiss();
                Log.d(TAG, "Webservice:getCurrentAmbulanceRequest:response" + response);
                if(!TextUtils.isEmpty(response))
                {
                    /*recyclerView.setVisibility(View.GONE);
                    llLocality.setVisibility(View.GONE);*/
                    try {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<EmergencyInfoVO>() {
                        }.getType();
                        mEmergencyInfoVO = gson.fromJson(
                                response, objectType);

                        Intent intent = new Intent(CurrentAmbulanceRequestActivity.this, CurrentAmbulanceRequestActivity.class);
                        intent.putExtra(Constant.KEY_SERIALIZED_EIVO,(Serializable) mEmergencyInfoVO);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //showProgress(false);
                progressDialog.dismiss();
                String message;
                Log.i("error1 - ", "" + android.os.Build.VERSION.SDK_INT);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(CurrentAmbulanceRequestActivity.this);
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
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        return mRequest;
    }
    private StringRequest getCurrentOfflineAmbulanceRequest() {
       // showProgress(true);
        progressDialog= CommonUtils.showProgressDialog(CurrentAmbulanceRequestActivity.this);
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.getCurrentAmbulanceRequest;
        Log.d("Webservice:getCurrentAmbulanceRequest:url ", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //showProgress(false);
                progressDialog.dismiss();
                Log.d(TAG, "Webservice:getCurrentAmbulanceRequest:response" + response);
                if(!TextUtils.isEmpty(response))
                {
                    /*recyclerView.setVisibility(View.GONE);
                    llLocality.setVisibility(View.GONE);*/
                    try {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<EmergencyInfoVO>() {
                        }.getType();
                        mEmergencyInfoVO = gson.fromJson(
                                response, objectType);

                        Intent intent = new Intent(CurrentAmbulanceRequestActivity.this, CurrentAmbulanceRequestActivity.class);
                        intent.putExtra(Constant.KEY_SERIALIZED_EIVO,(Serializable) mEmergencyInfoVO);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //showProgress(false);
                progressDialog.dismiss();
                String message;
                Log.i("error1 - ", "" + android.os.Build.VERSION.SDK_INT);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(CurrentAmbulanceRequestActivity.this);
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
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        return mRequest;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_location:
                if(mEmergencyInfoVO!=null) {
                    Uri gmmIntentUri = Uri.parse("geo:"+mEmergencyInfoVO.getDriverCurrentLat()+","+mEmergencyInfoVO.getDriverCurrentLong());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
                break;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(Constant.CANCEL_VOLLEY_REQUEST);
        }
        if(progressDialog!=null)
        {
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
    }
}


