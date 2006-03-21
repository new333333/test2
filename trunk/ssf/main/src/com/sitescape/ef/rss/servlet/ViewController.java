package com.sitescape.ef.rss.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.servlet.SAbstractController;

public class ViewController extends SAbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO To be done by Roy 
		// This serves request from RSS reader for a specific page. 
		// Unlike ListController, this deals with regular control flow, which
		// normally involves invoking some business methods, preparing models,
		// and then delegating the rendering procedure to a view component
		// such as a JSP page, etc. 

		// Just for testing
		String data = "<html><body>How are you?</body></html>";
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		response.getOutputStream().write(data.getBytes());

		response.getOutputStream().flush();
		
		return null; // Normally rendering should be delegated to a view
	}
}
