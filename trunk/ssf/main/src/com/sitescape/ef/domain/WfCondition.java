package com.sitescape.ef.domain;

public class WfCondition {
   	private String toStateName;
	private String operator;
	private String value;
	private String entryDefId;
	private String attributeName;
	
	public WfCondition (String entryDefId, String toStateName, String attributeName, String operator, String value) {
		this.entryDefId = entryDefId;
		this.attributeName = attributeName;
		this.toStateName = toStateName;
		this.operator = operator;
		this.value = value;
	}
	public String getToStateName() {
		return toStateName;
	}
	public String getOperator() {
		return operator;
	}
	public String getValue() {
		return value;
	}
	public String getEntryDefId() {
		return entryDefId;
	}
	public String getAttributeName() {
		return attributeName;
	}
}
