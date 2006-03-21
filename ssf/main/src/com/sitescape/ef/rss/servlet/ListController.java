package com.sitescape.ef.rss.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.servlet.SAbstractController;

public class ListController extends SAbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO To be done by Roy 
		// Get the list of RSS items and write it to servlet response as XML.

		// Just for testing
		String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<abc>\nHello\n</abc>";
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		response.getOutputStream().write(data.getBytes());

		response.getOutputStream().flush();

		return null;
	}

}
