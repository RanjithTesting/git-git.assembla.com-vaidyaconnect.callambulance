package com.patientz.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.patientz.VO.AvailabilityCapabilityVO;
import com.patientz.VO.Facility;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.ResponseVO;
import com.patientz.VO.Speciality;
import com.patientz.VO.SpecialityFacilityTimingsVO;
import com.patientz.activity.BloodAvailabilityActivity;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.DividerItemDecoration;
import com.patientz.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.RecyclerView.VERTICAL;

/**
 * Created by sunil on 14/4/17.
 */

public class NearbyOrgBranchesAdapter extends RecyclerView
        .Adapter<NearbyOrgBranchesAdapter
        .DataObjectHolder> {
    private static String TAG = "AdapterNearbyHospitals";
    private final LatLng latLng;
    private List<OrgBranchVO> mDataset;
    private Context applicationContext;
    private RequestQueue mRequestQueue = AppVolley.getRequestQueue();
    private DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(applicationContext);


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView tvHospitalLocation, tvHospitalName, tvDistanceInKms, tvTimeToReach;
        ;
        private ImageButton btDirections;
        private ImageButton btCall;
        private Button btBloodAvailability;
        private LinearLayout llGynaecology, llPaediatrics, llOrthopaedic, llICU, llCathLab, llCtScan, llMore, llLocation;


        public DataObjectHolder(View itemView) {
            super(itemView);
            tvHospitalLocation = (TextView) itemView.findViewById(R.id.tv_hospital_location);
            tvHospitalName = (TextView) itemView.findViewById(R.id.tv_hospital_name);
            btDirections = (ImageButton) itemView.findViewById(R.id.bt_directions);
            btCall = (ImageButton) itemView.findViewById(R.id.bt_call);
            btBloodAvailability = (Button) itemView.findViewById(R.id.bt_blood_availability);
            tvDistanceInKms = (TextView) itemView.findViewById(R.id.tv_distance_in_kms);
            llLocation = (LinearLayout) itemView.findViewById(R.id.ll_location);
            tvTimeToReach = (TextView) itemView.findViewById(R.id.tv_time_to_reach);
            llGynaecology = (LinearLayout) itemView.findViewById(R.id.ll_gynaecology);
            llPaediatrics = (LinearLayout) itemView.findViewById(R.id.ll_paediatrics);
            llOrthopaedic = (LinearLayout) itemView.findViewById(R.id.ll_orthopaedic);
            llICU = (LinearLayout) itemView.findViewById(R.id.ll_icu);
            llCathLab = (LinearLayout) itemView.findViewById(R.id.ll_cath_lab);
            llCtScan = (LinearLayout) itemView.findViewById(R.id.ll_ct_scan);
            llMore = (LinearLayout) itemView.findViewById(R.id.more_ll);

            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getPosition(), v);
        }
    }


    public NearbyOrgBranchesAdapter(Activity applicationContext, List<OrgBranchVO> myDataset, LatLng latLng) {
        mDataset = myDataset;
        this.applicationContext = applicationContext;
        this.latLng = latLng;
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
        Log.d("orgBranchId=",hospitalVO.getOrgBranchId()+"");
        Log.d("hospitalVO.getDisplayName()=",hospitalVO.getDisplayName());

        holder.tvHospitalName.setText(hospitalVO.getDisplayName());
        String address = getAddress(hospitalVO);
        if (!TextUtils.isEmpty(address)) {
            holder.llLocation.setVisibility(View.VISIBLE);
            holder.tvHospitalLocation.setText(address);
        } else {
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

        if (AppUtil.isOnline(applicationContext)) {

            mRequestQueue.add(createTimeCalRequestToDestination(hospitalVO, holder.tvTimeToReach, holder.tvDistanceInKms, latLng.latitude, latLng.longitude));
        } else {
            holder.tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms()) + " Km");
        }



        Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(mCalendar.getTime());

        final ArrayList<AvailabilityCapabilityVO> orgBranchSpecialities = mDatabaseHandler.getOrgBranchCapabilityAvailabilityList(hospitalVO.getOrgBranchId(), Constant.SPECIALITY);
        final ArrayList<AvailabilityCapabilityVO> orgBranchFacilitiesList = mDatabaseHandler.getOrgBranchCapabilityAvailabilityList(hospitalVO.getOrgBranchId(), Constant.FACILITY);
        final ArrayList<SpecialityFacilityTimingsVO> specialityFacilityTimingsVOS=new ArrayList<>();
        final ArrayList<SpecialityFacilityTimingsVO> facilityTimingsVOS=new ArrayList<>();

        // preparing specialities for current day with timings
        for (AvailabilityCapabilityVO speciality : orgBranchSpecialities) {
            SpecialityFacilityTimingsVO specialityFacilityTimingsVO=new SpecialityFacilityTimingsVO();
            Speciality speciality1 = mDatabaseHandler.getSpecialityDetails(speciality.getSpecialityId());
            specialityFacilityTimingsVO.setDisplayName(speciality1.getDisplayName());

            specialityFacilityTimingsVO.setTimings(mDatabaseHandler.getTimings(hospitalVO.getOrgBranchId(),dayOfTheWeek,speciality1.getId(),Constant.SPECIALITY));
            specialityFacilityTimingsVOS.add(specialityFacilityTimingsVO);

            if (speciality1.getCode().contains(Constant.GYNAECOLOGY)) {
                holder.llGynaecology.setVisibility(View.VISIBLE);
            } else if (speciality1.getCode().contains(Constant.PAEDIATRICS)) {
                holder.llPaediatrics.setVisibility(View.VISIBLE);
            } else if (speciality1.getCode().contains(Constant.ORTHOPAEDIC)) {
                holder.llOrthopaedic.setVisibility(View.VISIBLE);
            }
        }

        // preparing facilities for current day with timings
        for (AvailabilityCapabilityVO facility : orgBranchFacilitiesList) {
            SpecialityFacilityTimingsVO facilityTimingsVO=new SpecialityFacilityTimingsVO();
            Facility facility1 = mDatabaseHandler.getFacilityDetails(facility.getFacilityId());
            facilityTimingsVO.setDisplayName(facility1.getDisplayName());

            facilityTimingsVO.setTimings(mDatabaseHandler.getTimings(hospitalVO.getOrgBranchId(),dayOfTheWeek,facility1.getId(),Constant.FACILITY));
            facilityTimingsVOS.add(facilityTimingsVO);

            if (facility1.getCode().contains(Constant.ICU)) {
                holder.llICU.setVisibility(View.VISIBLE);
            } else if (facility1.getCode().contains(Constant.CATH_LAB)) {
                holder.llCathLab.setVisibility(View.VISIBLE);
            } else if (facility1.getCode().contains(Constant.CT_SCAN)) {
                holder.llCtScan.setVisibility(View.VISIBLE);
            }
        }

        if(orgBranchSpecialities.size()>0 || orgBranchFacilitiesList.size()>0)
        {
            holder.llMore.setVisibility(View.VISIBLE);
        }

        holder.llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Dialog dialog = new Dialog(v.getContext(), android.R.style.Theme_DeviceDefault);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.facilities_specialities_timings_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(v.getContext());
                RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(v.getContext());


                RecyclerView recyclerViewSpecialities = (RecyclerView) dialog.findViewById(R.id.recycler_view_specialities);
                recyclerViewSpecialities.setHasFixedSize(true);
                recyclerViewSpecialities.setLayoutManager(mLayoutManager1);
                DividerItemDecoration decoration = new DividerItemDecoration(applicationContext, VERTICAL);
                recyclerViewSpecialities.addItemDecoration(decoration);
                AdapterFacilitiesSpecialitiesTimings specialitiesTimingsAdapter = new AdapterFacilitiesSpecialitiesTimings(applicationContext, specialityFacilityTimingsVOS);
                recyclerViewSpecialities.setAdapter(specialitiesTimingsAdapter);

                RecyclerView recyclerViewFacilities = (RecyclerView) dialog.findViewById(R.id.recycler_view_facilities);
                recyclerViewFacilities.setHasFixedSize(true);
                DividerItemDecoration decoration1 = new DividerItemDecoration(applicationContext, VERTICAL);
                recyclerViewFacilities.setLayoutManager(mLayoutManager2);
                recyclerViewFacilities.addItemDecoration(decoration1);
                AdapterFacilitiesSpecialitiesTimings facilitiesTimingsAdapter = new AdapterFacilitiesSpecialitiesTimings(applicationContext, facilityTimingsVOS);
                recyclerViewFacilities.setAdapter(facilitiesTimingsAdapter);
                dialog.show();
            }
        });

        if (hospitalVO.getOrgType().equalsIgnoreCase(Constant.ORG_TYPE_URL_BLOOD_BANK)) {
            holder.btBloodAvailability.setVisibility(View.VISIBLE);
            holder.btBloodAvailability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(applicationContext, BloodAvailabilityActivity.class);
                    mIntent.putExtra(Constant.ORG_BRANCH_ID, hospitalVO.getOrgBranchId());
                    applicationContext.startActivity(mIntent);
                }
            });
        }

    }

    private StringRequest createTimeCalRequestToDestination(final OrgBranchVO hospitalVO, final TextView tvTimeToReach, final TextView tvDistanceInKms, Double currentLocationLat, Double currentLocationLon) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + currentLocationLat + "," + currentLocationLon + "&destination=" + hospitalVO.getAddress().getLatitude() + "," + hospitalVO.getAddress().getLongitude() + "&mode=driving" + "&key=" + applicationContext.getString(R.string.server_api_key);
        Log.d(TAG, "createTimeCalRequestToDestination>>" + url);
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
                    } else {
                        tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms()) + " Km");

                    }
                } catch (JSONException e1) {
                    Log.d(TAG, "exc=" + e1.getMessage());
                    e1.printStackTrace();
                    tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms()) + " Km");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms()) + " Km");
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
                            builder.setCancelable(false);
                            LayoutInflater inflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    AppUtil.showErrorDialog(applicationContext, error);

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

    private void call(OrgBranchVO hospitalVO) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        if (AppUtil.isPhoneNumberValid(hospitalVO.getEmergencyPhoneNumber(), mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, "IN"))) {
            call(hospitalVO.getEmergencyPhoneNumber());
        } else if (AppUtil.isPhoneNumberValid(hospitalVO.getTelephone(), mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, "IN"))) {
            call(hospitalVO.getTelephone());
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


    public void showDirections(OrgBranchVO hospitalVO) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.valueOf(hospitalVO.getAddress().getLatitude() != null ? hospitalVO.getAddress().getLatitude() : "") + "," + Double.valueOf(hospitalVO.getAddress().getLongitude() != null ? hospitalVO.getAddress().getLongitude() : ""));
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
