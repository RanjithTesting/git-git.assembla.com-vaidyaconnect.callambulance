package com.patientz.VO;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Doctrz on 10/21/2016.
 */

public class OrgBranchVO {
    long orgId;
    String orgName;
    String orgType;
    long orgBranchId;
    String branchName;
    AddressVO address;
    String telephone;
    long orgLogoId;
    String[] emergencyPhones; //EMergencyOrgSettiing related fields
    String displayName; //Branch Name AutoPopulate from orgName and Branch Name
    String email;
    String emergencyPhoneNumber;		//Emergency Number to Contact Hospital
    String googleTalkId;	//We are storing Comma separated Multiple google talk Ids in this field
    String description;
    boolean networkBranch;

    boolean active;
    double distanceInKms;
    int durationInSecs;
    String distance;
    String duration;

    Date dateCreated;
    Date lastUpdated;
    ArrayList<OrgBranchSpecialityVO> specialities;
    ArrayList<OrgBranchFacilityVO> facilities;

    public ArrayList<OrgBranchSpecialityVO> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(ArrayList<OrgBranchSpecialityVO> specialities) {
        this.specialities = specialities;
    }

    public ArrayList<OrgBranchFacilityVO> getFacilities() {
        return facilities;
    }

    public void setFacilities(ArrayList<OrgBranchFacilityVO> facilities) {
        this.facilities = facilities;
    }

    public boolean isNetworkBranch() {
        return networkBranch;
    }

    public void setNetworkBranch(boolean networkBranch) {
        this.networkBranch = networkBranch;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AddressVO getAddress() {
        return address;
    }

    public void setAddress(AddressVO address) {
        this.address = address;
    }

    public double getDistanceInKms() {
        return distanceInKms;
    }

    public void setDistanceInKms(double distanceInKms) {
        this.distanceInKms = distanceInKms;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getDurationInSecs() {
        return durationInSecs;
    }

    public void setDurationInSecs(int durationInSecs) {
        this.durationInSecs = durationInSecs;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public long getOrgBranchId() {
        return orgBranchId;
    }

    public void setOrgBranchId(long orgBranchId) {
        this.orgBranchId = orgBranchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }



    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public long getOrgLogoId() {
        return orgLogoId;
    }

    public void setOrgLogoId(long orgLogoId) {
        this.orgLogoId = orgLogoId;
    }

    public String[] getEmergencyPhones() {
        return emergencyPhones;
    }

    public void setEmergencyPhones(String[] emergencyPhones) {
        this.emergencyPhones = emergencyPhones;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmergencyPhoneNumber() {
        return emergencyPhoneNumber;
    }

    public void setEmergencyPhoneNumber(String emergencyPhoneNumber) {
        this.emergencyPhoneNumber = emergencyPhoneNumber;
    }

    public String getGoogleTalkId() {
        return googleTalkId;
    }

    public void setGoogleTalkId(String googleTalkId) {
        this.googleTalkId = googleTalkId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return this.displayName;
    }
}
