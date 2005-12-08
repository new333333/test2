package com.sitescape.ef.portalmodule.web.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sitescape.ef.portalmodule.web.crosscontext.DispatchClient;

public class SiteScapeServlet extends GenericServlet {

	public void init() throws ServletException {
		// Initialize cross-context dispatch client for SSF web app. 
		DispatchClient.init(getServletConfig());
	}
	
	public void destroy() {
		DispatchClient.fini();
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		// This servlet is not designed to serve interactive user requests. 
	}

}
