package com.sitescape.ef.ascore.cc;

import javax.servlet.RequestDispatcher;

public class SiteScapeCCUtil {

	private static RequestDispatcher ccDispatcher;
	private static String ssfContextPath;

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
