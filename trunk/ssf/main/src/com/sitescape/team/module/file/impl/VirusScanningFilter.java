package com.sitescape.team.module.file.impl;

import com.sitescape.team.module.file.ContentFilter;
import com.sitescape.team.module.file.FilterException;
import com.sitescape.team.util.FileUploadItem;

public class VirusScanningFilter implements ContentFilter {

	public void filter(FileUploadItem fui) throws FilterException {
		// Write here some code that actually does something useful,
		// e.g. invoking some external program to scan the file, etc.
		
		//if(fui.getOriginalFilename().equals("junk.txt"))
		//	throw new FilterException(fui.getOriginalFilename());
	}

}
