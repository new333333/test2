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
package com.sitescape.team.util;

import java.io.File;

public class DirPath {
	
	public static String getXsltDirPath() {
		return getWebappDirPathHidden("xslt");
	}

	public static String getRssDirPath() {
		return getWebappDirPathHidden("rss");
	}
	
	public static String getThumbnailDirPath() {
		return getImagesDirPath() + File.separator + "thumbnails";
	}
	
    private static String getWebinfDirPath() {
    	return SpringContextUtil.getWebappRootDirPath() + File.separator + "WEB-INF";
    }
    
    private static String getWebappDirPathVisible(String subdirName) {
    	return SpringContextUtil.getWebappRootDirPath() + File.separator + subdirName;
    }
    
    private static String getWebappDirPathHidden(String subdirName) {
    	return getWebinfDirPath() + File.separator + subdirName;
    }
    
	private static String getImagesDirPath() {
		return getWebappDirPathVisible("images");
	}
	
}
