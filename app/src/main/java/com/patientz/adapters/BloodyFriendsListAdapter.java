package com.patientz.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.VO.BloodyFriendVO;
import com.patientz.activity.R;

import java.util.ArrayList;


public class BloodyFriendsListAdapter extends RecyclerView.Adapter<BloodyFriendsListAdapter.DataObjectHolder> {
    private static String TAG = "BloodyFriendsListAdapter";
    ArrayList<BloodyFriendVO> contactslist;
    private Context applicationContext;

    boolean select_all;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        private ImageView ivBfPic;
        private TextView tvBfName,tvPno;
        public DataObjectHolder(View itemView) {
            super(itemView);
            ivBfPic = (ImageView)itemView.findViewById( R.id.iv_bf_pic );
            tvBfName = (TextView)itemView.findViewById( R.id.tv_bf_name );
            tvPno = (TextView)itemView.findViewById( R.id.tvPno );
        }
    }

    public BloodyFriendsListAdapter(Context applicationContext, ArrayList<BloodyFriendVO> contactslist) {
        this.contactslist = contactslist;
        this.applicationContext = applicationContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bloody_friends_list_adapter_layout, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {

        final BloodyFriendVO bloodyFriendVO = contactslist.get(position);
        if(getPhotoUri(bloodyFriendVO.getContactId())!=null)
        {
            holder.ivBfPic.setImageURI(getPhotoUri(bloodyFriendVO.getContactId()));
        }
        holder.tvBfName.setText(bloodyFriendVO.getContact());
        holder.tvPno.setText(bloodyFriendVO.getContactName());
    }
    public Uri getPhotoUri(int contactId) {
        try {
            Cursor cur = this.applicationContext.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(String.valueOf(contactId)));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    @Override
    public int getItemCount() {
        return contactslist.size();
    }


}
