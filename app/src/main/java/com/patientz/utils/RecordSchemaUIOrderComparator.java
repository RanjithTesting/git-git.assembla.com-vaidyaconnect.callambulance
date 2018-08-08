package com.patientz.utils;

import com.patientz.VO.RecordSchemaAttributes;

import java.util.Comparator;


public class RecordSchemaUIOrderComparator implements Comparator<RecordSchemaAttributes> {

	@Override
		public int compare(RecordSchemaAttributes mRecordSchemaAttributes1,
				RecordSchemaAttributes mRecordSchemaAttributes2) {
			return (mRecordSchemaAttributes1.getUiOrder() < mRecordSchemaAttributes2.getUiOrder() ) ? -1: (mRecordSchemaAttributes1.getUiOrder() > mRecordSchemaAttributes2.getUiOrder() ) ? 1:0 ;
		}
	}


