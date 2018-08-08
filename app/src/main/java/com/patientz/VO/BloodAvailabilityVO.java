package com.patientz.VO;

/**
 * Created by sunil on 3/5/18.
 */

public class BloodAvailabilityVO {
    long id;
    long orgBranchId;
    String bloodGroup;
    //A+,B- etc
    String bloodComponentType;	//Platelet,whole etc
    int bloodAvailability ;		//Number of units of blood.. Ex:30 units

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrgBranchId() {
        return orgBranchId;
    }

    public void setOrgBranchId(long orgBranchId) {
        this.orgBranchId = orgBranchId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getBloodComponentType() {
        return bloodComponentType;
    }

    public void setBloodComponentType(String bloodComponentType) {
        this.bloodComponentType = bloodComponentType;
    }

    public int getBloodAvailability() {
        return bloodAvailability;
    }

    public void setBloodAvailability(int bloodAvailability) {
        this.bloodAvailability = bloodAvailability;
    }
}