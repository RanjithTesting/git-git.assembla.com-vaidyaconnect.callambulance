package com.patientz.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.patientz.VO.AvailabilityCapabilityVO;
import com.patientz.VO.SpecialityFacilityTimingsVO;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;

import java.util.ArrayList;


public class AdapterFacilitiesSpecialitiesTimings extends RecyclerView
        .Adapter<AdapterFacilitiesSpecialitiesTimings
        .DataObjectHolder> {
    private static String TAG = "AdapterCurrentEmergencies";
    private ArrayList<SpecialityFacilityTimingsVO> mDataset;
    private static MyClickListener myClickListener;
    private Context applicationContext;


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView displayName, tvIsOpenNow,tvNoTimingsAvailable;
        LinearLayout llMoreTimings, llIsOpenNow;
        ImageView ivMoreTimings;

        public DataObjectHolder(View itemView) {
            super(itemView);
            displayName = (TextView) itemView.findViewById(R.id.display_name);
            tvIsOpenNow = (TextView) itemView.findViewById(R.id.tv_is_open_now);
            llMoreTimings = (LinearLayout) itemView.findViewById(R.id.ll_more_timings);
            llIsOpenNow = (LinearLayout) itemView.findViewById(R.id.ll_is_open_now);
            ivMoreTimings = (ImageView) itemView.findViewById(R.id.iv_more_timings);
            tvNoTimingsAvailable = (TextView) itemView.findViewById(R.id.tv_no_timings_Available);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getPosition(), v);
        }
    }

    public AdapterFacilitiesSpecialitiesTimings(Context applicationContext, ArrayList<SpecialityFacilityTimingsVO> myDataset) {
        mDataset = myDataset;
        this.applicationContext = applicationContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.facilities_specialities_timings_adapter_layout, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {
        Log.d("onBindViewHolder", "onBindViewHolder");

        SpecialityFacilityTimingsVO mSpecialityFacilityTimingsVO = mDataset.get(position);
        holder.displayName.setText(mSpecialityFacilityTimingsVO.getDisplayName());
        ArrayList<AvailabilityCapabilityVO> timingsList = mSpecialityFacilityTimingsVO.getTimings();
        Log.d("displayName",mSpecialityFacilityTimingsVO.getDisplayName());
        Log.d("timingsList_size",timingsList.size()+"");

        if (timingsList.size() > 0) {
            for (AvailabilityCapabilityVO timings : timingsList) {
                if (AppUtil.isCurrentTimeBetweenGivenTimes(timings.getStartTime(), timings.getEndTime())) {
                    holder.tvIsOpenNow.setText("Open Now - " + timings.getStartTime() + " - " + timings.getEndTime());
                } else {
                    holder.tvIsOpenNow.setText("Closed Now");
                }

                TextView tvTimings = new TextView(applicationContext);
                tvTimings.setText(timings.getStartTime() + " - " + timings.getEndTime());
                tvTimings.setTextColor(ContextCompat.getColor(applicationContext, R.color.outer_space));
                tvTimings.setTypeface(Typeface.create(applicationContext.getString(R.string.font_typeface_sans_serif_medium), Typeface.NORMAL));
                tvTimings.setTextSize(12);
                holder.llMoreTimings.addView(tvTimings);
            }
        }else
        {
            holder.llIsOpenNow.setVisibility(View.GONE);
            holder.tvNoTimingsAvailable.setVisibility(View.VISIBLE);
        }

        holder.llIsOpenNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.llMoreTimings.getVisibility() == View.VISIBLE) {
                    holder.llMoreTimings.setVisibility(View.GONE);
                } else {
                    holder.llMoreTimings.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }


}
