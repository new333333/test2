package org.kablink.teaming.gwt.client.profile;

import java.util.*;

import org.kablink.teaming.gwt.client.GwtUser;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfileStats  implements IsSerializable {

	private String entries = "0";
	private ArrayList<GwtUser> trackedUsers = new ArrayList<GwtUser>();

	//Must have default constructor in order to serialize
	public ProfileStats() {}

	public void setEntries(String posted) {
		this.entries = posted;
	}
	
	public String getEntries(){
		return this.entries;
	}

	public ArrayList<GwtUser> getTrackedUsers() {
		return trackedUsers;
	}

	public void addTrackedUser(GwtUser user) {
		trackedUsers.add(user);
	}
	
	public int getTrackedCnt(){
		return trackedUsers.size(); 
	}
}
