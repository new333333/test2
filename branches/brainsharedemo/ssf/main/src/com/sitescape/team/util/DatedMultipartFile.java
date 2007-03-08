package com.sitescape.team.util;

import java.io.InputStream;
import java.util.Date;


public class DatedMultipartFile extends SimpleMultipartFile implements FileModDateSupport {

	private Date modDate;

	public DatedMultipartFile(String fileName, InputStream content) {
		super(fileName, content);
	}
	
	public DatedMultipartFile(String fileName, InputStream content, 
			Date modificationDate) {
		super(fileName, content);
		this.modDate = modificationDate;
	}

	public Date getModDate() {
		return modDate;
	}

}
