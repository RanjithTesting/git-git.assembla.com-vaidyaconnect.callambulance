package com.patientz.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.patientz.VO.InsuranceVO;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;


public class InsuranceRecyclerViewAdapter extends RecyclerView.Adapter<InsuranceRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<InsuranceVO> mValues;
    private final InsuranceFragment.OnListFragmentInteractionListener mListener;
    private final Activity insuranceActivity;

    public InsuranceRecyclerViewAdapter(Activity context, ArrayList<InsuranceVO> items, InsuranceFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        insuranceActivity = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_insurance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        InsuranceVO mInsuranceVO=mValues.get(position);
        setPolicyDetailsStatus(holder,position);
        holder.mIdView.setText(mValues.get(position).getInsPolicyCompany());
        holder.mContentView.setText(mValues.get(position).getInsPolicyNo()!=null?mValues.get(position).getInsPolicyNo():"_ _ _ _ _ _ _ _ ");


        if(mInsuranceVO.getInsuranceUploadId()==0)
        {
            holder.ivNetworkHospitals.setVisibility(View.GONE);
            holder.mInsuranceStatusView.setTextColor(ContextCompat.getColor(insuranceActivity,R.color.yellow_green));
            holder.mInsuranceStatusView.setText(AppUtil.checkInsuranceStatus(mValues.get(position)) ? insuranceActivity.getString(R.string.active) : insuranceActivity.getString(R.string.expired));
        }else
        {
            holder.ivNetworkHospitals.setVisibility(View.VISIBLE);
            String[] insuranceUploadStatuses=insuranceActivity.getResources().getStringArray(R.array.insurance_upload_status);
            switch (mInsuranceVO.getStatus())
            {
                case 0:
                    holder.mInsuranceStatusView.setTextColor(ContextCompat.getColor(insuranceActivity,R.color.harvard_crimpson));
                    holder.mInsuranceStatusView.setText(insuranceUploadStatuses[mInsuranceVO.getStatus()]);
                    break;
                case 1:
                    holder.mInsuranceStatusView.setTextColor(ContextCompat.getColor(insuranceActivity,R.color.harvard_crimpson));
                    holder.mInsuranceStatusView.setText(insuranceUploadStatuses[mInsuranceVO.getStatus()]);
                    break;
                case 2:
                    holder.mInsuranceStatusView.setTextColor(ContextCompat.getColor(insuranceActivity,R.color.dark_jungle_green));
                    holder.mInsuranceStatusView.setText(insuranceUploadStatuses[mInsuranceVO.getStatus()]);
                    break;
            }
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        holder.ivNetworkHospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebServiceUrls.apollo_munich_network_hospitals));
                insuranceActivity.startActivity(browserIntent);
            }
        });
        setCallButton(mValues.get(position), holder);
    }

    private void setPolicyDetailsStatus(ViewHolder viewHolder, int position) {
        InsuranceVO mInsuranceVO=mValues.get(position);
        if(mInsuranceVO.getPolicyDoc()!=null)
        {
            if(mInsuranceVO.getPolicyDoc().getId()!=0)
            {
                Log.d("trolley_grey=","trolley_grey");
                viewHolder.llInsuranceApprovedStatus.setVisibility(View.VISIBLE);
                viewHolder.tvPolicyDetailsStatus.setText(insuranceActivity.getString(R.string.view_download_policy));
                viewHolder.tvPolicyDetailsStatus.setTextColor(ContextCompat.getColor(insuranceActivity, R.color.trolley_grey));
                viewHolder.tvPolicyDetailsStatus.setCompoundDrawables(ContextCompat.getDrawable(insuranceActivity,R.drawable.bell_icon),null,null,null);
            }
        }else if(mInsuranceVO.getInsuranceUploadId()!=0)
        {
            Log.d("harvard_crimpson1=","harvard_crimpson");
            if(!mInsuranceVO.isMobileNumberVerified() || !mInsuranceVO.isEmailVerified())
            {
                viewHolder.llInsuranceApprovedStatus.setVisibility(View.VISIBLE);
                viewHolder.tvPolicyDetailsStatus.setTextColor(ContextCompat.getColor(insuranceActivity, R.color.harvard_crimpson));
            }else
            {
                viewHolder.llInsuranceApprovedStatus.setVisibility(View.GONE);
            }
        }else
        {
            viewHolder.llInsuranceApprovedStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mInsuranceStatusView,tvPolicyDetailsStatus;
        public final ImageView callImageView;
        public InsuranceVO mItem;
        private LinearLayout llInsuranceApprovedStatus;
        private ImageView ivNetworkHospitals;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mInsuranceStatusView = (TextView) view.findViewById(R.id.insurance_status);
            callImageView = (ImageView) view.findViewById(R.id.provider_call_button);
            llInsuranceApprovedStatus =  view.findViewById(R.id.ll_insurance_approved_status);
            tvPolicyDetailsStatus =  view.findViewById(R.id.tv_policy_details_status);
            ivNetworkHospitals =  view.findViewById(R.id.iv_network_hsopitals);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


    public void setCallButton(InsuranceVO insuranceVO,
                              InsuranceRecyclerViewAdapter.ViewHolder holder) {
        final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(insuranceVO.getClaimPhoneNumber())) {
            nameValuePairs.add(new BasicNameValuePair("Mobile: ", insuranceVO.getClaimPhoneNumber()));
        }
        if (nameValuePairs.isEmpty() == false) {
            int i = 0;
            CharSequence[] cs = new String[nameValuePairs.size()];
            while (i != nameValuePairs.size()) {
                cs[i] = nameValuePairs.get(i).getName()
                        + nameValuePairs.get(i).getValue();
                i++;
            }
            final CharSequence[] items = cs;
            holder.callImageView.setVisibility(View.VISIBLE);
            holder.callImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            insuranceActivity);
                    builder.setTitle("Call Insurance Provider: ");
                    builder.setItems(items,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int item) {
                                    Toast.makeText(
                                            insuranceActivity,
                                            nameValuePairs.get(item).getValue(),
                                            Toast.LENGTH_SHORT).show();
                                    try {
                                        String phonee = "tel:"
                                                + nameValuePairs.get(item)
                                                .getValue();
                                        Intent intent = new Intent(
                                                Intent.ACTION_CALL, Uri
                                                .parse(phonee));
                                        insuranceActivity.startActivity(intent);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).show();
                }
            });

        } else {
            holder.callImageView.setVisibility(View.INVISIBLE);
        }

    }
}
