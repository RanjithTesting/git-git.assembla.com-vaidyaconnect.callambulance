package com.patientz.VO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sunil on 18/1/17.
 */
public class UserProfile implements Serializable{
    long id;
    String displayName;
    String telMobile;
    UserUploadedMedia profilePicture;
    String firstName;
    String lastName;
    ArrayList<UserSpecialty> otherSpecialities;
    boolean isNotifyingDoctor; // Added at android side to filter notifying doctors list

    public boolean isNotifyingDoctor() {
        return isNotifyingDoctor;
    }

    public void setNotifyingDoctor(boolean notifyingDoctor) {
        isNotifyingDoctor = notifyingDoctor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<UserSpecialty> getOtherSpecialities() {
        return otherSpecialities;
    }

    public void setOtherSpecialities(ArrayList<UserSpecialty> otherSpecialities) {
        this.otherSpecialities = otherSpecialities;
    }

    public UserUploadedMedia getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(UserUploadedMedia profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTelMobile() {
        return telMobile;
    }

    public void setTelMobile(String telMobile) {
        this.telMobile = telMobile;
    }
}
