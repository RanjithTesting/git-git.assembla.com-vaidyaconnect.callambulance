package com.patientz.VO;

public class AmbulanceProviderVO {

	long emergencyOrgSettingId;
	OrgLocationVO location;
	String phoneNumber;
	String phoneNumberISDCode;
	String emailId;

	String googleTalkId;
	String organizationName;
	long orgId;
	long orgLogoId;
	String locationName;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getOrgLogoId() {
		return orgLogoId;
	}

	public void setOrgLogoId(long orgLogoId) {
		this.orgLogoId = orgLogoId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


	public long getEmergencyOrgSettingId() {
		return emergencyOrgSettingId;
	}
	public void setEmergencyOrgSettingId(long emergencyOrgSettingId) {
		this.emergencyOrgSettingId = emergencyOrgSettingId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public OrgLocationVO getLocation() {
		return location;
	}

	public void setLocation(OrgLocationVO location) {
		this.location = location;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPhoneNumberISDCode() {
		return phoneNumberISDCode;
	}
	public void setPhoneNumberISDCode(String phoneNumberISDCode) {
		this.phoneNumberISDCode = phoneNumberISDCode;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getGoogleTalkId() {
		return googleTalkId;
	}
	public void setGoogleTalkId(String googleTalkId) {
		this.googleTalkId = googleTalkId;
	}


}
