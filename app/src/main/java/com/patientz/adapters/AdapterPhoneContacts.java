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
import com.patientz.activity.BloodyFriendsActivity;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.Log;

import java.util.ArrayList;


public class AdapterPhoneContacts extends RecyclerView
        .Adapter<AdapterPhoneContacts
        .DataObjectHolder> {
    private static String TAG = "AdapterPhoneContacts";
    ArrayList<BloodyFriendVO> contactslist;
    private Context applicationContext;
    SharedPreferences spnfs;
    SharedPreferences.Editor spedit;
    DatabaseHandler dh;
    boolean select_all;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        TextView tv_contact_number, tv_phone_name, tv_blood_group, tv_server_name;
        ImageView iv_contact_img;
        CheckBox cb_invite;

        public DataObjectHolder(View itemView) {
            super(itemView);
            iv_contact_img = (ImageView) itemView.findViewById(R.id.iv_contact_img);
            tv_contact_number = (TextView) itemView.findViewById(R.id.tv_contact_number);
            tv_server_name = (TextView) itemView.findViewById(R.id.tv_server_name);
            tv_phone_name = (TextView) itemView.findViewById(R.id.tv_phone_name);
            tv_blood_group = (TextView) itemView.findViewById(R.id.tv_blood_group);
            cb_invite = (CheckBox) itemView.findViewById(R.id.cb_invite);

        }
    }

    public AdapterPhoneContacts(Context applicationContext, ArrayList<BloodyFriendVO> contactslist, boolean select_all) {
        this.contactslist = contactslist;
        this.applicationContext = applicationContext;
        this.select_all = select_all;

        dh = DatabaseHandler.dbInit(applicationContext);
        spnfs = applicationContext.getSharedPreferences("blood_request_details", 4);
        spedit = spnfs.edit();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_phone_contacts, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {

        final BloodyFriendVO bloodyFriendVO = contactslist.get(position);

        holder.tv_contact_number.setText(bloodyFriendVO.getContact());
        holder.tv_phone_name.setText(bloodyFriendVO.getContactName());
        holder.tv_server_name.setText(bloodyFriendVO.getServerContactName());
        holder.tv_blood_group.setText(bloodyFriendVO.getBloodGroup());

        Log.e("select_all", "" + select_all);
        spedit.putBoolean("pos" + position, select_all).commit();
        holder.cb_invite.setChecked(spnfs.getBoolean("pos" + position, false));

        if (select_all == true) {
            BloodyFriendsActivity.selcontactslist.add(bloodyFriendVO);
        } else {
            BloodyFriendsActivity.selcontactslist.remove(bloodyFriendVO);
        }

        holder.cb_invite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spedit.putBoolean("pos" + position, true).commit();
                    // dh.insertRequestContact(bloodyFriendVO.getContact());
                    BloodyFriendsActivity.selcontactslist.add(bloodyFriendVO);
                    Log.e("true", "" + position);
                } else {
                    spedit.putBoolean("pos" + position, false).commit();
                    // dh.deleteRequestContactByNumber(bloodyFriendVO.getContact());
                    BloodyFriendsActivity.selcontactslist.remove(bloodyFriendVO);
                    Log.e("false", "" + position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return contactslist.size();
    }


}
