package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.rpc.IsSerializable;


public class ProfileAttributeListElement extends ProfileAttribute implements IsSerializable {
	
	private ProfileAttribute parent;
	private int position=0;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ProfileAttributeListElement() {
	}
	
	public ProfileAttributeListElement(String name, ProfileAttribute parent) {
		setName(name);
		setParent(parent);
	}

	public ProfileAttribute getParent() {
		return parent;
	}
	public void setParent(ProfileAttribute p) {
		this.parent = p;
	}

	public int getPosition() {
		return position;
	}
	public void setPosition(int pos) {
		this.position = pos;
	}
 }
