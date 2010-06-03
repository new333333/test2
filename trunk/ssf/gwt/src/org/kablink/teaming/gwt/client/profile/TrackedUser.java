package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TrackedUser implements IsSerializable {

	
	public String id;
	public String title;
	
	public TrackedUser() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	};
	
	
}
