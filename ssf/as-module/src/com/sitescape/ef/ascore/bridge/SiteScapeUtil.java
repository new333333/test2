package com.sitescape.ef.ascore.bridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.RequestDispatcher;

public class SiteScapeUtil {

	// Servlet request dispatcher to SSF web app to allow cross-context 
	// access to it. 
	private static RequestDispatcher ccDispatcher;
	// SSF context path. This is paramaterized just in case user changed it.
	private static String ssfContextPath;
	// SSF's web app class loader
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
	
	public static Object invoke(Method method, Object obj, Object... args) 
	throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ClassLoader clSave = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(SiteScapeUtil.getClassLoader());
				
			return method.invoke(obj, args);
		}
		finally {
			Thread.currentThread().setContextClassLoader(clSave);
		}
	}
}
