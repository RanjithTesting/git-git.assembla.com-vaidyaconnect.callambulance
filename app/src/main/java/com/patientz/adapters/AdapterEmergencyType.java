package com.patientz.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.activity.R;
import com.patientz.utils.Log;

public class AdapterEmergencyType extends ArrayAdapter {

    private int resource;
    private Context mContext;
    private Integer[] mThumbIds;
    private String[] mThumbText;
    SharedPreferences defaultSharedPreferences;

    public AdapterEmergencyType(Context context, int resource, Integer[] mThumbIds, String[] mThumbText) {
        super(context, resource);
        this.resource = resource;
        mContext = context;
        this.mThumbIds = mThumbIds;
        this.mThumbText = mThumbText;
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
            listholder.tvEmergencyTypeName = (TextView) rowview.findViewById(R.id.tv_emergency_type);
            listholder.tvEmergencyTypeImage = (ImageView) rowview
                    .findViewById(R.id.iv_emergency_type);
            listholder.tvEmergencyTypeName.setText(mThumbText[position]);
            listholder.tvEmergencyTypeImage.setImageResource(mThumbIds[position]);
            rowview.setTag(listholder);
        } else {
            listholder = (listHolder) rowview.getTag();
        }
        if (defaultSharedPreferences.getInt("file_upload_tag_id", -1) == position) {
            Log.e("file_upload_tag_id->", "" + defaultSharedPreferences.getInt("file_upload_tag_id", -1));
            rowview.setBackgroundColor(Color.GRAY);
        }
        return rowview;

    }

    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public String getItem(int position) {
        return mThumbText[position];
    }

// references to our captions


    static class listHolder {
        TextView tvEmergencyTypeName;
        ImageView tvEmergencyTypeImage;
    }

}
