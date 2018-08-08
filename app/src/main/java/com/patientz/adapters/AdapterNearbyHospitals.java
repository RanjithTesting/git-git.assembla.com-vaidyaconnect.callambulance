package com.patientz.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.ResponseVO;
import com.patientz.activity.BloodAvailabilityActivity;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class AdapterNearbyHospitals extends RecyclerView
        .Adapter<AdapterNearbyHospitals
        .DataObjectHolder> {
    private static String TAG = "AdapterNearbyHospitals";
    private final LatLng latLng;
    private List<OrgBranchVO> mDataset;
    private Activity applicationContext;
    private RequestQueue mRequestQueue= AppVolley.getRequestQueue();



    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView tvHospitalLocation, tvHospitalName,tvDistanceInKms,tvTimeToReach;;
        ImageView ivHospitalLogo;
        RelativeLayout rlParent;
        private ImageButton btDirections, btCall;
        private Button btBloodAvailability;
        private LinearLayout llLocation;
        private CardView cview;


        public DataObjectHolder(View itemView) {
            super(itemView);
            tvHospitalLocation = (TextView) itemView.findViewById(R.id.tv_hospital_location);
            tvHospitalName = (TextView) itemView.findViewById(R.id.tv_hospital_name);
            btDirections = (ImageButton) itemView.findViewById(R.id.bt_directions);
            btCall = (ImageButton) itemView.findViewById(R.id.bt_call);
            tvDistanceInKms = (TextView) itemView.findViewById(R.id.tv_distance_in_kms);
            llLocation = (LinearLayout) itemView.findViewById(R.id.ll_location);
            tvTimeToReach = (TextView) itemView.findViewById(R.id.tv_time_to_reach);
            cview = (CardView) itemView.findViewById(R.id.cview);
            btBloodAvailability = (Button) itemView.findViewById(R.id.bt_blood_availability);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getPosition(), v);
        }
    }



    public AdapterNearbyHospitals(Activity applicationContext, List<OrgBranchVO> myDataset, LatLng latLng) {
        mDataset = myDataset;
        this.applicationContext = applicationContext;
        this.latLng=latLng;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_nearby_hospitals, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        final OrgBranchVO hospitalVO = mDataset.get(position);
        holder.tvHospitalName.setText(hospitalVO.getDisplayName());
        String address = getAddress(hospitalVO);
        if(!TextUtils.isEmpty(address)) {
            holder.llLocation.setVisibility(View.VISIBLE);
            holder.tvHospitalLocation.setText(address);
        }else
        {
            holder.llLocation.setVisibility(View.INVISIBLE);
        }
        holder.btDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirections(hospitalVO);
            }
        });
        holder.btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(hospitalVO);
            }
        });
        Log.d("ORG_TYPE=", hospitalVO.getOrgType());
        if (hospitalVO.getOrgType().equalsIgnoreCase(Constant.ORG_TYPE_URL_BLOOD_BANK)) {
            holder.btBloodAvailability.setVisibility(View.VISIBLE);
        }



        holder.btBloodAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick","onClick");
                Intent mIntent = new Intent(applicationContext, BloodAvailabilityActivity.class);
                mIntent.putExtra(Constant.ORG_BRANCH_ID, hospitalVO.getOrgBranchId());
                applicationContext.startActivity(mIntent);
            }
        });

        //setDistance(hospitalVO,holder.tvDistanceInKms);
       /* SharedPreferences mSharedPreferences = applicationContext
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));*/
        if(AppUtil.isOnline(applicationContext)) {
            mRequestQueue.add(createTimeCalRequestToDestination(hospitalVO, holder.tvTimeToReach,holder.tvDistanceInKms,latLng.latitude, latLng.longitude));
        }else
        {
            holder.tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms())+" Km");
        }
         //setTimeToReach(hospitalVO,holder.tvTimeToReach);
        //setHospitalLogo(hospitalVO,holder.ivHospitalLogo);
    }

    private StringRequest createTimeCalRequestToDestination(final OrgBranchVO hospitalVO, final TextView tvTimeToReach, final TextView tvDistanceInKms, Double currentLocationLat, Double currentLocationLon) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+currentLocationLat+","+currentLocationLon+"&destination="+hospitalVO.getAddress().getLatitude()+","+hospitalVO.getAddress().getLongitude()+"&mode=driving"+"&key=" + applicationContext.getString(R.string.server_api_key);
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

                        tvTimeToReach.setText(timeToReachDest);
                        tvDistanceInKms.setText(distanceInKms);
                    }
                    else
                    {
                        tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms())+" Km");

                    }
                } catch (JSONException e1) {
                    Log.d(TAG, "exc=" + e1.getMessage());
                    e1.printStackTrace();
                    tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms())+" Km");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms())+" Km");
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(applicationContext);
                            break;
                    }
                }else
                {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError) {                         AppUtil.showToast(getApplicationContext(), applicationContext.getString(R.string.connection_error));                     }

                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(applicationContext, super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        return mRequest;
    }
    private void call(OrgBranchVO hospitalVO) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        if(AppUtil.isPhoneNumberValid(hospitalVO.getEmergencyPhoneNumber(),mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, "IN")))
        {
            call(hospitalVO.getEmergencyPhoneNumber());
        }else if(AppUtil.isPhoneNumberValid(hospitalVO.getTelephone(),mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, "IN")))
        {
            call(hospitalVO.getTelephone());
        }else {
            AppUtil.showToast(applicationContext, applicationContext.getString(R.string.toast_msg_no_phone_number));
        }
    }

    private void call(String emergencyPhoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + emergencyPhoneNumber));
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        applicationContext.startActivity(callIntent);
    }


    private String getAddress(OrgBranchVO hospitalVO) {
        String address = "";
        if (!TextUtils.isEmpty(hospitalVO.getAddress().getStreetAddress())) {
            address = address + hospitalVO.getAddress().getStreetAddress() + ", ";
        }
        if (!TextUtils.isEmpty(hospitalVO.getAddress().getAdditionalAddress())) {
            address = address + hospitalVO.getAddress().getAdditionalAddress();
        }

        return address;

    }

    private String getDurationInHrs(int durationInSecs) {
        String timeis = "";

        Log.d(TAG, "Secons=" + durationInSecs);
        Log.d(TAG, "Hours=" + (durationInSecs / 3600));
        Log.d(TAG, "Mins=" + (durationInSecs % 3600));
        int mins = (((durationInSecs % 3600)) / 60);

        if ((durationInSecs / 3600) != 0) {
            timeis = timeis + (durationInSecs / 3600) + " " + "hr ";
        }
        timeis = mins + "min";

        return timeis;
    }

    public void showDirections(OrgBranchVO hospitalVO) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.valueOf(hospitalVO.getAddress().getLatitude() != null ? hospitalVO.getAddress().getLatitude() : "") + "," + Double.valueOf(hospitalVO.getAddress().getLongitude() != null ? hospitalVO.getAddress().getLongitude() : ""));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mapIntent.resolveActivity(applicationContext.getPackageManager()) != null) {
            applicationContext.startActivity(mapIntent);
        }
    }

    /*private void setHospitalLogo(HospitalVO hospitalVO, ImageView ivPic) {

        String url = applicationContext.getString(R.string.serverBaseUrl) + applicationContext.getString(R.string.webservice_image_download) + mPatientUserVO.getPicId();
        Log.d(TAG, "Url=" + url);
        Picasso
                .with(applicationContext)
                .load(url)
                .placeholder(R.drawable.emergency_default_profile)
                .into(ivPic);
    }*/
    public void addItem(OrgBranchVO dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }
    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}
