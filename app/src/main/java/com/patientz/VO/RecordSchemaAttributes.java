package com.patientz.VO;

import java.util.List;

public class RecordSchemaAttributes
{
	String attributeKey;
    String attributeType;
    String regEx;
    String regExDescription;
    String uiElement;
    boolean isMandatory;
    long uiOrder;
    List<Long> roleIds;
	public String getAttributeKey() {
		return attributeKey;
	}
	public void setAttributeKey(String attributeKey) {
		this.attributeKey = attributeKey;
	}
	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	public String getRegEx() {
		return regEx;
	}
	public void setRegEx(String regEx) {
		this.regEx = regEx;
	}
	public String getRegExDescription() {
		return regExDescription;
	}
	public void setRegExDescription(String regExDescription) {
		this.regExDescription = regExDescription;
	}
	public String getUiElement() {
		return uiElement;
	}
	public void setUiElement(String uiElement) {
		this.uiElement = uiElement;
	}
	public boolean isMandatory() {
		return isMandatory;
	}
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	public long getUiOrder() {
		return uiOrder;
	}
	public void setUiOrder(long uiOrder) {
		this.uiOrder = uiOrder;
	}
	public List<Long> getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

}
