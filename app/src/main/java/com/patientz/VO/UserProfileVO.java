package com.patientz.VO;

import java.util.ArrayList;

public class UserProfileVO {
	
	long userProfileId;
	String profileUrl;
	String doctorType;
	String displayName;
	String email;
	long circleNetworkId;
	String telWork;
	String telMobile;
	String telHome;
	String handle;
	String aboutMe;
	String speciality;
	long serverTS;
	String cityOfPractice;
	public String getCityOfPractice() {
		return cityOfPractice;
	}
	public void setCityOfPractice(String cityOfPractice) {
		this.cityOfPractice = cityOfPractice;
	}
	public void setSynapse(boolean isSynapse) {
		this.isSynapse = isSynapse;
	}
	public void setInvited(boolean isInvited) {
		this.isInvited = isInvited;
	}
	ArrayList<WorkLocationVO> workLocations;
	ArrayList<UserSpecialityVO> specialities;
	ArrayList<UserEducationVO> userEducation;
	FileVO profileImage;
	long fileId;
	boolean isSynapse;
	boolean isInvited;
	boolean isDeleted;

	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public long getServerTS() {
		return serverTS;
	}
	public void setServerTS(long serverTS) {
		this.serverTS = serverTS;
	}
	public FileVO getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(FileVO profileImage) {
		this.profileImage = profileImage;
	}
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	public String getTelHome() {
		return telHome;
	}
	public void setTelHome(String telHome) {
		this.telHome = telHome;
	}
	public long getCircleNetworkId() {
		return circleNetworkId;
	}
	public void setCircleNetworkId(long circleNetworkId) {
		this.circleNetworkId = circleNetworkId;
	}

	public long getUserProfileId() {
		return userProfileId;
	}
	public void setUserProfileId(long userProfileId) {
		this.userProfileId = userProfileId;
	}
	public String getDoctorType() {
		return doctorType;
	}
	public void setDoctorType(String doctorType) {
		this.doctorType = doctorType;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public ArrayList<WorkLocationVO> getWorkLocations() {
		return workLocations;
	}
	public void setWorkLocations(ArrayList<WorkLocationVO> workLocations) {
		this.workLocations = workLocations;
	}
	public ArrayList<UserSpecialityVO> getSpecialities() {
		return specialities;
	}
	public void setSpecialities(ArrayList<UserSpecialityVO> specialities) {
		this.specialities = specialities;
	}
	public ArrayList<UserEducationVO> getUserEducation() {
		return userEducation;
	}
	public void setUserEducation(ArrayList<UserEducationVO> userEducation) {
		this.userEducation = userEducation;
	}
	public String getTelWork() {
		return telWork;
	}
	public void setTelWork(String telWork) {
		this.telWork = telWork;
	}
	public String getTelMobile() {
		return telMobile;
	}
	public void setTelMobile(String telMobile) {
		this.telMobile = telMobile;
	}
	public String getAboutMe() {
		return aboutMe;
	}
	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public boolean getIsSynapse() {
		return isSynapse;
	}
	public void setIsSynapse(boolean isSynapse) {
		this.isSynapse = isSynapse;
	}
	public boolean getIsInvited() {
		return isInvited;
	}
	public void setIsInvited(boolean isInvited) {
		this.isInvited = isInvited;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
