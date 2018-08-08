package com.patientz.VO;


import java.util.Date;

public class HospitalVO {

	long hospitalId;
	String displayName;
	String name;
	String notes;
	//AddressVO addressVO;
	String streetAddress;
	String additionalAddress;
	String contactNo;
	String email;
	String url;
	String city;
	String state;
	String displayString;
	Date dateCreated;
	Date lastUpdated;
	String latitude;
	String longitude;
	String postalCode;
    boolean isActive;
    double distanceInKms;
    int durationInSecs;
    String distance;
    String duration;
    String branchName;
    String branchPhoneNo;
    //Newly Added for sending Ambulance Provider details
    String ambulanceProviderPhoneNo;
    long ambulanceProviderId; //Org Id of the Ambulance provider
    //AddressVO ambulanceProviderAddressVO;

    /*String displayString; //Autocomplete Hospital Name from AppUtils getPreferredHospitalDisplayString method
    String notes;
    String email;
    String url;*/

    public String getBranchPhoneNo() {
        return branchPhoneNo;
    }

    public void setBranchPhoneNo(String branchPhoneNo) {
        this.branchPhoneNo = branchPhoneNo;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public int getDurationInSecs() {
        return durationInSecs;
    }

    public void setDurationInSecs(int durationInSecs) {
        this.durationInSecs = durationInSecs;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getDistanceInKms() {
        return distanceInKms;
    }

    public void setDistanceInKms(double distanceInKms) {
        this.distanceInKms = distanceInKms;
    }

    public String getCity() {
		return city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public long getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(long hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/*public AddressVO getAddressVO() {
		return addressVO;
	}
	public void setAddressVO(AddressVO addressVO) {
		this.addressVO = addressVO;
	}*/
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDisplayString() {
		return displayString;
	}
	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

}
