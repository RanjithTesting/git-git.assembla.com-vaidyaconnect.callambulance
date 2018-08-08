package com.patientz.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.patientz.VO.BloodAvailabilityVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BloodAvailabilityActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView plasma;
    Intent mIntent;
    private LinearLayout plasmaExapandable;
    private TextView plasmaAPlus;
    private TextView plasmaAMinus;
    private TextView plasmaBPlus;
    private TextView plasma0Plus;
    private TextView plasma0Minus;
    private TextView plasmaAbPlus;
    private TextView plasmaAbMinus;
    private TextView directed;
    private LinearLayout directedExapandable;
    private TextView directedAPlus;
    private TextView directedAMinus;
    private TextView directedBPlus;
    private TextView directed0Plus;
    private TextView directed0Minus;
    private TextView directedAbPlus;
    private TextView directedAbMinus;
    private TextView autologous;
    private LinearLayout autologousExapandable;
    private TextView autologousAPlus;
    private TextView autologousAMinus;
    private TextView autologousBPlus;
    private TextView autologous0Plus;
    private TextView autologous0Minus;
    private TextView autologousAbPlus;
    private TextView autologousAbMinus;
    private TextView powerId;
    private LinearLayout powerIdExapandable;
    private TextView powerIdAPlus;
    private TextView powerIdAMinus;
    private TextView powerIdBPlus;
    private TextView powerId0Plus;
    private TextView powerId0Minus;
    private TextView powerIdAbPlus;
    private TextView powerIdAbMinus;
    private TextView wholeBlood;
    private LinearLayout wholeBloodExapandable;
    private TextView wholeBloodAPlus;
    private TextView wholeBloodAMinus;
    private TextView wholeBloodBPlus;
    private TextView wholeBlood0Plus;
    private TextView wholeBlood0Minus;
    private TextView wholeBloodAbPlus;
    private TextView wholeBloodAbMinus;
    private TextView platelets;
    private LinearLayout plateletsExapandable;
    private TextView plateletsAPlus;
    private TextView plateletsAMinus;
    private TextView plateletsBPlus;
    private TextView platelets0Plus;
    private TextView platelets0Minus;
    private TextView plateletsAbPlus;
    private TextView plateletsAbMinus;
    private DatabaseHandler dh;
    private TextView tvBloodInfoNotFound;
    private LinearLayout bloodDataLayout;
    private ProgressDialog mProgressDialog;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-05-07 09:31:29 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        plasma = (TextView) findViewById(R.id.plasma);
        plasma.setOnClickListener(this);
        plasmaExapandable = (LinearLayout) findViewById(R.id.plasma_exapandable);
        plasmaAPlus = (TextView) findViewById(R.id.plasma_a_plus);

        plasmaAMinus = (TextView) findViewById(R.id.plasma_a_minus);


        plasmaBPlus = (TextView) findViewById(R.id.plasma_b_plus);

        plasma0Plus = (TextView) findViewById(R.id.plasma_0_plus);

        plasma0Minus = (TextView) findViewById(R.id.plasma_0_minus);

        plasmaAbPlus = (TextView) findViewById(R.id.plasma_ab_plus);

        plasmaAbMinus = (TextView) findViewById(R.id.plasma_ab_minus);

        directed = (TextView) findViewById(R.id.directed);
        directed.setOnClickListener(this);

        directedExapandable = (LinearLayout) findViewById(R.id.directed_exapandable);
        directedAPlus = (TextView) findViewById(R.id.directed_a_plus);

        directedAMinus = (TextView) findViewById(R.id.directed_a_minus);

        directedBPlus = (TextView) findViewById(R.id.directed_b_plus);

        directed0Plus = (TextView) findViewById(R.id.directed_0_plus);

        directed0Minus = (TextView) findViewById(R.id.directed_0_minus);

        directedAbPlus = (TextView) findViewById(R.id.directed_ab_plus);

        directedAbMinus = (TextView) findViewById(R.id.directed_ab_minus);


        autologous = (TextView) findViewById(R.id.autologous);
        autologous.setOnClickListener(this);

        autologousExapandable = (LinearLayout) findViewById(R.id.autologous_exapandable);
        autologousAPlus = (TextView) findViewById(R.id.autologous_a_plus);

        autologousAMinus = (TextView) findViewById(R.id.autologous_a_minus);

        autologousBPlus = (TextView) findViewById(R.id.autologous_b_plus);

        autologous0Plus = (TextView) findViewById(R.id.autologous_0_plus);

        autologous0Minus = (TextView) findViewById(R.id.autologous_0_minus);

        autologousAbPlus = (TextView) findViewById(R.id.autologous_ab_plus);

        autologousAbMinus = (TextView) findViewById(R.id.autologous_ab_minus);


        powerId = (TextView) findViewById(R.id.power_id);
        powerId.setOnClickListener(this);

        powerIdExapandable = (LinearLayout) findViewById(R.id.power_id_exapandable);
        powerIdAPlus = (TextView) findViewById(R.id.power_id_a_plus);

        powerIdAMinus = (TextView) findViewById(R.id.power_id_a_minus);

        powerIdBPlus = (TextView) findViewById(R.id.power_id_b_plus);

        powerId0Plus = (TextView) findViewById(R.id.power_id_0_plus);

        powerId0Minus = (TextView) findViewById(R.id.power_id_0_minus);

        powerIdAbPlus = (TextView) findViewById(R.id.power_id_ab_plus);

        powerIdAbMinus = (TextView) findViewById(R.id.power_id_ab_minus);


        wholeBlood = (TextView) findViewById(R.id.whole_blood);
        wholeBlood.setOnClickListener(this);
        wholeBloodExapandable = (LinearLayout) findViewById(R.id.whole_blood_exapandable);
        wholeBloodAPlus = (TextView) findViewById(R.id.whole_blood_a_plus);

        wholeBloodAMinus = (TextView) findViewById(R.id.whole_blood_a_minus);

        wholeBloodBPlus = (TextView) findViewById(R.id.whole_blood_b_plus);

        wholeBlood0Plus = (TextView) findViewById(R.id.whole_blood_0_plus);

        wholeBlood0Minus = (TextView) findViewById(R.id.whole_blood_0_minus);

        wholeBloodAbPlus = (TextView) findViewById(R.id.whole_blood_ab_plus);

        wholeBloodAbMinus = (TextView) findViewById(R.id.whole_blood_ab_minus);


        platelets = (TextView) findViewById(R.id.platelets);
        platelets.setOnClickListener(this);

        plateletsExapandable = (LinearLayout) findViewById(R.id.platelets_exapandable);
        plateletsAPlus = (TextView) findViewById(R.id.platelets_a_plus);

        plateletsAMinus = (TextView) findViewById(R.id.platelets_a_minus);

        plateletsBPlus = (TextView) findViewById(R.id.platelets_b_plus);

        platelets0Plus = (TextView) findViewById(R.id.platelets_0_plus);

        platelets0Minus = (TextView) findViewById(R.id.platelets_0_minus);

        plateletsAbPlus = (TextView) findViewById(R.id.platelets_ab_plus);

        plateletsAbMinus = (TextView) findViewById(R.id.platelets_ab_minus);
        tvBloodInfoNotFound = (TextView) findViewById(R.id.tv_blood_info_not_found);
        bloodDataLayout = (LinearLayout) findViewById(R.id.blood_data_layout);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_availability_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.blood_availability);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViews();
        dh = DatabaseHandler.dbInit(this);
        mIntent=getIntent();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestQueue mRequestQueue=AppVolley.getRequestQueue();
        mRequestQueue.add(getOrgBranchBloodAvailabilityStringRequest());
    }
    private StringRequest getOrgBranchBloodAvailabilityStringRequest() {
        mProgressDialog=CommonUtils.showProgressDialog(BloodAvailabilityActivity.this);
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.getOrgBranchBloodAvailability +"orgBranchId="+getIntent().getLongExtra(Constant.ORG_BRANCH_ID,0);
        Log.d("getOrgBranchAvailabilityCapabilityList>>url=",szServerUrl);
        final StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getOrgBranchAvailabilityCapabilityList>>response=",response);
                if(!TextUtils.isEmpty(response) && !response.equalsIgnoreCase("OrgBranch does not exist") && !response.equalsIgnoreCase("No Blood Availability Found")) {
                    List<BloodAvailabilityVO> bloodAvailabilityVOS = Arrays.asList(new Gson().fromJson(response, BloodAvailabilityVO[].class));
                    if (bloodAvailabilityVOS != null) {
                        bloodDataLayout.setVisibility(View.VISIBLE);
                        for (BloodAvailabilityVO mBloodAvailabilityVO : bloodAvailabilityVOS) {
                            if (mBloodAvailabilityVO.getBloodComponentType().equalsIgnoreCase("Plasma")) {
                                if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A+")) {
                                    plasmaAPlus.setText("Available");
                                    plasmaAPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));
                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A-")) {
                                    plasmaAMinus.setText("Available");
                                    plasmaAMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));
                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("B+")) {
                                    plasmaBPlus.setText("Available");
                                    plasmaBPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    plasma0Plus.setText("Available");
                                    plasma0Plus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    plasma0Minus.setText("Available");
                                    plasma0Minus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB+")) {
                                    plasmaAbPlus.setText("Available");                                    plasmaAPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));
                                    plasmaAbPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB-")) {
                                    plasmaAbMinus.setText("Available");
                                    plasmaAbMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                }
                            } else if (mBloodAvailabilityVO.getBloodComponentType().equalsIgnoreCase("directed")) {
                                if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A+")) {
                                    directedAPlus.setText("Available");
                                    directedAPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A-")) {
                                    directedAMinus.setText("Available");
                                    directedAMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("B+")) {
                                    directedBPlus.setText("Available");
                                    directedBPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    directed0Plus.setText("Available");
                                    directed0Plus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    directed0Minus.setText("Available");
                                    directed0Minus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB+")) {
                                    directedAbPlus.setText("Available");
                                    directedAbPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB-")) {
                                    directedAbMinus.setText("Available");
                                    directedAbMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                }
                            } else if (mBloodAvailabilityVO.getBloodComponentType().equalsIgnoreCase("autologous")) {
                                if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A+")) {
                                    autologousAPlus.setText("Available");
                                    autologousAPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A-")) {
                                    autologousAMinus.setText("Available");
                                    autologousAMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("B+")) {
                                    autologousBPlus.setText("Available");
                                    autologousBPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    autologous0Plus.setText("Available");
                                    autologous0Plus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    autologous0Minus.setText("Available");
                                    autologous0Minus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB+")) {
                                    autologousAbPlus.setText("Available");
                                    autologousAbPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB-")) {
                                    autologousAbMinus.setText("Available");
                                    autologousAbMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                }
                            } else if (mBloodAvailabilityVO.getBloodComponentType().equalsIgnoreCase("power red")) {
                                if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A+")) {
                                    powerIdAPlus.setText("Available");
                                    powerIdAPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A-")) {
                                    powerIdAMinus.setText("Available");
                                    powerIdAMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("B+")) {
                                    powerIdBPlus.setText("Available");
                                    powerIdBPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    powerId0Plus.setText("Available");
                                    powerId0Plus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    powerId0Minus.setText("Available");
                                    powerId0Minus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB+")) {
                                    powerIdAbPlus.setText("Available");
                                    powerIdAbPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB-")) {
                                    powerIdAbMinus.setText("Available");
                                    powerIdAbMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                }
                            } else if (mBloodAvailabilityVO.getBloodComponentType().equalsIgnoreCase("whole blood")) {
                                if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A+")) {
                                    wholeBloodAPlus.setText("Available");
                                    wholeBloodAPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A-")) {
                                    wholeBloodAMinus.setText("Available");
                                    wholeBloodAMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("B+")) {
                                    wholeBloodBPlus.setText("Available");
                                    wholeBloodBPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    wholeBlood0Plus.setText("Available");
                                    wholeBlood0Plus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    wholeBlood0Minus.setText("Available");
                                    wholeBlood0Minus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB+")) {
                                    wholeBloodAbPlus.setText("Available");
                                    wholeBloodAbPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB-")) {
                                    wholeBloodAbMinus.setText("Available");
                                    wholeBloodAbMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                }
                            } else if (mBloodAvailabilityVO.getBloodComponentType().equalsIgnoreCase("platelets")) {
                                if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A+")) {
                                    plateletsAPlus.setText("Available");
                                    plateletsAPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("A-")) {
                                    plateletsAMinus.setText("Available");
                                    plateletsAMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));

                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("B+")) {
                                    plateletsBPlus.setText("Available");
                                    plateletsBPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    platelets0Plus.setText("Available");
                                    platelets0Plus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("O+")) {
                                    platelets0Minus.setText("Available");
                                    platelets0Minus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB+")) {
                                    plateletsAbPlus.setText("Available");
                                    plateletsAbPlus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));


                                } else if (mBloodAvailabilityVO.getBloodGroup().equalsIgnoreCase("AB-")) {
                                    plateletsAbMinus.setText("Available");
                                    plateletsAbMinus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow_green));
                                }
                            }
                        }
                    }else
                    {
                        tvBloodInfoNotFound.setVisibility(View.VISIBLE);
                        bloodDataLayout.setVisibility(View.GONE);
                    }
                }else
                {
                    tvBloodInfoNotFound.setVisibility(View.VISIBLE);
                    bloodDataLayout.setVisibility(View.GONE);
                }
                dismissProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissProgressDialog();
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(40000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        return mRequest;
    }
    private void dismissProgressDialog() {
        if(mProgressDialog!=null)
        {
            if(mProgressDialog.isShowing())
            {
                mProgressDialog.dismiss();
            }
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.plasma:
                if (plasmaExapandable.getVisibility() == View.GONE) {
                    plasmaExapandable.setVisibility(View.VISIBLE);
                } else {
                    plasmaExapandable.setVisibility(View.GONE);
                }
                break;
            case R.id.plasma_exapandable:
                break;
            case R.id.directed:
                if (directedExapandable.getVisibility() == View.GONE) {
                    directedExapandable.setVisibility(View.VISIBLE);
                } else {
                    directedExapandable.setVisibility(View.GONE);
                }
                break;
            case R.id.directed_exapandable:
                break;

            case R.id.autologous:
                if (autologousExapandable.getVisibility() == View.GONE) {
                    autologousExapandable.setVisibility(View.VISIBLE);
                } else {
                    autologousExapandable.setVisibility(View.GONE);
                }
                break;
            case R.id.autologous_exapandable:
                break;

            case R.id.power_id:
                if (powerIdExapandable.getVisibility() == View.GONE) {
                    powerIdExapandable.setVisibility(View.VISIBLE);
                } else {
                    powerIdExapandable.setVisibility(View.GONE);
                }
                break;
            case R.id.power_id_exapandable:
                break;

            case R.id.whole_blood:
                if (wholeBloodExapandable.getVisibility() == View.GONE) {
                    wholeBloodExapandable.setVisibility(View.VISIBLE);
                } else {
                    wholeBloodExapandable.setVisibility(View.GONE);
                }
                break;
            case R.id.whole_blood_exapandable:
                break;

            case R.id.platelets:
                if (plateletsExapandable.getVisibility() == View.GONE) {
                    plateletsExapandable.setVisibility(View.VISIBLE);
                } else {
                    plateletsExapandable.setVisibility(View.GONE);
                }
                break;
            case R.id.platelets_exapandable:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissProgressDialog();
    }
}