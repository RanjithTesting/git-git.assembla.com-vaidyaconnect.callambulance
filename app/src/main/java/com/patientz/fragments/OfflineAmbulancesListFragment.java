package com.patientz.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.AmbulanceDetailsVO;
import com.patientz.activity.R;
import com.patientz.adapters.LACAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.DividerItemDecoration;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OfflineAmbulancesListFragment extends BaseFragment implements View.OnClickListener {

    int max;
    ProgressDialog dialog;
    private static final String TAG = "NearbyAmbulancesListActivity";
    RecyclerView recyclerView;
    private TextView tvEmptyListView2, tvFareInfo;
    private ArrayList<AmbulanceDetailsVO> ambulanceList2;
    private RequestQueue mRequestQueue;

    // Request code to use when launching the resolution activity
    private LatLng latLng;
    private Location mCurrentLocation;
    private LACAdapter mLacAdapter;
    private ProgressBar progressBar;
    private Button btMore;
    private SharedPreferences defaultSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultSharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        HashMap<String, Object> upshotData = new HashMap<>();
        upshotData.put(Constant.UpshotEvents.NEARBY_OFFLINE_AMB_SCREEN, true);
        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.NEARBY_OFFLINE_AMB_SCREEN);
        Log.d(TAG, "upshot data=" + upshotData.entrySet());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offline_ambulances_list_layout, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_offline_Amb);
        btMore = (Button) view.findViewById(R.id.btn_more_map);
        btMore.setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_offline_amb);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        tvEmptyListView2 = (TextView) view.findViewById(R.id.empty_list_view2);
        tvFareInfo = (TextView) view.findViewById(R.id.tv_fare_info);

        Bundle mBundle = getArguments();
        latLng = mBundle.getParcelable("latLng");
        if (latLng != null) {
            mCurrentLocation = new Location("");
            mCurrentLocation.setLatitude(latLng.latitude);
            mCurrentLocation.setLongitude(latLng.longitude);
            tvEmptyListView2.setVisibility(View.GONE);
            if (!AppUtil.isOnline(getActivity())) {
                recyclerView.setVisibility(View.VISIBLE);
                DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
                ArrayList<AmbulanceDetailsVO> ambulancesList = mDatabaseHandler.getNearbyAmbulanceListFromLocalDB(Constant.OFFLINE);
                if (ambulancesList.size() > 0) {
                    tvFareInfo.setVisibility(View.VISIBLE);
                    tvEmptyListView2.setVisibility(View.GONE);
                    mLacAdapter = new LACAdapter(getActivity(), ambulancesList, (LACAdapter.RecyclerViewOnclick) getActivity(), mCurrentLocation, Constant.OFFLINE);
                    recyclerView.setAdapter(mLacAdapter);
                } else {
                    tvFareInfo.setVisibility(View.GONE);
                    tvEmptyListView2.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } else {
                Log.d(TAG,"getLastKnownTime"+getLastKnownTime()+"");
                if(loadAmbsFromLocal())
                    {
                        Log.d(TAG,"LOADING_AMBULANCES_FROM_LOCAL_OFF");

                        recyclerView.setVisibility(View.VISIBLE);
                        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
                        ArrayList<AmbulanceDetailsVO> ambulancesList=mDatabaseHandler.getNearbyAmbulanceList(Constant.OFFLINE);
                        Location mCurrentLocation = new Location("");
                        mCurrentLocation.setLatitude(latLng.latitude);
                        mCurrentLocation.setLongitude(latLng.longitude);
                        mLacAdapter = new LACAdapter(getActivity(), ambulancesList, (LACAdapter.RecyclerViewOnclick) getActivity(), mCurrentLocation, Constant.OFFLINE);
                        recyclerView.setAdapter(mLacAdapter);
                    }else
                    {
                        Log.d(TAG,"LOADING_AMBULANCES_FROM_SERVER_OFF");
                        mRequestQueue = AppVolley.getRequestQueue();
                        mRequestQueue.add(getNearbyOfflineAmbulancesRequest(Constant.CANCEL_VOLLEY_REQUEST));

                    }
            }

        } else {
            //tvEmptyListView2.setVisibility(View.VISIBLE);
        }
        return view;

    }
    private long getLastKnownTime() {
        long lastKnownTimeStamp=defaultSharedPreferences.getLong(Constant.LAST_KNOWN_TIME_NEARBY_OFFLINE_AMB,0);
        long currentTimeInMillis=System.currentTimeMillis();
        Log.d("CURRENT_TIME_IN_MILLIS_OFFLINE=",currentTimeInMillis+"");
        Log.d("LAST_KNOWN_TIME_IN_MILLIS_OFFLINE=",lastKnownTimeStamp+"");
        Log.d("DIFFERENCE_OFFLINE",(currentTimeInMillis-lastKnownTimeStamp)+"");
        return currentTimeInMillis-lastKnownTimeStamp;
    }
    private boolean loadAmbsFromLocal() {
            Location lastKnownLoc=new Location("");
            lastKnownLoc.setLatitude(defaultSharedPreferences.getFloat(Constant.LAT_NEARBY_AMB_OFF,0));
            lastKnownLoc.setLongitude(defaultSharedPreferences.getFloat(Constant.LON_NEARBY_AMB_OFF,0));

            if (!CommonUtils.getSP(getActivity()).contains("lastTimeActionDoneoffline") || mCurrentLocation.distanceTo(lastKnownLoc)>500 ) {
                CommonUtils.getSP(getActivity()).edit().putLong("lastTimeActionDoneoffline", Calendar.getInstance().getTimeInMillis()).commit();
                SharedPreferences.Editor mEditor = defaultSharedPreferences.edit();
                mEditor.putFloat(Constant.LAT_NEARBY_AMB_OFF, (float) mCurrentLocation.getLatitude()).commit();
                mEditor.putFloat(Constant.LON_NEARBY_AMB_OFF, (float) mCurrentLocation.getLongitude()).commit();
                return false;
            } else {
                long day_diff = (10 * 60 * 1000);
                Log.e("day_diff", "" + day_diff);

                if (((Calendar.getInstance().getTimeInMillis() - CommonUtils.getSP(getActivity()).getLong("lastTimeActionDoneoffline", 0)) > day_diff) || mCurrentLocation.distanceTo(lastKnownLoc)>500) {
                    CommonUtils.getSP(getActivity()).edit().putLong("lastTimeActionDoneoffline", Calendar.getInstance().getTimeInMillis()).commit();
                    SharedPreferences.Editor mEditor = defaultSharedPreferences.edit();
                    mEditor.putFloat(Constant.LAT_NEARBY_AMB_OFF, (float) mCurrentLocation.getLatitude()).commit();
                    mEditor.putFloat(Constant.LON_NEARBY_AMB_OFF, (float) mCurrentLocation.getLongitude()).commit();
                    return false;
                } else {
                    return true;
                }
            }

    }

    private StringRequest getNearbyOfflineAmbulancesRequest(final String entryPoint) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        max = max + 10;
        String params = "?latitude=" + latLng.latitude + "&longitude=" + latLng.longitude + "&distanceInKms=20" + "&max=" + max + "&noDistance=true";
        ;
        Log.d(TAG, "Webservice:getNearbyOfflineAmbulances:params" + params.toString());
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.getNearbyOfflineAmbulances + params;
        Log.d("Webservice:getNearbyOfflineAmbulances:url ", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //dismissProgressDialog(dialog);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                //rootView.setVisibility(View.VISIBLE);
                Log.d(TAG, "ENTRY POINT=" + entryPoint);


                Log.d(TAG, "Webservice:getNearbyOfflineAmbulances:response" + response);
                try {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<ArrayList<AmbulanceDetailsVO>>() {
                    }.getType();

                    ambulanceList2 = gson.fromJson(
                            response, objectType);
                    Log.d(TAG, "Ambulance list>" + ambulanceList2);
                    if (ambulanceList2 != null) {
                        if (ambulanceList2.size() > 0) {
                            Location mCurrentLocation = new Location("");
                            mCurrentLocation.setLatitude(latLng.latitude);
                            mCurrentLocation.setLongitude(latLng.longitude);
                            tvFareInfo.setVisibility(View.VISIBLE);
                            tvEmptyListView2.setVisibility(View.GONE);
                            DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
                            //mDatabaseHandler.deleteNearbyAmbulances(Constant.OFFLINE);
                            for (AmbulanceDetailsVO mAmbulanceDetailsVO : ambulanceList2) {
                                mAmbulanceDetailsVO.setDistanceInKms(AppUtil.distance(mCurrentLocation.getLatitude(), Double.parseDouble(mAmbulanceDetailsVO.getLongitude()), Double.parseDouble(mAmbulanceDetailsVO.getLatitude()), mCurrentLocation.getLongitude()));
                            }
                            mDatabaseHandler.insertNearbyAmbulances(ambulanceList2, Constant.OFFLINE);
                            updateTimeStamp();
                            mLacAdapter = new LACAdapter(getActivity(), ambulanceList2, (LACAdapter.RecyclerViewOnclick) getActivity(), mCurrentLocation, Constant.OFFLINE);
                            recyclerView.setAdapter(mLacAdapter);
                            if (ambulanceList2.size() >= max) {
                                btMore.setVisibility(View.VISIBLE);
                            } else {
                                btMore.setVisibility(View.GONE);
                            }
                        } else {
                            tvFareInfo.setVisibility(View.GONE);
                            tvEmptyListView2.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        tvFareInfo.setVisibility(View.GONE);
                        tvEmptyListView2.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        return mRequest;
    }
    private void updateTimeStamp() {
        defaultSharedPreferences.edit().putLong(Constant.LAST_KNOWN_TIME_NEARBY_OFFLINE_AMB,System.currentTimeMillis()).commit();
    }
    private void storeLocationInSP(Location mCurrentLocation) {
        if(!defaultSharedPreferences.contains(Constant.LAT_NEARBY_AMB) || !defaultSharedPreferences.contains(Constant.LON_NEARBY_AMB))
        {
            Log.d(TAG,"****storeLocationInSP****");
            SharedPreferences.Editor mEditor=defaultSharedPreferences.edit();
            mEditor.putFloat(Constant.LAT_NEARBY_AMB, (float) mCurrentLocation.getLatitude()).commit();
            mEditor.putFloat(Constant.LON_NEARBY_AMB, (float) mCurrentLocation.getLongitude()).commit();
            Log.d(TAG,"****storeLocationInSP****");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(Constant.CANCEL_VOLLEY_REQUEST);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more_map:
                if (latLng != null) {
                    mRequestQueue = AppVolley.getRequestQueue();
                    mRequestQueue.add(getNearbyOfflineAmbulancesRequest(Constant.CANCEL_VOLLEY_REQUEST));
                }
                break;
        }

    }
}




