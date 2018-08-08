package com.patientz.VO;

import java.util.Date;

/**
 * Created by sunil on 15/09/16.
 */
public class HospitalWithAmbulanceProviderVO {

    long hospitalId;
    String hospitalName;

    long orgId;
    long orgName;
    String branchName; //Location Branch Name
    String locationId; //Location ID
    String branchPhoneNo;
    AddressVO hospitalAddressVO;

    String ambulanceProviderPhoneNo;
    long ambulanceProviderId; //Org Id of the Ambulance provider
    AddressVO ambulanceProviderAddressVO;
    Date dateCreated;
    Date lastUpdated;

    public long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public long getOrgName() {
        return orgName;
    }

    public void setOrgName(long orgName) {
        this.orgName = orgName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getBranchPhoneNo() {
        return branchPhoneNo;
    }

    public void setBranchPhoneNo(String branchPhoneNo) {
        this.branchPhoneNo = branchPhoneNo;
    }

    public AddressVO getHospitalAddressVO() {
        return hospitalAddressVO;
    }

    public void setHospitalAddressVO(AddressVO hospitalAddressVO) {
        this.hospitalAddressVO = hospitalAddressVO;
    }

    public String getAmbulanceProviderPhoneNo() {
        return ambulanceProviderPhoneNo;
    }

    public void setAmbulanceProviderPhoneNo(String ambulanceProviderPhoneNo) {
        this.ambulanceProviderPhoneNo = ambulanceProviderPhoneNo;
    }

    public long getAmbulanceProviderId() {
        return ambulanceProviderId;
    }

    public void setAmbulanceProviderId(long ambulanceProviderId) {
        this.ambulanceProviderId = ambulanceProviderId;
    }

    public AddressVO getAmbulanceProviderAddressVO() {
        return ambulanceProviderAddressVO;
    }

    public void setAmbulanceProviderAddressVO(AddressVO ambulanceProviderAddressVO) {
        this.ambulanceProviderAddressVO = ambulanceProviderAddressVO;
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
}
