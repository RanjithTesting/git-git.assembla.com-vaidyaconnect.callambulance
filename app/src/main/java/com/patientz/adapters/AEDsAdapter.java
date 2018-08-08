package com.patientz.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.patientz.VO.AED;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sunil on 8/6/17.
 */

public class AEDsAdapter extends RecyclerView
        .Adapter<AEDsAdapter
        .DataObjectHolder> {
    private static String TAG = "AEDsAdapter";
    private final LatLng latLng;
    private List<AED> mDataset;
    private Activity applicationContext;
    private RequestQueue mRequestQueue = AppVolley.getRequestQueue();


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView tvAddress, tvContactPersonName, tvDistanceInKms, tvTimeToReach,tvWorkTimings;
        private ImageButton btDirections, btCall;
        private LinearLayout  llLocation;


        public DataObjectHolder(View itemView) {
            super(itemView);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            tvContactPersonName = (TextView) itemView.findViewById(R.id.tv_contact_person);
            btDirections = (ImageButton) itemView.findViewById(R.id.bt_directions);
            btCall = (ImageButton) itemView.findViewById(R.id.bt_call);
            tvDistanceInKms = (TextView) itemView.findViewById(R.id.tv_distance_in_kms);
            llLocation = (LinearLayout) itemView.findViewById(R.id.ll_location);
            tvTimeToReach = (TextView) itemView.findViewById(R.id.tv_time_to_reach);
            tvWorkTimings = (TextView) itemView.findViewById(R.id.tv_work_timings);
        }

        @Override
        public void onClick(View v) {
        }
    }


    public AEDsAdapter(Activity applicationContext, List<AED> myDataset, LatLng latLng) {
        mDataset = myDataset;
        this.applicationContext = applicationContext;
        this.latLng = latLng;
    }

    @Override
    public AEDsAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_aeds_adapter_layout, parent, false);

        AEDsAdapter.DataObjectHolder dataObjectHolder = new AEDsAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(AEDsAdapter.DataObjectHolder holder, int position) {
        final AED aed = mDataset.get(position);
        holder.tvContactPersonName.setText(aed.getContactPersonName());
        String address = getAddress(aed);
        if (!TextUtils.isEmpty(address)) {
            holder.llLocation.setVisibility(View.VISIBLE);
            holder.tvAddress.setText(address);
        } else {
            holder.llLocation.setVisibility(View.INVISIBLE);
        }
        holder.btDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirections(aed);
            }
        });
        holder.btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(aed);
            }
        });

        SimpleDateFormat mSimpleDateFormat=new SimpleDateFormat("hh:mm aaa");
        Log.d("Timings",mSimpleDateFormat.format(new Date(aed.getOpenTime())) +" to "+mSimpleDateFormat.format(new Date(aed.getCloseTime())));

        holder.tvWorkTimings.setText(mSimpleDateFormat.format(new Date(aed.getOpenTime())) +" to "+mSimpleDateFormat.format(new Date(aed.getCloseTime())));

        if (AppUtil.isOnline(applicationContext)) {
            mRequestQueue.add(createTimeCalRequestToDestination(aed, holder.tvTimeToReach, holder.tvDistanceInKms, latLng.latitude, latLng.longitude));
        }
    }

    private StringRequest createTimeCalRequestToDestination(final AED aed, final TextView tvTimeToReach, final TextView tvDistanceInKms, Double currentLocationLat, Double currentLocationLon) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + currentLocationLat + "," + currentLocationLon + "&destination=" + aed.getAddress().getLatitude() + "," + aed.getAddress().getLongitude() + "&mode=driving" + "&key=" + applicationContext.getString(R.string.server_api_key);
        Log.d(TAG, "createTimeCalRequestToDestination>>" + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "createTimeCalRequestToDestination response " + response);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("OK")) {
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        JSONObject zero = routes.getJSONObject(0);
                        JSONArray legs = zero.getJSONArray("legs");
                        JSONObject duration = legs.getJSONObject(0).getJSONObject("duration");
                        JSONObject distance = legs.getJSONObject(0).getJSONObject("distance");

                        String timeToReachDest = (String) duration.get("text");
                        String distanceInKms = (String) distance.get("text");

                        tvTimeToReach.setText(timeToReachDest);
                        tvDistanceInKms.setText(distanceInKms);
                    }
                } catch (JSONException e1) {
                    Log.d(TAG, "exc=" + e1.getMessage());
                    e1.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(applicationContext);
                            break;
                    }
                }
               else {
                    AppUtil.showErrorDialog(applicationContext,error);
                }
                }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(applicationContext, super.getHeaders());
            }
        };
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        return mRequest;
    }

    private void call(AED aed) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        if (AppUtil.isPhoneNumberValid(aed.getPhoneNumber(), mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, "IN"))) {
            call(aed.getPhoneNumber());
        } else {
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


    private String getAddress(AED aed) {
        String address = "";
        if (!TextUtils.isEmpty(aed.getAddress().getStreetAddress())) {
            address = address + aed.getAddress().getStreetAddress() + ", ";
        }
        if (!TextUtils.isEmpty(aed.getAddress().getAdditionalAddress())) {
            address = address + aed.getAddress().getAdditionalAddress();
        }

        return address;

    }


    public void showDirections(AED aed) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.valueOf(aed.getAddress().getLatitude() != null ? aed.getAddress().getLatitude() : "") + "," + Double.valueOf(aed.getAddress().getLongitude() != null ? aed.getAddress().getLongitude() : ""));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mapIntent.resolveActivity(applicationContext.getPackageManager()) != null) {
            applicationContext.startActivity(mapIntent);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}

