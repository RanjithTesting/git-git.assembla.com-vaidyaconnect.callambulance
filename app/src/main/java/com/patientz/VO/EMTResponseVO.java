package com.patientz.VO;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sunil on 09/06/16.
 */
public class EMTResponseVO {
    long code;
    String response;
    long recordId;
    long patientId;
    UserVO user;
    UserProfileVO profile;
    Date version;
    EmergencyInfoVO emergencyInfo;
    ArrayList<OrganisationVO> orgList;
    String defaultRegId;
    ArrayList<HospitalVO> hospitalsList;
    String userDisplayName;
    long roleId;
    int currentStatus;
    HospitalVO preferredHospital;
    ArrayList <HospitalVO> networkHospitalVOList;
    ArrayList <HospitalVO> nonNetworkHospitalVOList;
    int tripCount;
    double distanceTravelledInKms;
    String tripDuration;

    public EmergencyInfoVO getEmergencyInfo() {
        return emergencyInfo;
    }

    public void setEmergencyInfo(EmergencyInfoVO emergencyInfo) {
        this.emergencyInfo = emergencyInfo;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public UserProfileVO getProfile() {
        return profile;
    }

    public void setProfile(UserProfileVO profile) {
        this.profile = profile;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public ArrayList<OrganisationVO> getOrgList() {
        return orgList;
    }

    public void setOrgList(ArrayList<OrganisationVO> orgList) {
        this.orgList = orgList;
    }

    public String getDefaultRegId() {
        return defaultRegId;
    }

    public void setDefaultRegId(String defaultRegId) {
        this.defaultRegId = defaultRegId;
    }

    public ArrayList<HospitalVO> getHospitalsList() {
        return hospitalsList;
    }

    public void setHospitalsList(ArrayList<HospitalVO> hospitalsList) {
        this.hospitalsList = hospitalsList;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public HospitalVO getPreferredHospital() {
        return preferredHospital;
    }

    public void setPreferredHospital(HospitalVO preferredHospital) {
        this.preferredHospital = preferredHospital;
    }

    public int getTripCount() {
        return tripCount;
    }

    public void setTripCount(int tripCount) {
        this.tripCount = tripCount;
    }

    public double getDistanceTravelledInKms() {
        return distanceTravelledInKms;
    }

    public void setDistanceTravelledInKms(double distanceTravelledInKms) {
        this.distanceTravelledInKms = distanceTravelledInKms;
    }

    public String getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(String tripDuration) {
        this.tripDuration = tripDuration;
    }

    public ArrayList<HospitalVO> getNetworkHospitalVOList() {
        return networkHospitalVOList;
    }

    public void setNetworkHospitalVOList(ArrayList<HospitalVO> networkHospitalVOList) {
        this.networkHospitalVOList = networkHospitalVOList;
    }

    public ArrayList<HospitalVO> getNonNetworkHospitalVOList() {
        return nonNetworkHospitalVOList;
    }

    public void setNonNetworkHospitalVOList(ArrayList<HospitalVO> nonNetworkHospitalVOList) {
        this.nonNetworkHospitalVOList = nonNetworkHospitalVOList;
    }
}
