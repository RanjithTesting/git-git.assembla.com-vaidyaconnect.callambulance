package com.patientz.VO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ResponseVO {

	long code;
	String response;
	String defaultRegId;
	ArrayList<LicenseAuthorityVO> registrationAuthList;
	ArrayList<UserSpecialityVO> specialitiesList;
	ArrayList<CountryVO> countriesList;
	UserVO user;
	UserProfileVO profile;
	ArrayList<PatientUserVO> patientUserVO;
	ArrayList<EmergencyContactsVO> searchList;
	ArrayList<AmbulanceProviderVO> ambulanceProvidersList;
	Date version;
	ArrayList<OrgBranchVO> orgBranchesList;

	public Date getVersion() {
		return version;
	}

	public void setVersion(Date version) {
		this.version = version;
	}

	ArrayList<DoctorSearchVO> doctorsVOList;
	InsuranceVO iVO;
	// ArrayList<AppointmentVO> appointments;
	ArrayList<AppointmentQueueVO> appointments;

	ArrayList<PatientSearchVO> patientSearchVOList;
	ArrayList<PatientRequestVO> pRVOList;

	HashMap<String, String> labelDictionary;
	ArrayList<RecordSchemaAttributes> schemaAttributeList;
	ArrayList<OrgLocationVO> orgLocationsList;
	ArrayList<OrganisationVO> orgList;
	ArrayList<ReferralVO> referralsList;
	ArrayList<PatientOrgIdentityDetailsVO> patientOrgIdentityDetailsVO;
	ArrayList<DiagnosticReportVO> reportVOs;
	ArrayList<DiagnosticTestsVO> testsVOs;

	public ArrayList<AmbulanceProviderVO> getAmbulanceProvidersList() {
		return ambulanceProvidersList;
	}

	public void setAmbulanceProvidersList(ArrayList<AmbulanceProviderVO> ambulanceProvidersList) {
		this.ambulanceProvidersList = ambulanceProvidersList;
	}



	public ArrayList<OrgLocationVO> getOrgLocationsList() {
		return orgLocationsList;
	}

	public ArrayList<PatientOrgIdentityDetailsVO> getPatientOrgIdentityDetailsVO() {
		return patientOrgIdentityDetailsVO;
	}

	public void setPatientOrgIdentityDetailsVO(
			ArrayList<PatientOrgIdentityDetailsVO> patientOrgIdentityDetailsVO) {
		this.patientOrgIdentityDetailsVO = patientOrgIdentityDetailsVO;
	}

	public ArrayList<OrgBranchVO> getOrgBranchesList() {
		return orgBranchesList;
	}

	public void setOrgBranchesList(ArrayList<OrgBranchVO> orgBranchesList) {
		this.orgBranchesList = orgBranchesList;
	}

	public ArrayList<DiagnosticReportVO> getReportVOs() {
		return reportVOs;
	}

	public void setReportVOs(ArrayList<DiagnosticReportVO> reportVOs) {
		this.reportVOs = reportVOs;
	}

	public ArrayList<DiagnosticTestsVO> getTestsVOs() {
		return testsVOs;
	}

	public void setTestsVOs(ArrayList<DiagnosticTestsVO> testsVOs) {
		this.testsVOs = testsVOs;
	}

	public void setOrgLocationsList(ArrayList<OrgLocationVO> orgLocationsList) {
		this.orgLocationsList = orgLocationsList;
	}

	public ArrayList<RecordSchemaAttributes> getSchemaAttributeList() {
		return schemaAttributeList;
	}

	public void setSchemaAttributeList(
			ArrayList<RecordSchemaAttributes> schemaAttributeList) {
		this.schemaAttributeList = schemaAttributeList;
	}

	/*
	 * public ArrayList<AppointmentVO> getAppointments() { return appointments;
	 * }
	 * 
	 * public void setAppointments(ArrayList<AppointmentVO> appointments) {
	 * this.appointments = appointments; }
	 */
	public HashMap<String, String> getLabelDictionary() {
		return labelDictionary;
	}

	public ArrayList<AppointmentQueueVO> getAppointments() {
		return appointments;
	}

	public void setAppointments(ArrayList<AppointmentQueueVO> appointments) {
		this.appointments = appointments;
	}

	public void setLabelDictionary(HashMap<String, String> labelDictionary) {
		this.labelDictionary = labelDictionary;
	}

	public ArrayList<PatientRequestVO> getpRVOList() {
		return pRVOList;
	}

	public void setpRVOList(ArrayList<PatientRequestVO> pRVOList) {
		this.pRVOList = pRVOList;
	}

	public ArrayList<PatientSearchVO> getPatientSearchVOList() {
		return patientSearchVOList;
	}

	public void setPatientSearchVOList(
			ArrayList<PatientSearchVO> patientSearchVOList) {
		this.patientSearchVOList = patientSearchVOList;
	}

	public InsuranceVO getiVO() {
		return iVO;
	}

	public void setiVO(InsuranceVO iVO) {
		this.iVO = iVO;
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public ArrayList<LicenseAuthorityVO> getRegistrationAuthList() {
		return registrationAuthList;
	}

	public void setRegistrationAuthList(
			ArrayList<LicenseAuthorityVO> registrationAuthList) {
		this.registrationAuthList = registrationAuthList;
	}

	public ArrayList<UserSpecialityVO> getSpecialitiesList() {
		return specialitiesList;
	}

	public void setSpecialitiesList(ArrayList<UserSpecialityVO> specialitiesList) {
		this.specialitiesList = specialitiesList;
	}

	public ArrayList<CountryVO> getCountriesList() {
		return countriesList;
	}

	public void setCountriesList(ArrayList<CountryVO> countriesList) {
		this.countriesList = countriesList;
	}

	public UserVO getUser() {
		return user;
	}

	public void setUser(UserVO user) {
		this.user = user;
	}

	public UserProfileVO getProfile() {
		return profile;
	}

	public void setProfile(UserProfileVO profile) {
		this.profile = profile;
	}

	public ArrayList<PatientUserVO> getPatientUserVO() {
		return patientUserVO;
	}

	public void setPatientUserVO(ArrayList<PatientUserVO> patientUserVO) {
		this.patientUserVO = patientUserVO;
	}

	public ArrayList<EmergencyContactsVO> getSearchList() {
		return searchList;
	}

	public void setSearchList(ArrayList<EmergencyContactsVO> searchList) {
		this.searchList = searchList;
	}

	public ArrayList<DoctorSearchVO> getDoctorsVOList() {
		return doctorsVOList;
	}

	public void setDoctorsVOList(ArrayList<DoctorSearchVO> doctorsVOList) {
		this.doctorsVOList = doctorsVOList;
	}

	public ArrayList<ReferralVO> getReferralsList() {
		return referralsList;
	}

	public void setReferralsList(ArrayList<ReferralVO> referralsList) {
		this.referralsList = referralsList;
	}

	public ArrayList<OrganisationVO> getOrgList() {
		return orgList;
	}

	public void setOrgList(ArrayList<OrganisationVO> orgList) {
		this.orgList = orgList;
	}



	public String getDefaultRegId() {
		return defaultRegId;
	}

	public void setDefaultRegId(String defaultRegId) {
		this.defaultRegId = defaultRegId;
	}
}
