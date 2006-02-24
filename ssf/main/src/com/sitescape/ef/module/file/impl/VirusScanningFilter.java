package com.sitescape.ef.module.file.impl;

import com.sitescape.ef.module.file.ContentFilter;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.util.FileUploadItem;

public class VirusScanningFilter implements ContentFilter {

	public void filter(FileUploadItem fui) throws FilterException {
		// Write here some code that actually does something useful,
		// e.g. invoking some external program to scan the file, etc.
		
		//if(fui.getOriginalFilename().equals("junk.txt"))
		//	throw new FilterException(fui.getOriginalFilename());
	}

}
