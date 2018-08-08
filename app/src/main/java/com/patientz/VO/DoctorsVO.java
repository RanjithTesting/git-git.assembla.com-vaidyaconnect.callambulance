package com.patientz.VO;

import java.util.Date;

public class DoctorsVO {
	long patientId;
	long doctorProfileId; // Profile ID of doctor
	String displayName;
	String mPhoneNo;
	String hPhoneNo;
	String wPhoneNo;
	String speciality;
	String cityOfPractice;
	String status;// can be values of accepted/rejected/pending
      String emailId;
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	long createdBy;
	long updatedBy;
	Date createdDate;
	Date updatedDate;

	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long userProfileId) {
		this.patientId = userProfileId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getmPhoneNo() {
		return mPhoneNo;
	}

	public void setmPhoneNo(String mPhoneNo) {
		this.mPhoneNo = mPhoneNo;
	}

	public String gethPhoneNo() {
		return hPhoneNo;
	}

	public void sethPhoneNo(String hPhoneNo) {
		this.hPhoneNo = hPhoneNo;
	}

	public String getwPhoneNo() {
		return wPhoneNo;
	}

	public void setwPhoneNo(String wPhoneNo) {
		this.wPhoneNo = wPhoneNo;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getCityOfPractice() {
		return cityOfPractice;
	}

	public void setCityOfPractice(String cityOfPractice) {
		this.cityOfPractice = cityOfPractice;
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

	public long getDoctorProfileId() {
		return doctorProfileId;
	}

	public void setDoctorProfileId(long doctorProfileId) {
		this.doctorProfileId = doctorProfileId;
	}
}
