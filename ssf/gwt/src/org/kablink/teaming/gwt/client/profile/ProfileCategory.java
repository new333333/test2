package org.kablink.teaming.gwt.client.profile;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfileCategory implements IsSerializable {
	
	ArrayList <ProfileAttribute> attrs = new ArrayList<ProfileAttribute>();
	String name = "";
	String title = "";
	
	public ProfileCategory() {
	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ProfileAttribute> getAttributes() {
		return attrs;
	}

	public ProfileAttribute get(String name) {
		ProfileAttribute attribute = null;
		
		for(ProfileAttribute attr: attrs){
			if(attr.getName().equals(name)) {
				attribute = attr;
				break;
			}
		}
		
		return attribute;
	}
	
	public ProfileAttribute get(int index) {
		return attrs.get(index);
	}
	
	public void add(ProfileAttribute attr) {
		attrs.add(attr);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
