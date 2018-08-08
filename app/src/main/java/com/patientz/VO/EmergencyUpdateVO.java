package com.patientz.VO;

public class EmergencyUpdateVO {
	
	String statusMsg;
	String subStatusMsg;
	String imageURL;
	String rowId;
	int statusCode;
	
	
	
	public String getStatusMsg() {
		return statusMsg;
	}
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	public String getSubStatusMsg() {
		return subStatusMsg;
	}
	public void setSubStatusMsg(String subStatusMsg) {
		this.subStatusMsg = subStatusMsg;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	

}
