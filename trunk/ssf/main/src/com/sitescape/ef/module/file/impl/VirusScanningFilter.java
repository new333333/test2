package com.sitescape.ef.module.file.impl;

import com.sitescape.ef.module.file.ContentFilter;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.util.FileUploadItem;

public class VirusScanningFilter implements ContentFilter {

	public void filter(FileUploadItem fui) throws FilterException {
		if(fui.getOriginalFilename().equals("junk.txt"))
			throw new FilterException("I don't like the file name!");
	}

}
