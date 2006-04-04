package com.sitescape.ef.ssfs.web.crosscontext;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispatchClient {
	
	private static final String SSF_CONTEXT_PATH_DEFAULT = "/ssf";
	
	private static final String SSFS_CC_DISPATCHER = "ssfsCCDispatcher";
	
	private static ServletConfig ssfsServletConfig;
	private static ServletContext ssfContext;
	
	public static void init(ServletConfig ssfsServletCfg) {
		ssfsServletConfig = ssfsServletCfg;
	}
	
	public static void fini() {	
	}
	
	public static void doDispatch(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		// We use just-in-time initialization, rather than doing it fully at the 
		// system startup time, because the SSF web app may have not been loaded
		// at the time the SSFS is initialized. So we defer completion of this
		// initialization until very first time user attempts to log into the system.
		initInternal();
		
		RequestDispatcher dispatcher = ssfContext.getNamedDispatcher(SSFS_CC_DISPATCHER);
		
		if(dispatcher != null) {
			dispatcher.include(request, response);
		}
		else {
			// This facility is used primarily to pass a request from SSFS
			// to SSF. If everything is configured and deployed properly,
			// the dispatcher should be always non null. However, under the
			// shutdown scenario where SSF app is shutdown before the portal,
			// it is possible that this code is no longer able to obtain
			// the dispatcher for the SSF servlet. Unfortunately, there is
			// no standard way of obtaining a handle on the log facility 
			// associated with the app server within which this code is being
			// executed. Therefore, we will simply log something to the console
			// for the informational purpose only. Since the app server is
			// shutting down anyway, this shouldn't be a big deal. 
			System.out.println("Unable to obtain request dispatcher for " +
					SSFS_CC_DISPATCHER + " from SSF context");
		}
	}
	
	private static void initInternal() {
		synchronized(DispatchClient.class) {
			if(ssfContext == null) {
				String ssfContextPath = ssfsServletConfig.getInitParameter("contextPath");
				if(ssfContextPath == null || ssfContextPath.length() == 0)
					ssfContextPath = SSF_CONTEXT_PATH_DEFAULT;					
				ssfContext = ssfsServletConfig.getServletContext().getContext(ssfContextPath);
			}
		}
	}
}
