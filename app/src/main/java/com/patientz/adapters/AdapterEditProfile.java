package com.patientz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.patientz.VO.PatientUserVO;
import com.patientz.activity.R;

import java.util.ArrayList;



public class AdapterEditProfile extends RecyclerView
		.Adapter<AdapterEditProfile
		.DataObjectHolder> {
	private static String TAG = "AdapterSelectPatientInEmergency";
	private ArrayList<PatientUserVO> mDataset;
	private static MyClickListener myClickListener;
	private Context applicationContext;

	public static class DataObjectHolder extends RecyclerView.ViewHolder
			implements View
			.OnClickListener {
		TextView tvUserName;
		ImageView ivUserPic;
		RelativeLayout rlParent;

		public DataObjectHolder(View itemView) {
			super(itemView);
			tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
			ivUserPic = (ImageView) itemView.findViewById(R.id.iv_user_pic);
			rlParent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			myClickListener.onItemClick(getPosition(), v);
		}
	}

	public void setOnItemClickListener(MyClickListener myClickListener) {
		AdapterEditProfile.myClickListener = myClickListener;
	}

	public AdapterEditProfile(Context applicationContext, ArrayList<PatientUserVO> myDataset) {
		mDataset = myDataset;
		this.applicationContext=applicationContext;
	}

	@Override
	public DataObjectHolder onCreateViewHolder(ViewGroup parent,
											   int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.adapter_select_patient_in_emergency, parent, false);

		DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
		return dataObjectHolder;
	}

	@Override
	public void onBindViewHolder(DataObjectHolder holder, int position) {
		PatientUserVO mPatientUserVO=mDataset.get(position);
		holder.tvUserName.setText(mPatientUserVO.getFirstName()+" "+mPatientUserVO.getLastName());
		if(mPatientUserVO.isUnderEmergency()){
			holder.rlParent.setBackgroundColor(Color.RED);
			holder.tvUserName.setTextColor(Color.WHITE);
			//holder.profileInfo.setText("Currently in Emergency.");
			//holder.profileInfo.setTextColor(Color.WHITE);
		}else{
			//mDataHolder.profileInfo.setText(mPatientUserVO.getRelationship()+" | "+mPatientUserVO.getRole());
			holder.tvUserName.setTextColor(applicationContext.getResources().getColor(R.color.black));
			//mDataHolder.profileInfo.setTextColor(Color.BLACK);
			holder.rlParent.setBackgroundColor(Color.WHITE);
		}
		//holder.ivUserPic.setText(mDataset.get(position).getmText2());
	}

	public void addItem(PatientUserVO dataObj, int index) {
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
