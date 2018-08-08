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

public class AdapterAllergyTypes extends ArrayAdapter {

    private static final String TAG = "AdapterAllergyTypes";
    private int resource;
    private Context mContext;
    SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
    ArrayList<String> listOfAllergies;
    String[] existingAllergiesList;

    public AdapterAllergyTypes(Context context, int resource, ArrayList<String> listOfAllergies, String[] existingAllergiesList) {
        super(context, resource);
        this.resource = resource;
        mContext = context;
        this.listOfAllergies = listOfAllergies;
        this.existingAllergiesList = existingAllergiesList;
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
            listholder.checkbox = (AppCompatCheckBox) rowview.findViewById(R.id.checkbox);
            listholder.checkbox.setTag(position);
            listholder.checkbox.setChecked(mSparseBooleanArray.get(position));
            listholder.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
            if (existingAllergiesList != null) {
                for (String allergy : existingAllergiesList) {
                    if (listOfAllergies.get(position).equalsIgnoreCase(allergy)) {
                        listholder.checkbox.setChecked(true);
                    }
                }

            } else {

            }
            listholder.tvAllergyTypeImage = (ImageView) rowview
                    .findViewById(R.id.iv_allergy_type);
            listholder.tvAlleryTypeName.setText(listOfAllergies.get(position));
            listholder.tvAllergyTypeImage.setImageResource(mThumbIds[position]);
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


        for (int i = 0; i < listOfAllergies.size(); i++) {
            if (mSparseBooleanArray.get(i)) {
                mTempArry.add(listOfAllergies.get(i));
            }
        }
        Log.d(TAG, "CHECKED ITEMS LIST SIZE=" + mTempArry.size());
        return mTempArry;
    }

    @Override
    public Object getItem(int position) {
        return listOfAllergies.get(position);
    }


    public int getCount() {
        return listOfAllergies.size();
    }

    private Integer[] mThumbIds = {R.drawable.ehr_allergy_peanut, R.drawable.ic_allergies_egg, R.drawable.ic_allergies_dairy,
            R.drawable.ic_allergies_pollen, R.drawable.ehr_allergy_dust,
            R.drawable.ic_allergies_cosmetics};
    // references to our captions


    static class listHolder {
        TextView tvAlleryTypeName;
        ImageView tvAllergyTypeImage;
        AppCompatCheckBox checkbox;
    }

}
