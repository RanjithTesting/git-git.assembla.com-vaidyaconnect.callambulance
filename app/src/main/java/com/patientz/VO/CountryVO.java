package com.patientz.VO;

public class CountryVO {

	String codeA2; //ISO_3166 Alpha 2 code
	String codeA3;//ISO_3166 Alpha 3 code
	String displayName;
	String ISDCode;
	
	long countryId;
	public long getCountryId() {
		return countryId;
	}
	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}
	public String getCodeA2() {
		return codeA2;
	}
	public void setCodeA2(String codeA2) {
		this.codeA2 = codeA2;
	}
	public String getCodeA3() {
		return codeA3;
	}
	public void setCodeA3(String codeA3) {
		this.codeA3 = codeA3;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getISDCode() {
		return ISDCode;
	}
	public void setISDCode(String iSDCode) {
		ISDCode = iSDCode;
	}
}
