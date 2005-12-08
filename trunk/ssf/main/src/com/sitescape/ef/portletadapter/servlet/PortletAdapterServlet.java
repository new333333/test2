package com.sitescape.ef.portletadapter.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

import com.sitescape.ef.context.request.RequestContextUtil;
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
    	
    	req.setAttribute(KeyNames.CTX, ctx);
    	
    	// TODO This is a temporary hack. 
		RequestContextUtil.setThreadContext("liferay.com", "wf_admin");

		super.service(req, res);
		
		req.removeAttribute(KeyNames.CTX);
		
		// TODO This is a temporary hack. 
		RequestContextUtil.clearThreadContext();
	}

}
