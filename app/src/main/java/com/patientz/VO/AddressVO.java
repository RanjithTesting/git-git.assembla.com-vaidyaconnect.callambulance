package com.patientz.VO;



public class AddressVO {

	long addressId;
	String streetAddress;
	String additionalAddress;
	String city;
	String state;
	String postalCode;
	String zone ;// Mandal, taluk, locality, mohalla ;-)
	String district;
	CountryVO country;
	String notes;

	String addressType ;//[Residence, dwelling, fort, Barrack]
	String longitude;
	String latitude;
	String geocode;
	String landmarks;
	String mapURL;
	long geonamesId;
	
	public long getAddressId() {
		return addressId;
	}
	public void setAddressId(long addressId) {
		this.addressId = addressId;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getAdditionalAddress() {
		return additionalAddress;
	}
	public void setAdditionalAddress(String additionalAddress) {
		this.additionalAddress = additionalAddress;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public CountryVO getCountry() {
		return country;
	}
	public void setCountry(CountryVO country) {
		this.country = country;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getGeocode() {
		return geocode;
	}
	public void setGeocode(String geocode) {
		this.geocode = geocode;
	}
	public String getLandmarks() {
		return landmarks;
	}
	public void setLandmarks(String landmarks) {
		this.landmarks = landmarks;
	}
	public String getMapURL() {
		return mapURL;
	}
	public void setMapURL(String mapURL) {
		this.mapURL = mapURL;
	}
	public long getGeonamesId() {
		return geonamesId;
	}
	public void setGeonamesId(long geonamesId) {
		this.geonamesId = geonamesId;
	}
}
