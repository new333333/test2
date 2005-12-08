package com.sitescape.ef.portletadapter.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.DispatcherServlet;

import com.sitescape.ef.portletadapter.support.KeyNames;

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
	
	    	// To test cross context session sharing: 
	    	if(1 == 2) {
				String loginName = req.getRemoteUser();
				ses.setAttribute("set-by-portlet-adapter", "Hi");
				System.out.println("*** PortletAdapterServlet login name: " + loginName);
				System.out.println("*** PortletAdapterServlet session id: " + ses.getId()); 
				System.out.println("*** PortletAdapterServlet set-by-main-servlet: " + ses.getAttribute("set-by-main-servlet")); 
				System.out.println("*** PortletAdapterServlet set-by-employees: " + ses.getAttribute("set-by-employees"));
				System.out.println("*** PortletAdapterServlet set-by-download-file: " + ses.getAttribute("set-by-download-file"));
	    	}
			// test ends:
	    	
			super.service(req, res);
    	}
		finally {
			req.removeAttribute(KeyNames.CTX);
		}
	}

}
