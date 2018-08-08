package com.patientz.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.VO.UserProfile;
import com.patientz.VO.UserSpecialty;
import com.patientz.activity.NotifyDoctorActivity;
import com.patientz.activity.R;
import com.patientz.interfaces.RecyclerViewClickListener;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.patientz.activity.R.id.iv_is_notifying_doctor;


/**
 * Created by YKSIT on 5/30/2016.
 */
public class NotifyDoctorsListAdapter extends RecyclerView
        .Adapter<NotifyDoctorsListAdapter
        .ViewHolder> {
    private static final String TAG = "NotifyDoctorsListAdapter";
    private ArrayList<UserProfile> doctorsList;
    private Activity activity;
    private RecyclerViewOnclick mRecyclerViewOnclick;
    List<UserProfile> doctorSearchVos = null;
    private ArrayList<UserProfile> doctorListFilterList;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView doctorName, specality;//,branchName,experience;
        private CardView cardView;
        private ImageView profilePic,ivIsNotifyinDoctor;
        public RecyclerViewClickListener mListener;
        // private final LinearLayout llDirection;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview);
            doctorName = (TextView) itemView.findViewById(R.id.tv_doctor_name);
            specality = (TextView) itemView.findViewById(R.id.tv_speciality);
            //branchName = (TextView) itemView.findViewById(R.id.tv_branch_name);
            //experience = (TextView) itemView.findViewById(R.id.tv_experience);
            profilePic = (ImageView) itemView.findViewById(R.id.iv_profile_pic);
            ivIsNotifyinDoctor = (ImageView) itemView.findViewById(iv_is_notifying_doctor);



        }
    }


    public NotifyDoctorsListAdapter(NotifyDoctorActivity activity, ArrayList<UserProfile> doctorSearchVos) {
        this.doctorsList = doctorSearchVos;
        this.activity = activity;
        this.mRecyclerViewOnclick = (RecyclerViewOnclick) activity;
        this.doctorSearchVos = doctorSearchVos;
        this.doctorListFilterList = new ArrayList<>();
        this.doctorListFilterList.addAll(doctorSearchVos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify_doctors_adapter_layout, parent, false);
        ViewHolder dataObjectHolder = new ViewHolder(inflatedView);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserProfile doctorDetails = doctorsList.get(position);
        holder.doctorName.setText("Dr." + doctorDetails.getFirstName() + " " + doctorDetails.getLastName());
        //holder.branchName.setText(doctorDetails.);
        ArrayList<UserSpecialty> specialities = doctorDetails.getOtherSpecialities();
        if(specialities.get(0)!=null)
        {
            holder.specality.setText(specialities.get(0).getSpeciality().getDisplayName());
        }else
        {
            holder.specality.setText("");
        }
        if(doctorDetails.isNotifyingDoctor())
        {
          holder.ivIsNotifyinDoctor.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.dashboard_green_tick));
        }else
        {
        }
        downloadAndSetProfileImage(doctorDetails, holder.profilePic);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerViewOnclick.getDoctorDetails(doctorDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorsList.size();
    }


    public interface RecyclerViewOnclick {
        public void getDoctorDetails(UserProfile userProfile);
    }

    public void downloadAndSetProfileImage(UserProfile doctorDetails, final ImageView profilePic) {
        long patientId=AppUtil.getCurrentSelectedPatientId(activity);
        String url = WebServiceUrls.getPatientAttachment +getProfilePicId(doctorDetails)+"&patientId="+patientId+"&moduleType="+ Constant.IMAGE_MODULE_TYPE_PROFILE_PIC;
        Log.d(TAG, "Url=" + url);
        Picasso
                .with(activity)
                .load(url)
                .placeholder(R.drawable.emergency_default_profile)
                .into(profilePic);

    }

    private long getProfilePicId(UserProfile doctorDetails) {
        long profilePicId = 0;
        if (doctorDetails.getProfilePicture() != null) {
            profilePicId = doctorDetails.getProfilePicture().getId();
        }
        return profilePicId;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        doctorSearchVos.clear();
        if (charText.length() == 0) {
            doctorSearchVos.addAll(doctorListFilterList);
        } else {
            for (UserProfile wp : doctorListFilterList) {
                if (wp.getFirstName().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getLastName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    doctorSearchVos.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
