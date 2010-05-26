package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfileStats  implements IsSerializable {

	private String entries = "0";
	private String followers = "0";
	private String following = "0";

	//Must have default constructor in order to serialize
	public ProfileStats() {}

	public void setEntries(String posted) {
		this.entries = posted;
	}
	
	public String getEntries(){
		return this.entries;
	}

	public String getFollowers() {
		return followers;
	}

	public void setFollowers(String followers) {
		this.followers = followers;
	}

	public String getFollowing() {
		return following;
	}

	public void setFollowing(String following) {
		this.following = following;
	}
	
	
}
