package com.patientz.VO;

import java.util.ArrayList;
import java.util.Date;

public class PatientUserVO {

    String firstName;
    String lastName;
    String status;
    String phoneNumber;
    String role;
    String fileName;
    String picName;
    String relationship;
    long picId;
    long userProfileId;
    long patientId;
    long patientAccessId;
    UserRecordVO recordVO;
    FileVO profilePicVO;
    long eventLogId;
    ArrayList<MedicalHistoryCollectionVO> diagnosisList;
    boolean underEmergency;
    String emergencyTokenUrl;
    Date emergencyInitiatedDate;
    String emergencyAPIKey;
    String emergencyToken;
    String currentSelectedPatient;
    String bloodGroup;
    String patientHandle;
    long sharePoints;

    public long getSharePoints() {
        return sharePoints;
    }

    public void setSharePoints(long sharePoints) {
        this.sharePoints = sharePoints;
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

    public String getEmergencyAPIKey() {
        return emergencyAPIKey;
    }

    public void setEmergencyAPIKey(String emergencyAPIKey) {
        this.emergencyAPIKey = emergencyAPIKey;
    }

    public String getCurrentSelectedPatient() {
        return currentSelectedPatient;
    }

    public void setCurrentSelectedPatient(String currentSelectedPatient) {
        this.currentSelectedPatient = currentSelectedPatient;
    }

    public String getPreferredAmbulancePhoneNo() {
        return preferredAmbulancePhoneNo;
    }

    public void setPreferredAmbulancePhoneNo(String preferredAmbulancePhoneNo) {
        this.preferredAmbulancePhoneNo = preferredAmbulancePhoneNo;
    }

    String preferredAmbulancePhoneNo;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public long getPicId() {
        return picId;
    }

    public void setPicId(long picId) {
        this.picId = picId;
    }

    public long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public UserRecordVO getRecordVO() {
        return recordVO;
    }

    public void setRecordVO(UserRecordVO recordVO) {
        this.recordVO = recordVO;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getPatientAccessId() {
        return patientAccessId;
    }

    public void setPatientAccessId(long patientAccessId) {
        this.patientAccessId = patientAccessId;
    }

    public FileVO getProfilePicVO() {
        return profilePicVO;
    }

    public void setProfilePicVO(FileVO profilePicVO) {
        this.profilePicVO = profilePicVO;
    }

    public long getEventLogId() {
        return eventLogId;
    }

    public void setEventLogId(long eventLogId) {
        this.eventLogId = eventLogId;
    }

    public ArrayList<MedicalHistoryCollectionVO> getDiagnosisList() {
        return diagnosisList;
    }

    public void setDiagnosisList(
            ArrayList<MedicalHistoryCollectionVO> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public boolean isUnderEmergency() {
        return underEmergency;
    }

    public void setUnderEmergency(boolean underEmergency) {
        this.underEmergency = underEmergency;
    }

    public String getEmergencyTokenUrl() {
        return emergencyTokenUrl;
    }

    public void setEmergencyTokenUrl(String emergencyTokenUrl) {
        this.emergencyTokenUrl = emergencyTokenUrl;
    }

    public Date getEmergencyInitiatedDate() {
        return emergencyInitiatedDate;
    }

    public void setEmergencyInitiatedDate(Date emergencyInitiatedDate) {
        this.emergencyInitiatedDate = emergencyInitiatedDate;
    }

    public String getEmergencyAPIKEY() {
        return emergencyAPIKey;
    }

    public void setEmergencyAPIKEY(String emergencyAPIKEY) {
        this.emergencyAPIKey = emergencyAPIKEY;
    }

    public String getEmergencyToken() {
        return emergencyToken;
    }

    public void setEmergencyToken(String emergencyToken) {
        this.emergencyToken = emergencyToken;
    }
}