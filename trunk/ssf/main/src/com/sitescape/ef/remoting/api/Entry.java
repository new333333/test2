package com.sitescape.ef.remoting.api;

public class Entry {

	private Long id;
	private Long binderId;
	//private long id;
	//private long binderId;
	private String title;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getBinderId() {
		return binderId;
	}
	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/*
	public long getBinderId() {
		return binderId;
	}
	public void setBinderId(long binderId) {
		this.binderId = binderId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}*/
}
