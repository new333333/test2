package com.sitescape.team.asmodule.bridge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SiteScapeBridgeUtil {

	// Servlet request dispatcher to SSF web app to allow cross-context 
	// access to it. 
	private static RequestDispatcher ccDispatcher;
	// SSF context path. This is paramaterized just in case user changed it.
	private static String ssfContextPath;
	// SSF's web app class loader
	private static ClassLoader classLoader;

	public static void setClassLoader(ClassLoader contextClassLoader) {
		classLoader = contextClassLoader;
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
			Thread.currentThread().setContextClassLoader(SiteScapeBridgeUtil.getClassLoader());
				
			return method.invoke(obj, args);
		}
		finally {
			Thread.currentThread().setContextClassLoader(clSave);
		}
	}
	
	/*
	 * Get the named method of the class.
	 */
	public static Method getMethod(String className, String methodName,
            Class... parameterTypes)
     throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		return getClass(className).getMethod(methodName, parameterTypes);
	}
	
	/*
	 * Create an instance of the specified class and return it as an Object.
	 */
	public static Object newInstance(String className) throws InstantiationException,
    IllegalAccessException, ClassNotFoundException {
		return getClass(className).newInstance();
	}
	
	/*
	 * Includes the content of a resource (servlet, JSP page, HTML file) 
	 * in the response.
	 */
	public static void include(ServletRequest req, ServletResponse res) 
	throws ServletException, IOException {
		getCCDispatcher().include(req, res);
	}
	
	/*
	 * Load the class using SSF's webapp classloader.
	 */
	protected static Class getClass(String className) throws ClassNotFoundException {
		return Class.forName(className, true, getClassLoader());
	}
	
	/*
	 * Get SSF's web app classloader.
	 */
	protected static ClassLoader getClassLoader() {
		return classLoader;
	}

	/*
	 * Get the request dispatcher for SSF's crosscontext dispatcher servlet.
	 */
	protected static RequestDispatcher getCCDispatcher() {
		return ccDispatcher;
	}
	
}
