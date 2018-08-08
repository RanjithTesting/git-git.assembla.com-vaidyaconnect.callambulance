package com.patientz.VO;

import java.util.Date;

public class UserInfoVO {

    long patientId;
    long userProfileId;
    String firstName;
    String lastName;
    String relationship;
    String role;
    String address;
    String companyName;
    String city;
    String state;
    String country;
    String pinCode;
    String phoneNumber;
    String phoneNumberIsdCode;
    String altPhoneNumber;
    String altPhoneNumberIsdCode;
    int gender;
    int age;
    String emailId;
    String picFileId;
    String picFileName;
    int emergencyLevel; // 0 - No emergency , 1 - Minor emergency , 2 - Major
    // emergency
    String financialStatus;
    String familyHistory;
    String habits;
    String remarks;
    String bloodDonation;
    String organDonation;
    String maritalStatus;
    String foodHabits;
    Date lastBloodDonationDate;
    boolean notifyBloodDonationRequest;

    String bloodGroup;

    Date dateOfBirth;
    long createdBy;
    long updatedBy;
    Date createdDate;
    Date updatedDate;
    long preferredOrgBranchId;
    long preferredBloodBankId;

    String patientHandle;


    public Date getLastBloodDonationDate() {
        return lastBloodDonationDate;
    }

    public void setLastBloodDonationDate(Date lastBloodDonationDate) {
        this.lastBloodDonationDate = lastBloodDonationDate;
    }

    public long getPreferredBloodBankId() {
        return preferredBloodBankId;
    }

    public void setPreferredBloodBankId(long preferredBloodBankId) {
        this.preferredBloodBankId = preferredBloodBankId;
    }

    public boolean isNotifyBloodDonationRequest() {
        return notifyBloodDonationRequest;
    }

    public void setNotifyBloodDonationRequest(boolean notifyBloodDonationRequest) {
        this.notifyBloodDonationRequest = notifyBloodDonationRequest;
    }

    public String getPatientHandle() {
        return patientHandle;
    }

    public void setPatientHandle(String patientHandle) {
        this.patientHandle = patientHandle;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public long getPreferredOrgBranchId() {
        return preferredOrgBranchId;
    }

    public void setPreferredOrgBranchId(long preferredOrgBranchId) {
        this.preferredOrgBranchId = preferredOrgBranchId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
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

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberIsdCode() {
        return phoneNumberIsdCode;
    }

    public void setPhoneNumberIsdCode(String phoneNumberIsdCode) {
        this.phoneNumberIsdCode = phoneNumberIsdCode;
    }

    public String getAltPhoneNumber() {
        return altPhoneNumber;
    }

    public void setAltPhoneNumber(String altPhoneNumber) {
        this.altPhoneNumber = altPhoneNumber;
    }

    public String getAltPhoneNumberIsdCode() {
        return altPhoneNumberIsdCode;
    }

    public void setAltPhoneNumberIsdCode(String altPhoneNumberIsdCode) {
        this.altPhoneNumberIsdCode = altPhoneNumberIsdCode;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPicFileId() {
        return picFileId;
    }

    public void setPicFileId(String picFileId) {
        this.picFileId = picFileId;
    }

    public String getPicFileName() {
        return picFileName;
    }

    public void setPicFileName(String picFileName) {
        this.picFileName = picFileName;
    }

    public int getEmergencyLevel() {
        return emergencyLevel;
    }

    public void setEmergencyLevel(int emergencyLevel) {
        this.emergencyLevel = emergencyLevel;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getFinancialStatus() {
        return financialStatus;
    }

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public void setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
    }

    public String getHabits() {
        return habits;
    }

    public void setHabits(String habits) {
        this.habits = habits;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBloodDonation() {
        return bloodDonation;
    }

    public void setBloodDonation(String bloodDonation) {
        this.bloodDonation = bloodDonation;
    }

    public String getOrganDonation() {
        return organDonation;
    }

    public void setOrganDonation(String organDonation) {
        this.organDonation = organDonation;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getFoodHabits() {
        return foodHabits;
    }

    public void setFoodHabits(String foodHabits) {
        this.foodHabits = foodHabits;
    }

}
