package com.patientz.VO;

import java.util.Date;


public class UserVO {
	
	Long id;
	Long userId;
	String username;
	String firstName;
	String lastName;
	String email;
	String password;
	UserProfileVO profileVO;
	
	int errorCode;
	boolean enabled;
	Date dateCreated;
	Date lastUpdated;
	boolean identityUploadFlag;
	String sessionId;
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	public boolean isIdentityUploadFlag() {
		return identityUploadFlag;
	}
	public void setIdentityUploadFlag(boolean identityUploadFlag) {
		this.identityUploadFlag = identityUploadFlag;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public UserProfileVO getProfileVO() {
		return profileVO;
	}
	public void setProfileVO(UserProfileVO profileVO) {
		this.profileVO = profileVO;
	}
	
}
