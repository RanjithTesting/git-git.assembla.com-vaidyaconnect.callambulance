package com.patientz.VO;

import java.util.Date;

public class InsuranceVO {
	long patientId; // user profile Id for the person
	String insPolicyNo;
	String insPolicyName;
	String insPolicyCoverage;
	String insPolicyCompany;
	String claimPhoneNumber;
	Date insPolicyStartDate;
	Date insPolicyEndDate;
	long createdBy;
	long updatedBy;
	Date createdDate;
	Date updatedDate;
	long insuranceId;

	String  firstName;
	String  lastName;
	int     gender;
	Date    dateOfBirth;
	String  aadharNo;
	String  email;
	String  mobileNumber;
	String  address1;
	String  address2;
	String  city;
	String  district;
	String  pinCode;
	String  nomineeName;
	String  nomineeRelationShip;
	String  customerId;
	int     status;
	Date    sentDate;
	UserUploadedMedia policyDoc;
	boolean isAadharVerified;
	boolean isEmailVerified;
	boolean isMobileNumberVerified;
	boolean isNotExisitingPolicy;
	long insuranceUploadId;
	String paytmRefId;

	public String getPaytmRefId() {
		return paytmRefId;
	}

	public void setPaytmRefId(String paytmRefId) {
		this.paytmRefId = paytmRefId;
	}
	public long getInsuranceUploadId() {
		return insuranceUploadId;
	}

	public void setInsuranceUploadId(long insuranceUploadId) {
		this.insuranceUploadId = insuranceUploadId;
	}

	public boolean isNotExisitingPolicy() {
		return isNotExisitingPolicy;
	}

	public void setNotExisitingPolicy(boolean notExisitingPolicy) {
		isNotExisitingPolicy = notExisitingPolicy;
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

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getNomineeName() {
		return nomineeName;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public String getNomineeRelationShip() {
		return nomineeRelationShip;
	}

	public void setNomineeRelationShip(String nomineeRelationShip) {
		this.nomineeRelationShip = nomineeRelationShip;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public UserUploadedMedia getPolicyDoc() {
		return policyDoc;
	}

	public void setPolicyDoc(UserUploadedMedia policyDoc) {
		this.policyDoc = policyDoc;
	}

	public boolean isAadharVerified() {
		return isAadharVerified;
	}

	public void setAadharVerified(boolean aadharVerified) {
		isAadharVerified = aadharVerified;
	}

	public boolean isEmailVerified() {
		return isEmailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		isEmailVerified = emailVerified;
	}

	public boolean isMobileNumberVerified() {
		return isMobileNumberVerified;
	}

	public void setMobileNumberVerified(boolean mobileNumberVerified) {
		isMobileNumberVerified = mobileNumberVerified;
	}

	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	public String getInsPolicyNo() {
		return insPolicyNo;
	}

	public void setInsPolicyNo(String insPolicyNo) {
		this.insPolicyNo = insPolicyNo;
	}

	public String getInsPolicyName() {
		return insPolicyName;
	}

	public void setInsPolicyName(String insPolicyName) {
		this.insPolicyName = insPolicyName;
	}

	public String getInsPolicyCoverage() {
		return insPolicyCoverage;
	}

	public void setInsPolicyCoverage(String insPolicyCoverage) {
		this.insPolicyCoverage = insPolicyCoverage;
	}

	public String getInsPolicyCompany() {
		return insPolicyCompany;
	}

	public void setInsPolicyCompany(String insPolicyCompany) {
		this.insPolicyCompany = insPolicyCompany;
	}

	public Date getInsPolicyStartDate() {
		return insPolicyStartDate;
	}

	public void setInsPolicyStartDate(Date insPolicyStartDate) {
		this.insPolicyStartDate = insPolicyStartDate;
	}

	public Date getInsPolicyEndDate() {
		return insPolicyEndDate;
	}

	public void setInsPolicyEndDate(Date insPolicyEndDate) {
		this.insPolicyEndDate = insPolicyEndDate;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public long getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(long insuranceId) {
		this.insuranceId = insuranceId;
	}

	public String getClaimPhoneNumber() {
		return claimPhoneNumber;
	}

	public void setClaimPhoneNumber(String claimPhoneNumber) {
		this.claimPhoneNumber = claimPhoneNumber;
	}
}