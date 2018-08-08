package com.patientz.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.patientz.VO.KeyValueObject;
import com.patientz.activity.R;

import java.util.List;

public class HealthRecordAdapter extends BaseAdapter {

	private LayoutInflater cInflater;
	private List<KeyValueObject> cList;
	Context mContext;

	public HealthRecordAdapter(Context context,
							   List<KeyValueObject> healthRecordList) {
		mContext = context;
		cInflater = LayoutInflater.from(context);
		cList = healthRecordList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = cInflater.inflate(R.layout.profile_row, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.key);
			holder.value = (TextView) convertView.findViewById(R.id.value);
			// holder.getVerifiedByDoctor=(TextView)convertView.findViewById(R.id.getVerifiedByDoctorTextView);
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.title.setText(cList.get(position).getKey());
		holder.value.setText(cList.get(position).getValue());

		return convertView;
	}

	static class ViewHolder {
		TextView getVerifiedByDoctor;
		TextView title;
		TextView value;
	}

	/*public void navigateToDoctorFrag(View view) {
		AppUtil.switchFragment((Activity) mContext, new DoctorsListFragment());
	}*/

}
