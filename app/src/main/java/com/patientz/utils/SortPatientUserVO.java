package com.patientz.utils;

import com.patientz.VO.PatientUserVO;

import java.util.Comparator;



public class SortPatientUserVO implements Comparator<PatientUserVO>{

	@Override
	 public int compare(PatientUserVO lhs, PatientUserVO rhs) {
		// TODO Auto-generated method stub
		return lhs.getFirstName().compareTo(rhs.getFirstName());
	}
	

}
