package com.patientz.VO;

import java.io.Serializable;

/**
 * Created by sunil on 13/09/16.
 */
public class EmergencyInfoVO implements Serializable {

    String driverName;
    String driverPhoneNo;
    int driverStatus;
    String ambulanceNo;
    String driverCurrentLong;
    String driverCurrentLat;
    int ambulanceStatus;
    float minFare; //Min fare for 5km by default
    float priceRate; //Price per Km
    float rideTimeRate;
    String category;
    String tokenizedUrl;
    String  ambulanceProviderPhoneNumber;
    String ambulanceType;
    String ambulanceProviderBranchName;
    Organization ambulanceProvider;
    UserProfile ambulanceManagerProfile;

    public String getDriverCurrentLong() {
        return driverCurrentLong;
    }

    public void setDriverCurrentLong(String driverCurrentLong) {
        this.driverCurrentLong = driverCurrentLong;
    }

    public String getDriverCurrentLat() {
        return driverCurrentLat;
    }

    public void setDriverCurrentLat(String driverCurrentLat) {
        this.driverCurrentLat = driverCurrentLat;
    }

    public UserProfile getAmbulanceManagerProfile() {
        return ambulanceManagerProfile;
    }

    public void setAmbulanceManagerProfile(UserProfile ambulanceManagerProfile) {
        this.ambulanceManagerProfile = ambulanceManagerProfile;
    }

    public Organization getAmbulanceProvider() {
        return ambulanceProvider;
    }

    public void setAmbulanceProvider(Organization ambulanceProvider) {
        this.ambulanceProvider = ambulanceProvider;
    }

    public String getAmbulanceProviderBranchName() {
        return ambulanceProviderBranchName;
    }

    public void setAmbulanceProviderBranchName(String ambulanceProviderBranchName) {
        this.ambulanceProviderBranchName = ambulanceProviderBranchName;
    }

    public String getAmbulanceType() {
        return ambulanceType;
    }

    public void setAmbulanceType(String ambulanceType) {
        this.ambulanceType = ambulanceType;
    }

    public String getAmbulanceProviderPhoneNumber() {
        return ambulanceProviderPhoneNumber;
    }

    public void setAmbulanceProviderPhoneNumber(String ambulanceProviderPhoneNumber) {
        this.ambulanceProviderPhoneNumber = ambulanceProviderPhoneNumber;
    }

    public String getTokenizedUrl() {
        return tokenizedUrl;
    }

    public void setTokenizedUrl(String tokenizedUrl) {
        this.tokenizedUrl = tokenizedUrl;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoneNo() {
        return driverPhoneNo;
    }

    public void setDriverPhoneNo(String driverPhoneNo) {
        this.driverPhoneNo = driverPhoneNo;
    }

    public int getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(int driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getAmbulanceNo() {
        return ambulanceNo;
    }

    public void setAmbulanceNo(String ambulanceNo) {
        this.ambulanceNo = ambulanceNo;
    }

    public int getAmbulanceStatus() {
        return ambulanceStatus;
    }

    public void setAmbulanceStatus(int ambulanceStatus) {
        this.ambulanceStatus = ambulanceStatus;
    }

    public float getMinFare() {
        return minFare;
    }

    public void setMinFare(float minFare) {
        this.minFare = minFare;
    }

    public float getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(float priceRate) {
        this.priceRate = priceRate;
    }

    public float getRideTimeRate() {
        return rideTimeRate;
    }

    public void setRideTimeRate(float rideTimeRate) {
        this.rideTimeRate = rideTimeRate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "EmergencyInfoVO{" +
                "driverName=" + driverName +
                ", driverPhoneNo='" + driverPhoneNo + '\'' +
                ", driverStatus=" + driverStatus +
                ", ambulanceNo=" + ambulanceNo +
                ", ambulanceStatus=" + ambulanceStatus +
                ", minFare=" + minFare +
                ", priceRate=" + priceRate +
                ", rideTimeRate=" + rideTimeRate +
                ", category='" + category + '\'' +
                ", tokenizedUrl=" + tokenizedUrl +
                ", ambulanceProviderPhoneNumber=" + ambulanceProviderPhoneNumber +
                ", ambulanceType=" + ambulanceType +
                ", ambulanceProviderBranchName=" + ambulanceProviderBranchName +
                ", ambulanceProvider=" + ambulanceProvider +
                ", ambulanceManagerProfile=" + ambulanceManagerProfile +
                '}';
    }


}