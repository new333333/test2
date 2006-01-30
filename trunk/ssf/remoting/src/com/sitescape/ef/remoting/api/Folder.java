package com.sitescape.ef.remoting.api;

public class Folder extends Binder {

	private Long parentFolderId;
	private Long topFolderId;
	
	public Long getParentFolderId() {
		return parentFolderId;
	}
	public void setParentFolderId(Long parentFolderId) {
		this.parentFolderId = parentFolderId;
	}
	
	public Long getTopFolderId() {
		return topFolderId;
	}
	public void setTopFolderId(Long topFolderId) {
		this.topFolderId = topFolderId;
	}
}
