package com.patientz.VO;

import java.util.Date;

/**
 * Created by sunil on 11/5/18.
 */

public class AvailabilityCapabilityVO {
    long orgId;
    long orgBranchId;
    int flag;
    long specialityId;
    long facilityId;
    long profileId;
    String dayOfWeek;
    String startTime;
    String endTime;
    boolean isUploaded;
    long certificationId;
    long insuranceCompanyId;
    String orgType;
    boolean availabilityStatus;
    long diagnosticTestsId;
    String bloodComponentType;
    Date lastUpdated;


    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public long getOrgBranchId() {
        return orgBranchId;
    }

    public void setOrgBranchId(long orgBranchId) {
        this.orgBranchId = orgBranchId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getSpecialityId() {
        return specialityId;
    }

    public void setSpecialityId(long specialityId) {
        this.specialityId = specialityId;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public long getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(long certificationId) {
        this.certificationId = certificationId;
    }

    public long getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(long insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public boolean isAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public long getDiagnosticTestsId() {
        return diagnosticTestsId;
    }

    public void setDiagnosticTestsId(long diagnosticTestsId) {
        this.diagnosticTestsId = diagnosticTestsId;
    }

    public String getBloodComponentType() {
        return bloodComponentType;
    }

    public void setBloodComponentType(String bloodComponentType) {
        this.bloodComponentType = bloodComponentType;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
