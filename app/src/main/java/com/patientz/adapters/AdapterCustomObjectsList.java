package com.patientz.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.patientz.VO.HospitalVO;
import com.patientz.activity.R;
import com.patientz.utils.Log;

import java.util.ArrayList;

public class AdapterCustomObjectsList extends BaseAdapter implements Filterable {

    private static String TAG = "AdapterCustomObjectsList";
    Context context;
    ArrayList<HospitalVO> list;
    ArrayList<HospitalVO> filteredEmergencyProvidersList;

    private final Object mLock = new Object();

    public AdapterCustomObjectsList(Context context,
                                    ArrayList<HospitalVO> mlist) {

        this.context = context;
        this.list = mlist;
        this.filteredEmergencyProvidersList = mlist;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Log.d(">>>ALA>>>gV", "getView");
        View rowView = convertView;
        Holder holder;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_ambulance_emergency_list, parent,
                    false);
            holder = new Holder();
            holder.tv1 = (TextView) rowView.findViewById(R.id.tv1);
            holder.tv2 = (TextView) rowView.findViewById(R.id.tv2);
            rowView.setTag(holder);

        } else {
            holder = (Holder) rowView.getTag();
        }

        if (!TextUtils.isEmpty(list.get(position).getName())) {
            holder.tv1.setVisibility(View.VISIBLE);
            holder.tv1.setText(list.get(position).getName());
            Log.d(TAG, "NAME=" + list.get(position).getName());
        } else {
            holder.tv1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(getAddress(list.get(position)))) {
            Log.d(TAG, "ADDRESS=" + getAddress(list.get(position)));
            holder.tv2.setVisibility(View.VISIBLE);
            holder.tv2.setText(getAddress(list.get(position)));
        } else {
            holder.tv2.setVisibility(View.GONE);
        }


        return rowView;
    }

    private static String getAddress(HospitalVO hospitalVO) {
        String address = null;
        String additionalAddress = hospitalVO.getAdditionalAddress();
        if (!TextUtils.isEmpty(additionalAddress)) {
            address = additionalAddress + ",";
        }
        address = (address == null ? hospitalVO.getCity() : address + hospitalVO.getCity());
        Log.d(TAG, "ADDRESS=" + address);
        Log.d(TAG, "ADDITIONAL ADDRESS=" + hospitalVO.getAdditionalAddress() + "");
        Log.d(TAG, "CITY=" + hospitalVO.getCity() + "");

        return address;

    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.d(TAG, "PERFORM FILTERING=" + constraint);
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<HospitalVO> filterResults = new ArrayList<>();
                Log.d(TAG, "EMERGENCY PROVIDER LIST=" + list.size());
                for (HospitalVO hospitalVO : filteredEmergencyProvidersList) {
                    Log.d(TAG, "hospitalVO NAME=" + hospitalVO.getName().toLowerCase());
                    Log.d(TAG, "CONSTRAINT NAME=" + constraint.toString().toLowerCase());
                    if (hospitalVO.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        Log.d(TAG, "hospitalVO NAME=" + hospitalVO.getName().toLowerCase());
                        Log.d(TAG, "CONSTRAINT NAME=" + constraint.toString().toLowerCase());

                        filterResults.add(hospitalVO);
                    }
                }
                results.values = filterResults;
                results.count = filterResults.size();
                return results;
            } else {
                Log.d(TAG, "ELSE");
                results.values = filteredEmergencyProvidersList;
                results.count = filteredEmergencyProvidersList.size();
                return results;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            list = (ArrayList<HospitalVO>) results.values;
            notifyDataSetChanged();
            // }
        }

    };


    static class Holder {

        TextView tv1;
        TextView tv2;


    }

}
