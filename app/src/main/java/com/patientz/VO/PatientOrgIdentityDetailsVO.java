package com.patientz.VO;

public class PatientOrgIdentityDetailsVO {

	PatientSearchVO patientSearchVO;
	String employeeNumber; // mandatory admission number for educational
	OrgLocationVO orgLocationVO; // mandatory
	String designation; // not applicable for educational intitute
	String department;// class for edu
	String managerName;// class teacher name edu
	String managerPhone;// class teacher phone
	String officialEmail;// student email
	String rollNumber;// student email
	long detailId;

	public long getDetailId() {
		return detailId;
	}

	public void setDetailId(long detailId) {
		this.detailId = detailId;
	}

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	public String getOfficialEmail() {
		return officialEmail;
	}

	public void setOfficialEmail(String officialEmail) {
		this.officialEmail = officialEmail;
	}

	public PatientSearchVO getPatientSearchVO() {
		return patientSearchVO;
	}

	public void setPatientSearchVO(PatientSearchVO patientSearchVO) {
		this.patientSearchVO = patientSearchVO;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public OrgLocationVO getOrgLocationVO() {
		return orgLocationVO;
	}

	public void setOrgLocationVO(OrgLocationVO orgLocationVO) {
		this.orgLocationVO = orgLocationVO;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerPhone() {
		return managerPhone;
	}

	public void setManagerPhone(String managerPhone) {
		this.managerPhone = managerPhone;
	}

}
