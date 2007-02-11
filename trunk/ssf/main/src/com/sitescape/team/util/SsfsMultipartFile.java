package com.sitescape.team.util;

import java.io.InputStream;
import java.util.Date;


public class SsfsMultipartFile extends SimpleMultipartFile implements FileModDateSupport {

	private Date modDate;

	public SsfsMultipartFile(String fileName, InputStream content) {
		super(fileName, content);
	}
	
	public SsfsMultipartFile(String fileName, InputStream content, 
			Date modificationDate) {
		super(fileName, content);
		this.modDate = modificationDate;
	}

	public Date getModDate() {
		return modDate;
	}

}
