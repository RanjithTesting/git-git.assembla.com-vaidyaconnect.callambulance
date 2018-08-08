package com.patientz.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.patientz.VO.AddressVO;
import com.patientz.VO.OrgBranchFacilityVO;
import com.patientz.VO.OrgBranchSpecialityVO;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.PublicProviderVO;
import com.patientz.VO.ResponseVO;
import com.patientz.activity.ActivityEmergencyStepsListener;
import com.patientz.activity.OrgBranchesActivity;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AdapterNonNetworkAPList extends RecyclerView
        .Adapter<AdapterNonNetworkAPList
        .DataObjectHolder> {
    private static String TAG = "AdapterNetworkAPList";
    private List<OrgBranchVO> mDataset;
    private OrgBranchesActivity applicationContext;
    private CountDownTimer countDownTimer;
    private RequestQueue mRequestQueue = AppVolley.getRequestQueue();


    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView tvHospitalName, tvBranchName, tvDistanceInKms, tvTimeToReach, tvHospitalPno;
        private LinearLayout llGynaecology, llPaediatrics, llOrthopaedic, llICU, llCathLab, llCtScan, llMore, llLocation;

        LinearLayout parentLayout;
        ImageView ivLocArrow;

        public DataObjectHolder(View itemView) {
            super(itemView);
            tvHospitalName = (TextView) itemView.findViewById(R.id.tv_display_name);
            tvDistanceInKms = (TextView) itemView.findViewById(R.id.tv_distance_in_kms);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.rowParent);
            tvTimeToReach = (TextView) itemView.findViewById(R.id.tv_time_to_reach);
            tvBranchName = (TextView) itemView.findViewById(R.id.tv_branch_name);
            ivLocArrow = (ImageView) itemView.findViewById(R.id.iv_loc_arrow);
            llGynaecology = (LinearLayout) itemView.findViewById(R.id.ll_gynaecology);
            llPaediatrics = (LinearLayout) itemView.findViewById(R.id.ll_paediatrics);
            llOrthopaedic = (LinearLayout) itemView.findViewById(R.id.ll_orthopaedic);
            llICU = (LinearLayout) itemView.findViewById(R.id.ll_icu);
            llCathLab = (LinearLayout) itemView.findViewById(R.id.ll_cath_lab);
            llCtScan = (LinearLayout) itemView.findViewById(R.id.ll_ct_scan);
            llMore = (LinearLayout) itemView.findViewById(R.id.more_ll);

        }
    }


    public AdapterNonNetworkAPList(OrgBranchesActivity applicationContext, List<OrgBranchVO> myDataset, CountDownTimer countDownTimer) {
        mDataset = myDataset;
        this.applicationContext = applicationContext;
        this.countDownTimer = countDownTimer;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ambulance_emergency_list, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        final OrgBranchVO hospitalVO = mDataset.get(position);
        holder.tvHospitalName.setText(!TextUtils.isEmpty(hospitalVO.getDisplayName()) ? (hospitalVO.getDisplayName()) : "");
        //setPhoneNumber(hospitalVO, holder.tvHospitalPno);
        setAddress(hospitalVO, holder);
        /*if(hospitalVO.getAddress()!=null)
        {
            holder.tvBranchName.setText(!TextUtils.isEmpty(hospitalVO.getAddress().getStreetAddress())?hospitalVO.getAddress().getStreetAddress():"");
        }else
        {
            holder.tvBranchName.setText("");
        }*/

        // setDistanceTimeToReach(hospitalVO, holder.tvDistanceInKms);
        SharedPreferences mSharedPreferences = applicationContext
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));

        if (AppUtil.isOnline(applicationContext)) {
            mRequestQueue.add(createTimeCalRequestToDestination(hospitalVO, holder.tvTimeToReach, holder.tvDistanceInKms, currentLocationLat, currentLocationLon));
        } else {
            holder.tvDistanceInKms.setText(CommonUtils.getTwoDigitDecimalValue(hospitalVO.getDistanceInKms()) + " Km");
        }
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                setProviderToCall(hospitalVO);
            }
        });
        ArrayList<OrgBranchSpecialityVO> specialities = hospitalVO.getSpecialities();
        ArrayList<OrgBranchFacilityVO> facilities = hospitalVO.getFacilities();
        final ArrayList<String> otherSpecialities = new ArrayList<String>();
        final ArrayList<String> otherFacilities = new ArrayList<String>();

        if (specialities != null) {
            for (OrgBranchSpecialityVO specialityVO : specialities) {
                if (specialityVO.getSpecialityCode().contains(Constant.GYNAECOLOGY) && specialityVO.isOrgSpecialityStatus()) {
                    holder.llGynaecology.setVisibility(View.VISIBLE);
                } else if (specialityVO.getSpecialityCode().contains(Constant.PAEDIATRICS) && specialityVO.isOrgSpecialityStatus()) {
                    holder.llPaediatrics.setVisibility(View.VISIBLE);
                } else if (specialityVO.getSpecialityCode().contains(Constant.ORTHOPAEDIC) && specialityVO.isOrgSpecialityStatus()) {
                    holder.llOrthopaedic.setVisibility(View.VISIBLE);
                } else {
                    if (specialityVO.isOrgSpecialityStatus()) {
                        otherSpecialities.add(specialityVO.getOrgSpecialityDisplayName());
                    }
                }
            }
        }

        if (facilities != null) {
            for (OrgBranchFacilityVO facilityVO : facilities) {
                if (facilityVO.getFacilityCode().contains(Constant.ICU) && facilityVO.isOrgFacilityStatus()) {
                    holder.llICU.setVisibility(View.VISIBLE);
                } else if (facilityVO.getFacilityCode().contains(Constant.CATH_LAB) && facilityVO.isOrgFacilityStatus()) {
                    holder.llCathLab.setVisibility(View.VISIBLE);
                } else if (facilityVO.getFacilityCode().contains(Constant.CT_SCAN) && facilityVO.isOrgFacilityStatus()) {
                    holder.llCtScan.setVisibility(View.VISIBLE);
                } else {
                    if (facilityVO.isOrgFacilityStatus()) {
                        otherFacilities.add(facilityVO.getOrgFacilityDisplayName());
                    }
                }
            }
        }
        if (otherSpecialities.size() > 0) {
            holder.llMore.setVisibility(View.VISIBLE);
        } else {
            holder.llMore.setVisibility(View.GONE);
        }

        holder.llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_fecities_list);

                ImageView text_close = (ImageView) dialog.findViewById(R.id.text_close);
                TextView text_fecilities = (TextView) dialog.findViewById(R.id.text_fecilities);
                text_fecilities.setText("Facilities");
                TextView text_info = (TextView) dialog.findViewById(R.id.text_info);
                text_info.setText("Specialities");
                ListView list_fecilities = (ListView) dialog.findViewById(R.id.list_fecilities);
                ListView list_info = (ListView) dialog.findViewById(R.id.list_info);

                if (otherFacilities.size() > 0) {
                    ArrayAdapter<String> feci_adapter = new ArrayAdapter<String>(v.getContext(), R.layout.item, otherFacilities);
                    list_fecilities.setAdapter(feci_adapter);
                } else {
                    text_fecilities.setVisibility(View.GONE);
                }

                if (otherSpecialities.size() > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(), R.layout.item, otherSpecialities);
                    list_info.setAdapter(adapter);
                } else {
                    text_info.setVisibility(View.GONE);
                }

                text_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
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
                        //JSONObject zero2 = legs.getJSONObject(0);
                        //JSONObject dist = zero2.getJSONObject("distance");
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
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(applicationContext);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(applicationContext,error);

                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(applicationContext, super.getHeaders());
            }
        };
        mRequest.setTag(Constant.TAG_GOOGLE_DISTANCE_CALL);
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        return mRequest;
    }


    private void setAddress(OrgBranchVO hospitalVO, DataObjectHolder dataObjectHolder) {
        // if (hospitalVO.getAmbulanceProviderAddressVO() != null) {
        //AddressVO addressVO = hospitalVO.getAmbulanceProviderAddressVO();
        AddressVO orgBranchAddress = hospitalVO.getAddress();
        String address = "";
        String COMMA = ",";
        if (!TextUtils.isEmpty(orgBranchAddress.getStreetAddress())) {
            address = address + orgBranchAddress.getStreetAddress() + COMMA;
        }
        if (!TextUtils.isEmpty(orgBranchAddress.getAdditionalAddress())) {
            address = address + orgBranchAddress.getAdditionalAddress() + COMMA;
        }
        if (!TextUtils.isEmpty(orgBranchAddress.getCity())) {
            address = address + orgBranchAddress.getCity() + COMMA;
        }
        Log.d(TAG, "ADDRESS=" + address);
        if (!TextUtils.isEmpty(address)) {
            dataObjectHolder.ivLocArrow.setVisibility(View.VISIBLE);
            dataObjectHolder.tvBranchName.setText(address.replaceAll(",$", ""));
        } else {
            dataObjectHolder.ivLocArrow.setVisibility(View.INVISIBLE);
        }

        /*} else {
            tvHospitalAddress.setText(!TextUtils.isEmpty(hospitalVO.getStreetAddress()) ? hospitalVO.getStreetAddress() : "");
        }*/
    }


    private void setProviderToCall(OrgBranchVO hospitalVO) {

        Intent intent = new Intent(applicationContext,
                ActivityEmergencyStepsListener.class);
        intent.putExtra("ambulance_provider_name", hospitalVO.getDisplayName());
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        if (AppUtil.isPhoneNumberValid(hospitalVO.getEmergencyPhoneNumber(), mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, "IN"))) {
            intent.putExtra("ambulance_provider_pno", hospitalVO.getEmergencyPhoneNumber());
        } else if (AppUtil.isPhoneNumberValid(hospitalVO.getTelephone(), mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, "IN"))) {
            intent.putExtra("ambulance_provider_pno", hospitalVO.getTelephone());
        } else {
            PublicProviderVO publicAmbProvider = AppUtil.getPublicEmergencyProvider(applicationContext);
            intent.putExtra("ambulance_provider_pno", publicAmbProvider != null ? publicAmbProvider.getEmergencyPhoneNo() : "108");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(intent);
        applicationContext.finish();

    }


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

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }


}