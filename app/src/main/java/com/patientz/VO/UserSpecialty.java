package com.patientz.VO;

import java.io.Serializable;

/**
 * Created by sunil on 3/2/17.
 */

public class UserSpecialty implements Serializable{
    Speciality speciality;

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }
}
