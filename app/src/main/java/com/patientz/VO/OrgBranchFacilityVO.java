package com.patientz.VO;

/**
 * Created by sunil on 11/4/17.
 */

public class OrgBranchFacilityVO {
    long   facilityId;
    long orgBranchFacilityId;
    String orgFacilityDisplayName;
    boolean orgFacilityStatus;
    String facilityCode;

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public long getFacilityId() {
        return facilityId;
    }
    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }
    public long getOrgBranchFacilityId() {
        return orgBranchFacilityId;
    }
    public void setOrgBranchFacilityId(long orgBranchFacilityId) {
        this.orgBranchFacilityId = orgBranchFacilityId;
    }
    public String getOrgFacilityDisplayName() {
        return orgFacilityDisplayName;
    }
    public void setOrgFacilityDisplayName(String orgFacilityDisplayName) {
        this.orgFacilityDisplayName = orgFacilityDisplayName;
    }
    public boolean isOrgFacilityStatus() {
        return orgFacilityStatus;
    }
    public void setOrgFacilityStatus(boolean orgFacilityStatus) {
        this.orgFacilityStatus = orgFacilityStatus;
    }

}
