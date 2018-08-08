package com.patientz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.patientz.VO.OrgBranchVO;
import com.patientz.activity.R;
import com.patientz.utils.Log;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {

    private static final String TAG ="SpinnerAdapter" ;
    ArrayList<OrgBranchVO> list;
    Context mContext;

    public SpinnerAdapter(Context mContext, ArrayList<OrgBranchVO> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public OrgBranchVO getItem(int position) {
        // TODO Auto-generated method stub
        Log.d(TAG,"position="+position);
        Log.d(TAG,"OrgBranch name="+list.get(position).getBranchName());
        Log.d(TAG,"OrgBranch id="+list.get(position).getOrgBranchId());

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.adapter_spinner, null);
        TextView text_name = (TextView) v.findViewById(R.id.text_name);
        //ImageView dropdownicon = (ImageView) v.findViewById(R.id.dropdownicon);

        text_name.setText(list.get(arg0).getDisplayName());
       /* if (arg0 == 0 && list.get(arg0).contains("Select")) {
            text_name.setTextColor(Color.parseColor("#0079b2"));
            text_name.setTypeface(Typeface.DEFAULT_BOLD);
            dropdownicon.setVisibility(View.VISIBLE);
        }

        if (arg0 == 0 && !list.get(arg0).contains("Select")) {
            dropdownicon.setVisibility(View.VISIBLE);
        }
*/
        return v;
    }

}
