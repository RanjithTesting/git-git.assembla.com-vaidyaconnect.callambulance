package com.patientz.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.patientz.VO.DoctorSearchVO;
import com.patientz.activity.R;
import com.patientz.utils.AppVolley;

import java.util.ArrayList;


/**
 * Created by YKSIT on 5/30/2016.
 */
public class NearbyDoctorsListAdapter extends RecyclerView.Adapter<NearbyDoctorsListAdapter.ViewHolder> {
    private static final String TAG = "NearbyDoctorsListAdapter";
    private ArrayList<DoctorSearchVO> doctorsList;
    private Context activity;
    private RequestQueue mRequestQueue = AppVolley.getRequestQueue();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_doctor_specality, tv_doctor_qualif, tv_doctor_name, tv_doctor_status, tvDistanceInKms, tvTimeToReach;
        private ImageButton btDirections, btCall;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_doctor_specality = (TextView) itemView.findViewById(R.id.tv_doctor_specality);
            tv_doctor_name = (TextView) itemView.findViewById(R.id.tv_doctor_name);
            tv_doctor_qualif = (TextView) itemView.findViewById(R.id.tv_doctor_qualif);
            btDirections = (ImageButton) itemView.findViewById(R.id.bt_directions);
            btCall = (ImageButton) itemView.findViewById(R.id.bt_call);
            tvDistanceInKms = (TextView) itemView.findViewById(R.id.tv_distance_in_kms);
            tvTimeToReach = (TextView) itemView.findViewById(R.id.tv_time_to_reach);
            tv_doctor_status = (TextView) itemView.findViewById(R.id.tv_doctor_status);

        }
    }

    public NearbyDoctorsListAdapter(Context activity, ArrayList<DoctorSearchVO> doctorSearchVos) {
        this.doctorsList = doctorSearchVos;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_doctrz_adapter, parent, false);
        ViewHolder dataObjectHolder = new ViewHolder(inflatedView);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DoctorSearchVO doctorDetails = doctorsList.get(position);

//        String d_name = "Dr. " + doctorDetails.getFirstName() + " " + doctorDetails.getLastName();
//        if (doctorDetails.getQualificationsCsv() != null) {
//            d_name = d_name + ", " + doctorDetails.getQualificationsCsv();
//        }
        holder.tv_doctor_name.setText("Dr. " + doctorDetails.getFirstName() + " " + doctorDetails.getLastName() + ",");
        holder.tv_doctor_qualif.setText(doctorDetails.getQualificationsCsv());
        doctorDetails.getSpecalitiesList().get(0).getDisplayName();
        if (doctorDetails.getSpecalitiesList() != null && doctorDetails.getSpecalitiesList().size() != 0 &&
                doctorDetails.getSpecalitiesList().get(0).getDisplayName() != null) {
            holder.tv_doctor_specality.setText(doctorDetails.getSpecalitiesList().get(0).getDisplayName());
        } else {
            holder.tv_doctor_specality.setText("");
        }
        holder.tvTimeToReach.setText(doctorDetails.getDuration());
        holder.tvDistanceInKms.setText(doctorDetails.getDistance());
        holder.tv_doctor_status.setText(getStatus(doctorDetails.getStatus()));

        holder.btDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirections(doctorDetails);
            }
        });
        holder.btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doctorDetails.getPhoneNumber() != null && doctorDetails.getPhoneNumber().trim().length() > 0) {
                    call(doctorDetails.getPhoneNumber());
                } else {
                    Toast.makeText(activity, "Phone no not available for thi user", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return doctorsList.size();
    }

    public interface RecyclerViewOnclick {
        public void getDoctorDetails(DoctorSearchVO userProfile);
    }

    private void call(String emergencyPhoneNumber) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + emergencyPhoneNumber));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivity(callIntent);

    }

    public void showDirections(DoctorSearchVO doctorSearchVO) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + doctorSearchVO.getLatitude() + "," + doctorSearchVO.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(mapIntent);
        }
    }

    public String getStatus(int status) {
        String statusis = "";
        if (status == 0) {
            statusis = "Off Duty";
        } else if (status == 1) {
            statusis = "Available on Call";
        } else if (status == 2) {
            statusis = "Available for Appointments";
        } else if (status == 3) {
            statusis = "Emergency Only";
        }
        return statusis;
    }

}
