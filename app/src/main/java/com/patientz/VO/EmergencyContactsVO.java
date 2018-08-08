package com.patientz.VO;

import java.util.ArrayList;
import java.util.Date;

public class EmergencyContactsVO {

    long ownerProfileId; // User profile ID of the person for whom this is an
    // emergency contacts
    long ownerPatientId;
    long contactId; // Contact ID of the person who is the emergency contact.
    // This is populated only if the emergency contact is not a
    // registered user
    long userProfileId; // User profile ID of the person who is the emergency
    // contact. This is populated if the emergency contact
    // is a registered user in the system
    String firstName;
    String lastName;
    String emailID;
    String phoneNumber;
    String phoneNumberIsd;
    String altPhoneNumber;
    String altPhoneNumberIsd;
    String address;
    String gender;
    String age;
    long patientAccessId;
    long picId;
    String city;
    String country;
    String state;
    String pinCode;
    String relationship; // Friend, Spouse, Son, Daughter, Father, Mother,
    // GrandFather, GrandMother
    String role; // Owner, Caregiver, Emergency Contact
    String recordType; // EC (emergency contact) or REC (registered emergency
    // contact)
    long createdBy;
    long updatedBy;
    Date createdDate;
    Date updatedDate;
    long profilePicId;
    String status;
    ArrayList<UserSpecialityVO> specialities;

    public long getProfilePicId() {
        return profilePicId;
    }

    public void setProfilePicId(long profilePicId) {
        this.profilePicId = profilePicId;
    }

    public ArrayList<UserSpecialityVO> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(ArrayList<UserSpecialityVO> specialities) {
        this.specialities = specialities;
    }

    public long getPatientAccessId() {
        return patientAccessId;
    }

    public void setPatientAccessId(long patientAccessId) {
        this.patientAccessId = patientAccessId;
    }

    public long getPicId() {
        return picId;
    }

    public void setPicId(long picId) {
        this.picId = picId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
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

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberIsd() {
        return phoneNumberIsd;
    }

    public void setPhoneNumberIsd(String phoneNumberIsd) {
        this.phoneNumberIsd = phoneNumberIsd;
    }

    public String getAltPhoneNumber() {
        return altPhoneNumber;
    }

    public void setAltPhoneNumber(String altPhoneNumber) {
        this.altPhoneNumber = altPhoneNumber;
    }

    public String getAltPhoneNumberIsd() {
        return altPhoneNumberIsd;
    }

    public void setAltPhoneNumberIsd(String altPhoneNumberIsd) {
        this.altPhoneNumberIsd = altPhoneNumberIsd;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
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

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
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

    public long getOwnerProfileId() {
        return ownerProfileId;
    }

    public void setOwnerProfileId(long ownerProfileId) {
        this.ownerProfileId = ownerProfileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getOwnerPatientId() {
        return ownerPatientId;
    }

    public void setOwnerPatientId(long ownerPatientId) {
        this.ownerPatientId = ownerPatientId;
    }
}
