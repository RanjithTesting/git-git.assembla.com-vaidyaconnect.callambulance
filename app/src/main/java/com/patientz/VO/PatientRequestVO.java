package com.patientz.VO;

import java.util.Date;

public class PatientRequestVO {

	String firstName;
	String lastName;
	String gender;
	Date dateOfBirth;
	String city;
	String inviteStatus; // ( accepted/pending/rejected)
	Long patientId;
	Long userProfileId;
	FileVO picFileVO;
	String role;
	Long patientAccessId;

	public Long getPatientAccessId() {
		return patientAccessId;
	}

	public void setPatientAccessId(Long patientAccessId) {
		this.patientAccessId = patientAccessId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getInviteStatus() {
		return inviteStatus;
	}

	public void setInviteStatus(String inviteStatus) {
		this.inviteStatus = inviteStatus;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public FileVO getPicFileVO() {
		return picFileVO;
	}

	public void setPicFileVO(FileVO picFileVO) {
		this.picFileVO = picFileVO;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

}