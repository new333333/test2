package org.kablink.teaming.gwt.client.profile;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfileInfo implements IsSerializable  {

	private String title;
	private String binderId;
	private String userId;
	private String pictureUrl;
	
	private ArrayList<ProfileCategory> categories = new ArrayList<ProfileCategory>();
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ProfileInfo() {
		
	}
	
	public ArrayList<ProfileCategory> getCategories() {
		return categories;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}

	public ProfileCategory get(String name) {
		
		ProfileCategory category = null;
		
		for(ProfileCategory cat: categories) {
			if( cat.getName().equals(name)) {
				category = cat;
				break;
			}
		}
		
		return category;
	}
	
	public ProfileCategory get(int index) {
		return categories.get(index);
	}

	public void add(ProfileCategory cat) {
		this.categories.add(cat);
	}

	public String getBinderId() {
		return binderId;
	}

	public void setBinderId(String binderId) {
		this.binderId = binderId;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
