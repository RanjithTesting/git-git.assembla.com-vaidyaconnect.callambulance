package com.patientz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.patientz.VO.EmergencyStatusVO;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.PublicProviderVO;
import com.patientz.adapters.AdapterNetworkAPList;
import com.patientz.adapters.AdapterNonNetworkAPList;
import com.patientz.adapters.AdapterPreferredHosiptalsList;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class OrgBranchesActivity extends BaseActivity {

    private static final String TAG = "AmbulanceProvidersEmergencyList";
    private RecyclerView lv1, lvNetworkHospitals, lvNonNetworkHospitals;
    private LinearLayout parentLv1, progressBar;
    long millisUntilFinishedd = 15000;
    private CountDownTimer countDownTimer;
    private TextView tvTimerCount, tvNonNetworkHospitalEmpty, tvNetworkHospitalEmpty, tvPhospitalEmpty;
    private RequestQueue mRequestQueue;
    private DatabaseHandler mDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_providers_emergency_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setPageTitle(toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
        final PublicProviderVO mPublicProviderVO = AppUtil.getPublicEmergencyProvider(getApplicationContext());
        lv1 = (RecyclerView) findViewById(R.id.rv_preferred_hospitals);
        lvNetworkHospitals = (RecyclerView) findViewById(R.id.list_network_hospital);
        lvNonNetworkHospitals = (RecyclerView) findViewById(R.id.list_non_network_hospital);
        CardView parent_lvvv = (CardView) findViewById(R.id.parent_lvvv);
        TextView tvPublicProviderName = (TextView) findViewById(R.id.tv_public_provider_name);
        //parentLv1 = (LinearLayout) findViewById(R.id.parent_lv1);
        tvTimerCount = (TextView) findViewById(R.id.tvTimer);
        TextView tvEmergencyNo = (TextView) findViewById(R.id.tv_emergency_no);
        progressBar = (LinearLayout) findViewById(R.id.progress_bar);
        tvPhospitalEmpty = (TextView) findViewById(R.id.tv_ph_empty);
        tvNetworkHospitalEmpty = (TextView) findViewById(R.id.tv_nh_empty);
        tvNonNetworkHospitalEmpty = (TextView) findViewById(R.id.tv_nnh_empty);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        lv1.setHasFixedSize(true);
        lvNetworkHospitals.setHasFixedSize(true);
        lvNonNetworkHospitals.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(this);
        lv1.setLayoutManager(mLayoutManager);
        lvNetworkHospitals.setLayoutManager(mLayoutManager2);
        lvNonNetworkHospitals.setLayoutManager(mLayoutManager3);

        tvEmergencyNo.setText("Calling " + (mPublicProviderVO != null ? mPublicProviderVO.getEmergencyPhoneNo() : "108") + " in ");
        setPublicAmbulanceProvider(parent_lvvv, tvPublicProviderName, mPublicProviderVO);
        setPreferredOrgBranch();
        setNetworkOrgBranches();
        setNonNetworkOrgBranches();

        inititateTimeCounter();

    }

    private void setPageTitle(Toolbar toolbar) {
        SharedPreferences mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPreferences.getBoolean(Constant.IS_TEST_EMERGENCY, false)) {
            toolbar.setTitle(R.string.test);
        } else {
            toolbar.setTitle(R.string.title_activity_ambulance_providers_emergency_list);
        }
    }

    private void setNonNetworkOrgBranches() {
        EmergencyStatusVO userInEmergency = mDatabaseHandler.getUserNotInEmergency();
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(userInEmergency.getPatientId());
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        long preferredOrgId = databaseHandlerAssetHelper.getPreferredOrgId(preferredOrgBranchId);
        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
        List<OrgBranchVO> activeNonNetworkOrgBranchesList = databaseHandlerAssetHelper.getAllActiveNonNetworkOrgBranchesList(getApplicationContext(), preferredOrgId, "Hospital", currentLocationLat, currentLocationLon); // getAllActiveNetworkOrgBranchesList within 5kms radius
        Log.d(TAG, "List size=" + activeNonNetworkOrgBranchesList.size());
        Collections.sort(activeNonNetworkOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });

        if (activeNonNetworkOrgBranchesList.size() > 10)
            activeNonNetworkOrgBranchesList = activeNonNetworkOrgBranchesList.subList(0, 10);


        Log.d(TAG, "Non-Active network org branches=" + activeNonNetworkOrgBranchesList.size());
        if (activeNonNetworkOrgBranchesList.size() != 0) {
            tvNonNetworkHospitalEmpty.setVisibility(View.GONE);
            AdapterNonNetworkAPList adapterNonNetworkAPList = new AdapterNonNetworkAPList(OrgBranchesActivity.this, activeNonNetworkOrgBranchesList, countDownTimer);
            lvNonNetworkHospitals.setAdapter(adapterNonNetworkAPList);
        } else {
            tvNonNetworkHospitalEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void setNetworkOrgBranches() {
        EmergencyStatusVO userInEmergency = mDatabaseHandler.getUserNotInEmergency();
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(userInEmergency.getPatientId());
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        long preferredOrgId = databaseHandlerAssetHelper.getPreferredOrgId(preferredOrgBranchId);
        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
        List<OrgBranchVO> activeNetworkOrgBranchesList = databaseHandlerAssetHelper.getAllActiveNetworkOrgBranchesList(getApplicationContext(), preferredOrgId, "Hospital", currentLocationLat, currentLocationLon);
        Collections.sort(activeNetworkOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });
        Log.d(TAG, "List size=" + activeNetworkOrgBranchesList.size());

        if (activeNetworkOrgBranchesList.size() > 10)
            activeNetworkOrgBranchesList = activeNetworkOrgBranchesList.subList(0, 10);

        if (activeNetworkOrgBranchesList.size() != 0) {
            tvNetworkHospitalEmpty.setVisibility(View.GONE);
            AdapterNetworkAPList adapterNetworkAPList = new AdapterNetworkAPList(OrgBranchesActivity.this, activeNetworkOrgBranchesList, countDownTimer);
            lvNetworkHospitals.setAdapter(adapterNetworkAPList);
        } else {
            tvNetworkHospitalEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void setPreferredOrgBranch() {

        EmergencyStatusVO userInEmergency = mDatabaseHandler.getUserNotInEmergency();
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(userInEmergency.getPatientId());
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        long preferredOrgId = databaseHandlerAssetHelper.getPreferredOrgId(preferredOrgBranchId);
        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, MODE_PRIVATE);
        Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
        List<OrgBranchVO> preferredOrgBranchesList = databaseHandlerAssetHelper.getPreferredOrgBranchesList(getApplicationContext(), currentLocationLat, currentLocationLon, preferredOrgId, "Hospital");

        Collections.sort(preferredOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });
        Log.d(TAG, "List size=" + preferredOrgBranchesList.size());

        if (preferredOrgBranchesList.size() > 3)
            preferredOrgBranchesList = preferredOrgBranchesList.subList(0, 3);
        if (preferredOrgBranchesList.size() != 0) {
            tvPhospitalEmpty.setVisibility(View.GONE);
            AdapterPreferredHosiptalsList adapterNetworkAPList = new AdapterPreferredHosiptalsList(OrgBranchesActivity.this, preferredOrgBranchesList, countDownTimer);
            lv1.setAdapter(adapterNetworkAPList);
        } else {
            tvPhospitalEmpty.setVisibility(View.VISIBLE);
        }

    }


    private void setPublicAmbulanceProvider(CardView parent_lvvv, TextView tvPublicProviderName, final PublicProviderVO mPublicProviderVO) {
        tvPublicProviderName.setText(mPublicProviderVO != null ? mPublicProviderVO.getDisplayName() : "");
        parent_lvvv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                createUpshotEvent();
                Intent intent = new Intent(OrgBranchesActivity.this,
                        ActivityEmergencyStepsListener.class);
                intent.putExtra("ambulance_provider_name", mPublicProviderVO.getDisplayName());
                intent.putExtra("ambulance_provider_pno", mPublicProviderVO.getEmergencyPhoneNo());
                Log.d(TAG, "AMB PROV NAME=" + mPublicProviderVO.getDisplayName());
                Log.d(TAG, "AMB PROV NO=" + mPublicProviderVO.getEmergencyPhoneNo());

                startActivity(intent);
                finish();
            }
        });

    }


    private void inititateTimeCounter() {

        countDownTimer = new CountDownTimer(millisUntilFinishedd, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (mRequestQueue != null) {
                    mRequestQueue.cancelAll("AMB_LIST_WEBSERVICE_TAG");
                }
                if (mRequestQueue != null) {
                    mRequestQueue.cancelAll(Constant.TAG_GOOGLE_DISTANCE_CALL);
                }
                millisUntilFinishedd = millisUntilFinished;
                Log.d(TAG, "Starting emergency call in  "
                        + millisUntilFinished);
                Log.d(TAG, "Starting emergency call in  "
                        + (millisUntilFinished) / 1000 + " s");
                tvTimerCount.setText(" " + ((millisUntilFinished) / 1000) + "s");
            }

            @Override
            public void onFinish() {
                createUpshotEvent();
                final PublicProviderVO mPublicProviderVO = AppUtil.getPublicEmergencyProvider(getApplicationContext());
                Intent mIntent = new Intent(OrgBranchesActivity.this, ActivityEmergencyStepsListener.class);
                Log.d(TAG, "APNOoo=" + mPublicProviderVO.getEmergencyPhoneNo());
                Log.d(TAG, "ANameeee=" + mPublicProviderVO.getDisplayName());

                mIntent.putExtra("ambulance_provider_name", mPublicProviderVO.getDisplayName());
                mIntent.putExtra("ambulance_provider_pno", mPublicProviderVO.getEmergencyPhoneNo());
                startActivity(mIntent);
                finish();
            }

        };
        countDownTimer.start();
    }

    private void createUpshotEvent() {
        HashMap<String, Object> upshotData = new HashMap<>();
        upshotData.put(Constant.UpshotEvents.EMERGENCY_AMBULANCE_PROVIDER_SELECTED, true);
        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_AMBULANCE_PROVIDER_SELECTED);
        Log.d(TAG, "upshot data=" + upshotData.entrySet());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        millisUntilFinishedd = savedInstanceState.getLong("counterTimeRemaining");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("counterTimeRemaining", millisUntilFinishedd);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll("AMB_LIST_WEBSERVICE_TAG");
        }
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(Constant.TAG_GOOGLE_DISTANCE_CALL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_kill:
                Log.d(TAG, "Killing Activity");
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
