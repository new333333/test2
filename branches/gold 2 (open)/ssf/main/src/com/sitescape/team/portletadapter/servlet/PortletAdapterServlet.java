/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portletadapter.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

import com.sitescape.team.portletadapter.support.KeyNames;
import com.sitescape.team.util.SPropsUtil;
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
    	
		String charEncoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
		
		req.setCharacterEncoding(charEncoding);
		
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
