package com.sitescape.team.taglib;

import javax.servlet.http.HttpServletRequest;

public class FileUrlTag extends UrlTag {

	private String fileName;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	protected void setup() {
		super.setup();
		fileName = null;
	}
	
	protected String getWebUrl(HttpServletRequest req) {
		return super.getWebUrl(req) + "/" + fileName;
	}
}
