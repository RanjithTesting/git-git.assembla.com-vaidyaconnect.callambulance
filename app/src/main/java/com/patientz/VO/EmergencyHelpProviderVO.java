package com.patientz.VO;

public class EmergencyHelpProviderVO {
private String name,phone,logoUri,location;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private double lat , lon;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getPhone() {
	return phone;
}
public void setPhone(String phone) {
	this.phone = phone;
}
public String getLogoUri() {
	return logoUri;
}
public void setLogoUri(String logoUri) {
	this.logoUri = logoUri;
}
public double getLat() {
	return lat;
}
public void setLat(double lat) {
	this.lat = lat;
}
public double getLon() {
	return lon;
}
public void setLon(double lon) {
	this.lon = lon;
}
}
