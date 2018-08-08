package com.patientz.VO;

/**
 * Created by sunil on 11/4/17.
 */

public class OrgBranchSpecialityVO {
    long specialityId;
    long orgBranchSpecialityId;
    String orgSpecialityDisplayName;
    boolean orgSpecialityStatus;
    String specialityCode;

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }

    public long getSpecialityId() {
        return specialityId;
    }

    public void setSpecialityId(long specialityId) {
        this.specialityId = specialityId;
    }

    public long getOrgBranchSpecialityId() {
        return orgBranchSpecialityId;
    }

    public void setOrgBranchSpecialityId(long orgBranchSpecialityId) {
        this.orgBranchSpecialityId = orgBranchSpecialityId;
    }

    public String getOrgSpecialityDisplayName() {
        return orgSpecialityDisplayName;
    }

    public void setOrgSpecialityDisplayName(String orgSpecialityDisplayName) {
        this.orgSpecialityDisplayName = orgSpecialityDisplayName;
    }

    public boolean isOrgSpecialityStatus() {
        return orgSpecialityStatus;
    }

    public void setOrgSpecialityStatus(boolean orgSpecialityStatus) {
        this.orgSpecialityStatus = orgSpecialityStatus;
    }
}
