package com.patientz.VO;

import java.util.Date;

public class ReferralVO {
	long referralId;

	long patientId;
	String patientFirstName;
	String patientLastName;
	Date patientDateOfBirth;
	long patientGender;
	String patientPhoneNumber;
	String patientPicFileId;
	long patientAccessId;
	
	String patientAccessStatus;
	MessageVO messageVO;

	long fromDocId;
	String fromDocDisplayName;
	String fromDocSpeciality;
	String fromDocPhoneNumber;

	long toDocId;
	String toDocDisplayName;
	String toDocSpeciality;
	String toDocPhoneNumber;

	int status;
	Date dateOfReferral;
	int referralType;//sent or recieved 

	public long getReferralId() {
		return referralId;
	}

	public void setReferralId(long referralId) {
		this.referralId = referralId;
	}

	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	public Date getPatientDateOfBirth() {
		return patientDateOfBirth;
	}

	public void setPatientDateOfBirth(Date patientDateOfBirth) {
		this.patientDateOfBirth = patientDateOfBirth;
	}

	public long getPatientGender() {
		return patientGender;
	}

	public void setPatientGender(long patientGender) {
		this.patientGender = patientGender;
	}

	public String getPatientPhoneNumber() {
		return patientPhoneNumber;
	}

	public void setPatientPhoneNumber(String patientPhoneNumber) {
		this.patientPhoneNumber = patientPhoneNumber;
	}

	public String getPatientPicFileId() {
		return patientPicFileId;
	}

	public void setPatientPicFileId(String patientPicFileId) {
		this.patientPicFileId = patientPicFileId;
	}

	public MessageVO getMessageVO() {
		return messageVO;
	}

	public void setMessageVO(MessageVO messageVO) {
		this.messageVO = messageVO;
	}

	public long getFromDocId() {
		return fromDocId;
	}

	public void setFromDocId(long fromDocId) {
		this.fromDocId = fromDocId;
	}

	public String getFromDocPhoneNumber() {
		return fromDocPhoneNumber;
	}

	public void setFromDocPhoneNumber(String fromDocPhoneNumber) {
		this.fromDocPhoneNumber = fromDocPhoneNumber;
	}

	public long getToDocId() {
		return toDocId;
	}

	public void setToDocId(long toDocId) {
		this.toDocId = toDocId;
	}

	public String getToDocPhoneNumber() {
		return toDocPhoneNumber;
	}

	public void setToDocPhoneNumber(String toDocPhoneNumber) {
		this.toDocPhoneNumber = toDocPhoneNumber;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getDateOfReferral() {
		return dateOfReferral;
	}

	public void setDateOfReferral(Date dateOfReferral) {
		this.dateOfReferral = dateOfReferral;
	}

	public int getReferralType() {
		return referralType;
	}

	public void setReferralType(int referralType) {
		this.referralType = referralType;
	}

	public String getPatientFirstName() {
		return patientFirstName;
	}

	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}

	public String getPatientLastName() {
		return patientLastName;
	}

	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}

	public String getFromDocDisplayName() {
		return fromDocDisplayName;
	}

	public void setFromDocDisplayName(String fromDocDisplayName) {
		this.fromDocDisplayName = fromDocDisplayName;
	}

	public String getToDocDisplayName() {
		return toDocDisplayName;
	}

	public void setToDocDisplayName(String toDocDisplayName) {
		this.toDocDisplayName = toDocDisplayName;
	}

	public long getPatientAccessId() {
		return patientAccessId;
	}

	public void setPatientAccessId(long patientAccessId) {
		this.patientAccessId = patientAccessId;
	}

	public String getPatientAccessStatus() {
		return patientAccessStatus;
	}

	public void setPatientAccessStatus(String patientAccessStatus) {
		this.patientAccessStatus = patientAccessStatus;
	}

	public String getFromDocSpeciality() {
		return fromDocSpeciality;
	}

	public void setFromDocSpeciality(String fromDocSpeciality) {
		this.fromDocSpeciality = fromDocSpeciality;
	}

	public String getToDocSpeciality() {
		return toDocSpeciality;
	}

	public void setToDocSpeciality(String toDocSpeciality) {
		this.toDocSpeciality = toDocSpeciality;
	}

}
