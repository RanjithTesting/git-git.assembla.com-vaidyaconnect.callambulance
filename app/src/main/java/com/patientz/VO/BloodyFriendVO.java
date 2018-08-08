package com.patientz.VO;

import java.io.Serializable;

/**
 * Created by windows.7 on 8/4/2016.
 */
public class BloodyFriendVO implements Serializable{//implements Parcelable {
    int _id;
    String contact;
    String lookUpId;
    int contactId;
    int contactType;
    String contactName;
    String bloodGroup;
    int update_status;
    String serverContactName;
    boolean userInvited;

    public boolean isUserInvited() {
        return userInvited;
    }

    public void setUserInvited(boolean userInvited) {
        this.userInvited = userInvited;
    }

    public String getServerContactName() {
        return serverContactName;
    }

    public void setServerContactName(String serverContactName) {
        this.serverContactName = serverContactName;
    }

    public int getUpdate_status() {
        return update_status;
    }

    public void setUpdate_status(int update_status) {
        this.update_status = update_status;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLookUpId() {
        return lookUpId;
    }

    public void setLookUpId(String lookUpId) {
        this.lookUpId = lookUpId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

   /* @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this._id);
        parcel.writeString(this.contact);
        parcel.writeString(this.lookUpId);
        parcel.writeInt(this.contactId);
        parcel.writeInt(this.contactType);
        parcel.writeString(this.contactName);
        parcel.writeString(this.bloodGroup);
        parcel.writeInt(this.update_status);
        parcel.writeString(this.serverContactName);
        parcel.writeInt(userInvited ? 1 : 0);
    }*/
}
