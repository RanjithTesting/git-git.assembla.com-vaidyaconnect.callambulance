package com.patientz.VO;

import java.util.Date;
import java.util.LinkedHashMap;

public class HealthRecordVO {
    long attendedById;
    long patientId;
    long doctorId;
    long orgId;
    String orgName;
    String attendedByDisplayName;
    long role;
    String roleDescription;
    String recordType;
    Date startDate;
    Date endDate;
    long visitRecordId;
    long lastUpdatedUserRoleId;
    String lastUpdatedUserDisplayName;
    String lastUpdatedDate;
    String unRegDoctorName;
    String unRegOrgName;
    LinkedHashMap<String, String> healthRecord;
    long id;
    LinkedHashMap<Long, FileVO> attachmentsMap;

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public long getVisitRecordId() {
        return visitRecordId;
    }

    public void setVisitRecordId(long visitRecordId) {
        this.visitRecordId = visitRecordId;
    }


    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getUnRegOrgName() {
        return unRegOrgName;
    }

    public void setUnRegOrgName(String unRegOrgName) {
        this.unRegOrgName = unRegOrgName;
    }

    public String getUnRegDoctorName() {
        return unRegDoctorName;
    }

    public void setUnRegDoctorName(String unRegDoctorName) {
        this.unRegDoctorName = unRegDoctorName;
    }


    public LinkedHashMap<Long, FileVO> getAttachmentsMap() {
        return attachmentsMap;
    }

    public void setAttachmentsMap(LinkedHashMap<Long, FileVO> attachmentsMap) {
        this.attachmentsMap = attachmentsMap;
    }

    public String getLastUpdatedUserDisplayName() {
        return lastUpdatedUserDisplayName;
    }

    public void setLastUpdatedUserDisplayName(String lastUpdatedUserDisplayName) {
        this.lastUpdatedUserDisplayName = lastUpdatedUserDisplayName;
    }

    public long getLastUpdatedUserRoleId() {
        return lastUpdatedUserRoleId;
    }

    public void setLastUpdatedUserRoleId(long lastUpdatedUserRoleId) {
        this.lastUpdatedUserRoleId = lastUpdatedUserRoleId;
    }


    /**
     * @return the attendedById
     */
    public long getAttendedById() {
        return attendedById;
    }

    public void setRole(long role) {
        this.role = role;
    }

    /**
     * @param attendedById the attendedById to set
     */
    public void setAttendedById(long attendedById) {
        this.attendedById = attendedById;
    }

    /**
     * @return the patientId
     */
    public long getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the doctorId
     */
    public long getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId the doctorId to set
     */
    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

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
     * @return the recordType
     */
    public String getRecordType() {
        return recordType;
    }

    /**
     * @param recordType the recordType to set
     */
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    /**
     * @return the attendedByDisplayName
     */
    public String getAttendedByDisplayName() {
        return attendedByDisplayName;
    }

    /**
     * @param attendedByDisplayName the attendedByDisplayName to set
     */
    public void setAttendedByDisplayName(String attendedByDisplayName) {
        this.attendedByDisplayName = attendedByDisplayName;
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
    public void setRole(int role) {
        this.role = role;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the healthRecord
     */
    public LinkedHashMap<String, String> getHealthRecord() {
        return healthRecord;
    }

    /**
     * @param healthRecord the healthRecord to set
     */
    public void setHealthRecord(LinkedHashMap<String, String> healthRecord) {
        this.healthRecord = healthRecord;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

}