package com.patientz.VO;

public class PatientDoctorInviteVO {

    String firstName;
    String lastName;
    String emailId;
    String phoneNumber;
    String phoneNumberISDCode;

    public String getPhoneNumberISDCode() {
        return phoneNumberISDCode;
    }
    public void setPhoneNumberISDCode(String phoneNumberISDCode) {
        this.phoneNumberISDCode = phoneNumberISDCode;
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
    public String getEmailId() {
        return emailId;
    }
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}