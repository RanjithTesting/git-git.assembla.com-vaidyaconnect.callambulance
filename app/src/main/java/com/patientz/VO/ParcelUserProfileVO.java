package com.patientz.VO;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelUserProfileVO implements Parcelable {

    UserProfileVO usr;

    public ParcelUserProfileVO() {
        usr = new UserProfileVO();
    }

    /* everything below here is for implementing Parcelable */

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(usr.getTelWork());
        out.writeString(usr.getTelHome());
        out.writeString(usr.getTelMobile());
        out.writeString(usr.getDisplayName());
        out.writeString(usr.getHandle());
        out.writeList(usr.getSpecialities());
        out.writeString(usr.getProfileUrl());
    }

    public static final Creator<ParcelUserProfileVO> CREATOR = new Creator<ParcelUserProfileVO>() {
        public ParcelUserProfileVO createFromParcel(Parcel in) {
            return new ParcelUserProfileVO(in);
        }

        public ParcelUserProfileVO[] newArray(int size) {
            return new ParcelUserProfileVO[size];
        }
    };

    private ParcelUserProfileVO(Parcel in) {
        usr.setTelWork(in.readString());
        usr.setTelHome(in.readString());
        usr.setTelMobile(in.readString());
        usr.setDisplayName(in.readString());
        usr.setHandle(in.readString());
//    	usr.setSpecialities((ArrayList<UserSpecialityVO>)in.readList(, loader));
        usr.setSpecialities(in.readArrayList(UserSpecialityVO.class.getClassLoader()));
        usr.setProfileUrl(in.readString());
        //usr = in.;
    }

    public ParcelUserProfileVO(UserProfileVO usrpro) {
        this.usr = usrpro;
    }

    public UserProfileVO returnProfile() {
        return usr;
    }
}