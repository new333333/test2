package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfileAttributeAttachment implements IsSerializable {
	
	private String name;
	private String relevanceUUID;
	private String id;
	private String value;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ProfileAttributeAttachment() {
	}
	
	public ProfileAttributeAttachment(String name, String id, String value) {
		this.name = name;
		this.id = id;
		this.value = value;
	}
	 
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}
	public void setName(String attrName) {
		this.name = attrName;
	}
	public String getRelevanceUUID() {
		return relevanceUUID;
	}
	public void setRelevanceUUID(String ID) {
		this.relevanceUUID = ID;
	}
	public String getId() {
        return id;
    }
    public void setId(String sId) {
        this.id = sId;
    }
    public String toString(){
		return value;
    }
}
