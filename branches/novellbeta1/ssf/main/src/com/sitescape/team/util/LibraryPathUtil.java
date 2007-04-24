package com.sitescape.team.util;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.module.binder.BinderModule;

public class LibraryPathUtil {

	public static final String DELIM = "/";
	
	public static String getName(String libraryPath) {
		int index = libraryPath.lastIndexOf(DELIM);
		return libraryPath.substring(index + 1);
	}
	
	public static String getParentBinderPath(String libraryPath) {
		int index = libraryPath.lastIndexOf(DELIM);
		if(index > 0)
			return libraryPath.substring(0, index);
		else
			return null;
	}
	
	public static String getPath(String parentPath, String name) {
		return parentPath + DELIM + name;
	}
	
	public static Binder getBinder(String libraryPath) {
		return getBinderModule().getBinderByPathName(libraryPath);
	}
	
	public static Binder getParentBinder(String libraryPath) {
		String parentBinderPath = getParentBinderPath(libraryPath);
		if(parentBinderPath != null)
			return getBinderModule().getBinderByPathName(parentBinderPath);
		else
			return null;
	}
	
	private static BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}
}
