package com.patientz.VO;

import java.util.ArrayList;

public class DoctorSearchVO {

    long userProfileId;
    String firstName;
    String lastName;
    String displayName;
    String cityofPractice;
    FileVO profilePicVO;
    boolean requested;
    String latitude;
    String longitude;
    String phoneNumber;
    int status;
    String qualificationsCsv;
    ArrayList<UserSpecialityVO> specalitiesList;
    String distance;
    String duration;

    public String getQualificationsCsv() {
        return qualificationsCsv;
    }

    public void setQualificationsCsv(String qualificationsCsv) {
        this.qualificationsCsv = qualificationsCsv;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<UserSpecialityVO> getSpecalitiesList() {
        return specalitiesList;
    }

    public void setSpecalitiesList(ArrayList<UserSpecialityVO> specalitiesList) {
        this.specalitiesList = specalitiesList;
    }


    public boolean isRequested() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    public long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
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

    public String getCityofPractice() {
        return cityofPractice;
    }

    public void setCityofPractice(String cityofPractice) {
        this.cityofPractice = cityofPractice;
    }

    public FileVO getProfilePicVO() {
        return profilePicVO;
    }

    public void setProfilePicVO(FileVO profilePicVO) {
        this.profilePicVO = profilePicVO;
    }


}
