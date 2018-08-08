package com.patientz.VO;

import java.util.Date;

/**
 * Created by sunil on 23/12/17.
 */

public class InsuranceUpload
{
    long id;
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
    long    patientId;
    String  customerId;
    int     status;
    Date    sentDate;
    String  policyNo;
    Date    startDate;
    Date    endDate;
    UserUploadedMedia policyDoc;
    boolean isAadharVerified;
    boolean isEmailVerified;
    boolean isMobileNumberVerified;
    Date dateCreated;
    Date    lastUpdated;
    long    createdByProfileId;
    long    updatedByProfileId;
    String paytmRefId;

    public String getPaytmRefId() {
        return paytmRefId;
    }

    public void setPaytmRefId(String paytmRefId) {
        this.paytmRefId = paytmRefId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
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

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public long getCreatedByProfileId() {
        return createdByProfileId;
    }

    public void setCreatedByProfileId(long createdByProfileId) {
        this.createdByProfileId = createdByProfileId;
    }

    public long getUpdatedByProfileId() {
        return updatedByProfileId;
    }

    public void setUpdatedByProfileId(long updatedByProfileId) {
        this.updatedByProfileId = updatedByProfileId;
    }
}
