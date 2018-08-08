package com.patientz.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.patientz.VO.OrgBranchVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivityNearbyHospitals extends BaseActivity {
    private static final String TAG = "ActivityNearbyHospitals";
    private RecyclerView lv1, lvNetworkHospitals, lvNonNetworkHospitals;
    private LinearLayout parentLv1, progressBar;
    long millisUntilFinishedd = 15000;
    private CountDownTimer countDownTimer;
    private TextView tvPhEmpty, tvEmptyRV1, tvEmptyRV2;
    private RequestQueue mRequestQueue;
    private DatabaseHandler mDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
         mDatabaseHandler=DatabaseHandler.dbInit(getApplicationContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        tvEmptyRV1 = (TextView) findViewById(R.id.tv_nh_empty);
        tvEmptyRV2 = (TextView) findViewById(R.id.tv_nnh_empty);
        tvPhEmpty = (TextView) findViewById(R.id.tv_ph_empty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.title_activity_nearby);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvNetworkHospitals = (RecyclerView) findViewById(R.id.list_network_hospital);
        lvNonNetworkHospitals = (RecyclerView) findViewById(R.id.list_non_network_hospital);
        lv1 = (RecyclerView) findViewById(R.id.rv_preferred_hospitals);

        lv1.setHasFixedSize(true);
        lvNetworkHospitals.setHasFixedSize(true);
        lvNonNetworkHospitals.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(this);
        lv1.setLayoutManager(mLayoutManager1);
        lvNetworkHospitals.setLayoutManager(mLayoutManager2);
        lvNonNetworkHospitals.setLayoutManager(mLayoutManager3);
        setPreferredOrgBranch();
        setNetworkOrgBranches();
        setNonNetworkOrgBranches();

        //inititateTimeCounter();

    }

    private void setNonNetworkOrgBranches() {
        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
        long patientId=AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(patientId);
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        long preferredOrgId=databaseHandlerAssetHelper.getPreferredOrgId(preferredOrgBranchId);
        List<OrgBranchVO> activeNonNetworkOrgBranchesList = databaseHandlerAssetHelper.getAllActiveNonNetworkOrgBranchesList(getApplicationContext(),preferredOrgId,"Hospital",currentLocationLat,currentLocationLon); // getAllActiveNetworkOrgBranchesList within 5kms radius
        Collections.sort(activeNonNetworkOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });
          Log.d(TAG,"List size="+activeNonNetworkOrgBranchesList.size());
        if (activeNonNetworkOrgBranchesList.size() > 10)
            activeNonNetworkOrgBranchesList= activeNonNetworkOrgBranchesList.subList(0,10);

        Log.d(TAG, "Non-Active network org branches=" + activeNonNetworkOrgBranchesList.size());
        if (activeNonNetworkOrgBranchesList.size() != 0) {
            tvEmptyRV2.setVisibility(View.GONE);
            //AdapterNearbyHospitals adapterNonNetworkAPList = new AdapterNearbyHospitals(ActivityNearbyHospitals.this, activeNonNetworkOrgBranchesList, latLng);
            //lvNonNetworkHospitals.setAdapter(adapterNonNetworkAPList);
        } else {
            tvEmptyRV2.setVisibility(View.VISIBLE);
        }

    }

    private void setNetworkOrgBranches() {
        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
        long patientId=AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(patientId);
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        long preferredOrgId=databaseHandlerAssetHelper.getPreferredOrgId(preferredOrgBranchId);
        List<OrgBranchVO> activeNetworkOrgBranchesList = databaseHandlerAssetHelper.getAllActiveNetworkOrgBranchesList(getApplicationContext(),preferredOrgId,"Hospital",currentLocationLat,currentLocationLon);
        Log.d(TAG, "Active network org branches=" + activeNetworkOrgBranchesList.size());
        Collections.sort(activeNetworkOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });
        Log.d(TAG,"List size="+activeNetworkOrgBranchesList.size());

        if (activeNetworkOrgBranchesList.size() > 10)
            activeNetworkOrgBranchesList= activeNetworkOrgBranchesList.subList(0,10);

        if (activeNetworkOrgBranchesList.size() != 0) {
            tvEmptyRV1.setVisibility(View.GONE);
        } else {
            tvEmptyRV1.setVisibility(View.VISIBLE);
        }
    }


    private void setPreferredOrgBranch() {
        long patientId=AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(patientId);
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        long preferredOrgId=databaseHandlerAssetHelper.getPreferredOrgId(preferredOrgBranchId);
        SharedPreferences mSharedPreferences = getApplicationContext()
                .getSharedPreferences(Constant.COMMON_SP_FILE, MODE_PRIVATE);
        Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));
        List<OrgBranchVO> preferredOrgBranchesList = databaseHandlerAssetHelper.getPreferredOrgBranchesList(getApplicationContext(), currentLocationLat, currentLocationLon, preferredOrgId,"Hospital");
        Collections.sort(preferredOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });

        Log.d(TAG,"List size="+preferredOrgBranchesList.size());

        if (preferredOrgBranchesList.size() > 3)
            preferredOrgBranchesList= preferredOrgBranchesList.subList(0,3);

        if (preferredOrgBranchesList.size() != 0) {
            tvPhEmpty.setVisibility(View.GONE);
        } else {
            tvPhEmpty.setVisibility(View.VISIBLE);
        }
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
