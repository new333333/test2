package com.sitescape.ef.module.file;

import com.sitescape.team.util.FileUploadItem;

public interface ContentFilter {
	
	public void filter(FileUploadItem fui) throws FilterException;
	
}
