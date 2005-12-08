package com.sitescape.ef.portalmodule.web.crosscontext;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.ef.portalmodule.web.util.AttributesAndParamsOnlyServletRequest;

public class DispatchClient {
	
	private static ServletConfig portalServletConfig;
	private static ServletContext ssfContext;
	
	public static void init(ServletConfig portalServletCfg) {
		portalServletConfig = portalServletCfg;
	}
	
	public static void fini() {	
	}
	
	public static void doDispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// We use just-in-time initialization, rather than doing it fully at the 
		// system startup time, because the SSF web app may have not been loaded
		// at the time the portal is initialized. So we defer completion of this
		// initialization until very first user attempts to log into the system.
		initInternal();
		
		RequestDispatcher dispatcher = ssfContext.getNamedDispatcher("ccDispatchServer");
		
		dispatcher.include(request, response);
	}
	
	private static void initInternal() {
		if(ssfContext == null) {
			synchronized(DispatchClient.class) {
				if(ssfContext == null) {
					String ssfContextPath = portalServletConfig.getInitParameter("contextPath");
					ssfContext = portalServletConfig.getServletContext().getContext(ssfContextPath);
				}
			}
		}
	}
}
