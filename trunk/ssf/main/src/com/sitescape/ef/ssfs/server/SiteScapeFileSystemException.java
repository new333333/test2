package com.sitescape.ef.ssfs.server;

public class SiteScapeFileSystemException extends RuntimeException {

	// tells whether warning or error
	private boolean warning = false; // default to 'error'
	
	public SiteScapeFileSystemException() {
	}
	
	public SiteScapeFileSystemException(String msg) {
		super(msg);
	}
	
	public SiteScapeFileSystemException(String msg, boolean warning) {
		this(msg);
		this.warning = warning;
	}
	
	public boolean isWarning() {
		return warning;
	}
}
