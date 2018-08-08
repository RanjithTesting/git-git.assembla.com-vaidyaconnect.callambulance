package com.patientz.VO;

import java.util.Date;

public class PublicUserProfileVO {
	
	
	String firstName;
	String lastName;
	Date dateOfBirth;
	String sex;
	String maritalStatus;
	String currentAddressLine1;
	String currentAddressLine2;
	String currentAddressLine3;
	String permanentAddressLine1;
	String permanentAddressLine2;
	String permanentAddressLine3;
	String phoneNumber;
	String email;
	long profileId;
	String ipAddress;
	long countryId;
	long serverTS;
	long userId;
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getUserId() {
		return userId;
	}
	public long getCountryId() {
		return countryId;
	}
	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}
	public void setCurrentAddressLine1(String currentAddressLine1) {
		this.currentAddressLine1 = currentAddressLine1;
	}
	public String getCurrentAddressLine1() {
		return currentAddressLine1;
	}
	public void setCurrentAddressLine2(String currentAddressLine2) {
		this.currentAddressLine2 = currentAddressLine2;
	}
	public String getCurrentAddressLine2() {
		return currentAddressLine2;
	}
	public void setCurrentAddressLine3(String currentAddressLine3) {
		this.currentAddressLine3 = currentAddressLine3;
	}
	public String getCurrentAddressLine3() {
		return currentAddressLine3;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setPermanentAddressLine1(String permanentAddressLine1) {
		this.permanentAddressLine1 = permanentAddressLine1;
	}
	public String getPermanentAddressLine1() {
		return permanentAddressLine1;
	}
	public void setPermanentAddressLine2(String permanentAddressLine2) {
		this.permanentAddressLine2 = permanentAddressLine2;
	}
	public String getPermanentAddressLine2() {
		return permanentAddressLine2;
	}
	public void setPermanentAddressLine3(String permanentAddressLine3) {
		this.permanentAddressLine3 = permanentAddressLine3;
	}
	public String getPermanentAddressLine3() {
		return permanentAddressLine3;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getSex() {
		return sex;
	}
	public void setServerTS(long serverTS) {
		this.serverTS = serverTS;
	}
	public long getServerTS() {
		return serverTS;
	}
	

}
