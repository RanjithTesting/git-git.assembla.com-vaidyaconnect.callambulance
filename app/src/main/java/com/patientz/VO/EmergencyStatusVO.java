package com.patientz.VO;

import android.location.Location;

public class EmergencyStatusVO {

	long emergencyId;
	long patientId;
	Location launchLocation;
	Location currentLocation;
	int webserviceStatus;
	int invokeFromDeviceStatus;
	int smsToServerStatus;
	int smsToContactsStatus;
	int locationUpdateStatus;
	int callStatus;
	boolean hasGotEmergencyLocation;
	boolean hasEmriNotified;
	boolean hasCalledAmbulance;
	boolean hasNotifiedContacts;
	String emergencyStatus;

	public String getEmergencyStatus() {
		return emergencyStatus;
	}

	public void setEmergencyStatus(String emergencyStatus) {
		this.emergencyStatus = emergencyStatus;
	}

	public boolean isHasCalledAmbulance() {
		return hasCalledAmbulance;
	}

	public void setHasCalledAmbulance(boolean hasCalledAmbulance) {
		this.hasCalledAmbulance = hasCalledAmbulance;
	}

	public boolean isHasNotifiedContacts() {
		return hasNotifiedContacts;
	}

	public void setHasNotifiedContacts(boolean hasNotifiedContacts) {
		this.hasNotifiedContacts = hasNotifiedContacts;
	}


	public boolean isHasEmriNotified() {
		return hasEmriNotified;
	}

	public void setHasEmriNotified(boolean hasEmriNotified) {
		this.hasEmriNotified = hasEmriNotified;
	}

	public boolean isHasGotEmergencyLocation() {
		return hasGotEmergencyLocation;
	}

	public void setHasGotEmergencyLocation(boolean hasGotEmergencyLocation) {
		this.hasGotEmergencyLocation = hasGotEmergencyLocation;
	}

	public long getEmergencyId() {
		return emergencyId;
	}

	public void setEmergencyId(long emergencyId) {
		this.emergencyId = emergencyId;
	}

	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	public Location getLaunchLocation() {
		return launchLocation;
	}

	public void setLaunchLocation(Location launchLocation) {
		this.launchLocation = launchLocation;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(int callStatus) {
		this.callStatus = callStatus;
	}

	public int getWebserviceStatus() {
		return webserviceStatus;
	}

	public void setWebserviceStatus(int webserviceStatus) {
		this.webserviceStatus = webserviceStatus;
	}

	public int getInvokeFromDeviceStatus() {
		return invokeFromDeviceStatus;
	}

	public void setInvokeFromDeviceStatus(int invokeFromDeviceStatus) {
		this.invokeFromDeviceStatus = invokeFromDeviceStatus;
	}

	public int getSmsToServerStatus() {
		return smsToServerStatus;
	}

	public void setSmsToServerStatus(int smsToServerStatus) {
		this.smsToServerStatus = smsToServerStatus;
	}

	public int getSmsToContactsStatus() {
		return smsToContactsStatus;
	}

	public void setSmsToContactsStatus(int smsToContactsStatus) {
		this.smsToContactsStatus = smsToContactsStatus;
	}

	public int getLocationUpdateStatus() {
		return locationUpdateStatus;
	}

	public void setLocationUpdateStatus(int locationUpdateStatus) {
		this.locationUpdateStatus = locationUpdateStatus;
	}

}
