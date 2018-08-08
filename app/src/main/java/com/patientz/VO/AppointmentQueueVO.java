package com.patientz.VO;

import java.util.Date;

public class AppointmentQueueVO {

    long appointmentId;
    String firstName;
    String lastName;
    int gender;
    int sequenceNumber;
    int status;
    String phoneNumber;
    String phoneNumberISDCode;
    String emailId;
    Date dateOfBirth;
    boolean isEstimated;
    Date timeOfAppointment;
    String remarks;
    int appointmentType; //Consultation/Review
    long appointmentWithId;
    String appointmentWithFirstName;
    String appointmentWithLastName;
    String appointmentWithDisplayName;
    long patientId;
    Date dateCreated;
    Date lastUpdated;
//    String referredBy;
    OrgLocationVO location;
    String symptoms;
    long ownerPatientId;
    long ownerProfileId;
    String orgRegistrationId;
    boolean patientOrgReqApproved;
    long appointmentWithRoleId;
    String appointmentWithSpeciality;
    String orgName;
    String orgPhoneNumber;
    long appointmentsCount;
    String preferredSession;

    String medicineList;
    long orgId;
    long locationId;
    String referredByDoctor;
    boolean homeDelivery;
    Date dateOfRequest;
    long requestedBy;
    Date createdDate;
    Date updatedDate;
    AddressVO address;
    FileVO prescriptionVO;
    Date appointmentDate;
    String testName;
    boolean homePickup;
    String token;
    String receiptNo;
    String barcodeNo;
    int requestType;

    public int getRequestType() {
        return requestType;
    }
    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }
    public long getAppointmentsCount() {
        return appointmentsCount;
    }
    public void setAppointmentsCount(long appointmentsCount) {
        this.appointmentsCount = appointmentsCount;
    }
    public String getAppointmentWithSpeciality() {
        return appointmentWithSpeciality;
    }
    public void setAppointmentWithSpeciality(String appointmentWithSpeciality) {
        this.appointmentWithSpeciality = appointmentWithSpeciality;
    }
    public String getOrgName() {
        return orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getOrgPhoneNumber() {
        return orgPhoneNumber;
    }
    public void setOrgPhoneNumber(String orgPhoneNumber) {
        this.orgPhoneNumber = orgPhoneNumber;
    }
    public long getAppointmentWithRoleId() {
        return appointmentWithRoleId;
    }
    public void setAppointmentWithRoleId(long appointmentWithRoleId) {
        this.appointmentWithRoleId = appointmentWithRoleId;
    }
    public String getOrgRegistrationId() {
        return orgRegistrationId;
    }
    public void setOrgRegistrationId(String orgRegistrationId) {
        this.orgRegistrationId = orgRegistrationId;
    }
    public OrgLocationVO getLocation() {
        return location;
    }
    public void setLocation(OrgLocationVO location) {
        this.location = location;
    }
    public String getSymptoms() {
        return symptoms;
    }
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    public long getAppointmentWithId() {
        return appointmentWithId;
    }
    public void setAppointmentWithId(long appointmentWithId) {
        this.appointmentWithId = appointmentWithId;
    }
    public long getPatientId() {
        return patientId;
    }
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }
    public long getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
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
    public int getSequenceNumber() {
        return sequenceNumber;
    }
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
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
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public boolean isEstimated() {
        return isEstimated;
    }
    public void setEstimated(boolean isEstimated) {
        this.isEstimated = isEstimated;
    }
    public Date getTimeOfAppointment() {
        return timeOfAppointment;
    }
    public void setTimeOfAppointment(Date timeOfAppointment) {
        this.timeOfAppointment = timeOfAppointment;
    }
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public int getAppointmentType() {
        return appointmentType;
    }
    public void setAppointmentType(int appointmentType) {
        this.appointmentType = appointmentType;
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
    public String getAppointmentWithFirstName() {
        return appointmentWithFirstName;
    }
    public void setAppointmentWithFirstName(String appointmentWithFirstName) {
        this.appointmentWithFirstName = appointmentWithFirstName;
    }
    public String getAppointmentWithLastName() {
        return appointmentWithLastName;
    }
    public void setAppointmentWithLastName(String appointmentWithLastName) {
        this.appointmentWithLastName = appointmentWithLastName;
    }
    public String getAppointmentWithDisplayName() {
        return appointmentWithDisplayName;
    }
    public void setAppointmentWithDisplayName(String appointmentWithDisplayName) {
        this.appointmentWithDisplayName = appointmentWithDisplayName;
    }
    public long getOwnerPatientId() {
        return ownerPatientId;
    }
    public void setOwnerPatientId(long ownerPatientId) {
        this.ownerPatientId = ownerPatientId;
    }
    public long getOwnerProfileId() {
        return ownerProfileId;
    }
    public void setOwnerProfileId(long ownerProfileId) {
        this.ownerProfileId = ownerProfileId;
    }
    public boolean isPatientOrgReqApproved() {
        return patientOrgReqApproved;
    }
    public void setPatientOrgReqApproved(boolean patientOrgReqApproved) {
        this.patientOrgReqApproved = patientOrgReqApproved;
    }
    public String getPreferredSession() {
        return preferredSession;
    }
    public void setPreferredSession(String preferredSession) {
        this.preferredSession = preferredSession;
    }
    public String getMedicineList() {
        return medicineList;
    }
    public void setMedicineList(String medicineList) {
        this.medicineList = medicineList;
    }
    public long getOrgId() {
        return orgId;
    }
    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }
    public long getLocationId() {
        return locationId;
    }
    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }
    public String getReferredByDoctor() {
        return referredByDoctor;
    }
    public void setReferredByDoctor(String referredByDoctor) {
        this.referredByDoctor = referredByDoctor;
    }
    public boolean isHomeDelivery() {
        return homeDelivery;
    }
    public void setHomeDelivery(boolean homeDelivery) {
        this.homeDelivery = homeDelivery;
    }
    public Date getDateOfRequest() {
        return dateOfRequest;
    }
    public void setDateOfRequest(Date dateOfRequest) {
        this.dateOfRequest = dateOfRequest;
    }
    public long getRequestedBy() {
        return requestedBy;
    }
    public void setRequestedBy(long requestedBy) {
        this.requestedBy = requestedBy;
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
    public AddressVO getAddress() {
        return address;
    }
    public void setAddress(AddressVO address) {
        this.address = address;
    }
    public FileVO getPrescriptionVO() {
        return prescriptionVO;
    }
    public void setPrescriptionVO(FileVO prescriptionVO) {
        this.prescriptionVO = prescriptionVO;
    }
    public Date getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    public String getTestName() {
        return testName;
    }
    public void setTestName(String testName) {
        this.testName = testName;
    }
    public boolean isHomePickup() {
        return homePickup;
    }
    public void setHomePickup(boolean homePickup) {
        this.homePickup = homePickup;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getReceiptNo() {
        return receiptNo;
    }
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }
    public String getBarcodeNo() {
        return barcodeNo;
    }
    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }



}