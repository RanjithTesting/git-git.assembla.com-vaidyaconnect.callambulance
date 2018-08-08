package com.patientz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.patientz.VO.UserInfoVO;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SelectInsuredPersonAdapter extends RecyclerView
        .Adapter<SelectInsuredPersonAdapter
        .DataObjectHolder> {
    private static String TAG = "SelectInsuredPersonAdapter";
    private ArrayList<UserInfoVO> mDataset;
    private static OnItemClickListener itemClickListener;
    private Context applicationContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvAgeGender;
        LinearLayout llPersonAgeGender;
        ImageView ivUserPic;
        RelativeLayout parentLayout;

        public DataObjectHolder(View itemView) {
            super(itemView);
            llPersonAgeGender = itemView.findViewById(R.id.ll_person_age_gender);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAgeGender = (TextView) itemView.findViewById(R.id.tv_age_gender);
            ivUserPic = (ImageView) itemView.findViewById(R.id.iv_user_pic);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


    public SelectInsuredPersonAdapter(Context applicationContext, ArrayList<UserInfoVO> myDataset,OnItemClickListener itemClickListener) {
        mDataset = myDataset;
        this.applicationContext = applicationContext;
        this.itemClickListener=itemClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_insured_person_adapter_layout, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        final UserInfoVO mUserInfoVO = mDataset.get(position);
        holder.tvName.setText(mUserInfoVO.getFirstName() + " " + mUserInfoVO.getLastName());
        if(mUserInfoVO.getDateOfBirth()!=null && mUserInfoVO.getGender()!=0)
        {
            holder.tvAgeGender.setVisibility(View.VISIBLE);
            holder.tvAgeGender.setText(AppUtil.convertToAge(mUserInfoVO.getDateOfBirth(),applicationContext) + " | " + (mUserInfoVO.getGender() == 2 ? applicationContext.getString(R.string.female) :applicationContext.getString(R.string.male)));
        }
        setUserPic(mDataset.get(position), holder.ivUserPic);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onListItemClicked(position,mUserInfoVO);
            }
        });
    }

    private void setUserPic(UserInfoVO mUserInfoVO, ImageView ivPic) {
        Log.d(TAG, "PIC ID=" + mUserInfoVO.getPicFileId());
        Log.d(TAG, "USER NAME=" + mUserInfoVO.getFirstName());
        String url = WebServiceUrls.serverUrl + WebServiceUrls.getUploadedImage + mUserInfoVO.getPicFileId();
        Log.d(TAG, "Url=" + url);
        int pic=13;
        if(mUserInfoVO.getGender()==2)
        {
            pic=R.drawable.sister;
        }else if(mUserInfoVO.getGender()==1)
        {
            pic=R.drawable.myself;
        }else
        {
            pic=R.drawable.add_person;
        }
        Picasso
                .with(applicationContext)
                .load(url)
                .placeholder(pic)
                .into(ivPic);
    }

    public void addItem(UserInfoVO dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnItemClickListener {
        void onListItemClicked(int position, UserInfoVO userInfoVO);
    }


}
