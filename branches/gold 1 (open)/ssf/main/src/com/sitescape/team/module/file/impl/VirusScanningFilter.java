/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
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
