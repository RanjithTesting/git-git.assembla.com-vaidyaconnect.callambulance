package com.patientz.VO;

public class PatientSearchVO {

	String firstName;
	String lastName;
	String gender;
	int age;
	String disease;
	long profilePicId;
	long patientProfileId;
	FileVO profilePic;
	long id;
	String regId;
	
	
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}
	/**
	 * @return the disease
	 */
	public String getDisease() {
		return disease;
	}
	/**
	 * @param disease the disease to set
	 */
	public void setDisease(String disease) {
		this.disease = disease;
	}
	/**
	 * @return the profilePicId
	 */
	public long getProfilePicId() {
		return profilePicId;
	}

	public void setPatientProfileId(long patientProfileId) {
		this.patientProfileId = patientProfileId;
	}
	public long getPatientProfileId() {
		return patientProfileId;
	}
	
	/**
	 * @param profilePicId the profilePicId to set
	 */
	public void setProfilePicId(long profilePicId) {
		this.profilePicId = profilePicId;
	}
	/**
	 * @return the profilePic
	 */
	public FileVO getProfilePic() {
		return profilePic;
	}
	/**
	 * @param profilePic the profilePic to set
	 */
	public void setProfilePic(FileVO profilePic) {
		this.profilePic = profilePic;
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
	
}