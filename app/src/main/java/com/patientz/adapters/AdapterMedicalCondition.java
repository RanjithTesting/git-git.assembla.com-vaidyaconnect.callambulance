package com.patientz.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.activity.R;
import com.patientz.utils.Log;

import java.util.ArrayList;

public class AdapterMedicalCondition extends ArrayAdapter {

    private int resource;
    private Context mContext;
    private static final String TAG = "AdapterMedicalCondition";
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
    private ArrayList<String> listOfMedicalConditions;
    String[] existingMedicalConditionList;


    public AdapterMedicalCondition(Context context, int resource, ArrayList<String> listOfMedicalConditions, String[] existingMedicalConditionList) {
        super(context, resource);
        this.resource = resource;
        mContext = context;
        this.listOfMedicalConditions=listOfMedicalConditions;
        this.existingMedicalConditionList=existingMedicalConditionList;
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
            listholder.tvAlleryTypeName = (TextView) rowview.findViewById(R.id.tv_allergy_type);
            listholder.tvAllergyTypeImage = (ImageView) rowview
                    .findViewById(R.id.iv_allergy_type);
            listholder.checkbox = (AppCompatCheckBox) rowview.findViewById(R.id.checkbox);
            listholder.checkbox.setTag(position);
            listholder.checkbox.setChecked(mSparseBooleanArray.get(position));
            listholder.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
            listholder.tvAlleryTypeName.setText(listOfMedicalConditions.get(position));
            listholder.tvAllergyTypeImage.setImageResource(mThumbIds[position]);
            if (existingMedicalConditionList != null) {
                for (String allergy : existingMedicalConditionList) {
                    if (listOfMedicalConditions.get(position).equalsIgnoreCase(allergy)) {
                        listholder.checkbox.setChecked(true);
                    }
                }
            } else {

            }
            rowview.setTag(listholder);
        } else {
            listholder = (listHolder) rowview.getTag();
        }
        return rowview;
    }
    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
            Log.d(TAG, "Check box tag=" + buttonView.getTag());
        }
    };

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();
        Log.d(TAG, "mTempArraySize=" + mTempArry.size());
        Log.d(TAG, "mThumbTextSize=" + listOfMedicalConditions.size());
        Log.d(TAG, "mSparseBooleanArraySize=" + mSparseBooleanArray.size());

        for (int i = 0; i < listOfMedicalConditions.size(); i++) {
            if (mSparseBooleanArray.get(i)) {
                mTempArry.add(listOfMedicalConditions.get(i));
            }
        }

        return mTempArry;
    }


    @Override
    public Object getItem(int position) {
        return listOfMedicalConditions.get(position);
    }


    public int getCount() {
        return listOfMedicalConditions.size();
    }

    private Integer[] mThumbIds = {R.drawable.ehr_medical_condition_diabetes, R.drawable.ehr_medical_condition_hypertension, R.drawable.ehr_medical_condition_heart,
            R.drawable.ehr_medical_condition_asthma, R.drawable.ehr_medical_condition_kidney,
            R.drawable.ehr_medical_condition_thyroid};
    // references to our captions



    static class listHolder {
        TextView tvAlleryTypeName;
        ImageView tvAllergyTypeImage;
        AppCompatCheckBox checkbox;
    }

}
