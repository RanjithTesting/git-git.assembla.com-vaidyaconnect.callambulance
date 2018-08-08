package com.patientz.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.patientz.VO.PatientUserVO;
import com.patientz.activity.R;

import java.util.ArrayList;

/**
 * Created by sunil on 17/05/16.
 */
public class AdapterListDialogFragment extends BaseAdapter {
    Context applicationContext;
    ArrayList<PatientUserVO> allUsers;
    public AdapterListDialogFragment(Context applicationContext, ArrayList<PatientUserVO> allUser) {
        this.applicationContext=applicationContext;
        allUsers=allUser;
    }

    @Override
    public int getCount() {
        return allUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return allUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                applicationContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_select_member, null);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.ownersRelationToUser = (TextView) convertView.findViewById(R.id.tv_relationship);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        PatientUserVO user=(PatientUserVO)getItem(position);
        holder.userName.setText(user.getFirstName());
        holder.ownersRelationToUser.setText(user.getRelationship()!=null?user.getRelationship():"");

        return convertView;
    }
public class ViewHolder
{
    TextView userName,ownersRelationToUser;
}

}
