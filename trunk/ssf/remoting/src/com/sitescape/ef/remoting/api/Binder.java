package com.sitescape.ef.remoting.api;

public class Binder {

	// For now, only two fields are exposed. 
	
	long id;
	String title;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
