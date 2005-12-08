package com.sitescape.ef.portletadapter.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.DispatcherServlet;

import com.sitescape.ef.portletadapter.support.KeyNames;
import com.sitescape.ef.web.util.DebugHelper;

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
	    	HttpSession ses = req.getSession();
	
	    	
	    	// Print debug information pertaining to cross context session sharing
			DebugHelper.testRequestEnv("PortletAdapterServlet", req);
	    	
			super.service(req, res);
    	}
		finally {
			req.removeAttribute(KeyNames.CTX);
		}
	}

}
