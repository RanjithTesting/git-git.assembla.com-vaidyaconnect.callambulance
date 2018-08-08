package com.patientz.VO;

public class OrgLocationVO {


	long orgId;
	String orgName;
	String orgType;
	long locationId;
	String locationName;
	String branchName;
	AddressVO locationAddress;
	String telephone;
	long orgLogoId;
	String[] emergencyPhones;

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String[] getEmergencyPhones() {
		return emergencyPhones;
	}

	public void setEmergencyPhones(String[] emergencyPhones) {
		this.emergencyPhones = emergencyPhones;
	}

	public long getOrgLogoId() {
		return orgLogoId;
	}

	public void setOrgLogoId(long orgLogoId) {
		this.orgLogoId = orgLogoId;
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

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public AddressVO getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(AddressVO locationAddress) {
		this.locationAddress = locationAddress;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

}