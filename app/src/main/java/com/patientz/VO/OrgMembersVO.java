package com.patientz.VO;

public class OrgMembersVO {
	
	long userProfileId;
	String displayName;
	String speciality;
	String roleDescription;
	long role;
	long orgId;
	/**
	 * @return the orgId
	 */
	public long getOrgId() {
		return orgId;
	}
	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	/**
	 * @return the userProfileId
	 */
	public long getUserProfileId() {
		return userProfileId;
	}
	/**
	 * @param userProfileId the userProfileId to set
	 */
	public void setUserProfileId(long userProfileId) {
		this.userProfileId = userProfileId;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the speciality
	 */
	public String getSpeciality() {
		return speciality;
	}
	/**
	 * @param speciality the speciality to set
	 */
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	/**
	 * @return the role
	 */
	public long getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(long role) {
		this.role = role;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	

}
