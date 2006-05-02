package com.sitescape.ef.ssfs.web.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sitescape.ef.ssfs.wck.Util;
import com.sitescape.ef.ssfs.web.crosscontext.DispatchClient;

public class SiteScapeServlet extends GenericServlet {

	public void init() throws ServletException {
		
		// First initialize some common paramaters. 
		ServletConfig ssfsServletCfg = getServletConfig();
		
		String ssfContextPath = ssfsServletCfg.getInitParameter("ssfContextPath");
		
		if(ssfContextPath != null && ssfContextPath.length() > 0)
			Util.setSsfContextPath(ssfContextPath);
		
		String defaultZoneName = ssfsServletCfg.getInitParameter("defaultZoneName");
		if(defaultZoneName != null && defaultZoneName.length() > 0)
			Util.setDefaultZoneName(defaultZoneName);
		
		// Initialize cross-context dispatch client for SSF web app.
		// This must come after the common paramater initialization above.  
		DispatchClient.init(ssfsServletCfg);
	}
	
	public void destroy() {
		DispatchClient.fini();
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		// This servlet is not designed to serve interactive user requests. 
	}

}
