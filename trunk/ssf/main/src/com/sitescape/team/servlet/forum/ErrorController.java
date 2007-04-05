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
package com.sitescape.team.servlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.servlet.SAbstractController;

/**
 * Call an error page. This Class was created to help the scenario of when your loading an OutputStream
 * and encounter an error. You can not just redirect to an error jsp page in this case.
 * 
 * 
 * @author Rob
 *
 */
public class ErrorController extends SAbstractController {
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		Map model = new HashMap();
		
		response.setContentType("text/html");
		String viewPath = "forum/html_converter_error";
		return new ModelAndView(viewPath, model);
	}
}

