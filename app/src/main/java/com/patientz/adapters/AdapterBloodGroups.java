package com.patientz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.patientz.activity.R;

public class AdapterBloodGroups extends ArrayAdapter {

    private int resource;
    private Context mContext;

    public AdapterBloodGroups(Context context, int resource) {
        super(context, resource);
        this.resource = resource;
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowview = convertView;
        listHolder listholder;
        if (rowview == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = inflater.inflate(resource, parent, false);
            listholder = new listHolder();
            listholder.tvAllergyTypeImage = (ImageView) rowview
                    .findViewById(R.id.iv_emergency_type);
            listholder.tvAllergyTypeImage.setImageResource(mThumbIds[position]);

            rowview.setTag(listholder);
        } else {
            listholder = (listHolder) rowview.getTag();
        }
        return rowview;
    }

    @Override
    public Object getItem(int position) {
        return mThumbText[position];
    }

    public int getCount() {
        return mThumbIds.length;
    }

    private Integer[] mThumbIds = {R.drawable.ehr_bloodgroup_a_positive, R.drawable.ehr_bloodgroup_a_negative,
            R.drawable.ehr_bloodgroup_b_positive, R.drawable.ehr_bloodgroup_b_negative,
            R.drawable.ehr_bloodgroup_ab_positive, R.drawable.ehr_bloodgroup_ab_negative, R.drawable.ehr_bloodgroup_o_positive, R.drawable.ehr_bloodgroup_o_negative};

    private String[] mThumbText = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    static class listHolder {
        ImageView tvAllergyTypeImage;
    }

}
