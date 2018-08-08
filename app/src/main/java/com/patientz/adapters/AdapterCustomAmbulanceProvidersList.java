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

import com.android.volley.RequestQueue;
import com.patientz.VO.AmbulanceProviderVO;
import com.patientz.VO.OrgLocationVO;
import com.patientz.activity.R;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Log;

import java.util.ArrayList;


public class AdapterCustomAmbulanceProvidersList extends BaseAdapter implements Filterable {

    Context context;
    ArrayList<AmbulanceProviderVO> list = new ArrayList<AmbulanceProviderVO>();
    private String TAG = "AdapterCustomAmbulanceProvidersList";
    ArrayList<AmbulanceProviderVO> filteredAmbulanceProvidersList;
    RequestQueue mRequestQueue;
    ArrayList<AmbulanceProviderVO> ambulanceList = new ArrayList<AmbulanceProviderVO>();

    public AdapterCustomAmbulanceProvidersList(Context context,
                                               ArrayList<AmbulanceProviderVO> list) {

        this.context = context;
        // this.list = list;
        this.filteredAmbulanceProvidersList = list;
        mRequestQueue = AppVolley.getRequestQueue();

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
            rowView = inflater.inflate(R.layout.row_ambulance_provider, parent,
                    false);
            holder = new Holder();
            holder.tv1 = (TextView) rowView.findViewById(R.id.tv1);
            holder.tv2 = (TextView) rowView.findViewById(R.id.tv2);
            holder.tv3 = (TextView) rowView.findViewById(R.id.tv3);
            rowView.setTag(holder);

        } else {
            holder = (Holder) rowView.getTag();
        }
        OrgLocationVO mOrgLocationVO = list.get(position).getLocation();
        String locationName = null;
        if (mOrgLocationVO != null) {
            locationName = mOrgLocationVO.getLocationName();
        }
        if (!TextUtils.isEmpty(list.get(position).getOrganizationName())) {
            holder.tv1.setVisibility(View.VISIBLE);
            holder.tv1.setText(list.get(position).getOrganizationName());
        } else {
            holder.tv1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(locationName)) {
            holder.tv2.setVisibility(View.VISIBLE);
            holder.tv2.setText(locationName);
        } else {
            holder.tv2.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(list.get(position).getPhoneNumber())) {
            holder.tv3.setVisibility(View.VISIBLE);
            holder.tv3.setText(list.get(position).getPhoneNumber());
        } else {
            holder.tv3.setVisibility(View.GONE);
        }
        return rowView;
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
                ArrayList<AmbulanceProviderVO> filterResults = new ArrayList<>();
                // getSessionAndCallUpdate(context, constraint);
                //mRequestQueue.add(getAllAmbulanceProviders(constraint));
                ArrayList<AmbulanceProviderVO> filteredAmbulanceProvidersList = ambulanceList;
                Log.d("AMBULANCE LIST SIZE FROM WEBSERVICE=", filteredAmbulanceProvidersList.size() + "");
                for (AmbulanceProviderVO ambulanceProviderVO : filteredAmbulanceProvidersList) {
                    Log.d(TAG, "Ambulance PROVIDER NAME=" + ambulanceProviderVO.getOrganizationName().toLowerCase());
                    Log.d(TAG, "CONSTRAINT NAME=" + constraint.toString().toLowerCase());
                    if (ambulanceProviderVO.getOrganizationName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        Log.d(TAG, "Ambulance PROVIDER NAME=" + ambulanceProviderVO.getOrganizationName().toLowerCase());
                        filterResults.add(ambulanceProviderVO);
                    }
                }
                Log.d("filterResults1=", filterResults.size() + "");
                results.values = filterResults;
                results.count = filterResults.size();
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                Log.d("filterResults2=", results.count + "");
                list = (ArrayList<AmbulanceProviderVO>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    };

    static class Holder {

        TextView tv2, tv3, tv1;

    }
}
