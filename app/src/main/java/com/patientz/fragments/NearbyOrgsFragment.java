package com.patientz.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.AED;
import com.patientz.VO.DoctorSearchVO;
import com.patientz.VO.OrgBranchVO;
import com.patientz.activity.R;
import com.patientz.adapters.AEDsAdapter;
import com.patientz.adapters.AdapterNearbyHospitals;
import com.patientz.adapters.NearbyDoctorsListAdapter;
import com.patientz.adapters.NearbyOrgBranchesAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NearbyOrgsFragment extends BaseFragment {

    private static final String TAG = "NearbyOrgsFragment";
    private RecyclerView lv1, lvNetworkHospitals, lvNonNetworkHospitals;
    private TextView tvPhEmpty, tvEmptyRV1, tvEmptyRV2;
    private LoadDataAsync loadDataAsync;
    private View mLoaderStatusView;
    private NestedScrollView layout;
    private LinearLayout recyclerView1, recyclerView2, recyclerView3, recyclerView4;
    private List<OrgBranchVO> preferredHospitalsList, networkHospitalsList, nonNetworkHospitalsList;
    TextView labelPreferredOrg;
    TextView labelNetworkOrgs;
    TextView labelNonNetworkOrgs;
    private DatabaseHandler mDatabaseHandler;
    private long patientId;
    private DatabaseHandlerAssetHelper databaseHandlerAssetHelper;
;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_nearby, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        patientId = AppUtil.getCurrentSelectedPatientId(getActivity());
        mDatabaseHandler = DatabaseHandler.dbInit(getActivity());

        databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getActivity());
        mLoaderStatusView = view.findViewById(R.id.loading_status);
        recyclerView1 = (LinearLayout) view.findViewById(R.id.layout1);
        recyclerView2 = (LinearLayout) view.findViewById(R.id.layout2);
        recyclerView3 = (LinearLayout) view.findViewById(R.id.layout3);
        recyclerView4 = (LinearLayout) view.findViewById(R.id.layout4);

        layout = (NestedScrollView) view.findViewById(R.id.layout);
        labelPreferredOrg = (TextView) view.findViewById(R.id.label_preferred_org);
        labelNetworkOrgs = (TextView) view.findViewById(R.id.label_nearby_orgs);
        labelNonNetworkOrgs = (TextView) view.findViewById(R.id.label_nearby_non_network_orgs);

        tvEmptyRV1 = (TextView) view.findViewById(R.id.tv_nh_empty);
        tvEmptyRV2 = (TextView) view.findViewById(R.id.tv_nnh_empty);
        tvPhEmpty = (TextView) view.findViewById(R.id.tv_ph_empty);
        lvNetworkHospitals = (RecyclerView) view.findViewById(R.id.list_network_hospital);
        lvNonNetworkHospitals = (RecyclerView) view.findViewById(R.id.list_non_network_hospital);
        lv1 = (RecyclerView) view.findViewById(R.id.rv_preferred_hospitals);

        lv1.setHasFixedSize(true);
        lvNetworkHospitals.setHasFixedSize(true);
        lvNonNetworkHospitals.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getActivity());
        lv1.setLayoutManager(mLayoutManager1);
        lvNetworkHospitals.setLayoutManager(mLayoutManager2);
        lvNonNetworkHospitals.setLayoutManager(mLayoutManager3);


        Bundle mBundle = getArguments();
        int currentSelectedTab = mBundle.getInt("current_tab_position");
        LatLng latLng = mBundle.getParcelable("latLon");
        Log.d(TAG, "currentSelectedTab=" + currentSelectedTab);
        Log.d(TAG, "latLng=" + latLng);
        if(latLng!=null)
        {
        switch (currentSelectedTab) {
            case 0:
                Log.d(TAG,"Loading tab="+currentSelectedTab);
                loadDataAsync = new LoadDataAsync(latLng, Constant.ORG_TYPE_URL_HOSPITAL);
                loadDataAsync.execute();
                labelPreferredOrg.setText(getString(R.string.labelPreferredHospital));
                labelNetworkOrgs.setText(getString(R.string.labelNetworkHospital));
                labelNonNetworkOrgs.setText(getString(R.string.labelNonNetworkHospital));
                break;
            case 3:
                Log.d(TAG,"Loading tab="+currentSelectedTab);

                loadDataAsync = new LoadDataAsync(latLng, Constant.ORG_TYPE_URL_BLOOD_BANK);
                loadDataAsync.execute();
                labelPreferredOrg.setText(getString(R.string.labelPreferredBloodBank));
                labelNetworkOrgs.setText(getString(R.string.labelNetworkBloodBanks));
                labelNonNetworkOrgs.setText(getString(R.string.labelNonNetworkBloodBanks));
                break;
            case 4:
                Log.d(TAG,"Loading tab="+currentSelectedTab);

                loadDataAsync = new LoadDataAsync(latLng, Constant.ORG_TYPE_URL_MEDICAL_STORE);
                loadDataAsync.execute();
                labelNetworkOrgs.setVisibility(View.GONE);
                labelNonNetworkOrgs.setVisibility(View.GONE);
               /* labelNetworkOrgs.setText(getString(R.string.labelNetworkPharmecies));
                labelNonNetworkOrgs.setText(getString(R.string.labelNonNetworkPharmecies));*/
                break;
            case 5:
                Log.d(TAG,"Loading tab="+currentSelectedTab);

                loadDataAsync = new LoadDataAsync(latLng, Constant.ORG_TYPE_URL_DIAGNOSTIC_LAB);
                loadDataAsync.execute();
                labelNetworkOrgs.setVisibility(View.GONE);
                labelNonNetworkOrgs.setVisibility(View.GONE);
              /*  labelNetworkOrgs.setText(getString(R.string.labelNetworkDiagnostics));
                labelNonNetworkOrgs.setText(getString(R.string.labelNonNetworkDiagnostics));*/
                break;
            case 2:
                Log.d(TAG,"Loading tab="+currentSelectedTab);
                labelPreferredOrg.setText(getString(R.string.labelAvailDoctrz));
                Log.d(TAG,"showprogress>>available doctrz");
                showProgress(true);
                RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                mRequestQueue.add(getNearbyDoctorsList(latLng));
                break;
            case 1:
                Log.d(TAG,"Loading tab="+currentSelectedTab);
                labelPreferredOrg.setText(getString(R.string.labelAED));
                Log.d(TAG,"showprogress>>"+getString(R.string.labelAED));
                showProgress(true);
                RequestQueue requestQueue = AppVolley.getRequestQueue();
                requestQueue.add(getNearbyAEDs(latLng));
                break;
        }}


        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private class LoadDataAsync extends AsyncTask<Void, Void, Void> {
        LatLng latLng;
        String orgType;

        public LoadDataAsync(LatLng latLng, String orgType) {
            this.latLng = latLng;
            this.orgType = orgType;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(isAdded()) {
                if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_HOSPITAL)) {
                    preferredHospitalsList = getPreferredHospitalsList(getPreferredOrgTypeId(), orgType, latLng);
                    networkHospitalsList = getNearbyNetworkHospitals(getPreferredOrgTypeId(), orgType, latLng);
                    nonNetworkHospitalsList = getNonNearbyNetworkHospitals(getPreferredOrgTypeId(), orgType, latLng);
                }
                if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_BLOOD_BANK)) {
                    long preferredBloodBankId = mDatabaseHandler.getPreferredBloodBankId(patientId);
                    OrgBranchVO mOrgBranchVO = databaseHandlerAssetHelper.getPreferredOrgBranch(preferredBloodBankId);
                    long preferredBloodBankOrgId = (mOrgBranchVO != null ? mOrgBranchVO.getOrgId() : 0);
                    preferredHospitalsList = getPreferredHospitalsList(preferredBloodBankOrgId, orgType, latLng);
                    networkHospitalsList = getNearbyNetworkHospitals(preferredBloodBankOrgId, orgType, latLng);
                    nonNetworkHospitalsList = getNonNearbyNetworkHospitals(preferredBloodBankOrgId, orgType, latLng);
                }
                if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_MEDICAL_STORE)) {
                    networkHospitalsList = getNearbyNetworkHospitals(getPreferredOrgTypeId(), orgType, latLng);
                    nonNetworkHospitalsList = getNonNearbyNetworkHospitals(getPreferredOrgTypeId(), orgType, latLng);
                }
                if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_DIAGNOSTIC_LAB)) {
                    networkHospitalsList = getNearbyNetworkHospitals(getPreferredOrgTypeId(), orgType, latLng);
                    nonNetworkHospitalsList = getNonNearbyNetworkHospitals(getPreferredOrgTypeId(), orgType, latLng);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
            Log.d(TAG,"showprogress>>true");

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "showprogress>>false");
            if (isAdded()) {
                if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_HOSPITAL)) {
                    Log.d(TAG, "");
                    NearbyOrgBranchesAdapter adapterNetworkAPList = new NearbyOrgBranchesAdapter(getActivity(), networkHospitalsList, latLng);
                    NearbyOrgBranchesAdapter adapterNonNetworkAPList = new NearbyOrgBranchesAdapter(getActivity(), nonNetworkHospitalsList, latLng);
                    NearbyOrgBranchesAdapter preferredHospitalsListAdapter = new NearbyOrgBranchesAdapter(getActivity(), preferredHospitalsList, latLng);
                    Log.d(TAG, "networkHospitalsList.size()" + networkHospitalsList.size());

                    if (networkHospitalsList.size() != 0) {
                        tvEmptyRV1.setVisibility(View.GONE);
                        lvNetworkHospitals.setAdapter(adapterNetworkAPList);
                    } else {
                        tvEmptyRV1.setVisibility(View.VISIBLE);
                    }

                    if (nonNetworkHospitalsList.size() != 0) {
                        tvEmptyRV2.setVisibility(View.GONE);
                        lvNonNetworkHospitals.setAdapter(adapterNonNetworkAPList);
                    } else {
                        tvEmptyRV2.setVisibility(View.VISIBLE);
                    }

                    if (preferredHospitalsList.size() != 0) {
                        tvPhEmpty.setVisibility(View.GONE);
                        lv1.setAdapter(preferredHospitalsListAdapter);
                    } else {
                        tvPhEmpty.setVisibility(View.VISIBLE);
                    }
                    recyclerView1.setVisibility(View.VISIBLE);
                    recyclerView2.setVisibility(View.VISIBLE);
                    recyclerView3.setVisibility(View.VISIBLE);

                }
                if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_BLOOD_BANK)) {
                    AdapterNearbyHospitals adapterNetworkAPList = new AdapterNearbyHospitals(getActivity(), networkHospitalsList, latLng);
                    AdapterNearbyHospitals adapterNonNetworkAPList = new AdapterNearbyHospitals(getActivity(), nonNetworkHospitalsList, latLng);
                    AdapterNearbyHospitals preferredHospitalsListAdapter = new AdapterNearbyHospitals(getActivity(), preferredHospitalsList, latLng);
                    if (networkHospitalsList.size() != 0) {
                        tvEmptyRV1.setVisibility(View.GONE);
                        lvNetworkHospitals.setAdapter(adapterNetworkAPList);
                    } else {
                        tvEmptyRV1.setVisibility(View.VISIBLE);
                    }

                    if (nonNetworkHospitalsList.size() != 0) {
                        tvEmptyRV2.setVisibility(View.GONE);
                        lvNonNetworkHospitals.setAdapter(adapterNonNetworkAPList);
                    } else {
                        tvEmptyRV2.setVisibility(View.VISIBLE);
                    }

                    if (preferredHospitalsList.size() != 0) {
                        tvPhEmpty.setVisibility(View.GONE);
                        lv1.setAdapter(preferredHospitalsListAdapter);
                    } else {
                        tvPhEmpty.setVisibility(View.VISIBLE);
                    }
                    recyclerView1.setVisibility(View.VISIBLE);
                    recyclerView2.setVisibility(View.VISIBLE);
                    recyclerView3.setVisibility(View.VISIBLE);

                }
                if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_MEDICAL_STORE)) {
                    AdapterNearbyHospitals adapterNetworkAPList = new AdapterNearbyHospitals(getActivity(), networkHospitalsList, latLng);
                    AdapterNearbyHospitals adapterNonNetworkAPList = new AdapterNearbyHospitals(getActivity(), nonNetworkHospitalsList, latLng);
                    if (networkHospitalsList.size() == 0 && nonNetworkHospitalsList.size() == 0) {
                        recyclerView2.setVisibility(View.VISIBLE);
                        tvEmptyRV1.setVisibility(View.VISIBLE);
                    } else if (networkHospitalsList.size() != 0 && nonNetworkHospitalsList.size() == 0) {
                        recyclerView2.setVisibility(View.VISIBLE);
                        lvNetworkHospitals.setAdapter(adapterNetworkAPList);
                    } else if (networkHospitalsList.size() == 0 && nonNetworkHospitalsList.size() != 0) {
                        recyclerView3.setVisibility(View.VISIBLE);
                        lvNonNetworkHospitals.setAdapter(adapterNonNetworkAPList);
                    } else {
                        recyclerView2.setVisibility(View.VISIBLE);
                        recyclerView3.setVisibility(View.VISIBLE);
                        lvNetworkHospitals.setAdapter(adapterNetworkAPList);
                        lvNonNetworkHospitals.setAdapter(adapterNonNetworkAPList);

                    }

                }
                if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_DIAGNOSTIC_LAB)) {
                    AdapterNearbyHospitals adapterNetworkAPList = new AdapterNearbyHospitals(getActivity(), networkHospitalsList, latLng);
                    AdapterNearbyHospitals adapterNonNetworkAPList = new AdapterNearbyHospitals(getActivity(), nonNetworkHospitalsList, latLng);
                    if (networkHospitalsList.size() == 0 && nonNetworkHospitalsList.size() == 0) {
                        recyclerView2.setVisibility(View.VISIBLE);
                        tvEmptyRV1.setVisibility(View.VISIBLE);
                    } else if (networkHospitalsList.size() != 0 && nonNetworkHospitalsList.size() == 0) {
                        recyclerView2.setVisibility(View.VISIBLE);
                        lvNetworkHospitals.setAdapter(adapterNetworkAPList);
                    } else if (networkHospitalsList.size() == 0 && nonNetworkHospitalsList.size() != 0) {
                        recyclerView3.setVisibility(View.VISIBLE);
                        lvNonNetworkHospitals.setAdapter(adapterNonNetworkAPList);
                    } else {
                        recyclerView2.setVisibility(View.VISIBLE);
                        recyclerView3.setVisibility(View.VISIBLE);
                        lvNetworkHospitals.setAdapter(adapterNetworkAPList);
                        lvNonNetworkHospitals.setAdapter(adapterNonNetworkAPList);

                    }
                }
                showProgress(false);
            }
        }
    }

    private long getPreferredOrgTypeId() {
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(patientId);
        long preferredOrgId = databaseHandlerAssetHelper.getPreferredOrgId(preferredOrgBranchId);
        return preferredOrgId;
    }

    private List<OrgBranchVO> getNonNearbyNetworkHospitals(long preferredOrgId, String orgType, LatLng latLng) {
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getActivity());

        List<OrgBranchVO> activeNonNetworkOrgBranchesList = databaseHandlerAssetHelper.getAllActiveNonNetworkOrgBranchesList(getActivity(), preferredOrgId, orgType, latLng.latitude, latLng.longitude);
        Log.d(TAG, "Active non network org branches=" + activeNonNetworkOrgBranchesList.size());
        Collections.sort(activeNonNetworkOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });

        Log.d(TAG, "List size=" + activeNonNetworkOrgBranchesList.size());

        if (activeNonNetworkOrgBranchesList.size() > 10)
            activeNonNetworkOrgBranchesList = activeNonNetworkOrgBranchesList.subList(0, 10);
        return activeNonNetworkOrgBranchesList;
    }

    private List<OrgBranchVO> getNearbyNetworkHospitals(long preferredOrgId, String orgType, LatLng latLng) {
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getActivity());
        List<OrgBranchVO> activeNetworkOrgBranchesList = databaseHandlerAssetHelper.getAllActiveNetworkOrgBranchesList(getActivity(), preferredOrgId, orgType, latLng.latitude, latLng.longitude);
        Log.d(TAG, "Active network org branches=" + activeNetworkOrgBranchesList.size());
        Collections.sort(activeNetworkOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });
        Log.d(TAG, "List size=" + activeNetworkOrgBranchesList.size());

        if (activeNetworkOrgBranchesList.size() > 10)
            activeNetworkOrgBranchesList = activeNetworkOrgBranchesList.subList(0, 10);

        return activeNetworkOrgBranchesList;

    }

    private List<OrgBranchVO> getPreferredHospitalsList(long preferredOrgId, String orgType, LatLng latLng) {
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getActivity());
        List<OrgBranchVO> preferredOrgBranchesList = databaseHandlerAssetHelper.getPreferredOrgBranchesList(getActivity(), latLng.latitude, latLng.longitude, preferredOrgId, orgType);
        Collections.sort(preferredOrgBranchesList, new Comparator<OrgBranchVO>() {
            @Override
            public int compare(OrgBranchVO c1, OrgBranchVO c2) {
                return Double.compare(c1.getDistanceInKms(), c2.getDistanceInKms());
            }
        });

        Log.d(TAG, "List size=" + preferredOrgBranchesList.size());

        if (preferredOrgBranchesList.size() > 3)
            preferredOrgBranchesList = preferredOrgBranchesList.subList(0, 3);
        return preferredOrgBranchesList;
    }


    public static NearbyOrgsFragment newInstance(Bundle mBundle) {
        NearbyOrgsFragment frag = new NearbyOrgsFragment();
        frag.setArguments(mBundle);
        return frag;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if(isAdded()) {
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

                layout.setVisibility(View.VISIBLE);
                layout.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                layout.setVisibility(show ? View.GONE
                                        : View.VISIBLE);
                            }
                        });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mLoaderStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                layout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }

    private StringRequest getNearbyDoctorsList(final LatLng latLng) {
        String url = WebServiceUrls.serverUrl + WebServiceUrls.getNearbyDoctorsList + "?distanceInKms=10&latitude=" + latLng.latitude + "&longitude=" + latLng.longitude;
        Log.d(TAG, "url = " + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "getNearbyDoctorsList response = " + response);
                if(isAdded()) {
                    recyclerView1.setVisibility(View.VISIBLE);
                    recyclerView4.setVisibility(View.VISIBLE);
                    if (response != null && response.length() != 0 && !response.contains("No Nearby Doctors Found")) {
                        Type objectType = new TypeToken<ArrayList<DoctorSearchVO>>() {
                        }.getType();
                        ArrayList<DoctorSearchVO> doctorsList = new Gson().fromJson(response, objectType);
                        Log.d(TAG, "doctorsList size = " + doctorsList.size());
                        if (doctorsList.size() > 0) {
                            tvPhEmpty.setVisibility(View.GONE);
                            NearbyDoctorsListAdapter mAdapter = new NearbyDoctorsListAdapter(getActivity(), doctorsList);
                            lv1.setAdapter(mAdapter);
                        }
                    } else {
                        tvPhEmpty.setText("No nearby doctors online right now.");
                        tvPhEmpty.setVisibility(View.VISIBLE);
                    }
                    showProgress(false);
                    Log.d(TAG, "showprogress close>>available doctrz");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(isAdded()) {
                    showProgress(false);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        switch (networkResponse.statusCode) {
                            case Constant.HTTP_CODE_SERVER_BUSY:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setCancelable(false);
                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View view = inflater.inflate(R.layout.fragment_server_busy_dialog, null);
                                builder.setView(view);
                                TextView mButton = (TextView) view.findViewById(R.id.bt_done);
                                final AlertDialog mAlertDialog = builder.create();
                                mButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAlertDialog.dismiss();
                                    }
                                });
                                mAlertDialog.show();
                                break;
                        }
                    } else {
                        AppUtil.showErrorDialog(getActivity(), error);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headerStringMap = null;
                if(isAdded()) {
                    headerStringMap=AppUtil.addHeadersForApp(getActivity(), super.getHeaders());
                    return headerStringMap;
                }
                return headerStringMap;
            }

        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }
    private StringRequest getNearbyAEDs(final LatLng latLng) {
        String url = WebServiceUrls.serverUrl + WebServiceUrls.getNearbyAEDsList + "latitude=" + latLng.latitude + "&longitude=" + latLng.longitude;
        Log.d(TAG, "url = " + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(isAdded()) {
                    Log.d(TAG, WebServiceUrls.getNearbyAEDsList + " response = " + response);
                    recyclerView1.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(response)) {
                        Type objectType = new TypeToken<ArrayList<AED>>() {
                        }.getType();
                        ArrayList<AED> aedsList = new Gson().fromJson(response, objectType);
                        Log.d(TAG, "No of AED's" + aedsList.size());
                        if (aedsList.size() > 0) {
                            tvPhEmpty.setVisibility(View.GONE);
                            AEDsAdapter mAdapterNearbyHospitals = new AEDsAdapter(getActivity(), aedsList, latLng);
                            lv1.setAdapter(mAdapterNearbyHospitals);
                        } else {
                            tvPhEmpty.setText("No Nearby AEDs available.");
                            tvPhEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvPhEmpty.setText("No Nearby AEDs available.");
                        tvPhEmpty.setVisibility(View.VISIBLE);
                    }

                    showProgress(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int responsecode = -2;
                try {
                    responsecode = error.networkResponse.statusCode;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    showProgress(false);

                Log.d(TAG,"showprogress close>>available doctrz");

                Log.d(TAG, "getNearbyDoctorsList Error responsecode - " + responsecode);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headerStringMap = null;
                if(isAdded()) {
                    headerStringMap=AppUtil.addHeadersForApp(getActivity(), super.getHeaders());
                    return headerStringMap;
                }
                return headerStringMap;
            }

        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (loadDataAsync != null) {
            loadDataAsync.cancel(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}




