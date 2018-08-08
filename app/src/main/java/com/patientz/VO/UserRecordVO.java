package com.patientz.VO;

import java.util.ArrayList;


public class UserRecordVO {
	long userProfileId;
	UserInfoVO userInfoVO;
	ArrayList<InsuranceVO> insuranceVOs;
	HealthRecordVO healthRecordVO;
	ArrayList<EmergencyContactsVO> emergencyContactVO;
	ArrayList<DoctorsVO> myDoctorsVO;
	ArrayList<HealthRecordVO> visitRecordVO;
	ArrayList<MedicalHistoryCollectionVO> diagnosisVO;
	ArrayList<OrganisationVO> patientOrgs;
	ArrayList<ReferralVO> referralsList;
	ArrayList<HealthRecordVO> prescriptionsList;
	ArrayList<HealthRecordVO> vaccinationsList;
	ArrayList<HealthRecordVO> nutritionVitalsList;

	public ArrayList<HealthRecordVO> getNutritionVitalsList() {
		return nutritionVitalsList;
	}

	public void setNutritionVitalsList(ArrayList<HealthRecordVO> nutritionVitalsList) {
		this.nutritionVitalsList = nutritionVitalsList;
	}

	public ArrayList<HealthRecordVO> getVaccinationsList() {
		return vaccinationsList;
	}

	public void setVaccinationsList(ArrayList<HealthRecordVO> vaccinationsList) {
		this.vaccinationsList = vaccinationsList;
	}

	public ArrayList<HealthRecordVO> getPrescriptionsList() {
		return prescriptionsList;
	}

	public void setPrescriptionsList(ArrayList<HealthRecordVO> prescriptionsList) {
		this.prescriptionsList = prescriptionsList;
	}

	public ArrayList<HealthRecordVO> getDischargeSummariesList() {
		return dischargeSummariesList;
	}

	public void setDischargeSummariesList(
			ArrayList<HealthRecordVO> dischargeSummariesList) {
		this.dischargeSummariesList = dischargeSummariesList;
	}

	ArrayList<HealthRecordVO> dischargeSummariesList;

	

	ArrayList<HealthRecordVO> reportsList;
	ArrayList<HealthRecordVO> receiptsList;

	public ArrayList<HealthRecordVO> getReportsList() {
		return reportsList;
	}

	public void setReportsList(ArrayList<HealthRecordVO> reportsList) {
		this.reportsList = reportsList;
	}

	public ArrayList<HealthRecordVO> getReceiptsList() {
		return receiptsList;
	}

	public void setReceiptsList(ArrayList<HealthRecordVO> receiptsList) {
		this.receiptsList = receiptsList;
	}

	public ArrayList<OrganisationVO> getPatientOrgs() {
		return patientOrgs;
	}

	public ArrayList<ReferralVO> getReferralsList() {
		return referralsList;
	}

	public void setReferralsList(ArrayList<ReferralVO> referralsList) {
		this.referralsList = referralsList;
	}

	public void setPatientOrgs(ArrayList<OrganisationVO> patientOrgs) {
		this.patientOrgs = patientOrgs;
	}


	public ArrayList<HealthRecordVO> getVisitRecordVO() {
		return visitRecordVO;
	}

	public void setVisitRecordVO(ArrayList<HealthRecordVO> visitRecordVO) {
		this.visitRecordVO = visitRecordVO;
	}

	public ArrayList<MedicalHistoryCollectionVO> getDiagnosisVO() {
		return diagnosisVO;
	}

	public void setDiagnosisVO(ArrayList<MedicalHistoryCollectionVO> diagnosisVO) {
		this.diagnosisVO = diagnosisVO;
	}

	public UserInfoVO getUserInfoVO() {
		return userInfoVO;
	}

	public void setUserInfoVO(UserInfoVO userInfoVO) {
		this.userInfoVO = userInfoVO;
	}

	public ArrayList<InsuranceVO> getInsuranceVOs() {
		return insuranceVOs;
	}

	public void setInsuranceVOs(ArrayList<InsuranceVO> insuranceVOs) {
		this.insuranceVOs = insuranceVOs;
	}

	public HealthRecordVO getHealthRecordVO() {
		return healthRecordVO;
	}

	public void setHealthRecordVO(HealthRecordVO mHealthRecordVO) {
		this.healthRecordVO = mHealthRecordVO;
	}

	public ArrayList<EmergencyContactsVO> getEmergencyContactVO() {
		return emergencyContactVO;
	}

	public void setEmergencyContactVO(
			ArrayList<EmergencyContactsVO> emergencyContactVO) {
		this.emergencyContactVO = emergencyContactVO;
	}

	public ArrayList<DoctorsVO> getMyDoctorsVO() {
		return myDoctorsVO;
	}

	public void setMyDoctorsVO(ArrayList<DoctorsVO> myDoctorsVO) {
		this.myDoctorsVO = myDoctorsVO;
	}

	public long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(long userProfileId) {
		this.userProfileId = userProfileId;
	}
}
