package com.patientz.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.VO.BloodyFriendVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Log;

import java.util.ArrayList;
import java.util.List;


public class AdapterPhoneContactsNew extends RecyclerView.Adapter<AdapterPhoneContactsNew.DataObjectHolder> {
    private static String TAG = "AdapterPhoneContactsNew";
    Context applicationContext;
    DatabaseHandler dh;
    List<BloodyFriendVO> patientSearchVOs = null;
    TextView text_count;
    SharedPreferences spnfs;
    int selectedContactsCount=0;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        TextView tv_contact_number, text_bf, tv_phone_name;
        ImageView iv_contact_img;
        CheckBox cb_invite;

        public DataObjectHolder(View itemView) {
            super(itemView);
            iv_contact_img = (ImageView) itemView.findViewById(R.id.iv_contact_img);
            tv_contact_number = (TextView) itemView.findViewById(R.id.tv_contact_number);
            tv_phone_name = (TextView) itemView.findViewById(R.id.tv_phone_name);
            text_bf = (TextView) itemView.findViewById(R.id.text_bf);
            cb_invite = (CheckBox) itemView.findViewById(R.id.cb_invite);
        }
    }

    public AdapterPhoneContactsNew(Context applicationContext, ArrayList<BloodyFriendVO> patientSearchVOs, TextView text_count) {
        this.text_count = text_count;
        this.patientSearchVOs = patientSearchVOs;
        this.applicationContext = applicationContext;
        dh = DatabaseHandler.dbInit(applicationContext);
        spnfs = applicationContext.getSharedPreferences("sync_data", 4);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_phone_contacts_new, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        final BloodyFriendVO bloodyFriendVO = patientSearchVOs.get(position);

       holder.tv_contact_number.setText(bloodyFriendVO.getContact());
        holder.tv_phone_name.setText(bloodyFriendVO.getContactName());
Log.d(TAG,"bloodyFriendVO.isUserInvited()>"+bloodyFriendVO.isUserInvited());
        Log.d(TAG,"bloodyFriendVO.getBloodGroup()>"+bloodyFriendVO.getBloodGroup());

        if (bloodyFriendVO.isUserInvited() && bloodyFriendVO.getBloodGroup() != null) {
            Log.e("isUserInvited--->", bloodyFriendVO.getContact() + ", " + bloodyFriendVO.isUserInvited() + ", " + bloodyFriendVO.getBloodGroup());
            holder.cb_invite.setVisibility(View.GONE);
            holder.text_bf.setVisibility(View.VISIBLE);
            if (bloodyFriendVO.getBloodGroup().trim().equals(CommonUtils.getSP(applicationContext).getString("bloodGroup", "").trim())) {
                //holder.text_bf.setTextColor(Color.parseColor("#A51400"));
                holder.text_bf.setText("Bloody Friend");
            } else {
                //holder.text_bf.setTextColor(Color.parseColor("#6785a9"));
                holder.text_bf.setText("Invited");
            }
        } else  if (bloodyFriendVO.isUserInvited() && bloodyFriendVO.getBloodGroup() == null && applicationContext.getSharedPreferences("invited_status", 4).getBoolean(bloodyFriendVO.getContact(),false)) {
            holder.cb_invite.setVisibility(View.GONE);
            holder.text_bf.setVisibility(View.VISIBLE);
            holder.text_bf.setText("Invited");
        }else{
            holder.cb_invite.setVisibility(View.VISIBLE);
            holder.text_bf.setVisibility(View.GONE);
        }

        holder.cb_invite.setOnCheckedChangeListener(null);
        holder.cb_invite.setChecked(spnfs.getBoolean(bloodyFriendVO.getContact(), false));

        holder.cb_invite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ArrayList<BloodyFriendVO> selcontactslist = dh.getSelected_5_SyncPhoneContacts();
                    Log.e("selcontact - > ", "" + selcontactslist.size());
                    if (selcontactslist.size() < 5) {
                        text_count.setVisibility(View.VISIBLE);
                        text_count.setText(++selectedContactsCount + " Selected");
                        dh.updatePhoneInviteStatus(bloodyFriendVO.get_id(), 1);
                        //bloodyFriendVO.setUserInvited(true);
                        spnfs.edit().putBoolean(bloodyFriendVO.getContact(), true).commit();
                        // notifyDataSetChanged();
                    } else {
                        buttonView.setChecked(false);
                        //spnfs.edit().putBoolean(bloodyFriendVO.getContact(), false).commit();
                        AppUtil.showToast(applicationContext, applicationContext.getString(R.string.label_sync_hint_sync5));
                    }
                    Log.e("true", "" + position);
                } else {
                    Log.e("selectedContactsCount", "" + selectedContactsCount);
                    int count=--selectedContactsCount;
                    if(count ==0)
                    {
                        text_count.setVisibility(View.GONE);
                    }else
                    {
                        text_count.setVisibility(View.VISIBLE);
                        text_count.setText(selectedContactsCount + " Selected");
                    }
                    dh.updatePhoneInviteStatus(bloodyFriendVO.get_id(), 0);
                    spnfs.edit().putBoolean(bloodyFriendVO.getContact(), false).commit();
                    //bloodyFriendVO.setUserInvited(false);
                    //notifyDataSetChanged();
                    Log.e("false", "" + position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return patientSearchVOs.size();
    }

}
