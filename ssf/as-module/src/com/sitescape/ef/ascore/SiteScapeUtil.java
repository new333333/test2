package com.sitescape.ef.ascore;

import javax.servlet.RequestDispatcher;

public class SiteScapeUtil {

	private static RequestDispatcher ccDispatcher;
	private static String ssfContextPath;
	private static ClassLoader classLoader;

	public static ClassLoader getClassLoader() {
		return classLoader;
	}

	public static void setClassLoader(ClassLoader contextClassLoader) {
		classLoader = contextClassLoader;
	}

	public static RequestDispatcher getCCDispatcher() {
		return ccDispatcher;
	}
	
	public static void setCCDispatcher(RequestDispatcher dispatcher) {
		ccDispatcher = dispatcher;
	}
	
	public static void setSSFContextPath(String contextPath) {
		ssfContextPath = contextPath;
	}
	
	public static String getSSFContextPath() {
		return ssfContextPath;
	}
}
