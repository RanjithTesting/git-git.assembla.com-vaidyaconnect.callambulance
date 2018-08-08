package com.patientz.VO;

import java.util.ArrayList;

/**
 * Created by sunil on 11/6/18.
 */

public class SpecialityFacilityTimingsVO {
    String displayName;
    ArrayList<AvailabilityCapabilityVO> timings;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<AvailabilityCapabilityVO> getTimings() {
        return timings;
    }

    public void setTimings(ArrayList<AvailabilityCapabilityVO> timings) {
        this.timings = timings;
    }
}
