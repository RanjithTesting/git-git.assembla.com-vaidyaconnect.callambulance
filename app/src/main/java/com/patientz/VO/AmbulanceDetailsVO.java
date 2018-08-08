package com.patientz.VO;

import java.io.Serializable;
import java.util.Date;

public class AmbulanceDetailsVO implements Serializable
{
	long ambulanceId;
	String registrationNo;
	String licensePlateNo;
	String category;
	int status;
	long orgId;
	// OrgLocationVO location;
	String orgName;
	String orgBranch;
	String makeAndModel;
	String yearOfManufacture;
	Date lastServiceDate;
	String serviceableOrgs;

	boolean driverAssigned;

	String longitude;
	String latitude;
	String driverName;
	String driverPhoneNo;
	String fullAddress;
	String emergencyToken;
	String distance;
	double distanceInKms;
	String duration;

	int type; //Inbound / Outbound / Both
	float minFare; //Min fare for 5km by default
	float priceRate; //Price per Km
	float rideTimeRate;
	String ambManagerPhoneNo;
	boolean oxygen;
	boolean wheelChair;
	boolean stretcher;
	boolean freezerBox;
	boolean ventilator;
	boolean airConditioner;
	boolean EMTAvailability;
	boolean AED;
	int seatingCapacityNumber;
	int ambulanceStatus;


	public int getAmbulanceStatus() {
		return ambulanceStatus;
	}

	public void setAmbulanceStatus(int ambulanceStatus) {
		this.ambulanceStatus = ambulanceStatus;
	}

	public double getDistanceInKms() {
		return distanceInKms;
	}

	public void setDistanceInKms(double distanceInKms) {
		this.distanceInKms = distanceInKms;
	}

	public boolean isEMTAvailability() {
		return EMTAvailability;
	}

	public void setEMTAvailability(boolean EMTAvailability) {
		this.EMTAvailability = EMTAvailability;
	}

	public boolean isAED() {
		return AED;
	}

	public void setAED(boolean AED) {
		this.AED = AED;
	}

	public int getSeatingCapacityNumber() {
		return seatingCapacityNumber;
	}

	public void setSeatingCapacityNumber(int seatingCapacityNumber) {
		this.seatingCapacityNumber = seatingCapacityNumber;
	}

	public boolean isOxygen() {
		return oxygen;
	}

	public void setOxygen(boolean oxygen) {
		this.oxygen = oxygen;
	}

	public boolean isWheelChair() {
		return wheelChair;
	}

	public void setWheelChair(boolean wheelChair) {
		this.wheelChair = wheelChair;
	}

	public boolean isStretcher() {
		return stretcher;
	}

	public void setStretcher(boolean stretcher) {
		this.stretcher = stretcher;
	}

	public boolean isFreezerBox() {
		return freezerBox;
	}

	public void setFreezerBox(boolean freezerBox) {
		this.freezerBox = freezerBox;
	}

	public boolean isVentilator() {
		return ventilator;
	}

	public void setVentilator(boolean ventilator) {
		this.ventilator = ventilator;
	}

	public boolean isAirConditioner() {
		return airConditioner;
	}

	public void setAirConditioner(boolean airConditioner) {
		this.airConditioner = airConditioner;
	}

	public String getAmbManagerPhoneNo() {
		return ambManagerPhoneNo;
	}

	public void setAmbManagerPhoneNo(String ambManagerPhoneNo) {
		this.ambManagerPhoneNo = ambManagerPhoneNo;
	}

	public boolean isDriverAssigned() {
		return driverAssigned;
	}

	public void setDriverAssigned(boolean driverAssigned) {
		this.driverAssigned = driverAssigned;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOrgBranch() {
		return orgBranch;
	}

	public void setOrgBranch(String orgBranch) {
		this.orgBranch = orgBranch;
	}

	public String getDriverPhoneNo() {
		return driverPhoneNo;
	}

	public void setDriverPhoneNo(String driverPhoneNo) {
		this.driverPhoneNo = driverPhoneNo;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getEmergencyToken() {
		return emergencyToken;
	}

	public void setEmergencyToken(String emergencyToken) {
		this.emergencyToken = emergencyToken;
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

	public long getAmbulanceId() {
		return ambulanceId;
	}

	public void setAmbulanceId(long ambulanceId) {
		this.ambulanceId = ambulanceId;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getLicensePlateNo() {
		return licensePlateNo;
	}

	public void setLicensePlateNo(String licensePlateNo) {
		this.licensePlateNo = licensePlateNo;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getMakeAndModel() {
		return makeAndModel;
	}

	public void setMakeAndModel(String makeAndModel) {
		this.makeAndModel = makeAndModel;
	}

	public String getYearOfManufacture() {
		return yearOfManufacture;
	}

	public void setYearOfManufacture(String yearOfManufacture) {
		this.yearOfManufacture = yearOfManufacture;
	}

	public Date getLastServiceDate() {
		return lastServiceDate;
	}

	public void setLastServiceDate(Date lastServiceDate) {
		this.lastServiceDate = lastServiceDate;
	}

	public String getServiceableOrgs() {
		return serviceableOrgs;
	}

	public void setServiceableOrgs(String serviceableOrgs) {
		this.serviceableOrgs = serviceableOrgs;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
}