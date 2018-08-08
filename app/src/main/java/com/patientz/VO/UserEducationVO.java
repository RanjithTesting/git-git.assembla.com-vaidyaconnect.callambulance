package com.patientz.VO;

import java.util.Date;


public class UserEducationVO<UserEducationVOPateintz> {
	
	String	degreeName;
	Date 	startDate;
	Date 	endDate;
	String 	notes;
	String 	share;
	long collegeId;
	String college;
	UserSpecialityVO speciality;
	private long userEducationId;
	private long userProfileId;
	boolean deleted;
   
	public void setCollegeId(long collegeId) {
		this.collegeId = collegeId;
	}
	public long getCollegeId() {
		return collegeId;
	}	
	public void setDegreeName(String degreeName) {
		this.degreeName = degreeName;
	}
 	public String getDegreeName() {
 		return degreeName;
 	}
 	public void setNotes(String notes) {
 		this.notes = notes;
 	}
 	public String getNotes() {
 		return notes;
 	}
 	public void setShare(String share) {
 		this.share = share;
 	}
 	public String getShare() {
 		return share;
 	}
 	public void setCollege(String college) {
 		this.college = college;
 	}
 	public String getCollege() {
 		return college;
 	}
 	public void setStartDate(Date startDate) {
 		this.startDate= startDate;
 	}
 	public Date getStartDate() {
 		return startDate;
 	}
 	public void setEndDate(Date endDate) {
 		this.endDate= endDate;
 	}
 	public Date getEndDate() {
 		return endDate;
 	}
 	public void setSpeciality(UserSpecialityVO speciality) {
 		this.speciality = speciality;
 	}
 	public UserSpecialityVO getSpeciality() {
 		return speciality;
 	}
 	public long getUserEducationId() {
		return userEducationId;
	}
	public void setUserEducationId(long userEducationId) {
		this.userEducationId = userEducationId;
	}
	public long getUserProfileId() {
		return userProfileId;
	}
	public void setUserProfileId(long userProfileId) {
		this.userProfileId = userProfileId;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.deleted = isDeleted;
	}
	
}
