package com.sitescape.ef.util;

import java.io.File;

public class DirPath {
	
	public static String getXsltDirPath() {
		return getWebappDirPathHidden("xslt");
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
