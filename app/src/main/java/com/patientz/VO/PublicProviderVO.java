package com.patientz.VO;

/**
 * Created by sunil on 05/10/16.
 */

public class PublicProviderVO {
    String displayName;
    String emergencyPhoneNo;
    String fireNumber;
    String policeNo;

    public String getFireNumber() {
        return fireNumber;
    }

    public void setFireNumber(String fireNumber) {
        this.fireNumber = fireNumber;
    }

    public String getPoliceNo() {
        return policeNo;
    }

    public void setPoliceNo(String policeNo) {
        this.policeNo = policeNo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmergencyPhoneNo() {
        return emergencyPhoneNo;
    }

    public void setEmergencyPhoneNo(String emergencyPhoneNo) {
        this.emergencyPhoneNo = emergencyPhoneNo;
    }
}
