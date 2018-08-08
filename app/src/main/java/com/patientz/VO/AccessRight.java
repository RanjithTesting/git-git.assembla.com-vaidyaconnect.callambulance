package com.patientz.VO;

public class AccessRight {
	long uid;
	boolean canRead;
	boolean canInsert;
	boolean canModify;
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public boolean isCanRead() {
		return canRead;
	}
	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}
	public boolean isCanInsert() {
		return canInsert;
	}
	public void setCanInsert(boolean canInsert) {
		this.canInsert = canInsert;
	}
	public boolean isCanModify() {
		return canModify;
	}
	public void setCanModify(boolean canModify) {
		this.canModify = canModify;
	}
	
	
}
