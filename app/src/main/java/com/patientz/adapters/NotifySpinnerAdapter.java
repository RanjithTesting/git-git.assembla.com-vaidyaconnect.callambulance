package com.patientz.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.patientz.VO.OrganisationVO;
import com.patientz.activity.R;

import java.util.ArrayList;

/**
 * Created by sunil on 8/2/17.
 */
public class NotifySpinnerAdapter implements SpinnerAdapter {
    ArrayList<OrganisationVO> data;
    Context mContext;

    public NotifySpinnerAdapter(Context context, ArrayList<OrganisationVO> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        System.out.println(data);
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return android.R.layout.simple_spinner_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = new TextView(mContext);
        v.setPadding(10, 10, 10, 10);
        v.setTextColor(Color.WHITE);
       // v.setBackground(R.drawable.);
        v.setText(data.get(position).getOrgName());
        return v;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return this.getView(position, convertView, parent);
    }
}
