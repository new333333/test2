package com.sitescape.team.portletadapter.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

import com.sitescape.team.asmodule.bridge.SiteScapeBridgeUtil;
import com.sitescape.team.portletadapter.support.KeyNames;
import com.sitescape.team.web.util.DebugHelper;

/**
 * This servlet is loaded at the system startup time to set up a runtime
 * environment that allows portlets to execute in SiteScape specific 
 * non-standard container.  
 * 
 * @author jong
 *
 */
public class PortletAdapterServlet extends DispatcherServlet {
	
    public void service(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

    	ServletContext ctx = getServletContext();
    	
    	// This attribute is used to distinguish adapter request from regular request
    	req.setAttribute(KeyNames.CTX, ctx);
    	
    	try {
	    	// Print debug information pertaining to cross context session sharing
			DebugHelper.testRequestEnv("PortletAdapterServlet", req);
	    	
			super.service(req, res);
    	}
		finally {
			req.removeAttribute(KeyNames.CTX);
		}
	}

}
