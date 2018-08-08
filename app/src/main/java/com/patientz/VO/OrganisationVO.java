package com.patientz.VO;

public class OrganisationVO {

	String orgName;
	String orgType;
	long orgId;
	long orgLogoId;
	FileVO orgLogo;
	String email;
	String phone;
	String status;
	String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgLogo(FileVO orgLogo) {
		this.orgLogo = orgLogo;
	}

	public FileVO getOrgLogo() {
		return orgLogo;
	}

	public void setOrgLogoId(long orgLogoId) {
		this.orgLogoId = orgLogoId;
	}

	public long getOrgLogoId() {
		return orgLogoId;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getOrgType() {
		return orgType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return this.orgName;
	}
}