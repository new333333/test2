package com.sitescape.ef.rss.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.web.servlet.SAbstractController;

public class ListController extends SAbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Get the list of RSS items and write it to servlet response as XML.
		Long binderId = new Long(RequestUtils.getRequiredStringParameter(request, "bi"));
		Binder binder = getBinderModule().getBinder(binderId);
		User user = RequestContextHolder.getRequestContext().getUser();
		
		
		response.resetBuffer();
		response.setContentType("text/xml; charset=UTF-8");
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		//use writer to enfoce character set
		response.getWriter().write(getRssGenerator().filterRss(binder,user));
		response.flushBuffer();
		return null;
	}

}
