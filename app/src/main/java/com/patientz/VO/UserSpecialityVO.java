package com.patientz.VO;

import java.io.Serializable;

public class UserSpecialityVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long   specialityId;
	long   userSpecialityId;
	long   userProfileId;
	// String code;
    String displayName;
   // String notes;
    boolean primary;
    /* boolean field's getter setter methods should be with "get" and "set" to work with Gson parser.
     *  "isPrimary()" kind of getters don't work while parsing.*/
	public long getUserSpecialityId() {
		return userSpecialityId;
	}
	public void setUserSpecialityId(long userSpecialityId) {
		this.userSpecialityId = userSpecialityId;
	}
	public long getUserProfileId() {
		return userProfileId;
	}
	public void setUserProfileId(long userProfileId) {
		this.userProfileId = userProfileId;
	}
    public long getSpecialityId() {
 		return specialityId;
 	}
 	public void setSpecialityId(long specialityId) {
 		this.specialityId = specialityId;
 	}
 	public String getDisplayName() {
 		return displayName;
 	}
 	public void setDisplayName(String displayName) {
 		this.displayName = displayName;
 	}
 	public boolean getPrimary() {
 		return primary;
 	}
 	public void setPrimary(boolean primary) {
 		this.primary = primary;
 	}
}
